package com.yqg.service.externalChannel.service;

import com.yqg.common.enums.system.SysThirdLogsEnum;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.externalChannel.dao.ExternalOrderRelationDao;
import com.yqg.externalChannel.entity.ExternalOrderRelation;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.externalChannel.config.CheetahConfig;
import com.yqg.service.externalChannel.request.Cash2ManualRequest;
import com.yqg.service.externalChannel.request.CheetahBaseRequest;
import com.yqg.service.externalChannel.request.CheetahGetOrdStatusRequest;
import com.yqg.service.externalChannel.request.CheetahOrdFeedRequest;
import com.yqg.service.externalChannel.response.Cash2OrdStatusFeedbackResponse;
import com.yqg.service.externalChannel.response.CheetahResponse;
import com.yqg.service.externalChannel.utils.*;
import com.yqg.service.order.OrdService;
import com.yqg.service.pay.RepayService;
import com.yqg.service.pay.request.RepayRequest;
import com.yqg.service.pay.response.RepayResponse;
import com.yqg.service.system.service.SysThirdLogsService;
import com.yqg.service.user.service.UsrService;
import com.yqg.system.dao.SysPaymentChannelDao;
import com.yqg.system.dao.SysProductDao;
import com.yqg.system.entity.SysPaymentChannel;
import com.yqg.system.entity.SysProduct;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by wanghuaizhou on 2019/1/8.
 */
@Service
@Slf4j
public class CheetahOrderService {

    // 订单状态反馈 url
    @Value("${third.cheetah.orderStatusFeedback}")
    private String STATUS_FEEDBACK_URL;

    @Autowired
    private ExternalOrderRelationDao externalOrderRelationDao;

    @Autowired
    private ExternalChannelDataService externalChannelDataService;
    @Autowired
    private OrdService ordService;
    @Autowired
    private CheetahConfig cheetahConfig;
    @Autowired
    private RepayService repayService;
    @Autowired
    private SysProductDao sysProductDao;
    @Autowired
    private SysPaymentChannelDao sysPaymentChannelDao;
    @Autowired
    private UsrService usrService;
    @Autowired
    private SysThirdLogsService sysThirdLogsService;
    @Autowired
    private OkHttpClient httpClient;
    // 提交还款申请url
    @Value("${pay.commitRepayUrl}")
    private String COMMIT_REPAY_URL;
    @Value("${pay.token}")
    private String PAY_TOKEN;
    /***
     * 订单状态反馈
     * @param order
     * @return
     */
    public void ordStatusFeedback(OrdOrder order, CheetahOrdStatusEnum ordStatusEnum) {

        ExternalOrderRelation relation = this.externalOrderRelationDao
                .selectByOrderNo(order.getUuid());
        if (relation == null || StringUtils.isEmpty(relation.getOrderNo())) {
            log.error("订单不存在");
        }

        OrdOrder ordOrder = ordService.getOrderByOrderNo(relation.getOrderNo());
        if (ordOrder == null) {
            log.error("未查询到Do-It订单");
        }
        CheetahOrdFeedRequest request = new CheetahOrdFeedRequest();
        try {

//            Map<String, Object> data = new HashMap<>();
//            data.put("orderId", ordOrder.getUuid());
//            data.put("productId", Integer.valueOf(ordOrder.getProductUuid()));
//            data.put("installments", 1);
//            data.put("orderStatus", getOrdStatusFromDoitToCheetah(ordOrder)); // 订单状态
//            data.put("auditStatus", getAuditStatus(ordOrder));  // 审核状态
//            data.put("updateTime", ordOrder.getUpdateTime()); // 时间戳，单位毫秒；反馈订单流经至对应状态的时间

            request.setOrderId(ordOrder.getUuid());
            request.setProductId(Integer.valueOf(ordOrder.getProductUuid()));
            request.setInstallments(1);
            request.setOrderStatus(getOrdStatusFromDoitToCheetah(ordOrder));
            request.setAuditStatus(getAuditStatus(ordOrder));
            request.setUpdateTime(ordOrder.getUpdateTime().getTime());

//            List<Map<String,Object>> billList = new ArrayList<>();
            Map<String,Object> bills = new HashMap<>();
            CheetahOrdFeedRequest.BillBean billBean = new CheetahOrdFeedRequest.BillBean();
            bills.put("installmentNumber",1);
            billBean.setInstallmentNumber(1);

            switch (ordOrder.getStatus()) {
                case 1:// 待提交 	(待补充资料)
                    break;
                case 2:// 待机审    （审批中）
                case 3:// 待初审    （审批中）
                case 4:// 待复审    （审批中）
                case 17:// 待电核   （审批中）
                case 18:// 待电核   （审批中）
                case 19:// 降额后等待用户确认  （审批中）
                    break;
                case 5:// 待放款    （审批通过）
                case 6:// 放款处理中 （审批通过）
                    break;
                case 7:// 待还款-未逾期7（放款成功）
//                    data.put("currentInstallmen", 1); //当前期数 0 表示已经还款完成，空表示为未生成账单
//                    data.put("disburseTime", ordOrder.getLendingTime()); //放款时间戳，单位毫秒

                    request.setCurrentInstallment(1);
                    request.setDisburseTime(ordOrder.getLendingTime().getTime());

//                    bills.put("status",1); //1未到期；2已还款；3逾期
//                    bills.put("amount",new BigDecimal(repayService.calculateRepayAmount(ordOrder,"1")).setScale(0, BigDecimal.ROUND_DOWN)); //本期账单应还款金额
//                    bills.put("interestAmount",ordOrder.getInterest().setScale(0, BigDecimal.ROUND_DOWN)); //应还利息
//                    bills.put("adminAmount",ordOrder.getServiceFee().setScale(0, BigDecimal.ROUND_DOWN)); //管理费
//                    bills.put("repaidAmount",0); //已还款总额
//                    bills.put("dueDate", DateUtils.DateToString(ordOrder.getRefundTime())); //账单到期日

                    billBean.setStatus(1);
                    billBean.setAmount(new BigDecimal(repayService.calculateRepayAmount(ordOrder,"1")).setScale(0, BigDecimal.ROUND_DOWN).longValue());
                    billBean.setInterestAmount(ordOrder.getInterest().setScale(0, BigDecimal.ROUND_DOWN).longValue());
                    billBean.setAdminAmount(ordOrder.getServiceFee().setScale(0, BigDecimal.ROUND_DOWN).longValue());
                    billBean.setRepaidAmount(0l);
                    billBean.setDueDate(DateUtils.DateToString(ordOrder.getRefundTime()));


                    break;

                case 8:// 待还款-已逾期8（逾期）
                case 9:// 还款处理中9（待还款）(放款成功)
//                    data.put("currentInstallmen", 1); //当前期数 0 表示已经还款完成，空表示为未生成账单
//                    data.put("disburseTime", ordOrder.getLendingTime()); //放款时间戳，单位毫秒
                    request.setCurrentInstallment(1);
                    request.setDisburseTime(ordOrder.getLendingTime().getTime());

//                    bills.put("status",3); //1未到期；2已还款；3逾期
//                    bills.put("amount",new BigDecimal(repayService.calculateRepayAmount(ordOrder,"1")).setScale(0, BigDecimal.ROUND_DOWN)); //本期账单应还款金额
//                    bills.put("interestAmount",ordOrder.getInterest().setScale(0, BigDecimal.ROUND_DOWN)); //应还利息
//                    bills.put("fineAmount",40000); //应还罚息
//                    bills.put("adminAmount",ordOrder.getServiceFee().setScale(0, BigDecimal.ROUND_DOWN)); //管理费
//                    bills.put("repaidAmount",0); //已还款总额
//                    bills.put("dueDate", DateUtils.DateToString(ordOrder.getRefundTime())); //账单到期日

                    billBean.setStatus(3);
                    billBean.setAmount(new BigDecimal(repayService.calculateRepayAmount(ordOrder,"1")).setScale(0, BigDecimal.ROUND_DOWN).longValue());
                    billBean.setInterestAmount(ordOrder.getInterest().setScale(0, BigDecimal.ROUND_DOWN).longValue());
                    BigDecimal delayFee = BigDecimal.ZERO;
                    if (ordOrder.getAmountApply().compareTo(BigDecimal.valueOf(500000)) <= 0){
                        delayFee = BigDecimal.valueOf(20000);
                    }else if (ordOrder.getAmountApply().compareTo(BigDecimal.valueOf(2000000)) > 0){
                        delayFee = BigDecimal.valueOf(60000);
                    }else {
                        delayFee = BigDecimal.valueOf(40000);
                    }
                    billBean.setFineAmount(delayFee.longValue());
                    billBean.setAdminAmount(ordOrder.getServiceFee().setScale(0, BigDecimal.ROUND_DOWN).longValue());
                    billBean.setRepaidAmount(0l);
                    billBean.setDueDate(DateUtils.DateToString(ordOrder.getRefundTime()));
                    break;

                case 10:// 正常已还款 （贷款结清）
                case 11:// 逾期已还款 （贷款结清）
//                    data.put("currentInstallmen", 0); //当前期数 0 表示已经还款完成，空表示为未生成账单
//                    data.put("disburseTime", ordOrder.getLendingTime()); //放款时间戳，单位毫秒
                    request.setCurrentInstallment(0);
                    request.setDisburseTime(ordOrder.getLendingTime().getTime());

//                    bills.put("status",2); //1未到期；2已还款；3逾期
//                    bills.put("dueDate", DateUtils.DateToString(ordOrder.getRefundTime())); //账单到期日
                    billBean.setStatus(2);
                    billBean.setDueDate(DateUtils.DateToString(ordOrder.getRefundTime()));
                    break;

                case 12:// 规则审核、机审不通过12（审批不通过）
                case 13:// 初审不通过13（审批不通过）
                case 14:// 复审不通过14（审批不通过）
                    break;
                case 15:// 取消      （贷款取消）
                    break;
                case 16:// 放款失败 （放款失败）
                    break;
            }

//        Integer[] reasons;
//        data.put("returnReason", );  //根据订单状态，打回则不为空

//            billList.add(bills);
//            data.put("bills",billList);
            List<CheetahOrdFeedRequest.BillBean> billBeanList = new ArrayList<>();
            billBeanList.add(billBean);
            request.setBills(billBeanList);
            // 加密请求
            CheetahBaseRequest baseRequest = new SendDataBuiler().buildParam(request,cheetahConfig, HttpMethod.POST);
            // 响应
            String response = HttpUtil.postJson(JsonUtils.serialize(request), STATUS_FEEDBACK_URL
                    + "?accessKey=" + baseRequest.getAccessKey() + "&timestamp=" + baseRequest.getTimestamp()
                    + "&sign=" + baseRequest.getSign());
            log.info("订单状态反馈 请求后返回:{}", JsonUtils.serialize(response));

            Cash2OrdStatusFeedbackResponse feedbackResponse = JsonUtils
                    .deserialize(response, Cash2OrdStatusFeedbackResponse.class);
            if (feedbackResponse.getCode() == 0) {
                log.info("订单状态反馈成功");
            } else {
                log.info("订单状态反馈失败，返回的message为:{}", feedbackResponse.getMessage());
            }

        } catch (Exception e) {
            log.error("订单状态反馈异常,异常原因:{}", e.getMessage());
        }

    }

    /***
     * 订单状态反馈
     * @param request
     * @return
     */
    public void ordStatusFeedbackManual(Cash2ManualRequest request){

        if (request.getOrderNo() != null){
            OrdOrder order = this.ordService.getOrderByOrderNo(request.getOrderNo());
            CheetahOrdStatusEnum cheetahOrdStatusEnum = CheetahOrdStatusEnum.CANCLE;
            ordStatusFeedback(order,cheetahOrdStatusEnum);
        }
    }


    /***
     * 订单信息拉取
     * @param request
     * @return
     */
    public CheetahResponse getOrdStatus(CheetahGetOrdStatusRequest request) {

        if (StringUtils.isEmpty(request.getOrderId())) {
            return CheetahResponseBuilder.buildResponse(
                    CheetahResponseCode.ORDER_NOT_EXIT_14001);
        }
        ExternalOrderRelation relation = externalChannelDataService
                .getExternalOrderRelationByExternalOrderNo(request.getOrderId());
        if (relation == null || StringUtils.isEmpty(relation.getOrderNo())) {
            return CheetahResponseBuilder.buildResponse(
                    CheetahResponseCode.ORDER_NOT_EXIT_14001);
        }

        OrdOrder ordOrder = ordService.getOrderByOrderNo(relation.getOrderNo());
        if (ordOrder == null) {
            return CheetahResponseBuilder.buildResponse(
                    CheetahResponseCode.ORDER_NOT_EXIT_14001).withData("the order cannot find");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("orderId", request.getOrderId());
        data.put("productId", Integer.valueOf(ordOrder.getProductUuid()));
        data.put("installments", 1);
        data.put("orderStatus", getOrdStatusFromDoitToCheetah(ordOrder)); // 订单状态
        data.put("auditStatus", getAuditStatus(ordOrder));  // 审核状态
        data.put("updateTime", ordOrder.getUpdateTime()); // 时间戳，单位毫秒；反馈订单流经至对应状态的时间

        List<Map<String,Object>> billList = new ArrayList<>();
        Map<String,Object> bills = new HashMap<>();
        bills.put("installmentNumber",1);

        switch (ordOrder.getStatus()) {
            case 1:// 待提交 	(待补充资料)
                break;
            case 2:// 待机审    （审批中）
            case 3:// 待初审    （审批中）
            case 4:// 待复审    （审批中）
            case 17:// 待电核   （审批中）
            case 18:// 待电核   （审批中）
            case 19:// 降额后等待用户确认  （审批中）
                break;
            case 5:// 待放款    （审批通过）
            case 6:// 放款处理中 （审批通过）
                break;
            case 7:// 待还款-未逾期7（放款成功）
                data.put("currentInstallmen", 1); //当前期数 0 表示已经还款完成，空表示为未生成账单
                data.put("disburseTime", ordOrder.getLendingTime()); //放款时间戳，单位毫秒
                bills.put("status",1); //1未到期；2已还款；3逾期

                bills.put("amount",new BigDecimal(repayService.calculateRepayAmount(ordOrder,"1")).setScale(0, BigDecimal.ROUND_DOWN)); //本期账单应还款金额
                bills.put("interestAmount",ordOrder.getInterest().setScale(0, BigDecimal.ROUND_DOWN)); //应还利息
                bills.put("adminAmount",ordOrder.getServiceFee().setScale(0, BigDecimal.ROUND_DOWN)); //管理费
                bills.put("repaidAmount",0); //已还款总额
                bills.put("dueDate", DateUtils.DateToString(ordOrder.getRefundTime())); //账单到期日

                break;

            case 8:// 待还款-已逾期8（逾期）
            case 9:// 还款处理中9（待还款）(放款成功)
                data.put("currentInstallmen", 1); //当前期数 0 表示已经还款完成，空表示为未生成账单
                data.put("disburseTime", ordOrder.getLendingTime()); //放款时间戳，单位毫秒
                bills.put("status",3); //1未到期；2已还款；3逾期
                bills.put("amount",new BigDecimal(repayService.calculateRepayAmount(ordOrder,"1")).setScale(0, BigDecimal.ROUND_DOWN)); //本期账单应还款金额
                bills.put("interestAmount",ordOrder.getInterest().setScale(0, BigDecimal.ROUND_DOWN)); //应还利息

                BigDecimal delayFee = BigDecimal.ZERO;
                if (ordOrder.getAmountApply().compareTo(BigDecimal.valueOf(500000)) <= 0){
                    delayFee = BigDecimal.valueOf(20000);
                }else if (ordOrder.getAmountApply().compareTo(BigDecimal.valueOf(2000000)) > 0){
                    delayFee = BigDecimal.valueOf(60000);
                }else {
                    delayFee = BigDecimal.valueOf(40000);
                }

                bills.put("fineAmount",delayFee); //应还罚息
                bills.put("adminAmount",ordOrder.getServiceFee().setScale(0, BigDecimal.ROUND_DOWN)); //管理费
                bills.put("repaidAmount",0); //已还款总额
                bills.put("dueDate", DateUtils.DateToString(ordOrder.getRefundTime())); //账单到期日
                break;

            case 10:// 正常已还款 （贷款结清）
            case 11:// 逾期已还款 （贷款结清）
                data.put("currentInstallmen", 0); //当前期数 0 表示已经还款完成，空表示为未生成账单
                data.put("disburseTime", ordOrder.getLendingTime()); //放款时间戳，单位毫秒
                bills.put("status",2); //1未到期；2已还款；3逾期
                bills.put("dueDate", DateUtils.DateToString(ordOrder.getRefundTime())); //账单到期日
                break;

            case 12:// 规则审核、机审不通过12（审批不通过）
            case 13:// 初审不通过13（审批不通过）
            case 14:// 复审不通过14（审批不通过）
                break;
            case 15:// 取消      （贷款取消）
                break;
            case 16:// 放款失败 （放款失败）
                break;
        }
//        Integer[] reasons;
//        data.put("returnReason", );  //根据订单状态，打回则不为空
        billList.add(bills);
        data.put("bills",billList);
        return CheetahResponseBuilder.buildResponse(
                CheetahResponseCode.CODE_OK_0).withData(data);
    }

    /***
     * 订单还款详情信息拉取
     * @param repaymentInfo
     * @return
     */
    public CheetahResponse getRepaymentInfo(CheetahGetOrdStatusRequest repaymentInfo) {


        if (StringUtils.isEmpty(repaymentInfo.getOrderId())) {
            return CheetahResponseBuilder.buildResponse(
                    CheetahResponseCode.ORDER_NOT_EXIT_14001);
        }
        ExternalOrderRelation relation = externalChannelDataService
                .getExternalOrderRelationByExternalOrderNo(repaymentInfo.getOrderId());
        if (relation == null || StringUtils.isEmpty(relation.getOrderNo())) {
            return CheetahResponseBuilder.buildResponse(
                    CheetahResponseCode.ORDER_NOT_EXIT_14001);
        }

        OrdOrder ordOrder = ordService.getOrderByOrderNo(relation.getOrderNo());
        if (ordOrder == null) {
            return CheetahResponseBuilder.buildResponse(
                    CheetahResponseCode.ORDER_NOT_EXIT_14001).withData("the order cannot find");
        }


        List<SysPaymentChannel> channelList = this.sysPaymentChannelDao.getRepaymentChanelListForDOKUNew();

        List<Map<String,String>> bankInfos = new ArrayList<>();

        for (SysPaymentChannel channel:channelList){

            Map<String, String> data = new HashMap<>();

            String depositMethod = "";
            switch (channel.getType()){
                case 9: // Alfamart
                    data.put("bankCode", "ALFA-GROUP");
                    data.put("accountType", "STORE");
                    data.put("accountNumber", "STORE");
                    depositMethod = "ALFAMART";
                    break;
                case 12: // PERMATA
                    data.put("bankCode", "PERMATA");
                    data.put("accountType", "BANK");
                    depositMethod = "PERMATA";
                    break;
                case 13: // BNI
                    data.put("bankCode", "BNI");
                    data.put("accountType", "BANK");
                    depositMethod = "BNI";
                    break;
                case 14: // OTHER BANKS
                    data.put("bankCode", "PERMATA");
                    data.put("accountType", "BANK");
                    depositMethod = "PERMATA";
                    break;
                case 15: // BCA
                    data.put("bankCode", "BCA");
                    data.put("accountType", "BANK");
                    depositMethod = "BCA";
                    break;
            }
            data.put("accountNumber", getPaymentCodeWithMethod(depositMethod,ordOrder));  //还款账户号
            bankInfos.add(data);
        }


        Map<String,Object>  responseData = new HashMap<>();
        responseData.put("orderId",repaymentInfo.getOrderId());
        responseData.put("repayAmount",new BigDecimal(repayService.calculateRepayAmount(ordOrder,"1")).setScale(0, BigDecimal.ROUND_DOWN));
        responseData.put("bankInfo",bankInfos);

        return CheetahResponseBuilder.buildResponse(
                CheetahResponseCode.CODE_OK_0).withData(responseData);
    }


    public String getPaymentCodeWithMethod(String depositMethod,OrdOrder ordOrder){
        String paymentCode = "";
        try {

            UsrUser user = this.usrService.getUserByUuid(ordOrder.getUserUuid());
            String userName = "";
            if (user.getRealName() != null){
                userName = user.getRealName().replaceAll("\\pP",""); //完全清除标点
            }

            RequestBody requestBody = new FormBody.Builder().build();
            Map<String, String> contents = new HashMap<String, String>();
            String paymentCount = repayService.calculateRepayAmount(ordOrder,"1");
            requestBody = new FormBody.Builder()
                    .add("externalId", ordOrder.getUuid())  // 订单号
                    .add("depositAmount", paymentCount)  // 还款金额
                    .add("depositChannel", "DOKU") // 还款渠道
                    .add("depositMethod", depositMethod) // 还款类型（alfamart,BRI.mandiri,BNI,otherBanks）
                    .add("currency", "IDR")
                    .add("depositType","PAYDAYLOAN")
                    .add("customerName",userName)   //????
                    .build();

            contents.put("externalId", ordOrder.getUuid());  // 订单号
            contents.put("depositAmount", paymentCount);  // 还款金额
            contents.put("depositChannel", "DOKU"); // 还款渠道
            contents
                    .put("depositMethod", depositMethod); // 还款类型（alfamart,BRI.mandiri,BNI,otherBanks）
            contents.put("currency", "IDR"); // 单位
            contents.put("depositType","PAYDAYLOAN");
            contents.put("customerName",userName);   //????

            log.info("该笔订单还款金额为" + paymentCount);

            Request request = new Request.Builder()
                    .url(COMMIT_REPAY_URL)
                    .post(requestBody)
                    .header("X-AUTH-TOKEN", PAY_TOKEN)
                    .build();

            // 请求数据落库，SysThirdLogs
            this.sysThirdLogsService.addSysThirdLogs(ordOrder.getUuid(), ordOrder.getUserUuid(),
                    SysThirdLogsEnum.COMMIT_REPAY.getCode(), 0, JsonUtils.serialize(contents), null);

            Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                String responseStr = response.body().string();
                // 还款响应
                log.info("还款 请求后返回:{}", JsonUtils.serialize(responseStr));

                RepayResponse repayResponse = JsonUtils
                        .deserialize(responseStr, RepayResponse.class);

                // 记录还款码
                RepayRequest repayRequest = new RepayRequest();
                repayRequest.setOrderNo(ordOrder.getUuid());
                repayRequest.setUserUuid(ordOrder.getUserUuid());
                repayService
                        .recordOrderPaymentCode(repayRequest, "2", repayResponse.getPaymentCode(),
                                ordOrder, paymentCount,null);
                // 响应数据落库，sysThirdLogs
                sysThirdLogsService
                        .addSysThirdLogs(repayRequest.getOrderNo(), repayRequest.getUserUuid(),
                                SysThirdLogsEnum.COMMIT_REPAY.getCode(), 0, null, responseStr);
                paymentCode = repayResponse.getPaymentCode(); //还款账户号

            } else {
                log.info("还款 请求失败，订单号:{}", ordOrder.getUuid());
                sysThirdLogsService.addSysThirdLogs(ordOrder.getUuid(), ordOrder.getUserUuid(),
                        SysThirdLogsEnum.COMMIT_REPAY.getCode(), 0, null, response.body().string());
            }
        } catch (Exception e) {
            log.info("还款请求异常",e);
        }
        return paymentCode;
    }

    /***
     * 获取产品信息
     * @return
     */
    public CheetahResponse getProductConfig() {

        /**
         *   猎豹暂不支持分用户不同产品   所以统一产品为 120w 30天 19.2%
         * */
        List<SysProduct> list =  sysProductDao.getProductConfWithProductLevelAndDuefeeRate(2,"0.192");;
        if(CollectionUtils.isEmpty(list)){
            return CheetahResponseBuilder.buildResponse(
                    CheetahResponseCode.NO_VAILID_PRODUCT);
        }

        List<Map<String, Object>> productList = new ArrayList<>();

        for (SysProduct product: list){

            Map<String, Object> data = new HashMap<>();
            data.put("name", "Do-It");  // 产品名称
            data.put("id", product.getUuid());  // 产品id
            data.put("amount", product.getBorrowingAmount().setScale(0, BigDecimal.ROUND_DOWN));  // 产品金额
            data.put("tenor", product.getBorrowingTerm());  // 产品期限
            data.put("tenorUnit", 1);  //借款期限单位 	1：天，2：月
            data.put("interestAmount", product.getInterest().setScale(0, BigDecimal.ROUND_DOWN));  // 利息
            data.put("installments", 1);  //分期数
            data.put("adminAmount", product.getDueFee().setScale(0, BigDecimal.ROUND_DOWN));  //管理费
            data.put("actualAmount", product.getBorrowingAmount().subtract(product.getDueFee()).setScale(0, BigDecimal.ROUND_DOWN));  //实际到账金额

            productList.add(data);
        }

        return CheetahResponseBuilder.buildResponse(
                CheetahResponseCode.CODE_OK_0).withData(productList);
    }

//    public static void main(String[] args) {
//        BigDecimal s = new BigDecimal(1200000.00).setScale(0, BigDecimal.ROUND_DOWN);
//        log.info(s+"");
//    }
//

    /**
     * 将Doit的订单状态 映射到 Cheetah 的订单状态
     */
    public int getOrdStatusFromDoitToCheetah(OrdOrder order) {

        switch (order.getStatus()) {
            case 1:// 待提交 	(待补充资料)
                return 80;
            case 2:// 待机审    （审批中）
            case 3:// 待初审    （审批中）
            case 4:// 待复审    （审批中）
            case 17:// 待电核   （审批中）
            case 18:// 待电核   （审批中）
            case 19:// 降额后等待用户确认  （审批中）
                return 90;
            case 5:// 待放款    （审批通过）
            case 6:// 放款处理中 （审批通过）
                return 100;
            case 7:// 待还款-未逾期7（放款成功）
            case 9:// 还款处理中9（待还款）(放款成功)
                return 170;
            case 8:// 待还款-已逾期8（逾期）
                return 180;
            case 10:// 正常已还款 （贷款结清）
            case 11:// 逾期已还款 （贷款结清）
                return 200;
            case 12:// 规则审核、机审不通过12（审批不通过）
            case 13:// 初审不通过13（审批不通过）
            case 14:// 复审不通过14（审批不通过）
                return 110;
            case 15:// 取消      （贷款取消）
                return 161;
            case 16:// 放款失败 （放款失败）
                return 169;
        }
        return 80;
    }


    /**
     * 获取订单的审核状态
     * 0：订单资料不全未开始审批
     * 10：通过
     * 20：审批中
     * 30：审批需重填资料
     * 40：不通过
     */
    public int getAuditStatus(OrdOrder order) {

        switch (order.getStatus()) {
            case 1:// 待提交 	(待补充资料)
                return 0;
            case 2:// 待机审    （审批中）
            case 3:// 待初审    （审批中）
            case 4:// 待复审    （审批中）
            case 17:// 待电核   （审批中）
            case 18:// 待电核   （审批中）
            case 19:// 降额后等待用户确认  （审批中）
                return 20;
            case 5:// 待放款    （审批通过）
            case 6:// 放款处理中 （审批通过）
            case 7:// 待还款-未逾期7（放款成功）
            case 9:// 还款处理中9（待还款）(放款成功)
            case 8:// 待还款-已逾期8（逾期）
            case 10:// 正常已还款 （贷款结清）
            case 11:// 逾期已还款 （贷款结清）
                return 10;
            case 12:// 规则审核、机审不通过12（审批不通过）
            case 13:// 初审不通过13（审批不通过）
            case 14:// 复审不通过14（审批不通过）
                return 40;
            case 15:// 取消      （贷款取消）
                return 40;
            case 16:// 放款失败 （放款失败）
                return 10;
        }
        return 80;
    }

}
