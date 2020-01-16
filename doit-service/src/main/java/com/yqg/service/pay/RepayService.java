package com.yqg.service.pay;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.yqg.common.enums.order.OrdBillStatusEnum;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.order.OrderTypeEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.system.SysThirdLogsEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.order.dao.OrdBillDao;
import com.yqg.order.dao.OrdDelayRecordDao;
import com.yqg.order.dao.OrdPaymentCodeDao;
import com.yqg.order.entity.CouponRecord;
import com.yqg.order.entity.OrdBill;
import com.yqg.order.entity.OrdDelayRecord;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdPaymentCode;
import com.yqg.service.order.OrdBillService;
import com.yqg.service.order.OrdService;
import com.yqg.service.p2p.request.UserRepayRequest;
import com.yqg.service.p2p.response.P2PUserPayResponse;
import com.yqg.service.p2p.service.P2PService;
import com.yqg.service.pay.request.RepayRequest;
import com.yqg.service.pay.response.RepayResponse;
import com.yqg.service.system.service.CouponService;
import com.yqg.service.system.service.SysThirdLogsService;
import com.yqg.service.user.service.UsrService;
import com.yqg.system.dao.SysPaymentChannelDao;
import com.yqg.system.dao.SysProductDao;
import com.yqg.system.entity.SysProduct;
import com.yqg.user.entity.UsrUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by wanghuaizhou on 2017/12/29.
 */
@Service
@Slf4j
public class RepayService {

    @Autowired
    private OrdService ordService;
    @Autowired
    private SysThirdLogsService sysThirdLogsService;
    @Autowired
    private SysProductDao sysProductDao;
    @Autowired
    private OrdPaymentCodeDao ordPaymentCodeDao;
    @Autowired
    private OrdDelayRecordDao ordDelayRecordDao;
    @Autowired
    private UsrService usrService;
    @Autowired
    private P2PService p2PService;

    @Autowired
    private OrdBillDao ordBillDao;

    @Autowired
    private SysPaymentChannelDao sysPaymentChannelDao;

    // ??????url
    @Value("${pay.commitRepayUrl}")
    private String COMMIT_REPAY_URL;

    @Value("${pay.ovoCommitRepayUrl}")
    private String OVO_COMMIT_REPAY_URL;

    @Value("${pay.token}")
    private String PAY_TOKEN;

    @Autowired
    private OkHttpClient httpClient;

    @Autowired
    private OrdBillService ordBillService;

    @Autowired
    private CouponService couponService;

    /**
     *   ??paymentCode
     *   ??payType?1 ????? bluePay ,  paymentCode?????????
     *   ??payType?2 ????? xendit  ,  paymentCode?????????
     **/
    public RepayResponse repayment(RepayRequest repayRequest) throws Exception{

        log.info("repayment orderNo: {}",repayRequest.getOrderNo());

        RepayResponse repayResponse ;
        String responseStr = "";


        if (repayRequest.getType().equals("1") || repayRequest.getType().equals("2")){
            // 普通还款 和 展期还款
            OrdOrder orderOrder = new OrdOrder();
            orderOrder.setDisabled(0);
            orderOrder.setUuid(repayRequest.getOrderNo());
            orderOrder.setUserUuid(repayRequest.getUserUuid());
            List<OrdOrder> orderList = this.ordService.orderInfo(orderOrder);
            if (CollectionUtils.isEmpty(orderList)) {
                log.info("the repayment order is not exist.");
                throw new ServiceException(ExceptionEnum.ORDER_NOT_FOUND);
            }
            OrdOrder order = orderList.get(0);
            if( order.getStatus() != OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode() && order.getStatus() != OrdStateEnum.RESOLVING_OVERDUE.getCode()){
                log.info("the order status is invalid for repayment");
                throw new ServiceException(ExceptionEnum.ORDER_STATES_ERROR);
            }
            if( order.getStatus() == OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode() || order.getStatus() == OrdStateEnum.RESOLVED_OVERDUE.getCode()){
                log.info("the order is already paid.");
                throw new ServiceException(ExceptionEnum.SYSTEM_IS_REFUND);
            }

            responseStr = getResponseStr(repayRequest,order);

        }else if (repayRequest.getType().equals("3")){
           //  分期账单还款
            OrdBill scan = new OrdBill();
            scan.setDisabled(0);
            scan.setUuid(repayRequest.getOrderNo());
            scan.setUserUuid(repayRequest.getUserUuid());
            List<OrdBill> bills = this.ordBillService.billInfo(scan);
            if (CollectionUtils.isEmpty(bills)) {
                log.info("the repayment order is not exist.");
                throw new ServiceException(ExceptionEnum.ORDER_NOT_FOUND);
            }
            OrdBill bill = bills.get(0);
            if( bill.getStatus() != OrdBillStatusEnum.RESOLVING.getCode() && bill.getStatus() != OrdBillStatusEnum.RESOLVING_OVERDUE.getCode() ){
                log.info("the order status is invalid for repayment");
                throw new ServiceException(ExceptionEnum.ORDER_STATES_ERROR);
            }
            if( bill.getStatus() == OrdBillStatusEnum.RESOLVED.getCode() || bill.getStatus() == OrdBillStatusEnum.RESOLVED_OVERDUE.getCode()){
                log.info("the order is already paid.");
                throw new ServiceException(ExceptionEnum.SYSTEM_IS_REFUND);
            }

            responseStr = getResponseStr(repayRequest,bill);
        }

        log.info("the commit repay response is: {}",responseStr);

        if (!StringUtils.isEmpty(responseStr)){
            repayResponse = JsonUtils.deserialize(responseStr,RepayResponse.class);
            if (!(repayResponse.getCode().equals("0") || repayResponse.getCode().equals("00"))){
                
                if(repayResponse.getCode().equals("14")){
                    throw new ServiceException(ExceptionEnum.OVO_INVALID_NUMBER);
                }else if(repayResponse.getCode().equals("17")){
                    throw new ServiceException(ExceptionEnum.OVO_TRANSACTION_DECLINE);
                }else if(repayResponse.getCode().equals("68")){
                    throw new ServiceException(ExceptionEnum.OVO_PENDING_REVERSAL);
                }else {
                    throw new ServiceException(ExceptionEnum.OVO_TRANSACTION_FAILED);
                }
            }

        }else {
            throw new ServiceException(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        return repayResponse;
    }

    public String getResponseStr(RepayRequest repayRequest,Object object) throws Exception{

        String paymentType = repayRequest.getRepaymentType();
        if (paymentType.equals("1")||paymentType.equals("2")||paymentType.equals("3")) {
            // CIMB
            return commitRepay(repayRequest, object, "CIMB");
        }else if(paymentType.equals("4")){
            // DOKU
            return  commitRepay(repayRequest, object, "DOKU");
        }else if(paymentType.equals("5")){
            // BNI
            return  commitRepay(repayRequest, object, "BNI");
        }else if(paymentType.equals("6")){
            // BNI
            return  commitRepay(repayRequest, object, "OVO");
        }else {
            log.info("the input repayment type(payment channel) is valid ");
            throw new ServiceException(ExceptionEnum.PAYMENT_CHANNEL_NOT_VALID);
        }
    }

    /**
     *    ??????
     * */
    @Transactional
    public String commitRepay(RepayRequest repayRequest,Object object,String paymentType) throws Exception{
        String orderNo="";

        String responseStr = "";
        try {

            String depositMethod = "";

            if (repayRequest.getRepaymentChannel().equals("1")){
                depositMethod = "ALFAMART";
            }else if (repayRequest.getRepaymentChannel().equals("2")){
                depositMethod = "BRI";
            }else if (repayRequest.getRepaymentChannel().equals("3")){
                depositMethod = "MANDIRI";
            }else if (repayRequest.getRepaymentChannel().equals("4")){
                depositMethod = "BNI";
            }else if (repayRequest.getRepaymentChannel().equals("5")){
                depositMethod = "OTHERS";
            }else if (repayRequest.getRepaymentChannel().equals("6")){
                depositMethod = "BCA";
            }else if (repayRequest.getRepaymentChannel().equals("7")){
                depositMethod = "CIMB";
            }else if (repayRequest.getRepaymentChannel().equals("8")){
                depositMethod = "CIMB";
            }else if (repayRequest.getRepaymentChannel().equals("9")){
                depositMethod = "ALFAMART";
            }else if (repayRequest.getRepaymentChannel().equals("10")){
                // DOKU渠道的CIMB
                depositMethod = "CIMB";
            }else if (repayRequest.getRepaymentChannel().equals("11")){
                // 选others 也要传CIMB 就是走CIMB （DOKU渠道的CIMB)
                depositMethod = "CIMB";
            }else if (repayRequest.getRepaymentChannel().equals("12")){
                // DOKU渠道的PERMATA
                depositMethod = "PERMATA";
            }else if (repayRequest.getRepaymentChannel().equals("13")){
                // DOKU渠道的BNI
                depositMethod = "BNI";
            }else if (repayRequest.getRepaymentChannel().equals("14")){
                // DOKU渠道的PERMATA
                // 选others 也要传PERMATA 就是走PERMATA
                depositMethod = "PERMATA";
            }else if (repayRequest.getRepaymentChannel().equals("15")){
                // DOKU渠道的BCA
                depositMethod = "BCA";
            }else if (repayRequest.getRepaymentChannel().equals("16")){
                // DOKU渠道的CIMB
                depositMethod = "CIMB";
            }else if (repayRequest.getRepaymentChannel().equals("17")){
                // Ovo
                depositMethod = "OVO";
            }else {
                depositMethod = "OTHERS";
            }


            RequestBody requestBody =  new FormBody.Builder().build();
            Map<String,String> contents = new HashMap<String, String>();

            String type = "";
            if (!StringUtils.isEmpty(repayRequest.getType())){
                type = repayRequest.getType();
            }else {
                type = "1";
            }

            UsrUser user = this.usrService.getUserByUuid(repayRequest.getUserUuid());
            String userName = "";
            if (user.getRealName() != null){
                userName = user.getRealName().replaceAll("\\pP","").replace("\u200b", ""); //完全清除标点
            }

            String paymentCount = "";
            BigDecimal interest = null;
            CouponRecord couponRecord = null;
            if (object instanceof OrdOrder){
                OrdOrder order = (OrdOrder) object;
                orderNo = order.getUuid();
                paymentCount = calculateRepayAmount(order,type);

                // 判断是否有优惠券
                if (!order.getOrderType().equals(OrderTypeEnum.STAGING)){
                    couponRecord = this.couponService.getCouponInfoWithOrderNo(order.getUuid());
                    // 有优惠券且未使用
                    if (couponRecord != null){
                        paymentCount = new BigDecimal(paymentCount).subtract(couponRecord.getMoney()).setScale(2)+"";
                    }
                }
                interest = order.getInterest();
            }else if (object instanceof OrdBill){
                OrdBill bill = (OrdBill) object;
                orderNo = bill.getOrderNo();
                paymentCount = calculateBillRepayAmount(bill);
                interest = bill.getInterest();
            }

            // 非BLUEPAY还款 不需要 paymentCode 和 transactionId 参数
            if(!paymentType.equals("OVO")){
                requestBody = new FormBody.Builder()
                .add("externalId",repayRequest.getOrderNo())  // ???
                .add("depositAmount",paymentCount)  // ????
                .add("depositChannel",paymentType) // ????
                .add("depositMethod",depositMethod) // ?????alfamart,BRI.mandiri,BNI,otherBanks?
                .add("currency","IDR")
                .add("depositType","PAYDAYLOAN")
                .add("customerName",userName)
                .build();

                contents.put("externalId",repayRequest.getOrderNo());  // ???
                contents.put("depositAmount",paymentCount);  // ????
                contents.put("depositChannel",paymentType); // ????
                contents.put("depositMethod",depositMethod); // ?????alfamart,BRI.mandiri,BNI,otherBanks?
                contents.put("currency","IDR"); // ??
                contents.put("depositType","PAYDAYLOAN");   //????
                contents.put("customerName",userName);   //????
            }
            else{
                requestBody = new FormBody.Builder().add("externalId",repayRequest.getOrderNo())  // ???
                .add("Amount",paymentCount)  // ????
                .add("ovoId",repayRequest.getOvoAccount()) // ????
                .add("remark","PAYDAYLOAN")
                .build();
            }

            log.info("用户还款金额"+paymentCount);

            String codeType = "";
            if (paymentType.equals("BLUEPAY")){
                codeType = "1";
            }else if(paymentType.equals("XENDIT")){
                codeType = "2";
            }else if(paymentType.equals("CIMB")){
                codeType = "3";
            }else if(paymentType.equals("DOKU")){
                codeType = "4";
            }else if(paymentType.equals("BNI")){
                codeType = "5";
            }else if(paymentType.equals("OVO")){
                codeType = "6";
            }

            //p2p还款
            if(p2PService.isP2PIssuedLoan(orderNo)){
                UserRepayRequest request = new UserRepayRequest();
                request.setAmountApply(new BigDecimal(paymentCount));
                request.setCreditorNo(repayRequest.getOrderNo());
                request.setInterest(interest);
                request.setOverdueFee(new BigDecimal(calculatePenaltyFee(object))); //逾期服务费
                request.setOverdueRate(new BigDecimal(calculateOverDueFee(object))); //滞纳金费-- 逾期服务费
                request.setUserName(userName);
                request.setUserUuid(repayRequest.getUserUuid());

//              repaymentType  还款类型 1正常还款 2分期还款 3展期还款
                request.setRepaymentType("1");
                if (object instanceof OrdOrder){
                    //  type 1 正常还款   2 展期还款    3 分期账单还款
                    if (type.equals("2")){
                        request.setRepaymentType("3");
                    }
                }else if (object instanceof OrdBill){
                    OrdBill bill = (OrdBill) object;
                    request.setRepaymentType("2");
                    request.setPeriodNo(Integer.valueOf(bill.getBillTerm()));
                }

                P2PUserPayResponse p2pResp = p2PService.userRepay(request);
                RepayResponse resp = new RepayResponse();
                resp.setCode("0");
                if (p2pResp.getData() == null || StringUtils.isEmpty(p2pResp.getData().getPaymentCode())) {
                    return "";
                }
                resp.setPaymentCode(p2pResp.getData().getPaymentCode());
                String respStr =  JsonUtils.serialize(resp);
                //p2p还款 也记录还款码
                recordOrderPaymentCode(repayRequest,codeType,resp.getPaymentCode(),object,paymentCount,couponRecord);
                return respStr;
            }

            String commitRepayURL = (paymentType.equals("OVO")) ? OVO_COMMIT_REPAY_URL : COMMIT_REPAY_URL;
            Request request = new Request.Builder()
                    .url(commitRepayURL)
                    .post(requestBody)
                    .header("X-AUTH-TOKEN", PAY_TOKEN)
                    .build();

            // ???????SysThirdLogs
            this.sysThirdLogsService.addSysThirdLogs(repayRequest.getOrderNo(),repayRequest.getUserUuid(), SysThirdLogsEnum.COMMIT_REPAY.getCode(),0,JsonUtils.serialize(contents),null);

            // new builder 
            httpClient.newBuilder()
                    .connectTimeout(30,TimeUnit.SECONDS)
                    .readTimeout(75,TimeUnit.SECONDS)
                    .writeTimeout(75,TimeUnit.SECONDS)
                    .build();
            
            
            Response response = httpClient.newCall(request).execute();

            if(response.isSuccessful())
            {
                responseStr = response.body().string();

                // ????
                log.info("?? ?????:{}", JsonUtils.serialize(responseStr));
                RepayResponse repayResponse = JsonUtils.deserialize(responseStr,RepayResponse.class);
                // 记录还款码
                recordOrderPaymentCode(repayRequest,codeType,repayResponse.getPaymentCode(),object,paymentCount,couponRecord);
                // ???????sysThirdLogs
                sysThirdLogsService.addSysThirdLogs(repayRequest.getOrderNo(),repayRequest.getUserUuid(), SysThirdLogsEnum.COMMIT_REPAY.getCode(),0,null,responseStr);
                return responseStr;
            }else {
                if (object instanceof OrdOrder){
                    OrdOrder order = (OrdOrder) object;
                    log.info("订单提交还款异常，订单号:{}",order.getUuid());
                }else if (object instanceof OrdBill){
                    OrdBill bill = (OrdBill) object;
                    log.info("账单提交还款异常，账单号:{}",bill.getUuid());
                }
                sysThirdLogsService.addSysThirdLogs(repayRequest.getOrderNo(),repayRequest.getUserUuid(), SysThirdLogsEnum.COMMIT_REPAY.getCode(),0,null,response.body().string());
            }

        } catch(SocketTimeoutException e){
            throw new ServiceException(ExceptionEnum.SYSTEM_TIMEOUT);
        }catch (Exception e){
            log.error("提交还款异常",e);
        }
        return responseStr;
    }

    /**
     *  ???????
     * */
    public void recordOrderPaymentCode(RepayRequest repayRequest ,String codeType,String paymentCode,Object object,String payAmout,CouponRecord couponRecord){

        OrdPaymentCode entity = new OrdPaymentCode();
        entity.setUserUuid(repayRequest.getUserUuid());
        entity.setPaymentCode(paymentCode);
        entity.setCodeType(codeType);
        entity.setActualRepayAmout(payAmout);

        if (object instanceof OrdOrder){
            OrdOrder order = (OrdOrder) object;
            entity.setOrderNo(repayRequest.getOrderNo());
            entity.setInterest(order.getInterest()+"");
            entity.setOverDueFee(calculateOverDueFee(order));
            entity.setPenaltyFee(calculatePenaltyFee(order));
            entity.setAmountApply(order.getAmountApply().toString());

            //  说明使用了优惠券  在paymentCode中记录使用
            if (couponRecord != null){
                if (couponRecord.getStatus().equals(3)){
                    entity.setCouponUuid(couponRecord.getUuid());
                }
            }

        }else if (object instanceof OrdBill){
            OrdBill bill = (OrdBill) object;
            entity.setOrderNo(bill.getUuid());
            entity.setInterest(bill.getInterest()+"");
            entity.setOverDueFee(calculateOverDueFee(bill));
            entity.setPenaltyFee(calculatePenaltyFee(bill));
            entity.setAmountApply(bill.getBillAmout().toString());
        }

        if (!StringUtils.isEmpty(repayRequest.getPrincipal())){
            entity.setPrincipal(repayRequest.getPrincipal());
        }


        List<OrdPaymentCode> scanList = this.ordPaymentCodeDao.scan(entity);
        if (CollectionUtils.isEmpty(scanList)){
            this.ordPaymentCodeDao.insert(entity);
        }
    }

    /**
     *   逾期服务费
     * */
    public String calculateOverDueFee(Object object){

        BigDecimal shouldPayAmount = BigDecimal.valueOf(0);
        // ?????????
        try {
            if (object instanceof OrdOrder){
                OrdOrder ordOrder = (OrdOrder) object;
                int overdueDay = (int) DateUtils.daysBetween(DateUtils.formDate(ordOrder.getRefundTime(),"yyyy-MM-dd"),DateUtils.formDate(new Date(),"yyyy-MM-dd"));

                if (overdueDay >0){
                    if (ordOrder.getOrderType().equals("0")){
                        SysProduct sysProd = sysProductDao.getProductInfoIgnorDisabled(ordOrder.getProductUuid());
                        shouldPayAmount = sysProd.getOverdueFee();
                    }else {

                        BigDecimal delayFee = BigDecimal.ZERO;
                        if (ordOrder.getAmountApply().compareTo(BigDecimal.valueOf(500000)) <= 0){
                            delayFee = BigDecimal.valueOf(20000);
                        }else if (ordOrder.getAmountApply().compareTo(BigDecimal.valueOf(2000000)) > 0){
                            delayFee = BigDecimal.valueOf(60000);
                        }else {
                            delayFee = BigDecimal.valueOf(40000);
                        }

                        shouldPayAmount = delayFee;
                    }
                }
            }else  if (object instanceof OrdBill){
                OrdBill bill = (OrdBill) object;
                int overdueDay = (int) DateUtils.daysBetween(DateUtils.formDate(bill.getRefundTime(),"yyyy-MM-dd"),DateUtils.formDate(new Date(),"yyyy-MM-dd"));
                if (overdueDay >0){
                    SysProduct sysProd = sysProductDao.getProductInfoIgnorDisabled(bill.getProductUuid());
                    shouldPayAmount = sysProd.getOverdueFee();
                }
            }

        } catch (ParseException e) {
            e.getStackTrace();
        } catch (Exception e) {
            e.getStackTrace();
        }
        return shouldPayAmount + "";
    }

    /**
     *   通过时间还款时间计算逾期服务费
     * */
    public String calculateOverDueFee(OrdOrder ordOrder, int overdueDay){

        BigDecimal shouldPayAmount = BigDecimal.valueOf(0);
        if (overdueDay > 0) {
            if ("0".equals(ordOrder.getOrderType())) {
                SysProduct sysProd = sysProductDao.getProductInfoIgnorDisabled(ordOrder.getProductUuid());
                shouldPayAmount = sysProd.getOverdueFee();
            } else {

                BigDecimal delayFee = BigDecimal.ZERO;
                if (ordOrder.getAmountApply().compareTo(BigDecimal.valueOf(500000)) <= 0){
                    delayFee = BigDecimal.valueOf(20000);
                }else if (ordOrder.getAmountApply().compareTo(BigDecimal.valueOf(2000000)) > 0){
                    delayFee = BigDecimal.valueOf(60000);
                }else {
                    delayFee = BigDecimal.valueOf(40000);
                }

                shouldPayAmount = delayFee;
            }
        }
        return shouldPayAmount + "";
    }
    /**
     *   ?????????
     * */
    public String calculatePenaltyFee(Object object){

        BigDecimal shouldPayAmount = BigDecimal.valueOf(0);
        // ?????????
        try {
            if (object instanceof OrdOrder){
                OrdOrder ordOrder = (OrdOrder) object;
                int overdueDay = DateUtils.daysBetween(DateUtils.formDate(ordOrder.getRefundTime(),"yyyy-MM-dd"),DateUtils.formDate(new Date(),"yyyy-MM-dd"));

                shouldPayAmount = new BigDecimal(calculatePenaltyFeeByRepayDays(ordOrder,overdueDay));
            }else if (object instanceof OrdBill){

                OrdBill bill = (OrdBill) object;
                int overdueDay = DateUtils.daysBetween(DateUtils.formDate(bill.getRefundTime(),"yyyy-MM-dd"),DateUtils.formDate(new Date(),"yyyy-MM-dd"));
                shouldPayAmount = new BigDecimal(calculatePenaltyFeeByRepayDays(bill,overdueDay));
            }


        } catch (ParseException e) {
            e.getStackTrace();
        } catch (Exception e) {
            e.getStackTrace();
        }
        return shouldPayAmount + "";
    }

    /**
     *   通过实际还款时间计算罚息
     *
     * */
    public String calculatePenaltyFeeByRepayDays(Object object, int overdueDay) {

        BigDecimal shouldPayAmount = BigDecimal.valueOf(0);

        if (overdueDay >0){

            if (object instanceof OrdOrder){
                OrdOrder ordOrder = (OrdOrder) object;
                BigDecimal[] overdueRates = getOverDueRates(ordOrder);

                if (ordOrder.getOrderType().equals("0")){
                    if(overdueDay <=3 ) {
                        shouldPayAmount = ordOrder.getAmountApply().multiply(overdueRates[0]).multiply(BigDecimal.valueOf(overdueDay)).setScale(2);
                    } else {
                        shouldPayAmount = ordOrder.getAmountApply().multiply(overdueRates[0]).multiply(BigDecimal.valueOf(3))
                                .add(ordOrder.getAmountApply().multiply(overdueRates[1]).multiply(BigDecimal.valueOf(overdueDay - 3))).setScale(2);
                    }
                } else {
                    if (overdueDay <=3 ) {
                        shouldPayAmount = ordOrder.getAmountApply().multiply(overdueRates[0]).multiply(BigDecimal.valueOf(overdueDay)).setScale(2);
                    } else {
                        shouldPayAmount = ordOrder.getAmountApply().multiply(overdueRates[0]).multiply(BigDecimal.valueOf(3))
                                .add(ordOrder.getAmountApply().multiply(overdueRates[1]).multiply(BigDecimal.valueOf(overdueDay - 3))).setScale(2);
                    }
                }
            }else if (object instanceof OrdBill){

                OrdBill bill = (OrdBill) object;
                shouldPayAmount = bill.getBillAmout().multiply(bill.getOverdueRate()).multiply(BigDecimal.valueOf(overdueDay)).setScale(2);
            }

        }
        return shouldPayAmount + "";
    }


    /**
     *   通过实际还款时间计算罚息  （针对分期产品的账单)
     *
     * */
    public BigDecimal calculatePenaltyFeeByRepayDaysForBills(OrdBill bill, int overdueDay) {

        BigDecimal shouldPayAmount = BigDecimal.valueOf(0);

        if (overdueDay >0){
            shouldPayAmount = bill.getBillAmout().multiply(bill.getOverdueRate()).multiply(BigDecimal.valueOf(overdueDay)).setScale(2);
        }
        return shouldPayAmount ;
    }
    /**
     *   ?????
     * */
    public String calculateDelayFee(OrdOrder ordOrder){

        BigDecimal shouldPayAmount = BigDecimal.valueOf(0);
        // ?????????
        try {
            if (ordOrder.getOrderType().equals("2") ){
                // ?? ?? ??????
                // ?OrdDelayRecord ???
                OrdDelayRecord record = new OrdDelayRecord();
                record.setOrderNo(ordOrder.getUuid());
                record.setDisabled(0);
                record.setUserUuid(ordOrder.getUserUuid());
                List<OrdDelayRecord> recordList = this.ordDelayRecordDao.scan(record);
                if(!CollectionUtils.isEmpty(recordList)){

                    OrdDelayRecord entity = recordList.get(0);
                    shouldPayAmount  = entity.getDelayFee();
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        return shouldPayAmount + "";
    }

    // 根据还款码的时间 计算还款金额
    public String calculateRepayAmountWithDate(OrdOrder ordOrder,Date repayDate) throws Exception{

        BigDecimal shouldPayAmount = BigDecimal.valueOf(0);
        int overdueDay = (int) DateUtils.daysBetween(DateUtils.formDate(ordOrder.getRefundTime(),"yyyy-MM-dd"),DateUtils.formDate(repayDate,"yyyy-MM-dd"));
        if (overdueDay >0){
            SysProduct sysProd = sysProductDao.getProductInfoIgnorDisabled(ordOrder.getProductUuid());
            // ??????????????+?? + ???

            // ??<=3??overdueRate1?>3??overdueRate2
            if(overdueDay <=3){
                // ????? = ????+  ????? + ?? + ????????*??????*??????
                shouldPayAmount =ordOrder.getAmountApply().add(ordOrder.getInterest()).add(sysProd.getOverdueFee()).add(ordOrder.getAmountApply().multiply(sysProd.getOverdueRate1()).multiply(BigDecimal.valueOf(overdueDay))).setScale(2);
            }else{

                shouldPayAmount =ordOrder.getAmountApply().add(ordOrder.getInterest())
                        .add(sysProd.getOverdueFee())
                        .add(ordOrder.getAmountApply().multiply(sysProd.getOverdueRate1()).multiply(BigDecimal.valueOf(3)))
                        .add(ordOrder.getAmountApply().multiply(sysProd.getOverdueRate2()).multiply(BigDecimal.valueOf(overdueDay - 3))).setScale(2);
            }
        }else {
            // ????
            shouldPayAmount = ordOrder.getAmountApply().add(ordOrder.getInterest());
        }

        //Janhsen: change 1.2 to 2 because max repayment overdue fee is 200%
        BigDecimal limit = ordOrder.getAmountApply().subtract(ordOrder.getServiceFee()).multiply(BigDecimal.valueOf(2)).setScale(2);
        if (shouldPayAmount.compareTo(limit) > 0 ){
            shouldPayAmount = limit;
        }
        return shouldPayAmount+"";
    }

        /**
         *   ??????
         * */
    public String calculateRepayAmount(OrdOrder ordOrder,String type){

        BigDecimal shouldPayAmount = BigDecimal.valueOf(0);
        // ?????????
        try {
            if (ordOrder.getOrderType().equals("0")||ordOrder.getOrderType().equals("2")){

                //????  ?? ??????
                if (!StringUtils.isEmpty(type)){
                    if (type.equals("1")){

                        shouldPayAmount = dealWithNum(ordOrder);
                    }else {
                        // ?????? ?OrdDelayRecord ???
                        OrdDelayRecord record = new OrdDelayRecord();
                        record.setOrderNo(ordOrder.getUuid());
                        record.setDisabled(0);
                        record.setUserUuid(ordOrder.getUserUuid());
                        List<OrdDelayRecord> recordList = this.ordDelayRecordDao.scan(record);
                       if(!CollectionUtils.isEmpty(recordList)){
//                        String repayAmount = StringUtils.formatMoney(Double.valueOf(this.repayService.calculateRepayAmount(order).replace(".00",""))).replaceAll(",",".");
//                        response.setRepayAmout(repayAmount);
                            if (!recordList.get(0).getRepayNum().equals(ordOrder.getAmountApply())){
                                OrdDelayRecord entity = recordList.get(0);
                                shouldPayAmount  =  entity.getRepayNum().add(entity.getInterest()).add(entity.getDelayFee()).add(new BigDecimal(entity.getOverDueFee())).add(new BigDecimal(entity.getPenaltyFee()));

                            }else {
                                shouldPayAmount = dealWithNum(ordOrder);
                            }

                        }
                    }
                }else {
                    shouldPayAmount = dealWithNum(ordOrder);
                }

            }else {
                //  ????  ?????????????)?
                int overdueDay = (int) DateUtils.daysBetween(DateUtils.formDate(ordOrder.getRefundTime(),"yyyy-MM-dd"),DateUtils.formDate(new Date(),"yyyy-MM-dd"));
                if (overdueDay >0){

                    BigDecimal delayFee = BigDecimal.ZERO;
                    if (ordOrder.getAmountApply().compareTo(BigDecimal.valueOf(500000)) <= 0){
                        delayFee = BigDecimal.valueOf(20000);
                    }else if (ordOrder.getAmountApply().compareTo(BigDecimal.valueOf(2000000)) > 0){
                        delayFee = BigDecimal.valueOf(60000);
                    }else {
                        delayFee = BigDecimal.valueOf(40000);
                    }

                    // ??<=3??overdueRate1?>3??overdueRate2
                    // SysProduct sysProd = sysProductDao.getProductInfoIgnorDisabled(ordOrder.getProductUuid());
                    BigDecimal[] overdueRates = getOverDueRates(ordOrder);
                    if(overdueDay <=3){
                        // ????? = ????+  ????? + ?? + ????????*??????*??????
                        shouldPayAmount = ordOrder.getAmountApply()
                                .add(delayFee)
                                .add(ordOrder.getAmountApply().multiply(overdueRates[0]).multiply(BigDecimal.valueOf(overdueDay))).setScale(2);

                    }else{

                        shouldPayAmount = ordOrder.getAmountApply()
                                .add(delayFee)
                                .add(ordOrder.getAmountApply().multiply(overdueRates[0]).multiply(BigDecimal.valueOf(3)))
                                .add(ordOrder.getAmountApply().multiply(overdueRates[1]).multiply(BigDecimal.valueOf(overdueDay - 3))).setScale(2);

                    }

                }else {
                    // ????
                    shouldPayAmount = ordOrder.getAmountApply();;
                }

            }

            //Janhsen: change 1.2 to 2 because max repayment overdue fee is 200%
            BigDecimal limit = ordOrder.getAmountApply().subtract(ordOrder.getServiceFee()).multiply(BigDecimal.valueOf(2)).setScale(2);
            if (shouldPayAmount.compareTo(limit) > 0 ){
                shouldPayAmount = limit;
            }

        } catch (ParseException e) {
            e.getStackTrace();
        } catch (Exception e) {
            e.getStackTrace();
        }
        return shouldPayAmount + "";
    }

    // 计算分期账单还款金额
    public String calculateBillRepayAmount(OrdBill bill){

        BigDecimal shouldPayAmount = BigDecimal.valueOf(0);
        try {

            int overdueDay = (int) DateUtils.daysBetween(DateUtils.formDate(bill.getRefundTime(),"yyyy-MM-dd"),DateUtils.formDate(new Date(),"yyyy-MM-dd"));
            if (overdueDay >0){
                // 逾期
                SysProduct sysProd = sysProductDao.getProductInfoIgnorDisabled(bill.getProductUuid());
                shouldPayAmount =bill.getBillAmout().add(bill.getInterest()).add(sysProd.getOverdueFee()).add(bill.getBillAmout().multiply(bill.getOverdueRate()).multiply(BigDecimal.valueOf(overdueDay))).setScale(2);

            }else {
                // 未逾期
                shouldPayAmount = bill.getBillAmout().add(bill.getInterest());
            }

            //Janhsen: change 1.2 to 2 because max repayment overdue fee is 200%

            //BigDecimal limit = bill.getBillAmout().subtract(bill.getBillAmout().multiply(BigDecimal.valueOf(0.006542)).multiply(BigDecimal.valueOf(30))).multiply(BigDecimal.valueOf(2)).setScale(2);
            
            OrdOrder order = ordService.getOrderByOrderNo(bill.getOrderNo());
            BigDecimal limit = bill.getBillAmout().subtract(order.getServiceFee().divide(BigDecimal.valueOf(order.getBorrowingTerm()), 0, RoundingMode.UP)).multiply(BigDecimal.valueOf(2));
            log.info("Limit:"+ limit);
            if (shouldPayAmount.compareTo(limit) > 0 ){
                shouldPayAmount = limit;
            }

        } catch (ParseException e) {
            e.getStackTrace();
        } catch (Exception e) {
            e.getStackTrace();
        }
        return shouldPayAmount + "";
    }

    /**
     * 通过实际还款时间计算应还款金额
     * @param ordOrder
     * @param overdueDay 实际还款时间差
     * @return
     */
    public String calculateShouldRepayAmount(OrdOrder ordOrder, int overdueDay){

        BigDecimal shouldPayAmount = BigDecimal.valueOf(0);
        try {
            if (ordOrder.getOrderType().equals("0")||ordOrder.getOrderType().equals("2")){

                shouldPayAmount = dealWithNumByRepayTime(ordOrder, overdueDay);
            } else {
                if (overdueDay > 0) {

                    BigDecimal delayFee = BigDecimal.ZERO;
                    if (ordOrder.getAmountApply().compareTo(BigDecimal.valueOf(500000)) <= 0){
                        delayFee = BigDecimal.valueOf(20000);
                    }else if (ordOrder.getAmountApply().compareTo(BigDecimal.valueOf(2000000)) > 0){
                        delayFee = BigDecimal.valueOf(60000);
                    }else {
                        delayFee = BigDecimal.valueOf(40000);
                    }

                    // SysProduct sysProd = sysProductDao.getProductInfoIgnorDisabled(ordOrder.getProductUuid());
                    BigDecimal[] overdueRates = getOverDueRates(ordOrder);

                    if (overdueDay <=3) {
                        shouldPayAmount = ordOrder.getAmountApply()
                                .add(delayFee)
                                .add(ordOrder.getAmountApply().multiply(overdueRates[0]).multiply(BigDecimal.valueOf(overdueDay))).setScale(2);

                    } else {

                        shouldPayAmount = ordOrder.getAmountApply()
                                .add(delayFee)
                                .add(ordOrder.getAmountApply().multiply(overdueRates[0]).multiply(BigDecimal.valueOf(3)))
                                .add(ordOrder.getAmountApply().multiply(overdueRates[1]).multiply(BigDecimal.valueOf(overdueDay - 3))).setScale(2);

                    }
                } else {
                    shouldPayAmount = ordOrder.getAmountApply();
                }

            }
            //Janhsen: change 1.2 to 2 because max repayment overdue fee is 200%
            BigDecimal limit = ordOrder.getAmountApply().subtract(ordOrder.getServiceFee()).multiply(BigDecimal.valueOf(2)).setScale(2);
            if (shouldPayAmount.compareTo(limit) > 0 ){
                shouldPayAmount = limit;
            }
        } catch (ParseException e) {
            e.getStackTrace();
        } catch (Exception e) {
            e.getStackTrace();
        }
        return shouldPayAmount + "";
    }

    // 已还款订单计算还款金额
    public String calculateRepayAmountWithRepayOrder(OrdOrder ordOrder){

        BigDecimal shouldPayAmount = BigDecimal.valueOf(0);
        // ?????????
        try {
            //  ????  ?????????????)?
            int overdueDay = (int) DateUtils.daysBetween(DateUtils.formDate(ordOrder.getRefundTime(),"yyyy-MM-dd"),DateUtils.formDate(ordOrder.getActualRefundTime(),"yyyy-MM-dd"));
            if (overdueDay >0){

                BigDecimal delayFee = BigDecimal.ZERO;
                if (ordOrder.getAmountApply().compareTo(BigDecimal.valueOf(500000)) <= 0){
                    delayFee = BigDecimal.valueOf(20000);
                }else if (ordOrder.getAmountApply().compareTo(BigDecimal.valueOf(2000000)) > 0){
                    delayFee = BigDecimal.valueOf(60000);
                }else {
                    delayFee = BigDecimal.valueOf(40000);
                }

                // SysProduct sysProd = sysProductDao.getProductInfoIgnorDisabled(ordOrder.getProductUuid());
                // ??<=3??overdueRate1?>3??overdueRate2

                BigDecimal[] overdueRates = getOverDueRates(ordOrder);
                if(overdueDay <=3){
                    // ????? = ????+  ????? + ?? + ????????*??????*??????
                    shouldPayAmount = ordOrder.getAmountApply()
                            .add(delayFee)
                            .add(ordOrder.getAmountApply().multiply(overdueRates[0]).multiply(BigDecimal.valueOf(overdueDay))).setScale(2);

                }else{

                    shouldPayAmount = ordOrder.getAmountApply()
                            .add(delayFee)
                            .add(ordOrder.getAmountApply().multiply(overdueRates[0]).multiply(BigDecimal.valueOf(3)))
                            .add(ordOrder.getAmountApply().multiply(overdueRates[1]).multiply(BigDecimal.valueOf(overdueDay - 3))).setScale(2);

                }
            }else {
                // ????
                shouldPayAmount = ordOrder.getAmountApply();
            }



        } catch (ParseException e) {
            e.getStackTrace();
        } catch (Exception e) {
            e.getStackTrace();
        }
        return shouldPayAmount + "";

    }

    public BigDecimal dealWithNum(OrdOrder ordOrder) throws Exception{
        BigDecimal shouldPayAmount = BigDecimal.valueOf(0);
        int overdueDay = (int) DateUtils.daysBetween(DateUtils.formDate(ordOrder.getRefundTime(),"yyyy-MM-dd"),DateUtils.formDate(new Date(),"yyyy-MM-dd"));
        if (overdueDay >0){
            SysProduct sysProd = sysProductDao.getProductInfoIgnorDisabled(ordOrder.getProductUuid());
            // ??????????????+?? + ???

            // ??<=3??overdueRate1?>3??overdueRate2
            if(overdueDay <=3){
                // ????? = ????+  ????? + ?? + ????????*??????*??????
                shouldPayAmount =ordOrder.getAmountApply().add(ordOrder.getInterest()).add(sysProd.getOverdueFee()).add(ordOrder.getAmountApply().multiply(sysProd.getOverdueRate1()).multiply(BigDecimal.valueOf(overdueDay))).setScale(2);
            }else{

                shouldPayAmount =ordOrder.getAmountApply().add(ordOrder.getInterest())
                        .add(sysProd.getOverdueFee())
                        .add(ordOrder.getAmountApply().multiply(sysProd.getOverdueRate1()).multiply(BigDecimal.valueOf(3)))
                        .add(ordOrder.getAmountApply().multiply(sysProd.getOverdueRate2()).multiply(BigDecimal.valueOf(overdueDay - 3))).setScale(2);
            }
        }else {
            // ????
            shouldPayAmount = ordOrder.getAmountApply().add(ordOrder.getInterest());
        }

        //Janhsen: change 1.2 to 2 because max repayment overdue fee is 200%
        BigDecimal limit = ordOrder.getAmountApply().subtract(ordOrder.getServiceFee()).multiply(BigDecimal.valueOf(2)).setScale(2);
        if (shouldPayAmount.compareTo(limit) > 0 ){
            shouldPayAmount = limit;
        }
        return shouldPayAmount;
    }

    public BigDecimal dealWithNumByRepayTime(OrdOrder ordOrder, int overdueDay) throws Exception{

        BigDecimal shouldPayAmount ;
        if (overdueDay >0){
            SysProduct sysProd = sysProductDao.getProductInfoIgnorDisabled(ordOrder.getProductUuid());
            if(overdueDay <=3){
                shouldPayAmount =ordOrder.getAmountApply().add(ordOrder.getInterest()).add(sysProd.getOverdueFee()).add(ordOrder.getAmountApply().multiply(sysProd.getOverdueRate1()).multiply(BigDecimal.valueOf(overdueDay))).setScale(2);
            }else{

                shouldPayAmount =ordOrder.getAmountApply().add(ordOrder.getInterest())
                        .add(sysProd.getOverdueFee())
                        .add(ordOrder.getAmountApply().multiply(sysProd.getOverdueRate1()).multiply(BigDecimal.valueOf(3)))
                        .add(ordOrder.getAmountApply().multiply(sysProd.getOverdueRate2()).multiply(BigDecimal.valueOf(overdueDay - 3))).setScale(2);
            }
        }else {
            // ????
            shouldPayAmount = ordOrder.getAmountApply().add(ordOrder.getInterest());
        }

        //Janhsen: change 1.2 to 2 because max repayment overdue fee is 200%
        BigDecimal limit = ordOrder.getAmountApply().subtract(ordOrder.getServiceFee()).multiply(BigDecimal.valueOf(2)).setScale(2);
        if (shouldPayAmount.compareTo(limit) > 0 ){
            shouldPayAmount = limit;
        }
        return shouldPayAmount;
    }

    public BigDecimal[] getOverDueRates(OrdOrder ordOrder){

        SysProduct sysProd = null;
        if(!ordOrder.getProductUuid().isEmpty()){
            sysProd = sysProductDao.getProductInfoIgnorDisabled(ordOrder.getProductUuid());
        }
        else{
            sysProd = sysProductDao.getBlankProductInfoIgnoreDisabled(ordOrder.getAmountApply());
        }

        BigDecimal[] overDueRates = new BigDecimal[2];
        overDueRates[0] = sysProd.getOverdueRate1();
        overDueRates[1] = sysProd.getOverdueRate2();

        log.info("orderNo: " + ordOrder.getUuid() + " productUuid: " + ordOrder.getProductUuid() + " overduerate1: " + overDueRates[0] + " overduerate2: " + overDueRates[1]);

        return overDueRates;
    }


}
