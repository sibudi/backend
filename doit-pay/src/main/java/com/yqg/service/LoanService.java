package com.yqg.service;

import com.yqg.activity.dao.ActivityAccountRecordDao;
import com.yqg.activity.dao.UsrActivityBankDao;
import com.yqg.activity.entity.ActivityAccountRecord;
import com.yqg.activity.entity.UsrActivityBank;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.constants.SysParamContants;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.activity.ActivityAccountRecordService;
import com.yqg.service.activity.request.ActivityAccountRecordReq;
import com.yqg.service.externalChannel.service.Cash2OrderService;
import com.yqg.service.externalChannel.service.CheetahOrderService;
import com.yqg.service.externalChannel.utils.Cash2OrdStatusEnum;
import com.yqg.service.externalChannel.utils.CheetahOrdStatusEnum;
import com.yqg.service.loan.response.LoanResponse;
import com.yqg.service.loan.service.LoanInfoService;
import com.yqg.service.order.OrdService;
import com.yqg.service.p2p.response.P2PResponse;
import com.yqg.service.p2p.response.P2PResponseDetail;
import com.yqg.service.p2p.response.P2PResponseDetail.RepaySuccessStatusDetail;
import com.yqg.service.p2p.service.P2PService;
import com.yqg.service.system.service.SysParamService;
import com.yqg.service.third.kaBinCheck.KaBinCheckService;
import com.yqg.service.third.sms.SmsServiceUtil;
import com.yqg.service.user.service.UsrService;
import com.yqg.user.dao.UsrBankDao;
import com.yqg.user.dao.UsrLoginHistoryDao;
import com.yqg.user.entity.UsrBank;
import com.yqg.user.entity.UsrLoginHistory;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Didit Dwianto on 2017/12/3.
 */
@Service
@Slf4j
public class LoanService {

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private SysParamService sysParamService;
    @Autowired
    private OrdDao ordDao;
    @Autowired
    private UsrBankDao usrBankDao;
    @Autowired
    private PayService payService;
    @Autowired
    private UsrService usrService;
    @Autowired
    private OrdService ordService;
    @Autowired
    private SmsServiceUtil smsServiceUtil;
    @Autowired
    private UsrLoginHistoryDao usrLoginHistoryDao;
    @Autowired
    private Cash2OrderService cash2OrderService;
    @Autowired
    private KaBinCheckService kaBinCheckService;
    @Autowired
    private ActivityAccountRecordDao activityAccountRecordDao;
    @Autowired
    private UsrActivityBankDao usrActivityBankDao;
    @Autowired
    private ActivityAccountRecordService activityAccountRecordService;
    @Autowired
    private P2PService p2PService;
    @Autowired
    private LoanInfoService loanInfoService;
    @Autowired
    private CheetahOrderService cheetahOrderService;
    /**
     * 放款
     */
    public void loanPay() {

        String sysParamValue = this.sysParamService.getSysParamValue(SysParamContants.LOAN_OFF_NO);
        String sysParamLoanStr = this.sysParamService.getSysParamValue(SysParamContants.NOT_IN_LOAN_SECTION);

        if (!StringUtils.isEmpty(sysParamValue) && sysParamValue.equals("1")) {

            if (!StringUtils.isEmpty(sysParamLoanStr)) {
                String[] strs = sysParamLoanStr.split("#");
                String hh = DateUtils.formDate(new Date(), "HH");
                for (String str : strs) {
                    if (str.contains(hh)) {
                        log.info("LoanPay skipped. The current time is not in the lending range");
                        return;
                    }
                }

                log.info("Lending started 。。。。。。。。。。。。");
                //  Approval of orders pending payment
                List<OrdOrder> orderOrders = this.ordDao.getLoanList();

                log.info("---------------Found {} order(s)", orderOrders.size());
                for (OrdOrder order : orderOrders) {

                    String s = this.redisClient.get(RedisContants.ORDER_LOAN_LOCK_NEW + order.getUuid());
                    if (!StringUtils.isEmpty(s)) {
                        log.info("Skipped. Already processed by other process：{}", order.getUuid());
                        continue;
                    }

                    // Check if the user has logged in after sending a text message
                    List<UsrLoginHistory> usrLoginHistoryList = this.usrLoginHistoryDao.getLoginByTiming(order.getUserUuid());
                    if (CollectionUtils.isEmpty(usrLoginHistoryList)) {
                        //The user cannot log in after sending the recall SMS and cannot log in
                        continue;
                    }

                    //Re-check the order, if it does not exist, jump out
                    OrdOrder orderOrder = new OrdOrder();
                    orderOrder.setDisabled(0);
                    orderOrder.setUuid(order.getUuid());
                    List<OrdOrder> orderList = this.ordDao.scan(orderOrder);
                    if (CollectionUtils.isEmpty(orderList)) {
                        continue;
                    }

                    OrdOrder tempOrder = orderList.get(0);
                    //  Reconfirm the order status, if it is not pending, jump out
                    if (tempOrder.getStatus() != OrdStateEnum.LOANING.getCode()) {
                        continue;
                    }
                    //  Reconfirm the status of the order. If it is in the process of lending, pay on behalf of you and jump out.
                    if (tempOrder.getStatus() == OrdStateEnum.LOANING_DEALING.getCode() ||
                            tempOrder.getStatus() == OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode() ||
                            tempOrder.getStatus() == OrdStateEnum.RESOLVING_OVERDUE.getCode()) {
                        continue;
                    }

                    UsrUser userUser = this.usrService.getUserByUuid(order.getUserUuid());
//                    TODO:Whether to check whether the bank card can be debited
                    UsrBank usrBank = new UsrBank();
                    usrBank.setUuid(order.getUserBankUuid());
                    List<UsrBank> bankList = this.usrBankDao.scan(usrBank);
                    if (CollectionUtils.isEmpty(bankList)) {
                        continue;
                    }

                    UsrBank bankEntity = bankList.get(0);
                    // If the bank card is 1 pending, jump out and let the task continue to poll the bank card status
                    if (bankEntity.getStatus() == 1) {
                        continue;
                    }
                    // The status of the bank card corresponding to this order is 3 failed.
                    // Send SMS reminder
                    /**
                     * Text message copy: [Do-It] Your bank account verification failed. Please log in to the DO-IT APP to re-bind the bank account, and the loan will be released after successful binding.
                     * */
                    if (bankEntity.getStatus() == 3) {
                        if (order.getThirdType() == 0) {
                            //log.info("Wrong bank card for order:{}", order.getUuid());
                            String sms = this.redisClient.get(RedisContants.SMS_BANK_CADR_LOCK + order.getUuid());
                            if (sms == null) {
                                String smsContent = "<Do-It> verifikasi kartu bank Anda gagal. silakan masuk ke aplikasi dan coba daftarkan kartu lagi. dana akan cair setelah kartu bank sesuai verifikasi.";
                                // No text messages sent today
                                try {
                                    String mobileNumberDes = userUser.getMobileNumberDES();
                                    String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                                    //budi: remark kirim sms jika kabin check failed
                                    //smsServiceUtil.sendTypeSmsCode("BANK_CARD_REMIND", mobileNumber, smsContent);
                                    this.redisClient.set(RedisContants.SMS_BANK_CADR_LOCK + order.getUuid(), order.getUuid(), 3600 * 24 * 7);
                                } catch (Exception e) {
                                    log.error("Sending a card binding alert message is abnormal", e);
                                }
                            }
                        }
                        continue;
                    }

                    if (p2PService.isLoanNeedSendToP2P(order, bankEntity)) {
                        p2pLoanIssuing(order, userUser);
                    } else {
                        normalLoanIssuing(order, userUser, bankEntity);
                    }
                }
            }
        } else {
            log.info("=====================》Loan switch is OFF《====================");
        }

    }

    /***
     * Normal system lending
     */
    private void normalLoanIssuing(OrdOrder order, UsrUser userUser, UsrBank bankEntity) {
        // TODO may make repeated payments due to abnormal service. Go to mk to check the order for insurance. If it exists, jump out.
        if (this.loanQuery(order, userUser)) {
            return;
        }

        log.info("=========================Start normalLoanIssuing=====================");

        //Current Lending Amount and Daily Lending Limit
        String loanLimit = this.sysParamService.getSysParamValue(SysParamContants.LOAN_ACCOUNT_LIMIT);
        String loanNow = this.sysParamService.getSysParamValue(SysParamContants.LOAN_ACCOUNT_NOW);
        if (new BigDecimal(loanNow).compareTo(new BigDecimal(loanLimit)) >= 0){
            log.info("The amount of lending has reached the upper limit. The current amount of lending is: {}, the maximum amount of daily lending: {}",loanNow,loanLimit);
            return;
        }else {
            log.info("The current loan amount is:{}",loanNow);
        }


        String lockKey = RedisContants.ORDER_LOAN_LOCK_FIX + order.getUserUuid();
        if (this.redisClient.lock(lockKey)) {
            try {

                LoanResponse response = new LoanResponse();

                String sysParamValue = this.sysParamService.getSysParamValue(SysParamContants.LOAN_OFF_NO_BCA);
                if (bankEntity.getBankCode().equals("BCA") && !StringUtils.isEmpty(sysParamValue) && sysParamValue.equals("1")) {
                    response = this.payService.commitPay(order, userUser,bankEntity,"BCA");
                }else {
                    String sysParamValue2 = this.sysParamService.getSysParamValue(SysParamContants.LOAN_OFF_NO_BNI);
                    if (bankEntity.getBankCode().equals("BNI") && !StringUtils.isEmpty(sysParamValue2) && sysParamValue2.equals("1")) {
                        response = this.payService.commitPay(order, userUser,bankEntity,"BNI");
                    }else {
                        String sysParamValue3 = this.sysParamService.getSysParamValue(SysParamContants.LOAN_OFF_NO_CIMB);
                        if (!StringUtils.isEmpty(sysParamValue3)
                                && sysParamValue3.equals("1")) {
                                // janhsen: flag if all LOAN_OFF_NO_BCA and or LOAN_OFF_NO_BNI turn off by default is CIMB
                                // && !bankEntity.getBankCode().equals("BNI")
                                // && !bankEntity.getBankCode().equals("BCA")) {
                            response = this.payService.commitPay(order, userUser, bankEntity, "CIMB");
                        }else {
                            log.info("The deposit switch is off"+order.getUuid());
                            return;
                        }
                    }
                }

                if (response != null && !StringUtils.isEmpty(response.getCode())) {
                    if (response.getCode().equals("0")) {
                        loanTempSuccess(order);
                    } else {
                        log.info("Lending failed, order number: {}, failure reason：{}", order.getUuid(), response.getErrorMessage());
                        if (response.getErrorCode().equals("RECIPIENT_ACCOUNT_NUMBER_ERROR") ||
                                response.getErrorCode().equals("INVALID_DESTINATION") ||
                                response.getErrorCode().equals("BANK_CODE_NOT_SUPPORTED_ERROR")
                                || (response.getErrorCode().equals("12") && response.getErrorMessage().equals("INVALID TRANSACTION"))
                                || (response.getErrorCode().equals("001") && response.getErrorMessage().equals("Account not Found"))
                                || (response.getErrorCode().equals("76") && response.getErrorMessage().equals("INVALID CREDIT ACCOUNT"))
                                || (response.getErrorCode().equals("ESB-82-021") && response.getErrorMessage().equals("Account cannot do transaction"))
                                || (response.getErrorCode().equals("0169") && response.getErrorMessage().equals("Account number is not found"))
                                || (response.getErrorCode().equals("0110") && response.getErrorMessage().equals("Account is closed"))) {
                            // Lending failed due to bank card problem
                            loanFaildWithCard(order);
                            if (order.getThirdType() == 1) {
                                // CashCash orders need to notify counterparty to modify bank card
                                kaBinCheckService.postBankCardResult2CashCash(order.getUuid(), "2", "银行返回失败");
                            }
                        } else {
                            loanFaild(order,response);
                        }
                    }
                } else {
                    log.error("Lending failed, interface returned empty, order number:{}", order.getUuid());
                    loanFaild(order,response);
                }
            } catch (Exception e) {
                log.error("Lending failed, order number:" + order.getUuid(), e);
                //TODO:Whether to add a loan failure table
                loanFaild(order,null);
            } finally {
                this.redisClient.unLock(lockKey);
            }
            this.redisClient.set(RedisContants.ORDER_LOAN_LOCK_NEW + order.getUuid(), order.getUuid(), 3600 * 24 * 30);
        }
    }

    /***
     * p2p通道放款
     */
    public void p2pLoanIssuing(OrdOrder order, UsrUser user) {
        String lockKey = RedisContants.ORDER_LOAN_LOCK_FIX + order.getUserUuid();
        if (!redisClient.lock(lockKey)) {
            return;
        }
        try {
            P2PResponse statusResponse = p2PService.checkOrderInfo(order);
            if (isNeedSendOrderInfo(statusResponse)) {
                p2PService.sendOrderInfoToFinancial(order, user);
            } else if (P2PResponse.isSuccessResponse(statusResponse) && statusResponse.getData() != null) {
                RepaySuccessStatusDetail detail = JsonUtils.deserialize(JsonUtils.serialize(statusResponse.getData()),
                        P2PResponseDetail.RepaySuccessStatusDetail.class);
                //有状态信息，说明之前已经推送过，更加状态做相应的处理(更新doit数据库)
                p2PService.handleP2PLoanStatus(order.getUuid(), detail.getStatus(), detail);
            }
        } catch (Exception e) {
            log.error("调用p2p放款失败,orderNo: " + order.getUuid(), e);
        } finally {
            redisClient.unLock(lockKey);
        }
    }

    private boolean isNeedSendOrderInfo(P2PResponse statusResponse) {
        //无标的
        if (statusResponse != null && statusResponse.getCode() != null && statusResponse.getCode() == 1002) {
            return true;
        }
        return false;
    }


    /**
     * Payment failed, modify order status
     */
    @Transactional
    public void loanFaild(OrdOrder order,LoanResponse response) {
        log.info("Payment failed=====================》Order number：" + order.getUuid());
        OrdOrder entity = new OrdOrder();
        entity.setUuid(order.getUuid());
        entity.setStatus(OrdStateEnum.LOAN_FAILD.getCode());
        entity.setUpdateTime(new Date());
        if (response != null){
            if (!StringUtils.isEmpty(response.getErrorMessage())){
                entity.setRemark(response.getErrorMessage());
            }
        }
        this.ordService.updateOrder(entity);
        order.setStatus(OrdStateEnum.LOAN_FAILD.getCode());
        this.ordService.addOrderHistory(order);

    }

    /**
     * Payment failed, modify order status
     */
    @Transactional
    public void loanFaildWithCard(OrdOrder order) {
        log.info("Payment failed (bank card error)=====================》Order number：" + order.getUuid());

        // If it is a cashcash order, feedback updates the order status
        if (order.getThirdType() == 1) {
            this.cash2OrderService.ordStatusFeedback(order, Cash2OrdStatusEnum.LOAN_FAILD);
        }else if (order.getThirdType() == 2) {
            this.cheetahOrderService.ordStatusFeedback(order, CheetahOrdStatusEnum.LOAN_FAILD);
        }

        OrdOrder entity = new OrdOrder();
        entity.setUuid(order.getUuid());
        entity.setStatus(OrdStateEnum.LOAN_FAILD.getCode());
        entity.setRemark("BANK_CARD_ERROR");
        entity.setUpdateTime(new Date());
        this.ordService.updateOrder(entity);

        order.setStatus(OrdStateEnum.LOAN_FAILD.getCode());
        this.ordService.addOrderHistory(order);
    }

    /**
     * Prepayment successful, modify order status
     */
    @Transactional
    public void loanTempSuccess(OrdOrder order) {

        log.info("Prepayment success=====================》Order number：" + order.getUuid());

        OrdOrder entity = new OrdOrder();
        entity.setUuid(order.getUuid());
        entity.setStatus(OrdStateEnum.LOANING_DEALING.getCode());
        entity.setUpdateTime(new Date());
        this.ordService.updateOrder(entity);

        order.setStatus(OrdStateEnum.LOANING_DEALING.getCode());
        order.setUpdateTime(new Date());
        this.ordService.addOrderHistory(order);


//        //  记录打款金额   每天限额 4，000，000，000
//        String loanNow = this.sysParamService.getSysParamValue(SysParamContants.LOAN_ACCOUNT_NOW);
//        log.info("当前放款金额为:{}",loanNow);
//        this.sysParamService.setSysParamValue(SysParamContants.LOAN_ACCOUNT_NOW,new BigDecimal(loanNow).add(order.getAmountApply()).toString());

    }

    /**
     * Check whether the loan order exists
     */
    public boolean loanQuery(OrdOrder order, UsrUser user) {

        boolean flag = false;
        try {
            log.info("Check the order number:{}", order.getUuid());
            LoanResponse response = this.payService.queryLoanResult(order.getUuid(), user.getUuid());

            if (response != null) {
                if (response.getCode().equals("0") || response.getCode().equals("1")) {
                    flag = true;
                }
            }
            return flag;
        } catch (Exception e) {
            log.error("Query order abnormality, order number: " + order.getUuid(), e);
        }
        return flag;
    }


    /**
     * 订单状态： CREATED, DISBURSING, PENDING, COMPLETED, FAILED
     * 查询放款中订单，同步状态 如果成功 COMPLETED 改成放款成功
     * <p>
     * 只查询借款端的 放款处理中 订单
     */
    public void cheakLoanOrder() {

        //??
        String sysParamValue = this.sysParamService.getSysParamValue(SysParamContants.CHEAK_LOANING_OFF_NO);

        if (!StringUtils.isEmpty(sysParamValue) && sysParamValue.equals("1")) {
//            log.info("=====================》查询待放款订单,同步状态 开关已打开《====================");
            //  审核放款中的订单
            List<OrdOrder> orderOrders = this.ordDao.getLoaningList();

            log.info("待放款服务费订单数量:" + orderOrders.size());
            for (OrdOrder order : orderOrders) {

                UsrUser userUser = this.usrService.getUserByUuid(order.getUserUuid());

                cheakLoaningOrder(order, userUser);
            }
        } else {
            log.info("=====================》查询待放款订单,同步状态 开关已关闭《====================");
        }
    }

    public void cheakLoaningOrder(OrdOrder order, UsrUser user) {
        try {
            log.info("查询订单号:{}", order.getUuid());
            if (p2PService.isP2PIssuedLoan(order.getUuid())) {
                //p2p平台处理的放款
                P2PResponse statusResponse = p2PService.checkOrderInfo(order);
                if (P2PResponse.isSuccessResponse(statusResponse) && statusResponse.getData() != null) {
                    RepaySuccessStatusDetail detail = JsonUtils.deserialize(JsonUtils.serialize(statusResponse.getData()),
                            P2PResponseDetail.RepaySuccessStatusDetail.class);
                    p2PService.handleP2PLoanStatus(order.getUuid(), detail.getStatus(), detail);
                }
                return;
            }

            LoanResponse response = this.payService.queryLoanResult(order.getUuid(), user.getUuid());
            if (response != null) {
                if (response.getCode().equals("0") || response.getCode().equals("1")) {
                    if (response.getDisburseStatus().equals("COMPLETED")) {
                        // 如果是cashcash的订单 反馈更新订单状态
                        if (order.getThirdType() == 1) {
                            this.cash2OrderService.ordStatusFeedback(order, Cash2OrdStatusEnum.LOAN_SUCCESS);
                        }else if (order.getThirdType() == 2) {
                            this.cheetahOrderService.ordStatusFeedback(order, CheetahOrdStatusEnum.LOAN_FAILD);
                        }

                        loanInfoService.issuedSuccess(order, response);

                    } else if (response.getDisburseStatus().equals("FAILED")) {

                        log.info("放款失败,订单号:{},失败原因：{}", order.getUuid(), response.getErrorMessage());
                        if (response.getErrorCode().equals("RECIPIENT_ACCOUNT_NUMBER_ERROR") ||
                                response.getErrorCode().equals("INVALID_DESTINATION") ||
                                response.getErrorCode().equals("BANK_CODE_NOT_SUPPORTED_ERROR")
                                || (response.getErrorCode().equals("12") && response.getErrorMessage().equals("INVALID TRANSACTION"))
                                || (response.getErrorCode().equals("001") && response.getErrorMessage().equals("Account not Found"))
                                || (response.getErrorCode().equals("76") && response.getErrorMessage().equals("INVALID CREDIT ACCOUNT"))
                                || (response.getErrorCode().equals("ESB-82-021") && response.getErrorMessage().equals("Account cannot do transaction"))
                                || (response.getErrorCode().equals("0169") && response.getErrorMessage().equals("Account number is not found"))
                                || (response.getErrorCode().equals("0110") && response.getErrorMessage().equals("Account is closed"))) {

                            // 因为银行卡问题 放款失败
                            loanFaildWithCard(order);
                            if (order.getThirdType() == 1) {
                                // CashCash订单  需要通知对方修改银行卡
                                kaBinCheckService.postBankCardResult2CashCash(order.getUuid(), "2", "银行返回失败");
                            }
                        } else {
                            // 放款失败
                            loanFaild(order,response);
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("查询订单异常,单号:" + order.getUuid(), e);
        }

    }

    /**
     * ?????? ????
     */
    public void LoanOrderByFinancial(String orderNo) throws Exception {

        if (!StringUtils.isEmpty(orderNo)) {
            //  ??????????
            //???????????????
            OrdOrder orderOrder = new OrdOrder();
            orderOrder.setDisabled(0);
            orderOrder.setUuid(orderNo);
            List<OrdOrder> orderList = this.ordDao.scan(orderOrder);

            log.info("---------------???????:" + orderNo);
            if (!CollectionUtils.isEmpty(orderList)) {

                OrdOrder order = orderList.get(0);

                String s = this.redisClient.get(RedisContants.ORDER_LOAN_LOCK_NEW + order.getUuid());
                if (!StringUtils.isEmpty(s)) {
                    log.info("???????{}", order.getUuid());
                    return;
                }


                //  ?????????????????????
                if (order.getStatus() != OrdStateEnum.LOANING.getCode()) {
                    return;
                }
                //  ????????? ???????????????
                if (order.getStatus() == OrdStateEnum.LOANING_DEALING.getCode() ||
                        order.getStatus() == OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode() ||
                        order.getStatus() == OrdStateEnum.RESOLVING_OVERDUE.getCode()) {
                    return;
                }

                UsrUser userUser = this.usrService.getUserByUuid(order.getUserUuid());
//                    TODO:????????????
                UsrBank usrBank = new UsrBank();
                usrBank.setUuid(order.getUserBankUuid());
                List<UsrBank> bankList = this.usrBankDao.scan(usrBank);
                if (CollectionUtils.isEmpty(bankList)) {
                    return;
                }

                UsrBank bankEntity = bankList.get(0);
                // ??????1 pending???????task?????????
                if (bankEntity.getStatus() == 1) {
                    return;
                }

                if (bankEntity.getStatus() == 3) {
                    String sms = this.redisClient.get(RedisContants.SMS_BANK_CADR_LOCK + order.getUuid());
                    if (sms == null) {
                        String smsContent = "<Do-It> verifikasi kartu bank Anda gagal. silakan masuk ke aplikasi dan coba daftarkan kartu lagi. dana akan cair setelah kartu bank sesuai verifikasi.";
                        // ???????
                        try {
                            String mobileNumberDes = userUser.getMobileNumberDES();
                            String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                            //budi: remark kirim sms jika kabin check failed
                            //smsServiceUtil.sendTypeSmsCode("BANK_CARD_REMIND", mobileNumber, smsContent);
                            this.redisClient.set(RedisContants.SMS_BANK_CADR_LOCK + order.getUuid(), order.getUuid(), 3600 * 24);
                        } catch (Exception e) {
                            log.info("??????????");
                            e.printStackTrace();
                        }
                    }
                    return;
                }

//                    //TODO ??????????????? ?????mk?????? ????????
                if (this.loanQuery(order, userUser)) {
                    return;
                }

                log.info("=========================????=====================");
                String lockKey = RedisContants.ORDER_LOAN_LOCK_FIX + order.getUserUuid();
                if (this.redisClient.lock(lockKey)) {

                    try {
                        LoanResponse response = this.payService.commitPay(order, userUser, bankEntity, "XENDIT");
                        if (response != null) {
                            if (response.getCode().equals("0")) {
                                loanTempSuccess(order);
                            } else {

                                log.info("????,???:{},?????{}", order.getUuid(), response.getErrorMessage());
                                if (response.getErrorCode().equals("RECIPIENT_ACCOUNT_NUMBER_ERROR") ||
                                        response.getErrorCode().equals("INVALID_DESTINATION") ||
                                        response.getErrorCode().equals("BANK_CODE_NOT_SUPPORTED_ERROR")
                                        || (response.getErrorCode().equals("12") && response.getErrorMessage().equals("INVALID TRANSACTION"))
                                        || (response.getErrorCode().equals("001") && response.getErrorMessage().equals("Account not Found"))
                                        || (response.getErrorCode().equals("76") && response.getErrorMessage().equals("INVALID CREDIT ACCOUNT"))
                                        || (response.getErrorCode().equals("ESB-82-021") && response.getErrorMessage().equals("Account cannot do transaction"))
                                        || (response.getErrorCode().equals("0169") && response.getErrorMessage().equals("Account number is not found"))
                                        || (response.getErrorCode().equals("0110") && response.getErrorMessage().equals("Account is closed"))
                                        ) {
                                    // ??????? ????
                                    loanFaildWithCard(order);
                                    if (order.getThirdType() == 1) {
                                        // CashCash订单  需要通知对方修改银行卡
                                        kaBinCheckService.postBankCardResult2CashCash(order.getUuid(), "2", "银行返回失败");
                                    }
                                } else {
                                    // ????
                                    loanFaild(order,response);
                                }
                            }
                        } else {

                            log.info("????,??????,???:{}", order.getUuid());
                            loanFaild(order,response);
                        }
                    } catch (Exception e) {

                        log.info("????,???:{}", order.getUuid());
                        e.getStackTrace();
//                       TODO:??????????? ????????
                        loanFaild(order,null);
                    } finally {

                        this.redisClient.unLock(lockKey);
                    }
                    this.redisClient.set(RedisContants.ORDER_LOAN_LOCK_NEW + order.getUuid(), order.getUuid(), 3600 * 24 * 30);
                }

            }
        } else {
            log.error("?????");
            throw new ServiceException(ExceptionEnum.ORDER_NOT_FOUND);
        }
    }

    /**
     * 邀请好友活动佣金打款  放款中 需要增加一个中间状态
     */
    public void activityLoan() {

        //开关
        String sysParamValue = this.sysParamService.getSysParamValue(SysParamContants.LOAN_ACTIVITY_OFF_NO);
        //非放款区间
        String sysParamLoanStr = this.sysParamService.getSysParamValue(SysParamContants.NOT_IN_LOAN_SECTION);

        if (!StringUtils.isEmpty(sysParamValue) && sysParamValue.equals("1")) {
            log.info("=====================》好友活动佣金打款 开关已打开《====================");

            if (!StringUtils.isEmpty(sysParamLoanStr)) {
                String[] strs = sysParamLoanStr.split("#");
                String hh = DateUtils.formDate(new Date(), "HH");
                for (String str : strs) {
                    if (str.contains(hh)) {
                        log.info("当前时间不在放款区间，官人也休息了，早上会记得放款嗷！");
                        return;
                    }
                }

                log.info("好友活动佣金打款 开始了。。。。。。。。。。。。");
                //  审核通过待放款的订单
                List<ActivityAccountRecord> recordList = this.activityAccountRecordDao.getLoanList();
                log.info("---------------准备放款{}笔", recordList.size());
                for (ActivityAccountRecord record : recordList) {

                    String s = this.redisClient.get(RedisContants.ORDER_LOAN_LOCK_NEW + record.getUuid());
                    if (!StringUtils.isEmpty(s)) {
                        log.info("已经放款一次：{}", record.getUuid());
                        continue;
                    }

                    // 查询活动绑定的银行卡
                    UsrActivityBank bank = new UsrActivityBank();
                    bank.setUserUuid(record.getUserUuid());
                    bank.setDisabled(0);
                    bank.setBankNumberNo(record.getCaseoutAccount());
                    List<UsrActivityBank> bankList = this.usrActivityBankDao.scan(bank);
                    if (CollectionUtils.isEmpty(bankList)) {
                        continue;
                    }

                    UsrActivityBank activityBank = bankList.get(0);
                    // 如果银行卡是1 pending状态，跳出，让task继续轮询银行卡状态
                    if (activityBank.getStatus() == 1 || activityBank.getStatus() == 3) {
                        continue;
                    }

                    OrdOrder order = new OrdOrder();
                    order.setUuid(record.getUuid());

                    UsrUser usrUser = new UsrUser();
                    usrUser.setUuid(record.getUserUuid());
//                    //TODO 可能会由于服务异常导致重复打款 保险起见去mk那里查询订单 如果存在，则跳出
                    if (this.loanQuery(order, usrUser)) {
                        continue;
                    }

                    log.info("=========================开始打款=====================");
                    String lockKey = RedisContants.ORDER_LOAN_LOCK_FIX + record.getUserUuid();
                    if (this.redisClient.lock(lockKey)) {

                        try {
                            UsrBank bankEntity = new UsrBank();
                            bankEntity.setBankCardName(activityBank.getBankCardName());
                            bankEntity.setBankCode(activityBank.getBankCode());
                            bankEntity.setBankNumberNo(activityBank.getBankNumberNo());
                            LoanResponse response = this.payService.commitPayWithActivity(record, usrUser, bankEntity);
                            if (response != null) {
                                if (response.getCode().equals("0")) {
                                    // 预放款成功
                                    activityLoanStatus(record, "1");
                                } else {

                                    log.info("放款失败,订单号:{},失败原因：{}", record.getUuid(), response.getErrorMessage());
                                    // 放款失败
                                    activityLoanStatus(record, "3");
                                }
                            } else {

                                log.info("放款失败,接口返回为空,订单号:{}", record.getUuid());
                                activityLoanStatus(record, "3");
                            }
                        } catch (Exception e) {

                            log.info("放款失败,订单号:{}", record.getUuid());
                            log.error(e.getMessage(), e);
//                       TODO:是否需要添加放款失败表 增加放款失败原因
                            activityLoanStatus(record, "3");
                        } finally {

                            this.redisClient.unLock(lockKey);
                        }
                        this.redisClient.set(RedisContants.ORDER_LOAN_LOCK_NEW + record.getUuid(), record.getUuid(), 3600 * 24 * 30);
                    }

                }
            }
        } else {
            log.info("=====================》好友活动佣金打款 开关已关闭《====================");
        }

    }

    // 修改流水记录
    @Transactional
    public void activityLoanStatus(ActivityAccountRecord record, String status) {

        try {
            ActivityAccountRecord entity = new ActivityAccountRecord();
            entity.setUuid(record.getUuid());
            entity.setLoanStatus(status);
            entity.setUpdateTime(new Date());
            this.activityAccountRecordDao.update(entity);

            if (status.equals("1")) {
                log.info("预打款成功=====================》订单号：" + record.getUuid());
            } else if (status.equals("2")) {
                log.info("打款成功=====================》订单号：" + record.getUuid());
                // 解除锁定的流水  主账户减去锁定金额
                ActivityAccountRecordReq req = new ActivityAccountRecordReq();
                req.setUserUuid(record.getUserUuid());
                req.setUuid(record.getUuid());
                this.activityAccountRecordService.withdrawSuccess(req);
            } else if (status.equals("3")) {
                log.info("打款失败=====================》订单号：" + record.getUuid());
                // 解除锁定的流水 再新增一条失败流水 主账户加上锁定金额
                ActivityAccountRecordReq req = new ActivityAccountRecordReq();
                req.setUserUuid(record.getUserUuid());
                req.setUuid(record.getUuid());
                this.activityAccountRecordService.withdrawFail(req);
            }
        } catch (Exception e) {
            log.error("放款失败,订单号:{}" + record.getUuid(), e);
        }

    }


    /**
     * 查询活动放款中订单
     */
    public void cheakActivityLoan() {

        //??
        String sysParamValue = this.sysParamService.getSysParamValue(SysParamContants.CHEAK_LOANING_OFF_NO);

        if (!StringUtils.isEmpty(sysParamValue) && sysParamValue.equals("1")) {
            //  查询活动放款中订单
            List<ActivityAccountRecord> recordList = this.activityAccountRecordDao.getLoaningList();
            ;
            log.info("number" + recordList.size());
            for (ActivityAccountRecord record : recordList) {

                UsrUser userUser = this.usrService.getUserByUuid(record.getUserUuid());

                try {
                    log.info("查询订单号:{}", record.getUuid());
                    OrdOrder order = new OrdOrder();
                    order.setUuid(record.getUuid());

                    LoanResponse response = this.payService.queryLoanResult(order.getUuid(), userUser.getUuid());
                    if (response != null) {
                        if (response.getCode().equals("0") || response.getCode().equals("1")) {
                            if (response.getDisburseStatus().equals("COMPLETED")) {
                                // 放款成功
                                activityLoanStatus(record, "2");
//                                // 发送放款成功短信
//                                sendMsg(order,user);

                            } else if (response.getDisburseStatus().equals("FAILED")) {

                                log.info("活动打款失败,订单号:{},失败原因：{}", record.getUuid(), response.getErrorMessage());
                                activityLoanStatus(record, "3");
                            }
                        }
                    }

                } catch (Exception e) {
                    log.info("查询活动提现订单异常,单号:{},异常信息:{}", record.getUuid(), e);
                    e.printStackTrace();
                }

            }
        } else {
            log.info("=====================》查询活动放款中订单 开关已关闭《====================");
        }
    }

    /***
     * p2p订单状态检查
     */
    public void checkP2PLoanStatus() {
        List<OrdOrder> ordOrders = ordDao.getP2PSendPendingOrders();
        if (CollectionUtils.isEmpty(ordOrders)) {
            return;
        }
        for (OrdOrder order : ordOrders) {
            try {
                P2PResponse statusResponse = p2PService.checkOrderInfo(order);
                if (isNeedSendOrderInfo(statusResponse)) {
                   if(order.getMarkStatus()==OrdOrder.P2PLoanStatusEnum.SEND_2_P2P_SUCCESS.getStatusCode()){
                       log.info("already send success but with 1002, orderNo: {}",order.getUuid());
                       continue;
                   }else{
                       UsrUser user = usrService.getUserByUuid(order.getUserUuid());
                       p2PService.sendOrderInfoToFinancial(order,user);
                   }
                }else{
                    if(P2PResponse.isSuccessResponse(statusResponse)&&statusResponse.getData()!=null){
                        RepaySuccessStatusDetail detail = JsonUtils.deserialize(JsonUtils.serialize(statusResponse.getData()),
                                P2PResponseDetail.RepaySuccessStatusDetail.class);
                        p2PService.handleP2PLoanStatus(order.getUuid(), detail.getStatus(), detail);
                    }
                }

            } catch (Exception e) {
                log.error("check p2p loan status error, orderNo: "+order.getUuid(),e);
            }
        }
    }


}
