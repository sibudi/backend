package com.yqg.service;

import com.mongodb.MongoException;
import com.mongodb.util.JSON;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.constants.SysParamContants;
import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.system.SysThirdLogsEnum;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.SmsCodeMandaoUtil;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.beans.RuleSetExecutedResult;
import com.yqg.drools.service.ApplicationService;
import com.yqg.drools.service.RuleApplicationService;
import com.yqg.drools.service.RuleService;
import com.yqg.mongo.dao.FDCResponseMongoDal;
//import com.yqg.drools.utils.RuleUtils;
import com.yqg.order.dao.OrdRiskRecordDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.risk.dao.FDCResponseMongo;
import com.yqg.risk.dao.RiskErrorLogDao;
import com.yqg.risk.entity.RiskErrorLog;
import com.yqg.service.externalChannel.service.Cash2OrderService;
import com.yqg.service.externalChannel.service.CheetahOrderService;
import com.yqg.service.externalChannel.utils.Cash2OrdCheckResultEnum;
import com.yqg.service.externalChannel.utils.Cash2OrdStatusEnum;
import com.yqg.service.externalChannel.utils.CheetahOrdStatusEnum;
import com.yqg.service.order.OrdService;
import com.yqg.service.order.request.SaveOrderUserUuidRequest;
import com.yqg.service.system.service.SysParamService;
import com.yqg.service.system.service.SysThirdLogsService;
import com.yqg.service.third.mobox.MoboxService;
import com.yqg.service.user.service.UsrService;
import com.yqg.service.risk.service.response.FDCResponse;
import com.yqg.service.risk.service.response.FDCResponse.DataDetail;
import com.yqg.service.risk.service.response.FDCResponse.DataDetail.PinjamanDetail;
import com.yqg.system.dao.SysAutoReviewRuleDao;
import com.yqg.system.entity.SysAutoReviewRule;
import com.yqg.user.dao.UsrBlackListDao;
import com.yqg.user.entity.UsrUser;
import com.yqg.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Created by Didit Dwianto on 2017/11/30.
 */
@Service
@Slf4j
public class RiskService {

    @Autowired
    private SysParamService sysParamService;
    @Autowired
    private SysThirdLogsService sysThirdLogsService;
    @Autowired
    private UsrService usrService;
    @Autowired
    private SysAutoReviewRuleDao sysAutoReviewRuleDao;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private OrdRiskRecordDao ordRiskRecordDao;
    @Autowired
    private OrdService ordService;
    @Autowired
    private OrdBlackService ordBlackService;
    @Autowired
    private RiskErrorLogService riskErrorLogService;

    @Autowired
    private CaculateScoreHandler caculateScoreHandler;// ??????

    @Autowired
    private RuleApplicationService ruleApplicationService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ReviewResultService reviewResultService;

    @Autowired
    private Cash2OrderService cash2OrderService;

    @Autowired
    private RiskErrorLogDao riskErrorLogDao;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private CheetahOrderService cheetahOrderService;

    @Autowired
    private MoboxService moboxService;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private OkHttpClient httpClient;

    @Autowired
    private FDCResponseMongoDal fdcResponseMongoDal;
    
    @Autowired
    private UsrBlackListDao usrBlackListDao;

    @Value("${fdc.inquiry_url}")
    private String fdc_inquiry_url;

    @Value("${fdc.credential}")
    private String fdc_credential;


    /**
     * 审核入口
     */
    public void risk(Integer num) {

        String sysParamValue = this.sysParamService.getSysParamValue(SysParamContants.RISK_OFF_NO);
        if (!StringUtils.isEmpty(sysParamValue) && Integer.valueOf(sysParamValue) == 1) {
            //List<OrdOrder> orderOrders = this.ordService.scanReviewOrder();// Single task
            List<OrdOrder> orderOrders = this.ordService.scanReviewOrderById(num);// Multitasking
            this.caculate(orderOrders);
        }
        else{
            log.info("======================= risk:off:no off =======================");
        }
    }

    /**
     * Start risk review
     */
    public void caculate(List<OrdOrder> orderOrders) {

        if (CollectionUtils.isEmpty(orderOrders)) {
            log.info("======================= The review list is empty =======================");
            return;
        }

        // 获取规则列表(1 有效 3测试)
        Map<String, SysAutoReviewRule> codeEntityMap = ruleService.getAllRules();

        for (OrdOrder order : orderOrders) {
            //已经签合约中：
            String signLock = redisClient.get(RedisContants.REVIEW_SIGN_LOCK + ":" + order.getUuid());
            if(StringUtils.isNotEmpty(signLock)){
                log.info("order {} in contract sign", order.getUuid());
                continue;
            }
            if (order.getStatus() != OrdStateEnum.MACHINE_CHECKING.getCode()) {
                log.info("current order status is: {}, orderNo: {}", order.getStatus(), order.getUuid());
                continue;
            }
            executorService.submit(()->{
                log.info("extractModel start == " + order.getUserUuid() + "; orderNo is " + order.getUuid());
                moboxService.getTongDunCreditBodyguardsData(order);
            });

            //设置规则执行条件
            LogUtils.addMDCRequestId(order.getUuid());
            log.error(order.getUuid() + "订单审核=========开始");
            Integer errorCount = riskErrorLogDao.errorCount(order.getUuid());
            if (errorCount != null && errorCount > 0) {
                log.warn("order with error, need to operate handle by operator, orderNo: {}",
                    order.getUuid());
                continue;
            }
            long startTime = System.currentTimeMillis();    //获取开始时间
            try {

                String reOrderNO = this.redisClient.get(RedisContants.RISK_ORDER + order.getUuid());
                if (!org.springframework.util.StringUtils.isEmpty(reOrderNO)) {
                    log.info("审核中订单号 :" + reOrderNO + "    id:" + order.getId());
                    continue;
                }
                redisClient.set(RedisContants.RISK_ORDER + order.getUuid(), order.getUuid(), 100);

                UsrUser user = this.usrService.getUserByUuid(order.getUserUuid());

                // budi: start FDC check
                boolean reject = false;

                try {

                    // 1 = new loan reason; 2 = inquiring existing customer
                    Integer reason = order.getBorrowingCount() == 1 ? 1 : 2;
                    
                    String jointUrl = new StringBuilder()
                        .append(fdc_inquiry_url)
                        .append("?id=").append(user.getIdCardNo())
                        .append("&reason=").append(reason)
                        .append("&reffid=").append(order.getUuid())
                        .toString();

                    log.info("FDC check no ktp: {}", user.getIdCardNo());
                    
                    //final String fdc_credential = Credentials.basic(fdc_username, fdc_password);
                    Request request = new Request.Builder()
                        .url(jointUrl)
                        .method("GET", null)
                        .addHeader("Content-Type", "text/plain")
                        .addHeader("Authorization", "APPCODE " + fdc_credential)
                        .build();

                    Response response = httpClient.newCall(request).execute();

                    String requestStr = request.toString();
                    String responseStr = response.body().string();

                    log.info("FDC Request: {}", requestStr);
                    log.info("FDC response: {}", responseStr);

                    if(response.isSuccessful()) {

                        FDCResponse fdcResponse = new FDCResponse();
                        DataDetail fdcDataDetailResponse = new DataDetail();
                        
                        fdcResponse = JsonUtils.deserialize(responseStr, FDCResponse.class); // data penyelenggara
                        fdcDataDetailResponse = JsonUtils.deserialize(responseStr, DataDetail.class); // data nasabah

                        // check if status found or not found in fdc
                        if (fdcResponse.getStatus().compareTo("Found") == 0) {

                            //log.info("fdcResponse: {}", fdcResponse);
                            //log.info("fdcDataDetailResponse: {}", fdcDataDetailResponse.getPinjaman());
                            for (PinjamanDetail pinjaman : fdcDataDetailResponse.getPinjaman()) {
                                
                                if (pinjaman.getKualitas_pinjaman().compareTo("1") != 0) {
        
                                    log.info("Nasabah reject karena ada pinjaman berstatus tidak lancar atau macet");
                                    reject = true;

                                    // masuk usrblack dan tidak lanjut risk
                                    if(usrBlackListDao.existsFraudUserByIdCardNo(user.getIdCardNo()) == 0) {
                                        // masuk usrBlackList type = 5, remark = "fdc reject"
                                        log.info("Insert usrBlackList!!!");
                                        usrBlackListDao.addFraudUser(order.getUserUuid(), "fdc reject");
                                    }

                                    // masuk ke systhirdlog
                                    sysThirdLogsService.addSysThirdLogs(order.getUuid(), order.getUserUuid(), SysThirdLogsEnum.FDC_INQUIRY.getCode(), null, requestStr, responseStr.substring(0, 2000));
                                    
                                    break;
                                }
                                //System.out.println(pinjaman.getStatus_pinjaman());
                            }
        
                            if (reject == false) {
        
                                // masuk risk
                                log.info("Pinjaman diteruskan ke risk, semua status fdc lancar");
                            }
        
                        } else if (fdcResponse.getStatus().compareTo("Not Found") == 0) {
        
                            // reject = false, masuk risk
                            log.info("Pinjaman Not Found di platform manapun");
                        } else {
        
                            // jika status selain found & not found, reject = false, masuk systhirdlogs
                            log.info("Status: {}", fdcResponse.getStatus());
                            // masuk ke systhirdlog
                            sysThirdLogsService.addSysThirdLogs(order.getUuid(), order.getUserUuid(), SysThirdLogsEnum.FDC_INQUIRY.getCode(), null, requestStr, responseStr.substring(0, 2000));
                        }

                        // save mongo yg sukses doang atau semua? di-remark aja, di-save di log function compute
                        //saveFDCResponseMongo(order.getUuid(), order.getUserUuid(), user.getIdCardNo(), reason, requestStr, responseStr);

                    } else {

                        //jika status selain 2xx, skip fdc, masuk systhirdlogs
                        log.error("FDC Inquiry failed " + response);
                        // masuk ke systhirdlog
                        sysThirdLogsService.addSysThirdLogs(order.getUuid(), order.getUserUuid(), SysThirdLogsEnum.FDC_INQUIRY.getCode(), null, requestStr, responseStr.substring(0, 2000));
                    }
                } catch (Exception e) { 

                    //jika ketemu exception masuk ke riskerrorlog
                    log.error("Error getting response from FDC", e);
                    // masuk ke riskerrorlog & systhirdlogs
                    riskErrorLogService.addRiskError(order.getUuid(), RiskErrorLog.RiskErrorTypeEnum.SYSTEM_ERROR, e.getMessage());
                }

                // end of FDC check

                if (order.getBorrowingCount() >= 2 && reject == false) {// 复借用户 budi: reject kalau ketemu status selain 'L' di FDC

                    this.multiReviewPass(order, user);
                    //新规则使用：
                    // RuleSetExecutedResult ruleSetResult = applicationService
                    //     .applyForReBorrowing(order, codeEntityMap);
                    // if (ruleSetResult.isRuleSetResult()) {
                    //     this.multiReviewPass(order, user);
                    // } else {
                    //     this.reviewRefuse(order, ruleSetResult.getFirstRejectRule());
                    // }

                } else if (order.getBorrowingCount() < 2 && reject == false) {// 初借用户

                    //reject all
                    SysAutoReviewRule rejectAll = codeEntityMap.get(BlackListTypeEnum.REJECT_ALL.getMessage());

                    boolean isKudoChannel = user.getUserSource() != null && Arrays.asList("81", "82", "83", "84").contains(user.getUserSource().toString());

                    //janhsen: remove isKudoChannel because need open for all
                    if (rejectAll != null && rejectAll.getRuleResult() == 2 ){ //&& !isKudoChannel) {
                        log.info("reject all first borrowing .");
                        this.reviewRefuse(order, rejectAll);
                    } else {
                        //新规则使用：
                        RuleSetExecutedResult ruleSetResult = applicationService
                                .apply(order, codeEntityMap);
                        if (!ruleSetResult.isPreExecuteResult()) {
                            //预处理失败
                            LogUtils.removeMDCRequestId();
                            continue;
                        }
                        if (ruleSetResult.isRuleSetResult()) {
                            this.reviewPass(order);
                        } else {
                            this.reviewRefuse(order, ruleSetResult.getFirstRejectRule());
                        }
                    }

                } else { // reject by fdc, reject = true

                    log.info("reject all by fdc.");
                    SysAutoReviewRule rejectAll = codeEntityMap.get(BlackListTypeEnum.REJECT_ALL.getMessage());
                    this.reviewRefuse(order, rejectAll);
                }
                //评分
                //this.caculateScoreHandler.doHandler(user, order, codeEntityMap);
            } catch (Exception e) {
                log.error("审核异常", e);
                try {
                    exceptionList(order, codeEntityMap, e.getMessage());
                } catch (Exception exp) {
                    log.error(exp.getMessage());
                }
                // 异常情况发送短信
                sendSmsCodeFun();
                //记录异常表
                riskErrorLogService.addRiskError(order.getUuid(), RiskErrorLog.RiskErrorTypeEnum.SYSTEM_ERROR, e.getMessage());
            }
            log.error(order.getUuid() + "订单审核=========结束");
            long endTime = System.currentTimeMillis();
            log.info("订单号:{}，审核时间为:{}s", order.getUuid(), (endTime - startTime) / 1000.0);
            LogUtils.removeMDCRequestId();
        }
        log.info("=============本次审核结束=======================");
    }



    public void sendSmsCodeFun() {
        String mobiles = "17610156636";
        String smsContont = "doit机审报异常";
        if (StringUtils.isEmpty(redisClient.get(SysParamContants.SMS_RISK_EXCEPTION))) {
            SmsCodeMandaoUtil.sendSmsCode(mobiles, smsContont);
            redisClient.set(SysParamContants.SMS_RISK_EXCEPTION, mobiles, 60 * 60);
        }
    }




    /**
     *   审核异常
     */
    public void exceptionList(OrdOrder order, Map<String, SysAutoReviewRule> codeEntityMap,
        String message) throws Exception {

        SysAutoReviewRule dataEmptyRule = codeEntityMap
            .get(BlackListTypeEnum.EXCEPTION_LIST.getMessage());
        Integer ruleStatus = dataEmptyRule.getRuleStatus();
        if (ruleStatus == 1) {

            this.ordBlackService.addBackList(order.getUuid(), message,
                dataEmptyRule.getRuleType().toString() + "-" + dataEmptyRule.getRuleDetailType(),
                "", order.getUserUuid(), "", 0);

        } else if (ruleStatus == 3) {


            this.ordBlackService.addBackListTemp(order.getUuid(), message,
                dataEmptyRule.getRuleType().toString() + "-" + dataEmptyRule.getRuleDetailType(),
                "", order.getUserUuid(), "");
        }
    }


    /**
     *  审核拒绝
     */
    public void reviewRefuse(OrdOrder ordOrder, SysAutoReviewRule sysAutoReviewRule) {

        // 如果是cashcash的订单 反馈更新订单状态 和审批状态
        if (ordOrder.getThirdType() == 1){
            this.cash2OrderService.ordStatusFeedback(ordOrder, Cash2OrdStatusEnum.NOT_PASS_CHECK);
            this.cash2OrderService.ordCheckResultFeedback(ordOrder, Cash2OrdCheckResultEnum.CHECK_NOT_PASS);
        }else if (ordOrder.getThirdType() == 2) {
            // 猎豹金融 cheetah
            this.cheetahOrderService.ordStatusFeedback(ordOrder, CheetahOrdStatusEnum.NOT_PASS_CHECK);
        }

        OrdOrder entity = new OrdOrder();
        entity.setUuid(ordOrder.getUuid());
        entity.setStatus(OrdStateEnum.MACHINE_CHECK_NOT_ALLOW.getCode());
        entity.setUpdateTime(new Date());
        this.ordService.updateOrder(entity);

        ordOrder.setStatus(OrdStateEnum.MACHINE_CHECK_NOT_ALLOW.getCode());
        ordOrder.setUpdateTime(new Date());
        this.ordService.addOrderHistory(ordOrder);
        log.info("订单号：" + ordOrder.getUuid() +"被拒绝，拒绝天数" + (sysAutoReviewRule != null ? sysAutoReviewRule.getRuleRejectDay() : ""));

        // ????
        SaveOrderUserUuidRequest saveMongo = new SaveOrderUserUuidRequest();
        saveMongo.setOrderNo(ordOrder.getUuid());
        saveMongo.setUserUuid(ordOrder.getUserUuid());
        this.redisClient.listAdd(RedisContants.SAVE_MANGO_ORDER_LIST, saveMongo);
    }



    /**
     *  Initial loan: approved
     */
    public void reviewPass(OrdOrder ordOrder) {

        log.info("risk reviewPass" + ordOrder.getUuid());
        //This will change Order status into OrdStateEnum.WAIT_CALLING
        reviewResultService.afterFistBorrowPass(ordOrder);

    }


    /**
     * Re-borrow: approved
     * @param ordOrder
     */
    public void multiReviewPass(OrdOrder ordOrder, UsrUser user) {
        log.info("risk multiReviewPass" + ordOrder.getUuid());
        //This will change Order status into OrdStateEnum.WAIT_CALLING
        reviewResultService.afterReBorrowingPass(ordOrder);
    }

    /** 
     * budi: save FDC Response to Mongo
    */
    public void saveFDCResponseMongo(String orderNo, String userUuid, String idCardNo, Integer reason, String requestData, String responseData) {
        FDCResponseMongo mongoEntity = new FDCResponseMongo();
        mongoEntity.setCreateTime(new Date());
        mongoEntity.setUpdateTime(new Date());
        mongoEntity.setOrderNo(orderNo);
        mongoEntity.setUserUuid(userUuid);
        mongoEntity.setIdCardNo(idCardNo);
        mongoEntity.setReason(reason);
        //mongoEntity.setReffId(reffId);
        mongoEntity.setRequestData(requestData);
        mongoEntity.setResponseData(responseData);
        mongoEntity.setDisabled(0);
        /*try {
            fdcResponseMongoDal.insert(mongoEntity);
        } catch (MongoException ex) {
            // Exception never thrown
            log.info("mongo exception");
        } catch (Exception ex) {
            // Handle exception
            log.info("exception");
        }*/
        fdcResponseMongoDal.insert(mongoEntity);
    }

    /*public static void main(String[] args) {

        OkHttpClient httpClient = new OkHttpClient();
        
        //try {    
            String jointUrl = new StringBuilder()
                .append("http://149.129.234.48/api/v1/Inquiry")
                .append("?id=").append("3207025501730003")
                .append("&reason=").append(1)
                .append("&reffid=").append("0120001")
                .toString();
                
            final String fdc_credential = Credentials.basic("it.dept@do-it.id", "820053");
            Request request = new Request.Builder()
                .url(jointUrl)
                .method("GET", null)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", fdc_credential)
                .build();

            String requestStr = "dummy request";
            String responseStr = "{\"noIdentitas\":\"3207025501730003\",\"userId\":\"it.dept@do-it.id\",\"userName\":\"Do-it\",\"inquiryReason\":\"1 - Applying loan via Platform\",\"memberId\":\"820053\",\"memberName\":\"DO-IT\",\"refferenceId\":\"123\",\"inquiryDate\":\"2020-05-13\",\"status\":\"Found\",\"pinjaman\":[{\"id_penyelenggara\":\"3\",\"jenis_pengguna\":1,\"nama_borrower\":\"Anih Dinarsih\",\"no_identitas\":\"3207025501730003\",\"no_npwp\":\"\",\"tgl_perjanjian_borrower\":\"2016-11-07\",\"tgl_penyaluran_dana\":\"2016-11-08\",\"nilai_pendanaan\":25000000.0,\"tgl_pelaporan_data\":\"2019-11-01\",\"sisa_pinjaman_berjalan\":15000000.0,\"tgl_jatuh_tempo_pinjaman\":\"2020-11-07\",\"kualitas_pinjaman\":\"1\",\"dpd_terakhir\":3,\"dpd_max\":23,\"status_pinjaman\":\"L\",\"jenis_pengguna_ket\":\"Individual\",\"kualitas_pinjaman_ket\":\"Lancar (<30 hari)\",\"status_pinjaman_ket\":\"Fully Paid\"},{\"id_penyelenggara\":\"4\",\"jenis_pengguna\":1,\"nama_borrower\":\"Anih Dinarsih\",\"no_identitas\":\"3207025501730003\",\"no_npwp\":\"\",\"tgl_perjanjian_borrower\":\"2017-10-17\",\"tgl_penyaluran_dana\":\"2017-10-18\",\"nilai_pendanaan\":50000000.0,\"tgl_pelaporan_data\":\"2019-11-01\",\"sisa_pinjaman_berjalan\":10000000.0,\"tgl_jatuh_tempo_pinjaman\":\"2020-10-17\",\"kualitas_pinjaman\":\"2\",\"dpd_terakhir\":30,\"dpd_max\":45,\"status_pinjaman\":\"O\",\"jenis_pengguna_ket\":\"Individual\",\"kualitas_pinjaman_ket\":\"Tidak Lancar (30 sd 90 hari)\",\"status_pinjaman_ket\":\"Outstanding\"},{\"id_penyelenggara\":\"3\",\"jenis_pengguna\":1,\"nama_borrower\":\"Anih Dinarsih\",\"no_identitas\":\"3207025501730003\",\"no_npwp\":\"\",\"tgl_perjanjian_borrower\":\"2018-11-23\",\"tgl_penyaluran_dana\":\"2018-11-24\",\"nilai_pendanaan\":45000000.0,\"tgl_pelaporan_data\":\"2019-11-01\",\"sisa_pinjaman_berjalan\":15000000.0,\"tgl_jatuh_tempo_pinjaman\":\"2020-11-23\",\"kualitas_pinjaman\":\"1\",\"dpd_terakhir\":1,\"dpd_max\":3,\"status_pinjaman\":\"O\",\"jenis_pengguna_ket\":\"Individual\",\"kualitas_pinjaman_ket\":\"Lancar (<30 hari)\",\"status_pinjaman_ket\":\"Outstanding\"},{\"id_penyelenggara\":\"3\",\"jenis_pengguna\":1,\"nama_borrower\":\"Anih Dinarsih\",\"no_identitas\":\"3207025501730003\",\"no_npwp\":\"\",\"tgl_perjanjian_borrower\":\"2019-01-07\",\"tgl_penyaluran_dana\":\"2019-01-08\",\"nilai_pendanaan\":450000.0,\"tgl_pelaporan_data\":\"2019-11-01\",\"sisa_pinjaman_berjalan\":50000.0,\"tgl_jatuh_tempo_pinjaman\":\"2020-01-07\",\"kualitas_pinjaman\":\"3\",\"dpd_terakhir\":90,\"dpd_max\":120,\"status_pinjaman\":\"W\",\"jenis_pengguna_ket\":\"Individual\",\"kualitas_pinjaman_ket\":\"Macet (>90)\",\"status_pinjaman_ket\":\"Write-Off\"},{\"id_penyelenggara\":\"2\",\"jenis_pengguna\":1,\"nama_borrower\":\"Franciscus A Pribadi\",\"no_identitas\":\"3207025501730003\",\"no_npwp\":\"\",\"tgl_perjanjian_borrower\":\"2019-02-10\",\"tgl_penyaluran_dana\":\"2019-02-10\",\"nilai_pendanaan\":500000.0,\"tgl_pelaporan_data\":\"2019-10-30\",\"sisa_pinjaman_berjalan\":0.0,\"tgl_jatuh_tempo_pinjaman\":\"2019-02-24\",\"kualitas_pinjaman\":\"3\",\"dpd_terakhir\":229,\"dpd_max\":229,\"status_pinjaman\":\"O\",\"jenis_pengguna_ket\":\"Individual\",\"kualitas_pinjaman_ket\":\"Macet (>90)\",\"status_pinjaman_ket\":\"Outstanding\"},{\"id_penyelenggara\":\"3\",\"jenis_pengguna\":1,\"nama_borrower\":\"Anih Dinarsih\",\"no_identitas\":\"3207025501730003\",\"no_npwp\":\"\",\"tgl_perjanjian_borrower\":\"2019-05-08\",\"tgl_penyaluran_dana\":\"2019-05-08\",\"nilai_pendanaan\":11000000.0,\"tgl_pelaporan_data\":\"2019-11-01\",\"sisa_pinjaman_berjalan\":8000000.0,\"tgl_jatuh_tempo_pinjaman\":\"2020-05-08\",\"kualitas_pinjaman\":\"1\",\"dpd_terakhir\":0,\"dpd_max\":0,\"status_pinjaman\":\"O\",\"jenis_pengguna_ket\":\"Individual\",\"kualitas_pinjaman_ket\":\"Lancar (<30 hari)\",\"status_pinjaman_ket\":\"Outstanding\"},{\"id_penyelenggara\":\"2\",\"jenis_pengguna\":1,\"nama_borrower\":\"Anih Dinarsih\",\"no_identitas\":\"3207025501730003\",\"no_npwp\":\"\",\"tgl_perjanjian_borrower\":\"2018-09-06\",\"tgl_penyaluran_dana\":\"2020-02-09\",\"nilai_pendanaan\":28000.0,\"tgl_pelaporan_data\":\"2020-03-11\",\"sisa_pinjaman_berjalan\":28000.0,\"tgl_jatuh_tempo_pinjaman\":\"2020-02-29\",\"kualitas_pinjaman\":\"1\",\"dpd_terakhir\":11,\"dpd_max\":25,\"status_pinjaman\":\"O\",\"jenis_pengguna_ket\":\"Individual\",\"kualitas_pinjaman_ket\":\"Lancar (<30 hari)\",\"status_pinjaman_ket\":\"Outstanding\"},{\"id_penyelenggara\":\"3\",\"jenis_pengguna\":1,\"nama_borrower\":\"Franciscus A Pribadi\",\"no_identitas\":\"3207025501730003\",\"no_npwp\":\"\",\"tgl_perjanjian_borrower\":\"2018-09-06\",\"tgl_penyaluran_dana\":\"2020-02-09\",\"nilai_pendanaan\":29000.0,\"tgl_pelaporan_data\":\"2020-03-11\",\"sisa_pinjaman_berjalan\":28000.0,\"tgl_jatuh_tempo_pinjaman\":\"2020-02-29\",\"kualitas_pinjaman\":\"1\",\"dpd_terakhir\":11,\"dpd_max\":25,\"status_pinjaman\":\"O\",\"jenis_pengguna_ket\":\"Individual\",\"kualitas_pinjaman_ket\":\"Lancar (<30 hari)\",\"status_pinjaman_ket\":\"Outstanding\"}]}";
            //String responseStr = "{\"noIdentitas\":\"1208010203925555\",\"userId\":\"it.dept@do-it.id\",\"userName\":\"Do-it\",\"inquiryReason\":\"1 - Applying loan via Platform\",\"memberId\":\"820053\",\"memberName\":\"DO-IT\",\"refferenceId\":\"123\",\"inquiryDate\":\"2020-05-13\",\"status\":\"Not Found\",\"pinjaman\":[]}";
            //Response response = httpClient.newCall(request).execute();
            
            //String requestStr = request.toString();
            //String responseStr = response.body().string();

            //if(response.isSuccessful()) {

                log.info("request: {}", requestStr);
                log.info("response: {}", responseStr);

                FDCResponse fdcResponse = new FDCResponse();
                DataDetail fdcDataDetailResponse = new DataDetail();
                boolean reject = false;

                fdcResponse = JsonUtils.deserialize(responseStr, FDCResponse.class); // data penyelenggara
                fdcDataDetailResponse = JsonUtils.deserialize(responseStr, DataDetail.class); // data nasabah

            // check if status found or not found in fdc
                if (fdcResponse.getStatus().compareTo("Found") == 0) {

                    //log.info("fdcResponse: {}", fdcResponse);
                    //log.info("fdcDataDetailResponse: {}", fdcDataDetailResponse.getPinjaman());
                    for (PinjamanDetail pinjaman : fdcDataDetailResponse.getPinjaman()) {
                        
                        if (pinjaman.getStatus_pinjaman().compareTo("W") == 0) {

                            log.info("Nasabah reject karena ada pinjaman berstatus write-off");
                            reject = true;
                            break; // keluar risk dan masuk usrblack
                        }
                        //System.out.println(pinjaman.getStatus_pinjaman());
                    }

                    if (reject == false) {

                        // masuk risk
                        log.info("Pinjaman diterima, tidak ada yang write-off");
                    }

                } else if (fdcResponse.getStatus().compareTo("Not Found") == 0) {

                    // masuk risk
                    log.info("Pinjaman Not Found di platform manapun");
                } else {

                    // jika status selain found & not found
                    log.info("Status: {}", fdcResponse.getStatus());
                }
            //}

            // save mongo
            //saveFDCResponseMongo("0120001", "2FA001", "3207025501730003", 1, "0120001", requestStr, responseStr);
            //saveFDCResponseMongo("3207025501730003");

            FDCResponseMongo scan = new FDCResponseMongo();
            scan.setIdCardNo("1208010203925555");
            List<FDCResponseMongo> fdcResponseMongoList = fdcResponseMongoDal.find(scan);
            log.info("{}", fdcResponseMongoList);

        //} catch (Exception e) { 

        //    log.error("Error getting response from FDC", e);
        //}
    }*/

}
