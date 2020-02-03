package com.yqg.service;

import com.yqg.common.constants.RedisContants;
import com.yqg.common.constants.SysParamContants;
import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.SmsCodeMandaoUtil;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.beans.RuleSetExecutedResult;
import com.yqg.drools.service.ApplicationService;
import com.yqg.drools.service.RuleApplicationService;
import com.yqg.drools.service.RuleService;
import com.yqg.drools.utils.RuleUtils;
import com.yqg.order.dao.OrdRiskRecordDao;
import com.yqg.order.entity.OrdOrder;
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
import com.yqg.service.third.mobox.MoboxService;
import com.yqg.service.user.service.UsrService;
import com.yqg.system.dao.SysAutoReviewRuleDao;
import com.yqg.system.entity.SysAutoReviewRule;
import com.yqg.user.entity.UsrUser;
import com.yqg.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * 审核入口
     */
    public void risk(Integer num) {

        String sysParamValue = this.sysParamService.getSysParamValue(SysParamContants.RISK_OFF_NO);
        if (!StringUtils.isEmpty(sysParamValue) && Integer.valueOf(sysParamValue) == 1) {
            log.info("=============审核开关打开=======================");
//            List<OrdOrder> orderOrders = this.ordService.scanReviewOrder();// 单任务
            List<OrdOrder> orderOrders = this.ordService.scanReviewOrderById(num);// 多任务
            this.caculate(orderOrders);
        }
    }

    /**
     * 开始审核
     */
    public void caculate(List<OrdOrder> orderOrders) {

        if (CollectionUtils.isEmpty(orderOrders)) {
            log.info("=============审核列表为空=======================");
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
                if (order.getBorrowingCount() >= 2) {// 复借用户

                    this.multiReviewPass(order, user);
                    //新规则使用：
                    // RuleSetExecutedResult ruleSetResult = applicationService
                    //     .applyForReBorrowing(order, codeEntityMap);
                    // if (ruleSetResult.isRuleSetResult()) {
                    //     this.multiReviewPass(order, user);
                    // } else {
                    //     this.reviewRefuse(order, ruleSetResult.getFirstRejectRule());
                    // }

                } else {// 初借用户

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
     *  初借：审核通过
     */
    public void reviewPass(OrdOrder ordOrder) {

        log.info("risk reviewPass" + ordOrder.getUuid());
        //订单状态改为等待外呼
        reviewResultService.afterFistBorrowPass(ordOrder);

    }


    /**
     * 复借：审核通过
     * @param ordOrder
     */
    public void multiReviewPass(OrdOrder ordOrder, UsrUser user) {
        log.info("复借通过机审订单号：" + ordOrder.getUuid());
        reviewResultService.afterReBorrowingPass(ordOrder);
    }


}
