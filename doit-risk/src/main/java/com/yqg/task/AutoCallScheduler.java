package com.yqg.task;

import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.beans.RuleSetExecutedResult;
import com.yqg.drools.service.ApplicationService;
import com.yqg.drools.service.OrderScoreService;
import com.yqg.drools.service.RuleService;
import com.yqg.drools.utils.DateUtil;
import com.yqg.enums.ScoreModelEnum;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdRiskRecord;
import com.yqg.risk.entity.OrderReviewStepEntity;
import com.yqg.risk.entity.OrderReviewStepEntity.StepEnum;
import com.yqg.risk.entity.OrderScore;
import com.yqg.risk.repository.OrderRiskRecordRepository;
import com.yqg.service.AutoCallService;
import com.yqg.service.NonManualReviewService;
import com.yqg.service.OrderReviewStepService;
import com.yqg.service.ReviewResultService;
import com.yqg.service.order.OrdService;
import com.yqg.service.risk.service.AutoCallSendService;
import com.yqg.service.third.Inforbip.InforbipService;
import com.yqg.service.third.Inforbip.Request.InforbipRequest;
import com.yqg.service.user.service.UserRiskService;
import com.yqg.service.util.RuleConstants;
import com.yqg.system.dao.TeleCallResultDao;
import com.yqg.system.entity.CallResult;
import com.yqg.system.entity.TeleCallResult;
import com.yqg.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/***
 * 自动外呼处理
 */
@Component
@Slf4j
public class AutoCallScheduler {

    public final List<BigDecimal> excludeApplyAmounts =
            Arrays.asList(RuleConstants.PRODUCT100, RuleConstants.PRODUCT50, RuleConstants.PRODUCT50_V0, RuleConstants.PRODUCT100_V0);

    @Autowired
    private InforbipService inforbipService;

    @Autowired
    private NonManualReviewService nonManualReviewService;

    @Autowired
    private ReviewResultService reviewResultService;
    @Autowired
    private AutoCallSendService autoCallSendService;

    @Autowired
    private AutoCallService autoCallService;
    @Autowired
    private OrderReviewStepService orderReviewStepService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private TeleCallResultDao teleCallResultDao;

    @Autowired
    private OrdDao ordDao;

    @Autowired
    private OrderRiskRecordRepository orderRiskRecordRepository;
    @Autowired
    private UserRiskService userRiskService;
    @Autowired
    private OrdService ordService;
    @Autowired
    private OrderScoreService orderScoreService;
    @Autowired
    private RedisClient redisClient;


    /***
     * Send outgoing calls regularly
     */
    // @Scheduled(cron = "0 0 9,12,15,18,20 * * ?")
    // ahalim: Currently auto call is being called by RiskScheduling.java risk()
    // public void autoCall() {
    //     //Query all the orders to be called out [Ignore within 30 minutes, call again next time]
    //     Date maxUpdateTime = DateUtils.addMinutes(new Date(), -30);
    //     log.warn("the maxUpdateTime is: {}", maxUpdateTime);
    //     List<OrdOrder> waitCallingList = ordDao.getWaitingAutoCallOrdersWithUpdateTime(maxUpdateTime);

    //     if (CollectionUtils.isEmpty(waitCallingList)) {
    //         log.info("the calling wait list is empty");
    //         return;
    //     }
    //     log.warn("current wait calling orders: {}", waitCallingList.size());
    //     List<InforbipRequest> requestList = new ArrayList<>();
    //     for (OrdOrder waitingItem : waitCallingList) {
    //         LogUtils.addMDCRequestId(waitingItem.getUuid());
    //         try {
    //             //是否需要本人外呼/公司外呼
    //             List<CallResult> callResultList = autoCallService.getTelCallList(waitingItem.getUuid(), null);
    //             ownerNeedReCall(callResultList, waitingItem, requestList);
    //             companyNeedReCall(callResultList, waitingItem, requestList);
    //         } catch (Exception e) {
    //             log.error("get auto call params error", e);
    //         } finally {
    //             LogUtils.removeMDCRequestId();
    //         }
    //     }

    //     if (CollectionUtils.isEmpty(requestList)) {
    //         log.info("no auto call request need to by sent");
    //         return;
    //     }

    //     log.info("auto call count: {}", requestList.size());

    //     //批量发送请求[每次最大10个号码]
    //     int batchCount = (int) Math.ceil(requestList.size() / (RuleConstants.MAX_SEND_NUMBERS_PER_TIME * 1.0));
    //     for (int i = 0; i < batchCount; i++) {
    //         int startIndex = i * RuleConstants.MAX_SEND_NUMBERS_PER_TIME;
    //         int endIndex = startIndex + RuleConstants.MAX_SEND_NUMBERS_PER_TIME;
    //         if (endIndex > requestList.size()) {
    //             endIndex = requestList.size();
    //         }
    //         long startTime = System.currentTimeMillis();
    //         inforbipService.sendVoiceMessage(requestList.subList(startIndex, endIndex));
    //         log.info("the cost of send voice message is {} ms", (System.currentTimeMillis() - startTime));

    //     }
    // }

    // private void ownerNeedReCall(List<CallResult> callResultList, OrdOrder waitingItem, List<InforbipRequest> requestList) {
    //     //是否需要本人外呼/公司外呼
    //     if (!needOwnerCallFinished(waitingItem)) {
    //         return;
    //     }
    //     if (CollectionUtils.isEmpty(callResultList)) {
    //         log.info("the call result list is empty");
    //         addAutoCallRequestParam(autoCallService.getOwnerAutoCallParam(waitingItem), requestList);
    //         return;
    //     }
    //     if (!isOwnerCallCompleted(callResultList)) {
    //         //log.info("owner tel need to be called...");
    //         addAutoCallRequestParam(autoCallService.getOwnerAutoCallParam(waitingItem), requestList);
    //     }
    // }

    // private void companyNeedReCall(List<CallResult> callResultList, OrdOrder waitingItem, List<InforbipRequest> requestList) {
    //     //是否需要本人外呼/公司外呼
    //     if (!needCompanyCallFinished(waitingItem)) {
    //         return;
    //     }
    //     if (CollectionUtils.isEmpty(callResultList)) {
    //         log.info("the call result list is empty ");
    //         addAutoCallRequestParam(autoCallService.getCompanyAutoCallParam(waitingItem), requestList);
    //         return;
    //     }
    //     if (!hasCompanyCall(callResultList)) {
    //         //log.info("company tel need to be called...");
    //         addAutoCallRequestParam(autoCallService.getCompanyAutoCallParam(waitingItem), requestList);
    //     }
    // }


    /***
     * Check order outgoing results
     */
    //ahalim: speedup for demo
    @Scheduled(cron = "0/30 * * * * ? ")
    //@Scheduled(cron = "0 0/1 * * * ? ")
    public void checkAutoCallResultTask0() {
        if (!autoCallService.isAutoCallSwitchOpen()) {
            log.info("checkAutoCallResultTask0 - risk:auto_call:switch off");
            return;
        }
        List<OrdOrder> ordOrders = ordDao.getWaitingAutoCallOrders(0);
        if (CollectionUtils.isEmpty(ordOrders)) {
            log.warn("checkAutoCallResultTask0 - count is empty");
        }
        log.info("checkAutoCallResultTask0 - count: {}", ordOrders.size());
        for (OrdOrder waitingItem : ordOrders) {
            if (waitingItem.getStatus() == OrdStateEnum.WAIT_CALLING.getCode()) {
                log.info("checkAutoCallResultTask0 - After Machine Check for order: {}", waitingItem.getUuid());
                checkOrderAutoCallStatusAfterMachineCheck(waitingItem);
            } else {
                log.info("checkAutoCallResultTask0 - After First Check for order: {}", waitingItem.getUuid());
                checkOrderAutoCallStatusAfterFirstCheck(waitingItem);
            }
        }
    }

    /***
     * 检查订单外呼结果
     */
    // @Scheduled(cron = "0 0/1 * * * ? ")
    // public void checkAutoCallResultTask1() {
    //     //所有机审后待外呼状态的单
    //     List<OrdOrder> ordOrders = ordDao.getWaitingAutoCallOrders(1);
    //     if (CollectionUtils.isEmpty(ordOrders)) {
    //         log.warn("no orders need to be checked for ..");
    //     }
    //     log.info("total size to check mode 1: {}", ordOrders.size());
    //     //检查外呼结果
    //     for (OrdOrder waitingItem : ordOrders) {
    //         if (waitingItem.getStatus() == OrdStateEnum.WAIT_CALLING.getCode()) {
    //             checkOrderAutoCallStatusAfterMachineCheck(waitingItem);
    //         } else {
    //             checkOrderAutoCallStatusAfterFirstCheck(waitingItem);
    //         }
    //     }
    // }

    /***
     * 检查订单外呼结果
     */
    // @Scheduled(cron = "0 0/1 * * * ? ")
    // public void checkAutoCallResultTask2() {
    //     //所有机审后待外呼状态的单
    //     List<OrdOrder> ordOrders = ordDao.getWaitingAutoCallOrders(2);
    //     if (CollectionUtils.isEmpty(ordOrders)) {
    //         log.warn("no orders need to be checked for ..");
    //     }
    //     log.info("total size to check mode 2: {}", ordOrders.size());
    //     //检查外呼结果
    //     for (OrdOrder waitingItem : ordOrders) {
    //         if (waitingItem.getStatus() == OrdStateEnum.WAIT_CALLING.getCode()) {
    //             checkOrderAutoCallStatusAfterMachineCheck(waitingItem);
    //         } else {
    //             checkOrderAutoCallStatusAfterFirstCheck(waitingItem);
    //         }
    //     }
    // }


    /***
     * @param waitingItem
     */
    private void checkOrderAutoCallStatusAfterFirstCheck(OrdOrder waitingItem) {
        LogUtils.addMDCRequestId(waitingItem.getUuid());
        try {
            List<CallResult> callResultList = autoCallService.getTelCallList(waitingItem.getUuid(), null);
            if (!isOrderAutoCallFinishedForFirstCheck(waitingItem, callResultList)) {
                log.info("Auto call is not finished, orderNo: {}", waitingItem.getUuid());
                return;
            }

            // Call rule determination:
            // No need to call out-> pass
            // Reject 3 outbound calls-> pass
            // The number of outgoing calls exceeded-》 reject
            RuleSetExecutedResult ruleResult = applicationService.autoCallForOwner(waitingItem, ruleService.getAllRules());
            if (ruleResult.isRuleSetResult()) {
                reviewResultService.autoCallPassAfterFirstCheck(waitingItem);
            } else {
                reviewResultService.autoCallRejectFirstCheck(waitingItem, ruleResult.getFirstRejectRule());
            }

        } catch (Exception e) {
            log.info("check order auto call status after first check error ", e);
        } finally {
            LogUtils.removeMDCRequestId();
        }
    }

    /***
     * @param waitingItem
     */
    private void checkOrderAutoCallStatusAfterMachineCheck(OrdOrder waitingItem) {
        LogUtils.addMDCRequestId(waitingItem.getUuid());
        try {
            //Has signed a contract:
            String signLock = redisClient.get(RedisContants.REVIEW_SIGN_LOCK + ":" + waitingItem.getUuid());
            if (StringUtils.isNotEmpty(signLock)) {
                log.info("order {} in contract sign auto call", waitingItem.getUuid());
                return;
            }

            List<CallResult> callResultList = autoCallService.getTelCallList(waitingItem.getUuid(), null);

            OrderReviewStepEntity dbReviewStep = orderReviewStepService.getCurrentReviewStep(waitingItem.getUuid());
            StepEnum reviewStep = StepEnum.UNKNOWN;
            if(dbReviewStep!=null){
                reviewStep = StepEnum.getEnumFromCode(dbReviewStep.getStep());
            }
            boolean callFinished;
            /***
             * Regarding outbound calls: 
             * Due to some related influences of the derating rules, 
             * my outbound calls are placed after contacts + company outbound calls
             * so the first borrowing outbound call is divided into two steps.
             */
            switch (reviewStep) {
                case LINKMAN_AND_COMPANY_CALL:
                    log.info("checkOrderAutoCallStatusAfterMachineCheck - review step: (1)LINKMAN_AND_COMPANY_CALL");
                    //Contacts have been reviewed to check whether the company's outgoing call is over
                    callFinished = isOwnerAutoCallFinishedForMachineCheck(waitingItem, callResultList);
                    break;
                case OWNER_CALL:
                    log.info("checkOrderAutoCallStatusAfterMachineCheck - review step: (2)OWNER_CALL");
                    //You have already made an outgoing call, 
                    //you don’t need to continue, 
                    //you may have to repeat the review [in case of contact back-off or manual review of the case], 
                    //if the last review time exceeds 20min, you can continue
                    if(dbReviewStep!=null && DateUtil.getDiffMinutes(dbReviewStep.getCreateTime(),new Date())>20){
                        //Can be reviewed after 20 minutes
                        log.info("checkOrderAutoCallStatusAfterMachineCheck - override reviewStep into (0)UNKNOWN");
                        reviewStep = StepEnum.UNKNOWN;
                    }else{
                        log.info("the order: {} already checked for auto call, please check ", waitingItem.getUuid());
                        return;
                    }
                    // !!! Notice that there is no break in here !!!
                case UNKNOWN:
                default:
                    log.info("checkOrderAutoCallStatusAfterMachineCheck - review step: (0)UNKNOWN");
                    //No review, check if the contact's outgoing call is over
                    callFinished = isLinkmanCompanyAutoCallFinishedForMachineCheck(waitingItem, callResultList);
                    break;
            }
            if (!callFinished) {
                log.info("Call not finished");
                return;
            }

            RuleSetExecutedResult ruleResult = null;
            log.info("reviewStep is: {}", reviewStep);
            if (reviewStep == StepEnum.UNKNOWN) {
                //Call emergency contact, company phone related rules
                ruleResult = applicationService.autoCall(waitingItem, ruleService.getAllRules());

                //Quota may change after audit
                //the amountApply maybe modified in afterRejectResult method, need to get the new order info.
                waitingItem = ordService.getOrderByOrderNo(waitingItem.getUuid());

                if (!ruleResult.isRuleSetResult()) {
                    //Whether the emergency contact refuses to call out
                    Integer rejectTimes = userRiskService.getEmergencyAutoCallRejectedTimes(waitingItem.getUuid());
                    if (rejectTimes < 3 && Arrays.asList(BlackListTypeEnum.MULTI_AUTO_CALL_REJECT_LINKMAN_VALID_COUNT.getMessage(),
                            BlackListTypeEnum.AUTO_CALL_REJECT_LINKMAN_VALID_COUNT.getMessage())
                            .contains(ruleResult.getFirstRejectRule().getRuleDetailType())) {
                        //Disabled the reason for rejection Re-fill in the contact, 
                        //the status changed to 1, 4 send SMS
                        reviewResultService.autoCallRejectToFillLinkman(waitingItem);
                        return;
                    }
                    //Refuse
                    // Record the review process:
                    orderReviewStepService.addReviewStep(waitingItem.getUuid(), waitingItem.getUserUuid(), StepEnum.LINKMAN_AND_COMPANY_CALL);
                    reviewResultService.autoCallReject(waitingItem, ruleResult.getFirstRejectRule());
                    return;
                }

                //Record review process:
                orderReviewStepService.addReviewStep(waitingItem.getUuid(), waitingItem.getUserUuid(), StepEnum.LINKMAN_AND_COMPANY_CALL);
                /********通过******/
                if (waitingItem.getBorrowingCount() > 1) {
                    reviewResultService.reBorrowingAutoReviewPass(waitingItem);
                    return;
                }
                //No nuclear or derating to make outbound calls
                //If reborrow = return false = doesn't need call owner
                if (needOwnerCallFinished(waitingItem)) {
                    autoCallSendService.sendOwnerCall(waitingItem);
                }
            } else {
                //step = LINKMAN_AND_COMPANY_CALL
                if (waitingItem.getBorrowingCount() > 1) {
                    log.info("reBorrowing order: {} not need to call owner", waitingItem.getUuid());
                    return;
                }
                ruleResult = applicationService.autoCallForOwner(waitingItem, ruleService.getAllRules());
                orderReviewStepService.addReviewStep(waitingItem.getUuid(), waitingItem.getUserUuid(), StepEnum.OWNER_CALL);
                //After the outgoing call, I enter the loan or preliminary review
                if (isOwnerAutoCallRejectToManual(waitingItem)) {
                    //Outgoing call refused to transfer
                    reviewResultService.autoCallRejectToManualReview(waitingItem);
                    return;
                }
                //reject
                if (!ruleResult.isRuleSetResult()) {
                    reviewResultService.autoCallReject(waitingItem, ruleResult.getFirstRejectRule());
                } else {//pass
                    //Derating does not require manual review
                    if (hitSpecifiedProduct(waitingItem)) {
                        reviewResultService.autoCallPassToConfirmForSpecifiedProduct(waitingItem);
                        return;
                    }
                    reviewResultService.autoReviewPass(waitingItem);
                }
            }

        } catch (Exception e) {
            log.error("order auto call check error", e);
        } finally {
            LogUtils.removeMDCRequestId();
        }
    }

    private boolean hitSpecifiedProduct(OrdOrder order) {
        if (order.getBorrowingCount() == 1 && excludeApplyAmounts.stream().
                filter(elem -> elem.compareTo(order.getAmountApply()) == 0).count() > 0) {
            return true;
        }
        return false;
    }

    private boolean isOwnerAutoCallRejectToManual(OrdOrder order) {
        OrdRiskRecord needToManual =
                orderRiskRecordRepository.getRuleResultByRuleName(BlackListTypeEnum.AUTO_CALL_REJECT_OWNER_CALL_INVALID.getMessage(),
                        order.getUuid());
        return needToManual != null && "true".equals(needToManual.getRuleRealValue());
    }


    /***
     * Do I need to end my outgoing call [first loan required]
     * @param order
     * @return
     */
    private boolean needOwnerCallFinished(OrdOrder order) {
        if (1 == order.getBorrowingCount()) {
            boolean nonManualOrder = nonManualReviewService.isNonManualReviewOrder(order.getUuid());
            // 100rmb products / 50rmb products --- "Derating products
            boolean isDecreasedLimitProduct = userRiskService.histSpecifiedProductWithDecreasedCreditLimit(order.getUuid());

            //The 600-score model test data is specially processed
            //if you need to manually call out, no need to consider other.
            OrderScore orderScore = orderScoreService.getLatestScoreWithModel(order.getUuid(), ScoreModelEnum.PRODUCT_600_V2);

            boolean scorePass = orderScore != null && orderScore.getScorePass() != null && orderScore.getScorePass() == 1;
            boolean scoreManual = orderScore != null && orderScore.getManualReview() != null && orderScore.getManualReview() == 1;
            boolean scorePassToCall = scorePass && !scoreManual;
            boolean scoreNotPassToCall = !scorePass && (nonManualOrder || isDecreasedLimitProduct);


            if ((scorePassToCall || scoreNotPassToCall) && order.getStatus() == OrdStateEnum.WAIT_CALLING.getCode()) {
                //Outgoing after machine review-"Exemption checklist requires outgoing call, 
                //non-exemption checklist does not require outgoing call
                return true;
            }
            if (!scorePassToCall && !scoreNotPassToCall && order.getStatus() == OrdStateEnum.WAITING_CALLING_AFTER_FIRST_CHECK.getCode()) {
                //From the initial review to the re-examination, my outgoing call is required.
                return true;
            }
        }
        return false;
    }

    /***
     * Do you need to end the company's outgoing calls? [First loan required]
     * @param order
     * @return
     */
    private boolean needCompanyCallFinished(OrdOrder order) {
        if (1 == order.getBorrowingCount()) {
            return true;
        }
        return false;
    }

    private boolean needLinkmanCallFinished(OrdOrder order) {
        if (CollectionUtils.isEmpty(autoCallService.getLinkmanAutoCallRequest(order))) {
            return false;
        }
        return true;
    }


    /***
     * 机审后外呼是否完成
     * @param order
     * @param callResultList
     * @return
     */
    public boolean isOrderAutoCallFinishedForMachineCheck(OrdOrder order, List<CallResult> callResultList) {
        if (CollectionUtils.isEmpty(callResultList)) {
            return false;
        }
        if (needOwnerCallFinished(order)) {
            if (!isOwnerCallCompleted(callResultList)) {
                //未完成
                return false;
            }
        }
        if (autoCallService.getCompanyAutoCallParam(order).isPresent() && needCompanyCallFinished(order)) {
            //有公司电话查看公司电话是否处理完[报告获取了或者调用失败任务已经结束]
            if (!isCompanyCallFinished(callResultList)) {
                //log.info("the company tel call not finished.");
                return false;
            }
        }
        //联系人外呼未完成
        if (!isLinkmanAutoCallCompleted(order, callResultList)) {
            return false;
        }

        return true;
    }

    /***
     * Judging the contact person, whether the company's outgoing call is over can be reviewed
     * Return false if:
     * 1. CallResult empty
     * 2. Has "Company" tel number, First borrower, TeleCallResult COMPANY not finished
     * 3. Has BackupLinkMan tel number,             TeleCallResult BACKUP_LINKMAN not finished
     * @param order
     * @param callResultList
     * @return
     */
    public boolean isLinkmanCompanyAutoCallFinishedForMachineCheck(OrdOrder order, List<CallResult> callResultList) {

        if (CollectionUtils.isEmpty(callResultList)) {
            return false;
        }
        boolean hasCompanyTel = autoCallService.getCompanyAutoCallParam(order).isPresent();
        if (hasCompanyTel && needCompanyCallFinished(order)) {
            // There is a company phone to check whether the company phone has been processed 
            // [the report has been obtained or the failed call task has ended]
            if (!isCompanyCallFinished(callResultList)) {
                //log.info("the company tel call not finished.");
                return false;
            }
        }
        //Contact outbound
        if (!isLinkmanAutoCallCompleted(order, callResultList)) {
            return false;
        }
        return true;
    }

    /***
     * Judging whether my outgoing call is over can be reviewed
     * @param order
     * @param callResultList
     * @return
     */
    public boolean isOwnerAutoCallFinishedForMachineCheck(OrdOrder order, List<CallResult> callResultList) {

        if (CollectionUtils.isEmpty(callResultList)) {
            return false;
        }
        if (needOwnerCallFinished(order)) {
            if (!isOwnerCallCompleted(callResultList)) {
                //undone
                return false;
            }
        }
        return true;
    }


    /***
     * 初审后外呼是否完成
     * @param order
     * @param callResultList
     * @return
     */
    private boolean isOrderAutoCallFinishedForFirstCheck(OrdOrder order, List<CallResult> callResultList) {
        if (CollectionUtils.isEmpty(callResultList)) {
            return false;
        }
        if (needOwnerCallFinished(order)) {
            if (!isOwnerCallCompleted(callResultList)) {
                //未完成
                return false;
            }
        }
        return true;
    }

    /**
     * Return true if:
     * 1. LinkMan not found ???
     * 2. last BACKUP_LINKMAN teleCallResult > 3 days
     * @param callResultList
     * @return
     */
    private boolean isLinkmanAutoCallCompleted(OrdOrder order, List<CallResult> callResultList) {
        if (!CollectionUtils.isEmpty(autoCallService.getLinkmanAutoCallRequest(order))) {
            //A contact needs to be called
            if (CollectionUtils.isEmpty(callResultList)) {
                return false;
            }
            List<CallResult> notFinishedList =
                    callResultList.stream().filter(elem -> elem.getCallType() == TeleCallResult.CallTypeEnum.EMERGENCY_LINKMAN.getCode()
                            || elem.getCallType() == TeleCallResult.CallTypeEnum.BACKUP_LINKMAN.getCode())
                            .filter(elem -> !elem.isCallFinished()).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(notFinishedList)) {
                // There is an outstanding call
                // Unfinished outgoing call retry for more than two days is treated as completion
                for (CallResult callResult : notFinishedList) {
                    Integer earliestDiffDays = teleCallResultDao.getEarliestCallDiffDays(callResult.getOrderNo(), callResult.getCallType(),
                            callResult.getTellNumber());
                    if (earliestDiffDays != null && earliestDiffDays > 3) {
                        log.info("the number auto call exceed days, orderNo: {}, telNumber: {} ", callResult.getOrderNo(), callResult.getTellNumber());
                        return true;
                    } else {
                        return false;// As long as there is a number of outbound calls "= 3, it is regarded as uncompleted
                    }
                }
                return false;
            }
        }
        return true;

    }


    private boolean hasCompanyCall(List<CallResult> calList) {
        if (CollectionUtils.isEmpty(calList)) {
            return false;
        }
        Optional<CallResult> companyCallExist = calList.stream().filter(elem -> TeleCallResult.CallTypeEnum.COMPANY.getCode().equals(elem
                .getCallType()))
                .findFirst();
        return companyCallExist.isPresent();
    }

    /***
     * 判定外呼是否完成--》检查是否还需要继续外呼
     * @param calList
     * @return
     */
    private boolean isOwnerAutoCallFinished(List<CallResult> calList) {
        if (CollectionUtils.isEmpty(calList)) {
            return false;
        }
        //本人是否超过限制
        List<CallResult> ownerCallList = calList.stream().filter(elem -> TeleCallResult.CallTypeEnum.OWNER.getCode().equals(elem
                .getCallType())).collect(Collectors.toList());
        return checkOwnerCallResult(ownerCallList);
    }

    /***
     * 判定外呼是否结束可以继续后续流程==》达到外呼次数并且外呼结果都有返回
     * @param calList
     * @return
     */
    public boolean isOwnerCallCompleted(List<CallResult> calList) {
        if (CollectionUtils.isEmpty(calList)) {
            return false;
        }
        //本人是否超过限制
        List<CallResult> ownerCallList = calList.stream().filter(elem -> TeleCallResult.CallTypeEnum.OWNER.getCode().equals(elem
                .getCallType()) && elem.isCallFinished()).collect(Collectors.toList());
        return checkOwnerCallResult(ownerCallList);
    }

    private boolean checkOwnerCallResult(List<CallResult> ownerCallList) {
        if (CollectionUtils.isEmpty(ownerCallList)) {
            return false;
        } else {
            //有过接通？
            boolean hasValidCall = ownerCallList.stream().filter(elem -> elem.isCallReceived()).count() > 0;
            //超过发送次数限制？
            boolean exceedLimit = ownerCallList.size() >= RuleConstants.ORDER_OWNER_CALL_LIMIT;
            //命中拒绝？
            boolean isNeedReject = ownerCallList.stream().filter(elem -> elem.isCallInvalid()).count() >= RuleConstants.REJECT_CALL_TIMES;
            if (hasValidCall || exceedLimit || isNeedReject) {
                return true;
            }
            return false;
        }
    }


    private List<InforbipRequest> addAutoCallRequestParam(Optional<InforbipRequest> param, List<InforbipRequest> targetList) {
        if (param != null && param.isPresent()) {
            targetList.add(param.get());
        }
        return targetList;
    }
    /**
     * Return true if:
     * 1. callResult is finished (CallStatusEnum.CALL_ERROR || CallStatusEnum.CALL_FINISHED)
     * 2. last COMPANY teleCallResult > 3 days
     * @param callResultList
     * @return
     */
    private boolean isCompanyCallFinished(List<CallResult> callResultList) {
        Optional<CallResult> callFinished = callResultList.stream().filter(elem -> TeleCallResult.CallTypeEnum.COMPANY
                .getCode().equals(elem.getCallType()) && elem.isCallFinished())
                .findFirst();
        if (callFinished.isPresent()) {
            return true;
        }

        //Retry number exceeds the number of days is also considered complete
        List<CallResult> allCompanyCalls = teleCallResultDao.getCallResultByOrderNoAndType(callResultList.get(0).getOrderNo(),
                TeleCallResult.CallTypeEnum.COMPANY.getCode());
        for (CallResult callResult : allCompanyCalls) {
            Integer earliestDiffDays = teleCallResultDao.getEarliestCallDiffDays(callResult.getOrderNo(), callResult.getCallType(),
                    callResult.getTellNumber());
            if (earliestDiffDays != null && earliestDiffDays > 3) {
                log.info("the number auto call exceed days, orderNo: {}, telNumber: {} ", callResult.getOrderNo(), callResult.getTellNumber());
                return true;
            }
        }
        //log.warn("the company tel cal is not finished");
        return false;
    }

}
