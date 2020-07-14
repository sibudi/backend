package com.yqg.service;

import com.yqg.activity.entity.ActivityAccountRecord;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.constants.SysParamContants;
import com.yqg.common.enums.order.OrdRepayAmountRecordStatusEnum;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.system.LoanDisburseTypeEnum;
import com.yqg.common.enums.system.SysThirdLogsEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.OrderNoCreator;
import com.yqg.order.dao.*;
import com.yqg.order.entity.*;
import com.yqg.request.ManualRepayOrderRequest;
import com.yqg.service.externalChannel.service.Cash2OrderService;
import com.yqg.service.externalChannel.service.CheetahOrderService;
import com.yqg.service.externalChannel.utils.Cash2OrdStatusEnum;
import com.yqg.service.externalChannel.utils.CheetahOrdStatusEnum;
import com.yqg.service.loan.response.CheckRepayResponse;
import com.yqg.service.loan.response.LoanResponse;
import com.yqg.service.loan.service.LoanInfoService;
import com.yqg.service.order.OrdService;
import com.yqg.service.p2p.response.P2PResponse;
import com.yqg.service.p2p.response.P2PResponseDetail;
import com.yqg.service.p2p.service.P2PService;
import com.yqg.service.pay.RepayService;
import com.yqg.service.system.service.SysParamService;
import com.yqg.service.system.service.SysThirdLogsService;
import com.yqg.service.third.kaBinCheck.KaBinCheckService;
import com.yqg.service.third.sms.SmsServiceUtil;
import com.yqg.service.user.service.UsrBankService;
import com.yqg.service.user.service.UsrService;
import com.yqg.user.entity.UsrBank;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanghuaizhou on 2017/12/3.
 */
@Service
@Slf4j
public class PayService {

    @Autowired
    private SysThirdLogsService sysThirdLogsService;
    @Autowired
    private SysParamService sysParamService;
    @Autowired
    private OrdDao ordDao;
    @Autowired
    private UsrService usrService;
    @Autowired
    private OrdService ordService;
    @Autowired
    private Cash2OrderService cash2OrderService;

    @Autowired
    private RepayService repayService;
    @Autowired
    private KaBinCheckService kaBinCheckService;
    @Autowired
    private P2PService p2PService;

    // 放款url (用与借款订单放款，需要传入服务费参数)
    @Value("${pay.loanUrl}")
    private String COMMIT_LOAN_URL;

    // 放款url(用户服务费 邀请好友放款)
    @Value("${pay.loanExpenseUrl}")
    private String COMMIT_SERVICE_LOAN_URL;

    // 查询放款 url
    @Value("${pay.cheakLoanUrl}")
    private String QUERY_LOAN_URL;
    // 查询还款 url
    @Value("${pay.cheakRepayUrl}")
    private String QUERY_REPAY_URL;
    @Value("${pay.token}")
    private String PAY_TOKEN;

    @Autowired
    private OrdPaymentCodeDao ordPaymentCodeDao;
    @Autowired
    private OrdRepayAmoutRecordDao ordRepayAmoutRecordDao;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    protected UsrBankService usrBankService;
    @Autowired
    private LoanInfoService loanInfoService;
    @Autowired
    private SmsServiceUtil smsServiceUtil;
    @Autowired
    private CheetahOrderService cheetahOrderService;
    @Autowired
    LoanCoverChargeService loanCoverChargeService;
    @Autowired
    private OkHttpClient httpClient;

    @Autowired
    private OrdBillDao ordBillDao;

    /**
     *   放款
     * */
    public LoanResponse commitPay(OrdOrder order, UsrUser usrUser,UsrBank usrBank,String disburseChannel) throws Exception{

        //  记录打款金额   每天限额 3，000，000，000
        String loanNow = this.sysParamService.getSysParamValue(SysParamContants.LOAN_ACCOUNT_NOW);
        log.info("当前放款金额为:{}",loanNow);
        this.sysParamService.setSysParamValue(SysParamContants.LOAN_ACCOUNT_NOW,new BigDecimal(loanNow).add(order.getAmountApply()).toString());


        LoanResponse loanResponse = new LoanResponse();

        String loanAmount = loanInfoService.calculateLoanAmount(order);

        //  生成前置服务费订单
        String serviceOrderNo =  loanCoverChargeService.createServiceOrder(order,disburseChannel);

        String userBankName = "";
        if (usrBank.getRemark().equals("姓名更正为银行返回的姓名")){
            userBankName = usrBank.getBankCardName();
        }else {
            userBankName = usrBank.getBankCardName().replaceAll("\\pP","").replaceAll("`","");
        }

        OrdOrder newestOrder = this.ordService.getOrderByOrderNo(order.getUuid());

        // HTTP请求参数
        Map<String,String> contents = new HashMap<String, String>();
        contents.put("externalId",order.getUuid()); // 订单号
        contents.put("subExternalId",serviceOrderNo); // 服务费订单号
        contents.put("bankCode",usrBank.getBankCode()); // 银行卡code
        contents.put("accountNumber",usrBank.getBankNumberNo()); // 银行卡号
        contents.put("accountHolderName",userBankName); // 银行卡开户人姓名
        contents.put("creditAmount",loanAmount); // 实际放款金额
        contents.put("totalAmount",order.getAmountApply()+""); // 订单总金额
        contents.put("serviceFee",newestOrder.getServiceFee()+""); // 订单服务费
        contents.put("description","DO-IT"); // 描述
        contents.put("disburseType","PAYDAYLOAN"); // 支付类型
        contents.put("disburseChannel",disburseChannel); // 支付通道


        RequestBody requestBody = new FormBody.Builder()
                .add("externalId",order.getUuid())  // 订单号
                .add("subExternalId",serviceOrderNo)  // 服务费订单号
                .add("bankCode",usrBank.getBankCode())  // 银行卡code
                .add("accountNumber",usrBank.getBankNumberNo()) // 银行卡号
                .add("accountHolderName",userBankName) // 银行卡开户人姓名
                .add("creditAmount",loanAmount) // 打款金额
                .add("totalAmount",order.getAmountApply()+"") // 订单总金额
                .add("serviceFee",newestOrder.getServiceFee()+"") // 订单服务费
                .add("description","Disbursed from Doit")
                .add("disburseType",LoanDisburseTypeEnum.PAYDAYLOAN.getType())
                .add("disburseChannel",disburseChannel)
                .build();

        log.info("该笔订单放款金额为"+ loanAmount);

        Request request = new Request.Builder()
                .url(COMMIT_LOAN_URL)
                .post(requestBody)
                .header("X-AUTH-TOKEN", PAY_TOKEN)
                .build();

        // 请求数据落库，SysThirdLogs
        this.sysThirdLogsService.addSysThirdLogs(order.getUuid(),usrUser.getUuid(), SysThirdLogsEnum.COMMIT_LOAN.getCode(),0, JsonUtils.serialize(contents),null);

        Response response = httpClient.newCall(request).execute();
        if(response.isSuccessful())
        {
            String  responseStr = response.body().string();
            // 放款响应
            log.info("放款 请求后返回:{}", JsonUtils.serialize(responseStr));
            loanResponse = JsonUtils.deserialize(responseStr,LoanResponse.class);
            // 响应数据落库，sysThirdLogs
            sysThirdLogsService.addSysThirdLogs(order.getUuid(),usrUser.getUuid(), SysThirdLogsEnum.COMMIT_LOAN.getCode(),0,null,responseStr);
        }

        return loanResponse;
    }


    /**
     *   活动放款
     * */
    public LoanResponse commitPayWithActivity(ActivityAccountRecord record, UsrUser usrUser, UsrBank usrBank) throws Exception{

        LoanResponse loanResponse = new LoanResponse();

        // HTTP请求参数
        Map<String,String> contents = new HashMap<String, String>();
        contents.put("externalId",record.getUuid()); // 订单号
        contents.put("bankCode",usrBank.getBankCode()); // 银行卡code
        contents.put("accountNimber",usrBank.getBankNumberNo()); // 银行卡号
        contents.put("accountHolderName",usrBank.getBankCardName().replaceAll("\\pP","")); // 银行卡开户人姓名
        contents.put("amount",String.valueOf(record.getAmount())); // 打款金额
        contents.put("description","DO-IT"); // 描述
        contents.put("disburseType", LoanDisburseTypeEnum.BONUS.getType()); // 支付类型
        contents.put("disburseChannel","XENDIT"); // 支付通道


        RequestBody requestBody = new FormBody.Builder()
                .add("externalId",record.getUuid())  // ???
                .add("bankCode",usrBank.getBankCode())  // ???code
                .add("accountNumber",usrBank.getBankNumberNo()) // ????
                .add("accountHolderName",usrBank.getBankCardName()) // ????????
                .add("amount",String.valueOf(record.getAmount())) // ????
                .add("description","Disbursed from Doit")
                .add("disburseType","PAYDAYLOAN")
                .add("disburseChannel","XENDIT")
                .build();

        log.info("该笔订单放款金额为"+String.valueOf(record.getAmount()));

        Request request = new Request.Builder()
                .url(COMMIT_SERVICE_LOAN_URL)
                .post(requestBody)
                .header("X-AUTH-TOKEN", PAY_TOKEN)
                .build();

        // 请求数据落库，SysThirdLogs
        this.sysThirdLogsService.addSysThirdLogs(record.getUuid(),usrUser.getUuid(), SysThirdLogsEnum.COMMIT_LOAN.getCode(),0, JsonUtils.serialize(contents),null);

        Response response = httpClient.newCall(request).execute();
        if(response.isSuccessful())
        {
            String  responseStr = response.body().string();
            // 放款响应
            log.info("放款 请求后返回:{}", JsonUtils.serialize(responseStr));
            loanResponse = JsonUtils.deserialize(responseStr,LoanResponse.class);
            // 响应数据落库，sysThirdLogs
            sysThirdLogsService.addSysThirdLogs(record.getUuid(),usrUser.getUuid(), SysThirdLogsEnum.COMMIT_LOAN.getCode(),0,null,responseStr);
        }

        return loanResponse;
    }


    /**
     *   查询放款
     * */
    public LoanResponse queryLoanResult(String orderNo, String userUuid) throws Exception{

        LoanResponse loanResponse = new LoanResponse();

        Request request = new Request.Builder()
                .url(QUERY_LOAN_URL+orderNo)
                .header("X-AUTH-TOKEN", PAY_TOKEN)
                .build();

        // 请求数据落库，SysThirdLogs
        this.sysThirdLogsService.addSysThirdLogs(orderNo,userUuid, SysThirdLogsEnum.CHEAK_LOAN.getCode(),0, orderNo,null);

        Response response = httpClient.newCall(request).execute();
        if(response.isSuccessful())
        {
            String  responseStr = response.body().string();
            // 查询放款响应
            log.info("查询放款 请求后返回:{}", JsonUtils.serialize(responseStr));
            loanResponse = JsonUtils.deserialize(responseStr,LoanResponse.class);
            // 响应数据落库，sysThirdLogs
            sysThirdLogsService.addSysThirdLogs(orderNo,userUuid, SysThirdLogsEnum.CHEAK_LOAN.getCode(),0,null,responseStr);
        }

        return loanResponse;
    }





    /**
     *  查询代还款状态 订单，去第三方查询还款，同步状态 如果成功 COMPLETED 改成还款成功
     * */
    public void cheakRepayOrder(Integer num) {

        //开关
        String sysParamValue = this.sysParamService.getSysParamValue(SysParamContants.CHEAK_REPAY_OFF_NO);

        if (!StringUtils.isEmpty(sysParamValue) && sysParamValue.equals("1") ){
//            log.info("=====================》查询还款处理中 订单,同步状态 开关已打开《====================");
            //   还款处理中 订单
            List<OrdOrder> orderOrders = this.ordDao.getInRepayOrderListById(num);
//            List<OrdOrder> orderOrders = this.ordDao.getInRepayOrderListByIdWithFlowMrker(num);

            this.caculate(orderOrders);
        }else {
            log.info("=====================》查询还款处理中,同步状态 开关已关闭《====================");
        }
    }

    /**
     * 开始查询
     */
    private void caculate(List<OrdOrder> orderOrders) {

        if (CollectionUtils.isEmpty(orderOrders)) {
            log.info("=============查询列表为空=======================");
            return;
        }
        for (OrdOrder order : orderOrders) {
//            if(p2PService.isP2PIssuedLoan(order.getUuid())){
////              ahalim: P2P - For now disable P2P
////              rizky 2020/05/05 re enable P2p
//                p2pPaymentCheck(order);
//            }else{
                try {
                    normalPaymentCheck(order);
                }catch (Exception e){
                    log.error("查询代还款订单异常，订单号："+order.getUuid(),e);
                }
//            }

        }
    }

    /**
     * 查询待还款账单
     */
    private void caculateBill(List<OrdBill> bills) {

        if (CollectionUtils.isEmpty(bills)) {
            log.info("=============查询列表为空=======================");
            return;
        }
        for (OrdBill bill : bills) {

            try {
                normalPaymentCheck(bill);
            }catch (Exception e){
                log.error("查询代还款帐单异常，帐单号："+bill.getUuid(),e);
            }

        }
    }

    private void p2pPaymentCheck(OrdOrder order) {
        try {
            P2PResponse statusResponse = p2PService.checkOrderInfo(order);
            if (P2PResponse.isSuccessResponse(statusResponse) && statusResponse.getData() != null) {
                P2PResponseDetail.RepaySuccessStatusDetail detail = JsonUtils.deserialize(JsonUtils.serialize(statusResponse.getData()),
                        P2PResponseDetail.RepaySuccessStatusDetail.class);
                p2PService.handleP2PLoanStatus(order.getUuid(), detail.getStatus(), detail);
            }
        } catch (Exception e) {
            log.error("get p2p loan status error，orderNo=" + order.getUuid(), e);
        }
    }

    private void normalPaymentCheck(Object object) {

        if (object instanceof OrdOrder){
            OrdOrder order = (OrdOrder) object;
            // 再去paymentCode表查询该订单最近十天有没有生成还款code，如果有再去第三方cheak
            List<OrdPaymentCode> scanList = this.ordPaymentCodeDao.getOrderPaymentCodeByOrderNo(order.getUuid());
            if (CollectionUtils.isEmpty(scanList)) {
                return;
            }
            // 该订单有还款码
            // 查询还款处理中 订单在第三方状态
            UsrUser userUser = this.usrService.getUserByUuid(order.getUserUuid());
            CheckRepayResponse response = this.cheakRepay(order.getUuid(), userUser);
            if (response != null) {
                if (response.getCode() == null){
                    log.error("待还款订单号查询返回无code,订单号："+order.getUuid());
                    log.info(JsonUtils.serialize(response));
                }
                if (response.getCode().equals("0")) {
                    if (response.getDepositStatus().equals("COMPLETED")) {
                        // 还款成功
                        if (order.getThirdType() == 1){
                            this.cash2OrderService.ordStatusFeedback(order, Cash2OrdStatusEnum.REPAY_SUCCESS);
                        }else if (order.getThirdType() == 2) {
                            // 猎豹金融 cheetah
                            this.cheetahOrderService.ordStatusFeedback(order, CheetahOrdStatusEnum.REPAY_SUCCESS);
                        }
                        loanInfoService.repaymentSuccess(order,response);
                    }
                }
            }

        }else if (object instanceof OrdBill){
            OrdBill bill = (OrdBill) object;
            // 再去paymentCode表查询该订单最近十天有没有生成还款code，如果有再去第三方cheak
            List<OrdPaymentCode> scanList = this.ordPaymentCodeDao.getOrderPaymentCodeByOrderNo(bill.getUuid());
            if (CollectionUtils.isEmpty(scanList)) {
                return;
            }
            // 该订单有还款码
            // 查询还款处理中 订单在第三方状态
            UsrUser userUser = this.usrService.getUserByUuid(bill.getUserUuid());
            CheckRepayResponse response = this.cheakRepay(bill.getUuid(), userUser);
            if (response != null) {
                if (response.getCode() == null){
                    log.error("待还款账单号查询返回无code,订单号："+bill.getUuid());
                    log.info(JsonUtils.serialize(response));
                }
                if (response.getCode().equals("0")) {
                    if (response.getDepositStatus().equals("COMPLETED")) {
                        loanInfoService.repaymentSuccess(bill,response);
                    }
                }
            }
        }



    }

        /**
         *    在第三方查询还款处理中 订单
         * */
    public CheckRepayResponse cheakRepay(String orderNo, UsrUser usrUser) {

//        log.info("查询还款处理中 订单号:{}", order.getUuid());
        CheckRepayResponse repayResponse = new CheckRepayResponse();

        try {

            Request request = new Request.Builder()
                    .url(QUERY_REPAY_URL+orderNo)
                    .header("X-AUTH-TOKEN", PAY_TOKEN)
                    .build();


            Response response = httpClient.newCall(request).execute();
            if(response.isSuccessful())
            {
                String  responseStr = response.body().string();
                // 查询还款处理中订单状态 响应
                log.info("查询还款处理中订单状态 请求后返回:{}", JsonUtils.serialize(responseStr));
                repayResponse = JsonUtils.deserialize(responseStr,CheckRepayResponse.class);

                if (repayResponse != null){
                    if (repayResponse.getCode().equals("0")){
                        if (!repayResponse.getDepositStatus().equals("PENDING")){
                            // 请求数据落库，SysThirdLogs
                            this.sysThirdLogsService.addSysThirdLogs(orderNo,usrUser.getUuid(), SysThirdLogsEnum.CHEAK_REPAY.getCode(),0, orderNo,null);
                            // 响应数据落库，sysThirdLogs
                            sysThirdLogsService.addSysThirdLogs(orderNo,usrUser.getUuid(), SysThirdLogsEnum.CHEAK_REPAY.getCode(),0,null,responseStr);
                        }
                    }
                }

            }
        } catch (Exception e) {
            log.error("查询还款处理中订单状态异常,单号: "+orderNo ,e);
        }
        return repayResponse;
    }



    /**
     *   手动处理还款订单 除非用户直接转账到对公账号 否则慎用
     * */
    @Transactional
    public void manualOperationRepayOrder(ManualRepayOrderRequest repayOrderRequest) throws Exception{

        OrdOrder order = new OrdOrder();
        order.setUuid(repayOrderRequest.getOrderNo());
        order.setUserUuid(repayOrderRequest.getUserUuid());
//        order.setUuid("011801292234536460");
//        order.setUserUuid("11195268C5BD47899965FEC576FB4EFE");

        List<OrdOrder> scanList = this.ordDao.scan(order);
        if (!CollectionUtils.isEmpty(scanList)){
            OrdOrder dealOrder = scanList.get(0);

            // 订单状态必须是代还款
            if (dealOrder.getStatus() == OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode() ||
                    dealOrder.getStatus() == OrdStateEnum.RESOLVING_OVERDUE.getCode() ){

                if (dealOrder.getStatus() == OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode()){
                    // 说明是正常还款
                    log.info("正常订单 实际还款成功=====================》订单号："+dealOrder.getUuid());
                    OrdOrder entity = new OrdOrder();
                    entity.setUuid(dealOrder.getUuid());
                    entity.setStatus(OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode());
                    entity.setActualRefundTime(new Date());
                    entity.setUpdateTime(new Date());
                    this.ordService.updateOrder(entity);

                    dealOrder.setStatus(OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode());
                    dealOrder.setUpdateTime(new Date());
                    dealOrder.setActualRefundTime(new Date());
                    this.ordService.addOrderHistory(dealOrder);

                }else {
                    log.info("逾期订单 实际还款成功=====================》订单号："+dealOrder.getUuid());
                    OrdOrder entity = new OrdOrder();
                    entity.setUuid(dealOrder.getUuid());
                    entity.setStatus(OrdStateEnum.RESOLVED_OVERDUE.getCode());
                    entity.setActualRefundTime(new Date());
                    entity.setUpdateTime(new Date());
                    this.ordService.updateOrder(entity);

                    dealOrder.setStatus(OrdStateEnum.RESOLVED_OVERDUE.getCode());
                    dealOrder.setActualRefundTime(new Date());
                    dealOrder.setActualRefundTime(new Date());
                    this.ordService.addOrderHistory(dealOrder);

                }

                OrdRepayAmoutRecord record = new OrdRepayAmoutRecord();
                record.setOrderNo(dealOrder.getUuid());
                record.setUserUuid(dealOrder.getUserUuid());
                record.setRepayMethod("MANUAL");
                record.setTransactionId("");
                record.setActualRepayAmout(repayService.calculateRepayAmount(dealOrder,"1"));
                //record.setInterest(dealOrder.getInterest()+"");
                //Overdue service fee regardless overdue date
                //record.setOverDueFee(repayService.calculateOverDueFee(dealOrder));
                record.setOverDueFee("0");
                //Actual overdue fee based on overdue date, without limit.
                //Currently the penaltyfee in ordRepayAmoutRecord is actual overdue fee without limit
                record.setPenaltyFee(repayService.calculatePenaltyFee(dealOrder));
                record.setActualDisbursedAmount(new BigDecimal("".equals(dealOrder.getApprovedAmount()) ? "0.00" : dealOrder.getApprovedAmount()));
                BigDecimal interestRatio = new BigDecimal("0.001");     //0.1%
                //Interest is calculated based on amount apply
                BigDecimal interest = dealOrder.getAmountApply().multiply(interestRatio).multiply(BigDecimal.valueOf(dealOrder.getBorrowingTerm()));
                //Actual serviceFee is ordOrder serviceFee - Interest
                BigDecimal serviceFee = dealOrder.getServiceFee().subtract(interest);
                record.setServiceFee(serviceFee);
                record.setInterest(interest.toString());
                record.setStatus(OrdRepayAmountRecordStatusEnum.WAITING_REPAYMENT_TO_RDN.toString());
                record.setRepayChannel("3");
                this.ordRepayAmoutRecordDao.insert(record);

            }else {
                log.error("订单状态异常");
                throw new ServiceException(ExceptionEnum.ORDER_STATES_ERROR);
            }

        }else {

            log.error("订单不存在");
            throw new ServiceException(ExceptionEnum.ORDER_NOT_FOUND);
        }

    }


//    public void dealWithOrderWithProblem(){
//
//        OrdOrder o = new OrdOrder();
//        o.setUuid("011803141351262940");
//        List <OrdOrder> orderList = this.ordDao.scan(o);
//        OrdOrder order = orderList.get(0);
//
//        // ??????
//        OrdOrder newOrder = new OrdOrder();
//        newOrder.setChannel(order.getChannel());// ????
//        String newOrderNo = OrderNoCreator.createOrderNo();
//        newOrder.setUuid(newOrderNo);
//        newOrder.setCreateTime(new Date());
//        newOrder.setUpdateTime(new Date());
//        newOrder.setLendingTime(new Date());
//        newOrder.setChannel(order.getChannel());
//        newOrder.setPayChannel(order.getPayChannel());
//        newOrder.setBorrowingCount(order.getBorrowingCount());
//        newOrder.setOrderPositionId(order.getOrderPositionId());
//        newOrder.setUserBankUuid(order.getUserBankUuid());
//        newOrder.setOrderStep(order.getOrderStep());
//        newOrder.setStatus(OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode());
//        newOrder.setInterest(BigDecimal.valueOf(0));// ??????? ??
//        newOrder.setUserUuid(order.getUserUuid());// ????uuid
//        newOrder.setApplyTime(new Date());// ????
//        newOrder.setAmountApply(new BigDecimal(500000)
//        );// ????
//        Integer day =  7;
//        newOrder.setBorrowingTerm(day);// ????
//        newOrder.setRefundTime(new Date(new Date().getTime() + (day - 1) * 24 * 3600000));
//        newOrder.setServiceFee(BigDecimal.valueOf(0));// ???
//        newOrder.setOrderType("1");
//        this.ordDao.insert(newOrder);
//
//        // ?????? ??????
//        OrdOrder entity = new OrdOrder();
//        entity.setUuid(order.getUuid());
//        entity.setUpdateTime(new Date());
//        entity.setOrderType("2");
//        this.ordDao.update(entity);
//
//        OrdDelayRecord update = new OrdDelayRecord();
//        update.setUuid("7E0424B3D6F947369A58EACBB236F5EF");
//        update.setType("2");
//        update.setDelayOrderNo(newOrderNo);
//        update.setUpdateTime(new Date());
//        this.ordDelayRecordDao.update(update);
//
//    }

    // 处理今日异常订单 -20190225
    public void checkOrdLoanFaildToday(){

        List<OrdOrder> loanFalidList = this.ordDao.getOrderWithLoanFaild();
        if (!CollectionUtils.isEmpty(loanFalidList)){
            log.info("打款失败的订单总个数为："+loanFalidList.size());
            for (OrdOrder order:loanFalidList){
                log.info("本次查询的打款订单号"+order.getUuid());
                //  再次确认订单状态， 如果不是打款失败，跳出
                if (order.getStatus() != OrdStateEnum.LOAN_FAILD.getCode()) {
                    continue;
                }
                try {
                    //1.使淼科那边的订单号无效化
                    this.usrBankService.inactiveOrder(order);
                    //2.清除redis里面的放款记录(为了相同订单号能够继续打款 删除之前在redis里面的order)
                    this.redisClient.del(RedisContants.ORDER_LOAN_LOCK_NEW + order.getUuid());
                    //3.更改订单状态为待放款

                    OrdOrder entity = new OrdOrder();
                    entity.setUuid(order.getUuid());
                    entity.setStatus(OrdStateEnum.LOANING.getCode());
                    entity.setApplyTime(new Date());
                    entity.setRemark("");
                    entity.setUpdateTime(new Date());
                    this.ordService.updateOrder(entity);

                    if (order.getStatus() != OrdStateEnum.LOANING.getCode()){

                        order.setStatus(OrdStateEnum.LOANING.getCode());
                        order.setApplyTime(new Date());
                        order.setUpdateTime(new Date());
                        this.ordService.addOrderHistory(order);
                    }

                    log.info("重新回到打款队列，订单号：{}",order.getUuid());
                } catch (Exception e) {
                    log.error("查询订单异常,单号:"+ order.getUuid(), e);
                }
            }
        }

    }

    // 每天定时处理打款失败的订单
    public void checkOrdLoanFaild(){

        List<OrdOrder> loanFalidList = this.ordDao.getUserOrderLoanFaildList();
        if (!CollectionUtils.isEmpty(loanFalidList)){
            log.info("打款失败的订单总个数为："+loanFalidList.size());
            for (OrdOrder order:loanFalidList){
                log.info("本次查询的打款订单号"+order.getUuid());
                //  再次确认订单状态， 如果不是打款失败，跳出
                if (order.getStatus() != OrdStateEnum.LOAN_FAILD.getCode()) {
                    continue;
                }

                // 如果有订单状态为 是待审核 待打款 待还款 待外呼的订单 不允许继续下单
                List<OrdOrder> processingOrderList = this.ordDao.canReloanOrder(order.getUserUuid());
                if (!CollectionUtils.isEmpty(processingOrderList)){
                    log.error("有未处理完的订单！");
                    continue;
                }

                UsrUser user = this.usrService.getUserByUuid(order.getUserUuid());
                try {
                    LoanResponse response = queryLoanResult(order.getUuid(), user.getUuid());

                    if (response != null){

                        if (response.getCode().equals("0")||response.getCode().equals("1")){
                            if (response.getDisburseStatus().equals("FAILED")){

                                log.info("放款失败原因：{}",response.getErrorMessage());
                                if (response.getErrorCode().equals("SWITCHING_NETWORK_ERROR")||
                                        response.getErrorCode().equals("REJECTED_BY_BANK") ||
                                        (response.getErrorCode().equals("16") &&response.getErrorMessage().equals("REACHED AMOUNT LIMIT"))||
                                        (response.getErrorCode().equals("15") &&response.getErrorMessage().equals("EXCEEDED NUMBER LIMIT"))||
                                        (response.getErrorCode().equals("24") &&response.getErrorMessage().equals("EXCEEDS FUNDS AVAILABLE"))||
//                                        (response.getErrorCode().equals("38") &&response.getErrorMessage().equals("Transaction Failed, because the Reference No already used"))||
                                        (response.getErrorCode().equals("86") &&response.getErrorMessage().equals("TRANSACTION COULD NOT BE PROCESSED"))||
                                        (response.getErrorCode().equals("10") &&response.getErrorMessage().equals("TRANSACTION COULD NOT BE PROCESSED"))||
                                        (response.getErrorCode().equals("23") &&response.getErrorMessage().equals("ACCOUNT ALREADY NSF"))||
                                        (response.getErrorCode().equals("ESB-82-019") &&response.getErrorMessage().equals("Insufficient fund"))||
//                                        (response.getErrorCode().equals("009") &&response.getErrorMessage().equals("Error in processing request"))||
//                                        response.getErrorCode().equals("TEMPORARY_BANK_NETWORK_ERROR")||
                                        (response.getErrorCode().equals("029") &&response.getErrorMessage().equals("Security words's locked"))||
                                        (response.getErrorCode().equals("22") &&response.getErrorMessage().equals("DO NOT HONOR "))||
                                        (response.getErrorCode().equals("0002") &&response.getErrorMessage().equals("Insufficient Balance"))||
                                        (response.getErrorCode().equals("ESB-82-006") &&response.getErrorMessage().equals("Max amount transaction is exceeded"))

//                                        (response.getErrorCode().equals("UNKNOW_ERROR") &&response.getDisburseChannel().equals("XENDIT"))||
//                                        response.getErrorCode().equals("INSUFFICIENT_BALANCE")
                                        ){
                                    // 网络波动导致的放款失败 可以重新打款

                                    //1.使淼科那边的订单号无效化
                                    this.usrBankService.inactiveOrder(order);
                                    //2.清除redis里面的放款记录(为了相同订单号能够继续打款 删除之前在redis里面的order)
                                    this.redisClient.del(RedisContants.ORDER_LOAN_LOCK_NEW + order.getUuid());
                                    //3.更改订单状态为待放款

                                    OrdOrder entity = new OrdOrder();
                                    entity.setUuid(order.getUuid());
                                    entity.setStatus(OrdStateEnum.LOANING.getCode());
                                    entity.setApplyTime(new Date());
                                    entity.setRemark("");
                                    entity.setUpdateTime(new Date());
                                    this.ordService.updateOrder(entity);

                                    if (order.getStatus() != OrdStateEnum.LOANING.getCode()){

                                        order.setStatus(OrdStateEnum.LOANING.getCode());
                                        order.setApplyTime(new Date());
                                        order.setUpdateTime(new Date());
                                        this.ordService.addOrderHistory(order);
                                    }

                                    log.info("重新回到打款队列，订单号：{}",order.getUuid());
                                }else if (response.getErrorCode().equals("015")){
                                  // 姓名错误
                                  String errorMessage = response.getErrorMessage();
                                    String[] strings = errorMessage.split("-");
                                    if (strings.length >= 2){

                                        int pos = errorMessage.indexOf("-");
                                        String realName = errorMessage.substring(pos + 1, errorMessage.length());
                                        if(realName.substring(0,2).equals("  ")){
                                            realName = realName.substring(1,realName.length());
                                        }else {
                                            realName = realName.trim();
                                        }

                                        UsrBank usrBank = new UsrBank();
                                        usrBank.setUserUuid(order.getUserUuid());
                                        usrBank.setUuid(order.getUserBankUuid());
                                        usrBank.setBankCardName(realName);
                                        usrBank.setUpdateTime(new Date());
                                        usrBank.setRemark("姓名更正为银行返回的姓名");
                                        log.info("要更新的银行卡ID为"+order.getUserBankUuid());
                                        log.info("要更新的用户名"+realName);
                                        this.usrBankService.updateBankCardInfo(usrBank);

                                        //1.使淼科那边的订单号无效化
                                        this.usrBankService.inactiveOrder(order);
                                        //2.清除redis里面的放款记录(为了相同订单号能够继续打款 删除之前在redis里面的order)
                                        this.redisClient.del(RedisContants.ORDER_LOAN_LOCK_NEW + order.getUuid());
                                        //3.更改订单状态为待放款

                                        OrdOrder entity = new OrdOrder();
                                        entity.setUuid(order.getUuid());
                                        entity.setStatus(OrdStateEnum.LOANING.getCode());
                                        entity.setApplyTime(new Date());
                                        entity.setRemark("");
                                        entity.setUpdateTime(new Date());
                                        this.ordService.updateOrder(entity);

                                        if (order.getStatus() != OrdStateEnum.LOANING.getCode()){

                                            order.setStatus(OrdStateEnum.LOANING.getCode());
                                            order.setApplyTime(new Date());
                                            order.setUpdateTime(new Date());
                                            this.ordService.addOrderHistory(order);
                                        }

                                        log.info("重新回到打款队列，订单号：{}",order.getUuid());
                                    }

                                }
                                else if(response.getErrorCode().equals("12")){

                                    if (!StringUtils.isEmpty(response.getErrorMessage())){
                                        if (response.getErrorMessage().equals("INVALID TRANSACTION")){
                                            OrdOrder entity = new OrdOrder();
                                            entity.setUuid(order.getUuid());
                                            entity.setRemark("BANK_CARD_ERROR");
                                            this.ordService.updateOrder(entity);

                                            if (order.getThirdType() == 1){
                                                // CashCash订单  需要通知对方修改银行卡
                                                this.kaBinCheckService.postBankCardResult2CashCash(order.getUuid(),"2","银行返回失败");
                                            }else if (order.getThirdType() == 0) {
                                                UsrUser usrUser  = this.usrService.getUserByUuid(order.getUserUuid());
                                                String sms = this.redisClient.get(RedisContants.SMS_BANK_CADR_LOCK + order.getUuid());
                                                if (sms == null) {
                                                    String smsContent = "<Do-It> verifikasi kartu bank Anda gagal. silakan masuk ke aplikasi dan coba daftarkan kartu lagi. dana akan cair setelah kartu bank sesuai verifikasi.";
                                                    // 今天未发送短信
                                                    try {
                                                        String mobileNumberDes = usrUser.getMobileNumberDES();
                                                        String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                                                        //budi: remark kirim sms jika kabin check failed
                                                        //smsServiceUtil.sendTypeSmsCode("BANK_CARD_REMIND", mobileNumber, smsContent);
                                                        this.redisClient.set(RedisContants.SMS_BANK_CADR_LOCK + order.getUuid(), order.getUuid(), 3600 * 120);
                                                    } catch (Exception e) {
                                                        log.info("发送绑卡提醒短信异常");
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("查询订单异常,单号:"+ order.getUuid(), e);
                }
            }
        }

    }

    public static void main(String[] args) {
        String errorMessage = "Beneficary name is invalid - MOH. ROMLI";
        String[] strings = errorMessage.split("-");
        if (strings.length >= 2) {

            int pos = errorMessage.indexOf("-");
            String realName = errorMessage.substring(pos + 1, errorMessage.length());
            if(realName.substring(0,2).equals("  ")){
                realName = realName.substring(1,realName.length());
            }else {
                realName = realName.trim();
            }
            log.info(realName);
        }
        }

    public void dealWithLoanFaildOrder() {

        List<OrdOrder> loanFalidList = this.ordDao.getLoanFaildOrderWithBCABank();
        if (!CollectionUtils.isEmpty(loanFalidList)) {
            log.info("BCA打款异常的订单总个数为：" + loanFalidList.size());
            for (OrdOrder order : loanFalidList) {
                log.info("本次处理的打款订单号" + order.getUuid());

                try {

                    //1.使淼科那边的订单号无效化
                    this.usrBankService.inactiveOrder(order);
                    //2.清除redis里面的放款记录(为了相同订单号能够继续打款 删除之前在redis里面的order)
                    this.redisClient.del(RedisContants.ORDER_LOAN_LOCK_NEW + order.getUuid());
                    //3.更改订单状态为待放款

                    OrdOrder entity = new OrdOrder();
                    entity.setUuid(order.getUuid());
                    entity.setStatus(OrdStateEnum.LOANING.getCode());
                    entity.setApplyTime(new Date());
                    entity.setRemark("");
                    entity.setUpdateTime(new Date());
                    this.ordService.updateOrder(entity);

                    if (order.getStatus() != OrdStateEnum.LOANING.getCode()) {

                        order.setStatus(OrdStateEnum.LOANING.getCode());
                        order.setApplyTime(new Date());
                        order.setUpdateTime(new Date());
                        this.ordService.addOrderHistory(order);
                    }

                    log.info("重新回到打款队列，订单号：{}", order.getUuid());

                } catch (Exception e) {
                    log.error("查询订单异常,单号:" + order.getUuid(), e);
                }

            }
        }
    }


    /**
     *  查询待还款状态 账单，去第三方查询还款，同步状态 如果成功 COMPLETED 改成还款成功
     * */
    public void cheakRepayBill() {

        //开关
        String sysParamValue = this.sysParamService.getSysParamValue(SysParamContants.CHEAK_REPAY_OFF_NO);

        if (!StringUtils.isEmpty(sysParamValue) && sysParamValue.equals("1") ){
            //   放款处理中 订单
            List<OrdBill> bills = this.ordBillDao.getInRepayBillList();
            this.caculateBill(bills);

        }else {
            log.info("=====================》查询还款处理中,同步状态 开关已关闭《====================");
        }
    }


}
