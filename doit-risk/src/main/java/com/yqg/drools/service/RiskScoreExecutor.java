package com.yqg.drools.service;

import com.yqg.common.constants.RedisContants;
import com.yqg.common.redis.RedisClient;
import com.yqg.risk.dao.ScoreTemplateDao;
import com.yqg.risk.entity.ScoreTemplate;
import com.yqg.drools.executor.base.FlowEnum;
import com.yqg.drools.extract.RiskScoreExtractor;
import com.yqg.drools.model.ScoreModel;
import com.yqg.drools.model.base.RiskScoreCondition;
import com.yqg.drools.model.base.ScoreRuleResult;
import com.yqg.enums.ScoreModelEnum;
import com.yqg.order.entity.OrdOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RiskScoreExecutor {
    @Autowired
    private RiskScoreExtractor riskScoreExtractor;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private ScoreTemplateDao scoreTemplateDao;
    @Autowired
    private OrderScoreService orderScoreService;
    @Autowired
    private RedisClient redisClient;

    /***
     *
     * @param order 订单信息
     * @param facts：其他模型计算出来的facts
     */
    public void calculateScore(OrdOrder order, List<Object> facts, ScoreModelEnum modelEnum) {
        boolean calculateSwitchOpen = "true".equalsIgnoreCase(redisClient.get(RedisContants.SCORE_MODEL_SWITCH_CALCULATE + modelEnum));
        if (!calculateSwitchOpen) {
            log.info("calculate switch is not open");
            return;
        }
        //执行评分
        try {
            Long startTime = System.currentTimeMillis();
            //Janhsen: create score model from the facts based on previous fetch rule
            Optional<ScoreModel> scoreModel = riskScoreExtractor.extractModel(order, facts);

            if (scoreModel.isPresent()) {
                List<Object> scoreFacts = new ArrayList<>();
                scoreFacts.add(scoreModel.get());

                //Janhsen: Get risk score template based on doit.ScoreTemplate
                RiskScoreCondition condition = buildScoreModelCondition(modelEnum);
                scoreFacts.add(condition);
                log.info("start calculate score. model: {} ", modelEnum);
                List<ScoreRuleResult> scoreList = ruleService.executeRiskScore(FlowEnum.RISK_SCORE, scoreFacts);
                orderScoreService.saveScoreInfo(scoreList, order, condition);
                log.info("calculate score finished. model: {} ,cost: {} ms", modelEnum, (System.currentTimeMillis() - startTime));
            }

        } catch (Exception e) {
            log.error("extract scoreMode error", e);
        }
    }


    private RiskScoreCondition buildScoreModelCondition(ScoreModelEnum modelEnum) {

        List<ScoreTemplate> list =
                scoreTemplateDao.getAllAvailableTemplate().stream().filter(elem -> modelEnum.name().equals(elem.getModelName())).collect(Collectors.toList());

        RiskScoreCondition condition = new RiskScoreCondition();
        if (CollectionUtils.isEmpty(list)) {
            return condition;
        }
        Map<String, ScoreTemplate> templateMap = list.stream().collect(Collectors.toMap(ScoreTemplate::getThresholdName, Function.identity()));
        condition.setScoreTemplateMap(templateMap);
        condition.setVersion(list.get(0).getVersion());
        condition.setBaseScore(list.get(0).getBaseScore());
        condition.setTotalThresholdScore(list.get(0).getTotalThresholdScore());
        return condition;
    }


    public void test(ScoreModel model, ScoreModelEnum modelEnum, OrdOrder order) {
        Long startTime = System.currentTimeMillis();
        List<Object> scoreFacts = new ArrayList<>();
        scoreFacts.add(model);
        RiskScoreCondition condition = buildScoreModelCondition(modelEnum);
        scoreFacts.add(condition);
        log.info("start calculate score. model: {} ", modelEnum);
        List<ScoreRuleResult> scoreList = ruleService.executeRiskScore(FlowEnum.RISK_SCORE, scoreFacts);
        orderScoreService.saveScoreInfo(scoreList, order, condition);
        log.info("calculate score finished. model: {} ,cost: {} ms", modelEnum, (System.currentTimeMillis() - startTime));
    }


}
