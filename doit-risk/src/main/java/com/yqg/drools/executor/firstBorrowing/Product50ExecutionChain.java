package com.yqg.drools.executor.firstBorrowing;

import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.drools.beans.RuleSetExecutedResult;
import com.yqg.drools.executor.RuleResultService;
import com.yqg.drools.executor.base.BaseExecutionChain;
import com.yqg.drools.executor.base.FlowEnum;
import com.yqg.drools.extract.ModelScoreResultExtractor;
import com.yqg.drools.model.ModelScoreResult;
import com.yqg.drools.model.base.RuleResult;
import com.yqg.drools.service.OrderScoreService;
import com.yqg.drools.service.RiskScoreExecutor;
import com.yqg.drools.utils.RuleUtils;
import com.yqg.enums.ScoreModelEnum;
import com.yqg.order.entity.OrdOrder;
import com.yqg.risk.dao.RiskResultDao;
import com.yqg.service.user.service.UserRiskService;
import com.yqg.service.util.RuleConstants;
import com.yqg.system.entity.SysAutoReviewRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class Product50ExecutionChain extends BaseExecutionChain implements InitializingBean {

    @Autowired
    private UserRiskService userRiskService;
    @Autowired
    private OrderScoreService orderScoreService;
    @Autowired
    private RiskScoreExecutor riskScoreExecutor;
    @Autowired
    private ModelScoreResultExtractor modelScoreResultExtractor;
    @Autowired
    private Product50ExtendRuleExecutionChain product50ExtendRuleExecutionChain;
    @Autowired
    private RiskResultDao riskResultDao;
    @Autowired
    private RuleResultService ruleResultService;

    @Override
    public boolean preFilter(OrdOrder order) {
        if (!order.getAmountApply().equals(RuleConstants.PRODUCT50)) {
            return false;
        }
        if (!userRiskService.isSuitableFor100RMBProduct(order)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initChains(product50ExtendRuleExecutionChain, null);
    }

    @Override
    protected FlowEnum getFLowType() {
        return FlowEnum.PRODUCT_50;
    }

    @Override
    protected void afterRejectResult(Map<String, SysAutoReviewRule> allRules, OrdOrder order) throws Exception {
        //设置评分标记为:
        orderScoreService.setRulePassFlag(order, ScoreModelEnum.PRODUCT_50, false);
    }

    public void beforeExecuteInternal(OrdOrder order, Map<String, SysAutoReviewRule> allRules, List<Object> facts) {
        riskScoreExecutor.calculateScore(order, facts, ScoreModelEnum.PRODUCT_50);
    }

    @Override
    protected List<Object> addExtendFact(OrdOrder order, List<Object> facts) {
        //添加50评分结果数据
        Optional<ModelScoreResult> scoreResult = modelScoreResultExtractor.extractModel(order);
        if (scoreResult.isPresent()) {
            //可能之前已经有这个对象
            //facts = facts.stream().filter(elem->!(elem instanceof ModelScoreResult)).collect(Collectors.toList());
            Optional<Object> fact = facts.stream().filter(elem -> elem instanceof ModelScoreResult).findFirst();
            if (fact.isPresent()) {
                facts.remove(fact.get());
            }
            facts.add(scoreResult.get());
        }
        return facts;
    }


    @Override
    protected RuleSetExecutedResult afterPassResult(Map<String, SysAutoReviewRule> allRules, OrdOrder order, List<Object> facts) throws Exception {
        //TODO add ABTest info for some flow
        //检查是否超过测试量的限制
        SysAutoReviewRule rule = allRules.get(BlackListTypeEnum.FLOW_MAX_ISSUED_LIMIT.getMessage());
        if (rule != null && rule.getRuleResult() == 2) {
            //增加拒绝规则,暂时只放600单
            Integer totalCount = riskResultDao.totalIssuedByAmount(RuleConstants.PRODUCT50);
            if (totalCount != null && totalCount > 600) {
                List<RuleResult> limitResultList = new ArrayList<>();
                RuleResult ruleResult = RuleUtils.buildHitRuleResult(BlackListTypeEnum.FLOW_MAX_ISSUED_LIMIT.getMessage(),
                        totalCount.toString(),
                        rule.getRuleDesc()
                );
                limitResultList.add(ruleResult);
                ruleResultService.batchRecordRuleResult(order, allRules, limitResultList, getFLowType());
                return new RuleSetExecutedResult(false, rule);
            }

        } else {
            log.info("no rule FLOW_MAX_ISSUED_LIMIT");
        }
        return new RuleSetExecutedResult(true, null);
    }

//    @Override
//    protected RuleSetExecutedResult abTestAfterReject(OrdOrder order, Map<String, SysAutoReviewRule> allRules, List<Object> facts) {
//       if(true){
//           //暂时不放50
//           log.info("ignore the score limit rule");
//           return DEFAULT_REJECT_RESULT;
//       }
//        //50评分测试:
//        ScoreModelEnum modelName = ScoreModelEnum.PRODUCT_50;
////
////        boolean useScoreSwitchOpen = "true".equalsIgnoreCase(redisClient.get(RedisContants.SCORE_MODEL_SWITCH_USE_SCORE + modelName));
////        if (!useScoreSwitchOpen) {
////            log.info("use score switch is not open : product_50");
////            return DEFAULT_REJECT_RESULT;
////        }
////        //进行ABTest--》
////        //查看评分结果，如果大于490分，以10%的概率通过200单
////        Integer currentCount = userRiskService.getAutoReviewPassABTestOrderCount(RuleConstants.MODEL_RISK_SCORE_PASS_REMARK + modelName);
////
////        String strMaxCount = redisClient.get(RedisContants.SCORE_MODEL_TEST_MAX_COUNT + modelName);
////        Integer maxCount = 300;
////        if (StringUtil.isNotEmpty(strMaxCount)) {
////            maxCount = Integer.valueOf(strMaxCount);
////        }
////
////        if (currentCount > maxCount) {
////            log.info("exceed 50 score model abTest");
////            //大于200单，reject
////            return DEFAULT_REJECT_RESULT;
////        }
////
//        OrderScore orderScore = orderScoreService.getLatestScoreWithModel(order.getUuid(), modelName);
//        if (orderScore == null) {
//            //无,走后续流程
//            log.info("not exists orderScore info");
//            return DEFAULT_REJECT_RESULT;
//        }
////
////        ScoreTemplate template = scoreTemplateDao.getScoreTemplateByModelNameAndVersion(modelName.name(), orderScore.getVersion());
////
////        //通过概率占比
////        String testRate = redisClient.get(RedisContants.SCORE_MODEL_TEST_RATE + modelName);
////        if (StringUtil.isEmpty(testRate)) {
////            testRate = "1";
////        }
//        boolean scorePass = orderScore.getTotalScore().compareTo(new BigDecimal("690")) > 0;
//        if (scorePass) {
//            log.info("the order is pass for model score and random rate, " + modelName);
//            //通过,disabled掉ordRisk中的记录
//            ruleResultService.disabledOrdBlackWithRemark(order.getUuid(), RuleConstants.MODEL_RISK_SCORE_PASS_REMARK + modelName);
//            orderScoreService.setFlags(order.getUuid(), modelName, false, true, false);
//            return new RuleSetExecutedResult(true, null);
//        } else {
//            log.info("the order does not suitable for score model");
//            orderScoreService.setFlags(order.getUuid(), modelName, false, false, false);
//        }
//        return DEFAULT_REJECT_RESULT;
//    }

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