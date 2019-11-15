package com.yqg.service;

import com.yqg.common.constants.RedisContants;
import com.yqg.common.constants.SysParamContants;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.common.utils.AesUtil;
import com.yqg.service.order.OrdService;
import com.yqg.service.order.request.SaveOrderUserUuidRequest;
import com.yqg.service.system.service.SysParamService;
import com.yqg.system.dao.SysParamDao;
import com.yqg.system.dao.SysProductDao;
import com.yqg.system.dao.SysSmsCodeDao;
import com.yqg.system.entity.SysParam;
import com.yqg.system.entity.SysSmsCode;
import com.yqg.user.dao.UsrBlackListDao;
import com.yqg.user.dao.UsrDao;
import com.yqg.user.entity.UsrBlackList;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by Didit Dwianto on 2018/1/4.
 */
@Service
@Slf4j
public class RepairOrderDataService {

    @Autowired
    private OrdDao ordDao;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private OrdService ordService;

    @Value("${jxl.sendReportTokenUrl}")
    private String sendTokenUrl;

    @Autowired
    private SysProductDao sysProductDao;
    @Autowired
    private OrdDao orderDao;

    @Autowired
    private SysParamService sysParamService;
    @Autowired
    private SysParamDao sysParamDao;
    @Autowired
    private UsrDao usrDao;
    @Autowired
    private UsrBlackListDao usrBlackListDao;
    @Autowired
    private SysSmsCodeDao sysSmsCodeDao;

    public void repairData(){

        List<OrdOrder> orderList = ordDao.getUserOrderDataList();

        log.info("?????????"+orderList.size());
        for (OrdOrder order : orderList){
            log.info("????????"+order.getUuid());
            // ????
            try {
                SaveOrderUserUuidRequest saveMongo = new SaveOrderUserUuidRequest();
                saveMongo.setOrderNo(order.getUuid());
                saveMongo.setUserUuid(order.getUserUuid());
                this.redisClient.listAdd(RedisContants.SAVE_MANGO_ORDER_LIST,saveMongo);
            }catch (Exception e){
                log.info("????,orderNo:"+order.getUuid());
                e.getStackTrace();
            }
            log.info("??????mongo?"+order.getUuid());
        }
    }

    public void repairData2(){

        OrdOrder scan = new OrdOrder();
        scan.setUuid("011801231424172670");
        List<OrdOrder> orderList = ordDao.scan(scan);
        log.info("?????????"+orderList.size());
        for (OrdOrder order : orderList){
            log.info("????????"+order.getUuid());
            // ????
            try {
                SaveOrderUserUuidRequest saveMongo = new SaveOrderUserUuidRequest();
                saveMongo.setOrderNo(order.getUuid());
                saveMongo.setUserUuid(order.getUserUuid());
                this.redisClient.listAdd(RedisContants.SAVE_MANGO_ORDER_LIST,saveMongo);
            }catch (Exception e){
                log.info("????,orderNo:"+order.getUuid());
                e.getStackTrace();
            }
            log.info("??????mongo?"+order.getUuid());
        }
    }

    public  void  loanOrderWithSixthNumber(){

       List<OrdOrder> orderList = this.ordDao.getLoanOrderWithSixthyNumber();
       log.info("???????????"+orderList.size());

       for (OrdOrder order : orderList){
           log.info("??????????"+order.getUuid());

           order.setStatus(OrdStateEnum.SECOND_CHECK.getCode());
           this.ordService.addOrderHistory(order);

           order.setStatus(OrdStateEnum.LOANING.getCode());
           this.ordService.addOrderHistory(order);
       }
    }

    public void repairData3(){


        String orderStr = "011712300742518390,011801081606517930,011801131225176140,011801131308511640,011801181331136860,011801182313513900,011801201457253100,011801210947265560,011801230641014290,011801251429104040,011801261605575670,011801261708463020,011801261959354540,011801262116078630,011801270019246490,011801270022437210,011801270032192080,011801270723238160,011801270743364070,011801270752181310,011801270756533870,011801270831020490,011801270834457480,011801270840516500,011801270922330370,011801270925557040,011801270939570660,011801270958285070,011801271004476000,011801271017251960,011801271024212600,011801271033285120,011801271053293030,011801271054432140,011801271118575890,011801271150493850,011801271200445540,011801271205067200,011801271220482060,011801271250451360,011801271317598340,011801271446224370,011801271514191230,011801271515270910,011801271535429020,011801271556118890,011801271601277750,011801271640013090,011801271646400370,011801271703333710,011801271716147130,011801271719065560,011801271744598350,011801271753321860,011801271824042620,011801271850264010,011801271941454750,011801271956200390,011801272010347170";

        String[]  orderArr = orderStr.split(",");
        log.info("?????????"+orderArr.length);
        for (String orderNo : orderArr){
            log.info("????????"+ orderNo);

            OrdOrder order = new OrdOrder();
            order.setUuid(orderNo);
            List<OrdOrder> scanList = this.ordDao.scan(order);
            if (!CollectionUtils.isEmpty(scanList)){
                // ????
                try {
                    SaveOrderUserUuidRequest saveMongo = new SaveOrderUserUuidRequest();
                    saveMongo.setOrderNo(orderNo);
                    saveMongo.setUserUuid(scanList.get(0).getUserUuid());
                    this.redisClient.listAdd(RedisContants.SAVE_MANGO_ORDER_LIST,saveMongo);
                }catch (Exception e){
                    log.info("????,orderNo:"+orderNo);
                    e.getStackTrace();
                }
            }
            log.info("??????mongo?"+orderNo);
        }
    }

    public  void  repairData4(){

        List<OrdOrder> orderList = this.ordDao.getUserOrderDataList2();
        log.info("???????????"+orderList.size());

        for (OrdOrder order : orderList){

            log.info("????????"+order.getUuid());
            // ????
            try {
                SaveOrderUserUuidRequest saveMongo = new SaveOrderUserUuidRequest();
                saveMongo.setOrderNo(order.getUuid());
                saveMongo.setUserUuid(order.getUserUuid());
                this.redisClient.listAdd(RedisContants.SAVE_MANGO_ORDER_LIST,saveMongo);
            }catch (Exception e){
                log.info("????,orderNo:"+order.getUuid());
                e.getStackTrace();
            }
            log.info("??????mongo?"+order.getUuid());
        }
    }


//    //  ??redis  ??facebook???? ??
//    public  void  repairData5(){
//
//
//        List<UserCertificationInfoInRedis> list=  redisClient.getList(RedisContants.USER_CERTIFICATION_LIST,UserCertificationInfoInRedis.class);
//
//        log.info("?????????"+list.size());
//        for (UserCertificationInfoInRedis certRedisEntity : list){
//
//            if (certRedisEntity != null){
//
//                // ?????
//                String certType = certRedisEntity.getCertType();
//                String reportToken = certRedisEntity.getReport_task_token();
////                "report_task_token": "bfdc45782dc54b328854df3d7dcfbda4",
//                if (certType.equals("facebook")) {
//                    sendReportToken(reportToken);
//                }
//            }
//
//        }
//        log.info("??facebook????");
//    }
//
//    /**
//     *  ??facebook??? ???????????
//     * */
//    public void sendReportToken(String token) {
//
//        log.info("???token?"+token);
//        try {
//
//            OkHttpClient httpClient = new OkHttpClient.Builder()
//                    .connectTimeout(30, TimeUnit.SECONDS)
//                    .readTimeout(10,TimeUnit.SECONDS)
//                    .build();
//
//            Request request = new Request.Builder()
//                    .url(sendTokenUrl+token)
//                    .build();
//
//            Response response = httpClient.newCall(request).execute();
//            log.info("???response?"+response);
//
//            if(response.isSuccessful())
//            {
//                String  responseStr = response.body().string();
//                log.info("???????:{}", JsonUtils.serialize(responseStr));
//            }else {
//                log.info("????");
//            }
//
//        }catch (Exception e){
//            log.error("??facebook????");
//            e.getStackTrace();
//        }
//    }


//    //  修复降额后的订单没有产品id的问题
//    public  void  repairData6(){
//
//        List<OrdOrder> orderList = this.ordDao.getOrderWithNoProductUuid();
//        log.info("待处理的订单总数为："+orderList.size());
//
//        for (OrdOrder order : orderList){
//            log.info("订单编号："+order.getUuid());
//
//            //更新产品编号
//            OrdOrder updateEntity = new OrdOrder();
//            SysProduct sysProduct = sysProductDao.getProductByAmountAndTermIgnoreDisabled(new BigDecimal("200000.00"),order.getBorrowingTerm());
//            updateEntity.setUuid(order.getUuid());
//            updateEntity.setUpdateTime(new Date());
//            updateEntity.setProductUuid(sysProduct.getProductCode());
//            orderDao.update(updateEntity);
//        }
//
//        log.info("修复降额后的订单没有产品id  处理完成");
//    }
//

    // 每天凌晨更新p2p推单数量
    public void updateP2pOrderCount(){

        SysParam entity = new SysParam();
        entity.setDisabled(0);
        entity.setSysKey(SysParamContants.LOAN_P2P_COUNT);
        List<SysParam> sysParams = this.sysParamDao.scan(entity);
        if (!CollectionUtils.isEmpty(sysParams)) {
            String result = sysParams.get(0).getSysValue();
            this.sysParamService.setSysParamValue(SysParamContants.LOAN_P2P_COUNT,result);
        }
    }

    // 每天凌晨更新 每日放款金额
    public void updateDayLoanAmount(){

        this.sysParamService.setSysParamValue(SysParamContants.LOAN_ACCOUNT_NOW,"0");

    }

//    //  修复redis迁移后  部分key没有过期时间的问题
//    public  void  repairData6(){
//        Set<String> keys = redisClient.keys("repeatLock");
//        log.info("对应的keys共有："+keys.size());
//        for(String key : keys){
//
//             long expireTime = redisClient.getExpireTime(key);
//             if (expireTime == -1){
//                 log.info("没有超时时间的key为"+key);
//                 // 未超时的下单锁
//                 String s =  redisClient.get(key);
//                 log.info(s);
//
//                 redisClient.del(key);
//                 String s2 =  redisClient.get(key);
//                 log.info(s2);
//             }
//        }
//    }

    //  清洗数据库中的手机号码加密   DES-》AES
    public  void  repairData7(){

            List<UsrUser> userList = this.usrDao.getAllUser();
            if (!CollectionUtils.isEmpty(userList)) {
                log.info("需要处理的用户数量有" + userList.size());
                for (UsrUser user : userList) {
                    try {
                        log.info("用户的id为：" + user.getUuid());
                        String mobileDes = user.getMobileNumberDES();
                        log.info("用户手机号DES加密：" + mobileDes);
                        String mobile = DESUtils.decrypt(mobileDes);
                        if (!StringUtils.isEmpty(mobile)) {
                            log.info("用户手机号为：" + mobile);
                            String mobileAes = AesUtil.encryptMobile(mobile);
                            log.info("用户手机号AES加密为：" + mobileAes);
                            if (!StringUtils.isEmpty(mobileAes)) {
                                UsrUser update = new UsrUser();
                                update.setUuid(user.getUuid());
                                update.setMobileNumberDES(mobileAes);
                                this.usrDao.update(update);
                            }

                        }
                    } catch (Exception e) {
                        log.error("清洗数据库中的手机号码加密 异常", e);
                        log.error("异常的用户uuid为：" + user.getUuid());
                    }
                }
            }
    }

    //  清洗数据库中的手机号码加密  usrUser  mobileNumberDES  DES-》AES
    public  void  repairData8(){

        List<UsrBlackList> usrBlackLists = this.usrBlackListDao.getAllBlackList();
        if (!CollectionUtils.isEmpty(usrBlackLists)) {
            log.info("需要处理的用户黑名单数量有" + usrBlackLists.size());
            for (UsrBlackList usrBlackList : usrBlackLists) {
                try {
                    log.info("用户的黑名单id为：" + usrBlackList.getUuid());
                    String mobileDes = usrBlackList.getMobileDes();
                    log.info("用户手机号DES加密：" + mobileDes);
                    String mobile = DESUtils.decrypt(mobileDes);
                    if (!StringUtils.isEmpty(mobile)) {
                        log.info("用户手机号为：" + mobile);
                        String mobileAes = AesUtil.encryptMobile(mobile);
                        log.info("用户手机号AES加密为：" + mobileAes);
                        if (!StringUtils.isEmpty(mobileAes)) {
                            UsrBlackList update = new UsrBlackList();
                            update.setUuid(usrBlackList.getUuid());
                            update.setMobileDes(mobileAes);
                            this.usrBlackListDao.update(update);
                        }

                    }
                } catch (Exception e) {
                    log.error("清洗数据库中的用户黑名单号码加密 异常", e);
                    log.error("异常的用户黑名单uuid为：" + usrBlackList.getUuid());
                }
            }
        }
    }
//
//    //  清洗数据库中的手机号码加密 SysSmsCode mobile  DES-》AES
//    public  void  repairData9(){
//
//        List<SysSmsCode> smsCodeList = this.sysSmsCodeDao.getAllSysSmsCode();
//        if (!CollectionUtils.isEmpty(smsCodeList)) {
//            log.info("需要处理的验证码数量有" + smsCodeList.size());
//            for (SysSmsCode code : smsCodeList) {
//                try {
//                    log.info("验证码的id为：" + code.getUuid());
//                    String mobileDes = code.getMobile();
//                    log.info("验证码手机号DES加密：" + mobileDes);
//                    String mobile = DESUtils.decrypt(mobileDes);
//                    if (!StringUtils.isEmpty(mobile)) {
//                        log.info("验证码手机号为：" + mobile);
//                        String mobileAes = AesUtil.encryptMobile(mobile);
//                        log.info("验证码手机号AES加密为：" + mobileAes);
//                        if (!StringUtils.isEmpty(mobileAes)) {
//                            SysSmsCode update = new SysSmsCode();
//                            update.setUuid(code.getUuid());
//                            update.setMobile(mobileAes);
//                            this.sysSmsCodeDao.update(update);
//                        }
//
//                    }
//                } catch (Exception e) {
//                    log.error("清洗数据库中的验证码手机号码加密 异常", e);
//                    log.error("异常的验证码uuid为：" + code.getUuid());
//                }
//            }
//        }
//    }
}
