package com.yqg.drools.executor.firstBorrowing;

import com.github.pagehelper.StringUtil;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.redis.RedisClient;
import com.yqg.drools.beans.RuleSetExecutedResult;
import com.yqg.drools.executor.RuleResultService;
import com.yqg.drools.executor.base.BaseExecutionChain;
import com.yqg.drools.executor.base.FlowEnum;
import com.yqg.drools.extract.ModelScoreResultExtractor;
import com.yqg.drools.model.ExtendModel;
import com.yqg.drools.model.ModelScoreResult;
import com.yqg.drools.service.OrderScoreService;
import com.yqg.drools.service.RiskScoreExecutor;
import com.yqg.enums.ScoreModelEnum;
import com.yqg.order.entity.OrdOrder;
import com.yqg.risk.dao.ScoreTemplateDao;
import com.yqg.risk.entity.OrderScore;
import com.yqg.risk.entity.ScoreTemplate;
import com.yqg.service.order.OrdService;
import com.yqg.service.user.service.UserRiskService;
import com.yqg.service.util.RuleConstants;
import com.yqg.system.entity.SysAutoReviewRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
public class Product100ExecutionChain extends BaseExecutionChain implements InitializingBean {

    @Autowired
    private Product50ExecutionChain product50ExecutionChain;
    @Autowired
    private Product100ExtendRuleExecutionChain product100ExtendRuleExecutionChain;

    @Autowired
    private OrdService ordService;

    @Autowired
    private UserRiskService userRiskService;
    @Autowired
    private RuleResultService ruleResultService;
    @Autowired
    private RiskScoreExecutor riskScoreExecutor;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private OrderScoreService orderScoreService;
    @Autowired
    private ScoreTemplateDao scoreTemplateDao;
    @Autowired
    private ModelScoreResultExtractor modelScoreResultExtractor;

    private static BigDecimal SCORE_THRESHOLD_PRD100 = new BigDecimal("715");

    @Override
    public boolean preFilter(OrdOrder order) {
        if (!order.getAmountApply().equals(RuleConstants.PRODUCT100)) {
            return false;
        }
        if (!userRiskService.isSuitableFor100RMBProduct(order)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void afterRejectResult(Map<String, SysAutoReviewRule> allRules, OrdOrder order) throws Exception {
        //设置评分标记为:
        orderScoreService.setRulePassFlag(order, ScoreModelEnum.PRODUCT_100, false);
        if (userRiskService.isSuitableFor100RMBProduct(order)) {
            //拒绝原因disabled掉
            ruleResultService.disabledOrdBlackWithRemark(order.getUuid(), RuleConstants.PRODUCT150TO80);
            ordService.changeOrderTo50RMBProduct(order);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initChains(product100ExtendRuleExecutionChain, product50ExecutionChain);
    }

    @Override
    protected FlowEnum getFLowType() {
        return FlowEnum.PRODUCT_100;
    }


    public void beforeExecuteInternal(OrdOrder order, Map<String, SysAutoReviewRule> allRules, List<Object> facts) {
        riskScoreExecutor.calculateScore(order, facts, ScoreModelEnum.PRODUCT_100);
    }

    @Override
    protected List<Object> addExtendFact(OrdOrder order, List<Object> facts) {
        //添加100评分结果数据
        Optional<ModelScoreResult> scoreResult = modelScoreResultExtractor.extractModel(order);
        if (scoreResult.isPresent()) {
            //可能之前已经有这个对象
//            facts = facts.stream().filter(elem->!(elem instanceof ModelScoreResult)).collect(Collectors.toList());
            Optional<Object> fact = facts.stream().filter(elem -> elem instanceof ModelScoreResult).findFirst();
            if (fact.isPresent()) {
                facts.remove(fact.get());
            }
            facts.add(scoreResult.get());
        }
        return facts;
    }


//    @Override
//    protected RuleSetExecutedResult abTestAfterReject(OrdOrder order, Map<String, SysAutoReviewRule> allRules, List<Object> facts) {
//        //100评分测试:
//
////        boolean useScoreSwitchOpen = "true".equalsIgnoreCase(redisClient.get(RedisContants.SCORE_MODEL_SWITCH_USE_SCORE + ScoreModelEnum.PRODUCT_100));
////        if (!useScoreSwitchOpen) {
////            log.info("use score switch is not open : product_100");
////            return DEFAULT_REJECT_RESULT;
////        }
////        //进行ABTest--》
////        //查看评分结果，如果大于490分，以10%的概率通过200单
////        Integer currentCount = userRiskService.getABTestOrderIssuedCount(RuleConstants.PRODUCT100WITHRISKSCORE);
////
////        String strMaxCount = redisClient.get(RedisContants.SCORE_MODEL_TEST_MAX_COUNT + ScoreModelEnum.PRODUCT_100);
////        Integer maxCount = 200;
////        if (StringUtil.isNotEmpty(strMaxCount)) {
////            maxCount = Integer.valueOf(strMaxCount);
////        }
////
////        if (currentCount > maxCount) {
////            log.info("exceed 100 score model abTest");
////            //大于200单，reject
////            return DEFAULT_REJECT_RESULT;
////        }
//
//        OrderScore orderScore = orderScoreService.getLatestScoreWithModel(order.getUuid(), ScoreModelEnum.PRODUCT_100);
//        if (orderScore == null) {
//            //无,走后续流程
//            log.info("not exists orderScore info");
//            return DEFAULT_REJECT_RESULT;
//        }
////
////        ScoreTemplate template = scoreTemplateDao.getScoreTemplateByModelNameAndVersion(ScoreModelEnum.PRODUCT_100.name(), orderScore.getVersion());
////
////        //通过概率占比
////        String testRate = redisClient.get(RedisContants.SCORE_MODEL_TEST_RATE+ScoreModelEnum.PRODUCT_100);
////        if (StringUtil.isEmpty(testRate)) {
////            testRate = "0.1";
////        }
////        boolean scorePass = orderScore.getTotalScore().compareTo(template.getTotalThresholdScore()) > 0 && randomABTestPass(Double.valueOf(testRate));
//        boolean scorePass = orderScore.getTotalScore().compareTo(SCORE_THRESHOLD_PRD100) > 0;
//        if (scorePass) {
//            log.info("the order is pass for model score and random rate, product 100");
//            //通过,disabled掉ordRisk中的记录
//            ruleResultService.disabledOrdBlackWithRemark(order.getUuid(), RuleConstants.PRODUCT100WITHRISKSCORE);
//            orderScoreService.setFlags(order.getUuid(), ScoreModelEnum.PRODUCT_100, false, true, false);
//            return new RuleSetExecutedResult(true, null);
//        } else {
//            log.info("the order does not suitable for score model");
//            orderScoreService.setFlags(order.getUuid(), ScoreModelEnum.PRODUCT_100, false, false, false);
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
