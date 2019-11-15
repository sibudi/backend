package com.yqg.drools.executor.firstBorrowing;

import com.github.pagehelper.StringUtil;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.drools.beans.RuleSetExecutedResult;
import com.yqg.drools.executor.ExecutorUtil;
import com.yqg.drools.executor.RuleResultService;
import com.yqg.drools.executor.base.BaseExecutionChain;
import com.yqg.drools.executor.base.FlowEnum;
import com.yqg.drools.extract.ModelScoreResultExtractor;
import com.yqg.drools.model.ModelScoreResult;
import com.yqg.drools.model.base.RuleResult;
import com.yqg.drools.service.OrderScoreService;
import com.yqg.enums.ScoreModelEnum;
import com.yqg.order.entity.OrdBlack;
import com.yqg.order.entity.OrdOrder;
import com.yqg.risk.dao.RiskResultDao;
import com.yqg.risk.dao.ScoreTemplateDao;
import com.yqg.risk.entity.OrderScore;
import com.yqg.risk.entity.ScoreTemplate;
import com.yqg.service.NonManualReviewService;
import com.yqg.service.order.OrdService;
import com.yqg.service.user.service.UserRiskService;
import com.yqg.service.util.RuleConstants;
import com.yqg.system.entity.SysAutoReviewRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/***
 * 600产品规则执行
 */
@Service
@Slf4j
public class Product600ExecutionChain extends BaseExecutionChain implements InitializingBean {


    public static final int SCORE_MODEL_MAX_PASS_COUNT_PRODUCT_600 = 200;

    //    public static final int SCORE_MODEL_MAX_PASS_COUNT_PRODUCT_600_V2 = 300;
    @Autowired
    private ExecutorUtil executorUtil;

    @Autowired
    private OrdService ordService;
    @Autowired
    private RuleResultService ruleResultService;

    @Autowired
    private UserRiskService userRiskService;

    @Autowired
    private NonManualExecutionChain nonManualExecutionChain;

    @Autowired
    private Product100ExecutionChain product100ExecutionChain;
    @Autowired
    private OrderScoreService orderScoreService;
    @Autowired
    private ScoreTemplateDao scoreTemplateDao;
    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RiskResultDao riskResultDao;

    @Autowired
    private NonManualReviewService nonManualReviewService;


//
//    @Override
//    protected void afterRejectResult(Map<String, SysAutoReviewRule> allRules, OrdOrder order) throws Exception {
//
//        //设置评分标记为:
//        orderScoreService.setRulePassFlag(order, ScoreModelEnum.PRODUCT_600, false);
//
//        orderScoreService.setRulePassFlag(order, ScoreModelEnum.PRODUCT_600_V2, false);
//
//        if (userRiskService.isSuitableFor100RMBProduct(order)) {
//            //拒绝原因disabled掉
//            ruleResultService.disabledOrdBlackWithRemark(order.getUuid(), RuleConstants.PRODUCT600TO150);
//            ordService.changeOrderTo100RMBProduct(order);
//        }
//    }

    @Override
    public boolean preFilter(OrdOrder order) {
        return executorUtil.normalFlowFilter(order);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        super.initChains(null, null);
    }

    @Override
    protected FlowEnum getFLowType() {
        return FlowEnum.PRODUCT_600;
    }


    protected RuleSetExecutedResult afterPassResult(Map<String, SysAutoReviewRule> allRules, OrdOrder order, List<Object> facts) throws Exception {

        boolean isNonManual = nonManualReviewService.isNonManualReviewOrder(order.getUuid());
        if (isNonManual) {
            return BaseExecutionChain.DEFAULT_PASS_RESULT;
        } else {
            ruleResultService.disabledOrdBlackWithRemark(order.getUuid(), RuleConstants.REJECT_REASON_NOT_NON_MANUAL);
            return BaseExecutionChain.DEFAULT_REJECT_RESULT;
        }

    }


    @Override
    protected RuleSetExecutedResult abTestAfterReject(OrdOrder order,
                                                      Map<String, SysAutoReviewRule> allRules, List<Object> facts) {

//        RuleSetExecutedResult result = abTestWorkAddress(allRules, order);
//        if (result.isRuleSetResult()) {
//            return result;
//        }
        return abTestAfterRejectByScoreModel(allRules, order, ScoreModelEnum.PRODUCT_600_V2);
    }

//    private RuleSetExecutedResult abTestWorkAddress (Map<String,SysAutoReviewRule> allRules, OrdOrder order){
//        List<RuleResult> currentResultList = getRuleResultDetailList();
//        if (CollectionUtils.isEmpty(currentResultList)) {
//            log.warn("the order is rejected , but there is no rule result detail");
//            return DEFAULT_REJECT_RESULT;
//        }
//        currentResultList = currentResultList.stream().filter(elem -> {
//            SysAutoReviewRule rule = allRules.get(elem.getRuleName());
//            //取得模型分值或者工作地址的
//            if (elem.getRuleName().equalsIgnoreCase(BlackListTypeEnum.SCORE_MODEL_PRODUCT_600_V1.getMessage())) {
//                return true;
//            }
//            boolean isHitRule = elem.isPass() && !elem.getRuleName()
//                    .equalsIgnoreCase(BlackListTypeEnum.DATA_EMPTY.getMessage());
//            if (isHitRule && rule.getRuleStatus() == 1 && rule.getRuleResult() == 2) {
//                return true;
//            }
//            return false;
//        }).collect(Collectors.toList());
//        if (currentResultList.size() == 2) {
//            if (currentResultList.get(0).getRuleName()
//                    .equalsIgnoreCase(BlackListTypeEnum.WORK_ADDRESS_INVALID.getMessage())) {
//                if (currentResultList.get(1).getRuleName()
//                        .equalsIgnoreCase(BlackListTypeEnum.SCORE_MODEL_PRODUCT_600_V1.getMessage())) {
//                    if (currentResultList.get(1).isPass()) {
//                        ruleResultService.disabledOrdBlackWithRemark(order.getUuid(), "PRODUCT_600 hit model score great then 457");
//                        return new RuleSetExecutedResult(true, null);
//                    }
//                }
//            } else {
//                if (currentResultList.get(0).getRuleName()
//                        .equalsIgnoreCase(BlackListTypeEnum.SCORE_MODEL_PRODUCT_600_V1.getMessage())) {
//                    if (currentResultList.get(0).isPass()) {
//                        ruleResultService.disabledOrdBlackWithRemark(order.getUuid(), "PRODUCT_600 hit model score great then 457");
//                        return new RuleSetExecutedResult(true, null);
//                    }
//                }
//            }
//        } else if (currentResultList.size() > 2) {
//            //记录其他的拒绝原因
//            ruleResultService.disabledOrdBlackWithRemark(order.getUuid(), "PRODUCT_600 hit model score great then 457");
//            RuleResult result = currentResultList.stream().filter(elem -> {
//                return !(elem.getRuleName().equalsIgnoreCase(BlackListTypeEnum.WORK_ADDRESS_INVALID.getMessage()) ||
//                        elem.getRuleName().equalsIgnoreCase(BlackListTypeEnum.SCORE_MODEL_PRODUCT_600_V1.getMessage()));
//            }).findFirst().get();
//            SysAutoReviewRule rule = allRules.get(result.getRuleName());
//            String respMessage =
//                    StringUtils.isEmpty(result.getDesc()) ? result.getRealValue() : result.getDesc();
//
//            List<OrdBlack> ordBlacks = new ArrayList<>();
//            OrdBlack ordBlack = new OrdBlack();
//            ordBlack.setOrderNo(order.getUuid());
//            ordBlack.setUserUuid(order.getUserUuid());
//            ordBlack.setResponseMessage(respMessage);
//            ordBlack.setUuid(UUIDGenerateUtil.uuid());
//            ordBlack.setRuleHitNo(rule.getRuleType() + "-" + rule.getRuleDetailType());
//            ordBlack.setRuleRealValue(result.getRealValue());
//            ordBlack.setRuleValue(rule.getRuleValue());
//            ordBlack.setRuleRejectDay(rule.getRuleRejectDay());
//            ordBlacks.add(ordBlack);
//            riskResultDao.addBlackList(ordBlacks);
//        }
//        return DEFAULT_REJECT_RESULT;
//    }

    private RuleSetExecutedResult abTestAfterRejectByScoreModel(Map<String, SysAutoReviewRule> allRules, OrdOrder order,
                                                                ScoreModelEnum scoreModelEnum) {

        boolean useScoreSwitchOpen = "true".equalsIgnoreCase(redisClient.get(RedisContants.SCORE_MODEL_SWITCH_USE_SCORE + scoreModelEnum.name()));
        if (!useScoreSwitchOpen) {
            log.info("use score switch is not open");
            return DEFAULT_REJECT_RESULT;
        }
        //进行ABTest--》
        //查看评分结果，如果大于490分，以10%的概率通过200单
        Integer currentCount = userRiskService.getABTestOrderIssuedCount(RuleConstants.ordBlackRemarks.get(scoreModelEnum.name()));

        String strMaxCount = redisClient.get(RedisContants.SCORE_MODEL_TEST_MAX_COUNT + scoreModelEnum.name());
        Integer maxCount = SCORE_MODEL_MAX_PASS_COUNT_PRODUCT_600;
        if (StringUtil.isNotEmpty(strMaxCount)) {
            maxCount = Integer.valueOf(strMaxCount);
        }

        if (currentCount > maxCount) {
            log.info("exceed 600 score model abTest");
            //大于200单，reject
            return DEFAULT_REJECT_RESULT;
        }

        OrderScore orderScore = orderScoreService.getLatestScoreWithModel(order.getUuid(), scoreModelEnum);
        if (orderScore == null) {
            //无,走后续流程
            log.info("not exists orderScore info");
            return DEFAULT_REJECT_RESULT;
        }

        ScoreTemplate template = scoreTemplateDao.getScoreTemplateByModelNameAndVersion(scoreModelEnum.name(), orderScore.getVersion());

        //通过概率占比
        String testRate = redisClient.get(RedisContants.SCORE_MODEL_TEST_RATE + scoreModelEnum.name());
        if (StringUtil.isEmpty(testRate)) {
            testRate = "0.1";
        }
        boolean scorePass = orderScore.getTotalScore().compareTo(template.getTotalThresholdScore()) > 0 && randomABTestPass(Double.valueOf(testRate));
        if (scorePass) {
            log.info("the order is pass for model score and random rate");
            //随机标记是否走人工，50%
//            boolean manualReview = randomABTestPass(0.5);
            //查询免核的数量
            int count = userRiskService.getABTestNotManualReviewCount(RuleConstants.ordBlackRemarks.get(scoreModelEnum.name()));
            int totalLoanCount = Integer.valueOf(redisClient.get(RedisContants.SCORE_MODEL_LOAN_COUNT + scoreModelEnum.name()));
            boolean manualReview = true;
            if (count < totalLoanCount) {
                manualReview = false;
            }
            //通过,disabled掉ordRisk中的记录
            ruleResultService.disabledOrdBlackWithRemark(order.getUuid(), RuleConstants.ordBlackRemarks.get(scoreModelEnum.name()));
            orderScoreService.setFlags(order.getUuid(), scoreModelEnum, false, true, manualReview);
            return new RuleSetExecutedResult(true, null);
        } else {
            log.info("the order does not suitable for score model");
            orderScoreService.setFlags(order.getUuid(), scoreModelEnum, false, false, false);
        }
        return DEFAULT_REJECT_RESULT;
    }

    private boolean randomABTestPass(double rate) {
        //10%的概率
        Random random = new Random();
        double nextDouble = random.nextDouble();
        if (nextDouble <= rate) {
            return true;
        }
        return false;
    }

}
