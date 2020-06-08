
package com.yqg.service;

import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.order.OrdStepTypeEnum;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.DESUtils;
import com.yqg.drools.service.OrderScoreService;
import com.yqg.drools.service.UserService;
import com.yqg.enums.ScoreModelEnum;
import com.yqg.order.entity.OrdBlack;
import com.yqg.order.entity.OrdOrder;
import com.yqg.risk.dao.RiskResultDao;
import com.yqg.risk.entity.OrderScore;
import com.yqg.service.externalChannel.service.Cash2OrderService;
import com.yqg.service.externalChannel.service.CheetahOrderService;
import com.yqg.service.externalChannel.utils.Cash2OrdCheckResultEnum;
import com.yqg.service.externalChannel.utils.Cash2OrdStatusEnum;
import com.yqg.service.externalChannel.utils.CheetahOrdStatusEnum;
import com.yqg.service.order.OrdService;
import com.yqg.service.order.request.SaveOrderUserUuidRequest;
import com.yqg.service.risk.service.RiskReviewService;
import com.yqg.service.third.sms.SmsServiceUtil;
import com.yqg.service.user.service.UsrBaseInfoService;
import com.yqg.service.util.RuleConstants;
import com.yqg.system.entity.SysAutoReviewRule;
import com.yqg.task.entity.AsyncTaskInfoEntity;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Slf4j
public class ReviewResultService {

    @Autowired
    private NonManualReviewService nonManualReviewService;
    @Autowired
    private OrderScoreService orderScoreService;

    @Autowired
    private Cash2OrderService cash2OrderService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private AutoCallService autoCallService;

    @Autowired
    private OrdService ordService;

    @Autowired
    private UsrBaseInfoService usrBaseInfoService;

    @Autowired
    private RiskResultDao riskResultDao;
    @Autowired
    private UserService userService;
    @Autowired
    private SmsServiceUtil smsServiceUtil;
    @Autowired
    private CheetahOrderService cheetahOrderService;
    @Autowired
    private RiskReviewService riskReviewService;

    /****
     * Process approved first borrowing
     * @param ordOrder
     */
    public void afterFistBorrowPass(OrdOrder ordOrder) {
        //如果是复审过来的100rmb产品，可以直接进入待确认，无需走后续流程
        if(isOrderFrom600RMBSeniorReview(ordOrder)){
            //to confirm
            log.info("senior review to 100 final pass..");
            this.autoCallPassToConfirmForSpecifiedProduct(ordOrder);
            return;
        }
        if (autoCallService.isAutoCallSwitchOpen()) {
            //调用外呼
            AutoCallService.SendAutoCallResult sendAutoCallResult = autoCallService.sendFirstBorrowAutoCall(ordOrder);
            if(sendAutoCallResult.isNeedCall()){
                //订单状态改为待外呼
                ordService.changeOrderStatus(ordOrder, OrdStateEnum.WAIT_CALLING);
                return;
            }else{
                //不需要外呼，如果是命中100rmb产品，更改产品信息
            }
        }
        autoReviewPass(ordOrder);
    }


    /**
     * Process approved reborrow
     * @param order
     */
    public void afterReBorrowingPass(OrdOrder order){
        if (autoCallService.isAutoCallSwitchOpen()) {
            AutoCallService.SendAutoCallResult sendAutoCallResult = autoCallService.sendReBorrowingAutoCall(order);
            if(sendAutoCallResult.isNeedCall()){
                ordService.changeOrderStatus(order, OrdStateEnum.WAIT_CALLING);
                return;
            }
        }
        reBorrowingAutoReviewPass(order);
    }

    /***
     * 外呼拒绝订单转人工
     * @param order
     */
    @Transactional(rollbackFor = Exception.class)
    public void  autoCallRejectToManualReview(OrdOrder order){
        ordService.changeOrderStatus(order,  OrdStateEnum.FIRST_CHECK);
    }
    /***
     * 外呼转人工(100RMB product)
     * @param order
     */
    @Transactional(rollbackFor = Exception.class)
    public void autoCallPassToManualReviewFor100RMBProduct(OrdOrder order) throws Exception {
        //ordService.changeOrderTo100RMBProduct(order);
        ordService.changeOrderStatus(order,  OrdStateEnum.FIRST_CHECK);
    }

    /***
     * 外呼直接到待用户确认(100RMB product/50RMB product)
     * @param order
     */
    @Transactional(rollbackFor = Exception.class)
    public void autoCallPassToConfirmForSpecifiedProduct(OrdOrder order) {
        // budi: remark agar tidak masuk digisign
        //if (contractSignService.isDigitalSignSwitchOpen(order)) {
        //    asyncTaskService.addTask(order, AsyncTaskInfoEntity.TaskTypeEnum.CONTRACT_SIGN_TASK);
        //} else {
            ordService.changeOrderStatus(order, OrdStateEnum.WAITING_CONFIRM);
            //cashcash的推送数据
            if (order.getThirdType() != null && order.getThirdType() == 1) {
                this.cash2OrderService.ordStatusFeedback(order, Cash2OrdStatusEnum.WAIT_CONFIRM);
                this.cash2OrderService.ordCheckResultFeedback(order, Cash2OrdCheckResultEnum.WAITING_CONFIRM);
            }
        //}
    }

    /***
     * 自动审核通过后处理-- 初借
     * @param ordOrder
     */
    public void autoReviewPass(OrdOrder ordOrder) {
        log.info("order {} auto review pass",ordOrder.getUuid());
        OrdStateEnum status = OrdStateEnum.FIRST_CHECK;
        boolean isNonManual = nonManualReviewService.isNonManualReviewOrder(ordOrder.getUuid());
        OrderScore orderScore = orderScoreService.getLatestScoreWithModel(ordOrder.getUuid(), ScoreModelEnum.PRODUCT_600_V2);

        boolean scorePass = orderScore!=null && orderScore.getScorePass()!=null && orderScore.getScorePass() == 1;
        boolean scoreManual = orderScore!=null && orderScore.getManualReview()!=null && orderScore.getManualReview() == 1;
        boolean scorePassToCall = scorePass&& !scoreManual;
        boolean scoreNotPassToCall = !scorePass && isNonManual;

        log.info("the result of order: {} is:  scorePass: {}, scoreManual: {}, isNonManual: {}",ordOrder.getUuid(),scorePass,scoreManual,isNonManual);

        if(scorePassToCall||scoreNotPassToCall){
            status = OrdStateEnum.LOANING;
        }

        if (status == OrdStateEnum.LOANING) {
            //advance检查
            boolean hitAdvanceCheck = riskReviewService.hitBlackListOrMultiPlatform(ordOrder);
            if (hitAdvanceCheck) {
                archiveOrder(ordOrder);
                sendFeedBackInfo2CashCashAfterReject(ordOrder);
            } else {
                // budi: remark digisign, karena mau langsung ke status 5 (send to P2P) bukan 20
                // not backward compatible with non-P2P loan
                //if (contractSignService.isDigitalSignSwitchOpen(ordOrder)) {
                //    asyncTaskService.addTask(ordOrder, AsyncTaskInfoEntity.TaskTypeEnum.CONTRACT_SIGN_TASK);
                //} else {
                    ordService.changeOrderStatus(ordOrder, status);
                //}
                archiveOrder(ordOrder);
                //进入待放款状态后处理
                sendFeedBackInfo2CashCashAfterPass(ordOrder);
            }
        } else {
            ordService.changeOrderStatus(ordOrder, status);
        }

    }



    /***
     * 自动审核通过后处理-- 复借
     * @param order
     */
    public void reBorrowingAutoReviewPass(OrdOrder order){
        // budi: remark digisign, karena mau langsung ke status 5 (send to P2P) bukan 20
        // not backward compatible with non-P2P loan
        //if(contractSignService.isDigitalSignSwitchOpen(order)){
        //    asyncTaskService.addTask(order, AsyncTaskInfoEntity.TaskTypeEnum.CONTRACT_SIGN_TASK);
        //}else{
            ordService.changeOrderStatus(order,OrdStateEnum.LOANING);
        //}
            // 订单归档
            archiveOrder(order);
            sendFeedBackInfo2CashCashAfterPass(order);
    }

    /**
     * 自动审核拒绝后处理
     * @param ordOrder
     * @param rejectRule
     */
    public void autoCallReject(OrdOrder ordOrder, SysAutoReviewRule rejectRule){
        ordService.changeOrderStatus(ordOrder,OrdStateEnum.MACHINE_CHECK_NOT_ALLOW);
        // 订单归档
        archiveOrder(ordOrder);
        sendFeedBackInfo2CashCashAfterReject(ordOrder);
        log.info("订单号：{} 外呼被拒 {} 天", ordOrder.getUuid(),rejectRule.getRuleRejectDay());
    }

    public void autoCallRejectToFillLinkman(OrdOrder order) {
        try {
            log.info("orderNo: {} change order status to re add linkman", order.getUuid());
            riskResultDao.disableOrdBlackRecord(order.getUuid(), RuleConstants.NEED_RE_FILL_LINKMAN);
            ordService.changeOrderStatus(order, OrdStateEnum.SUBMITTING);
            usrBaseInfoService.updateOrderStep(order.getUuid(), order.getUserUuid(), OrdStepTypeEnum.WORK_INFO.getType());
            //发送提醒短信
            UsrUser user = userService.getUserInfo(order.getUserUuid());
            String mobileNumber = "62" + DESUtils.decrypt(user.getMobileNumberDES());
            smsServiceUtil.sendSms(SmsServiceUtil.SmsTypeEnum.EMERGENCY_LINKMAN_NUMBER_ERROR_REMINDER, mobileNumber);
        } catch (Exception e) {
            log.info("reject to add linkman status error,orderNo: " + order.getUuid(), e);
        }
    }


    public void autoCallPassAfterFirstCheck(OrdOrder order){
        ordService.changeOrderStatus(order,OrdStateEnum.SECOND_CHECK);
        archiveOrder(order);
    }

    public void autoCallRejectFirstCheck(OrdOrder order, SysAutoReviewRule rejectRule){
        ordService.changeOrderStatus(order,OrdStateEnum.FIRST_CHECK_NOT_ALLOW);
        archiveOrder(order);
        sendFeedBackInfo2CashCashAfterReject(order);
        log.info("Order No: {} Outbound call rejected in first trial {} days", order.getUuid(),rejectRule.getRuleRejectDay());
    }

    /***
     * Save to redis save:mango:orderList -> OrderUserMangoScheduling will save to Mongo OrderUserDataMongo
     * @param ordOrder
     */
    private void archiveOrder(OrdOrder ordOrder) {
        SaveOrderUserUuidRequest saveMongo = new SaveOrderUserUuidRequest();
        saveMongo.setOrderNo(ordOrder.getUuid());
        saveMongo.setUserUuid(ordOrder.getUserUuid());
        this.redisClient.listAdd(RedisContants.SAVE_MANGO_ORDER_LIST, saveMongo);
    }

    /***
     * 审核通过返回订单状态到CashCash
     * @param ordOrder
     */
    private void sendFeedBackInfo2CashCashAfterReject(OrdOrder ordOrder) {
        // 如果是CashCash的订单 反馈更新订单状态 和审批状态
        if (ordOrder.getThirdType() == 1) {
            this.cash2OrderService.ordStatusFeedback(ordOrder, Cash2OrdStatusEnum.NOT_PASS_CHECK);
            this.cash2OrderService.ordCheckResultFeedback(ordOrder, Cash2OrdCheckResultEnum.CHECK_NOT_PASS);
        }else if (ordOrder.getThirdType() == 2) {
            // 猎豹金融 cheetah
            this.cheetahOrderService.ordStatusFeedback(ordOrder, CheetahOrdStatusEnum.NOT_PASS_CHECK);
        }
    }
    /***
     * 审核拒绝返回订单状态到CashCash
     * @param ordOrder
     */
    private void sendFeedBackInfo2CashCashAfterPass(OrdOrder ordOrder) {
        // 如果是CashCash的订单 反馈更新订单状态
        if (ordOrder.getThirdType() == 1) {
            this.cash2OrderService.ordStatusFeedback(ordOrder, Cash2OrdStatusEnum.PASS_CHECK);
            this.cash2OrderService.ordCheckResultFeedback(ordOrder, Cash2OrdCheckResultEnum.CHECK_PASS);
        }else if (ordOrder.getThirdType() == 2) {
            // 猎豹金融 cheetah
            this.cheetahOrderService.ordStatusFeedback(ordOrder, CheetahOrdStatusEnum.PASS_CHECK);
        }
    }


    private boolean isOrderFrom600RMBSeniorReview(OrdOrder order) {
        List<OrdBlack> blackList = riskResultDao.getOrderBlackListIgnoreDisabled(order.getUuid());
        if (CollectionUtils.isEmpty(blackList)) {
            return false;
        }
        return blackList.stream().filter(elem -> RuleConstants.PRODUCT600TO150_SENIOR_REVIEW.equals(elem.getRemark())).count() > 0;
    }



}