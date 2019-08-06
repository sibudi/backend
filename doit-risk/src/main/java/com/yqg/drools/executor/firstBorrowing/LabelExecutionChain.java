package com.yqg.drools.executor.firstBorrowing;

import com.yqg.drools.executor.ExecutorUtil;
import com.yqg.drools.executor.base.BaseExecutionChain;
import com.yqg.drools.executor.base.FlowEnum;
import com.yqg.drools.extract.ModelScoreResultExtractor;
import com.yqg.drools.model.ModelScoreResult;
import com.yqg.drools.service.RiskScoreExecutor;
import com.yqg.enums.ScoreModelEnum;
import com.yqg.order.entity.OrdOrder;
import com.yqg.system.entity.SysAutoReviewRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 处理达标器规则
 */
@Service
@Slf4j
public class LabelExecutionChain extends BaseExecutionChain implements InitializingBean {

    @Autowired
    private ExecutorUtil executorUtil;

    @Autowired
    private NonManualExecutionChain nonManualExecutionChain;

    @Autowired
    private RiskScoreExecutor riskScoreExecutor;

    @Autowired
    private ModelScoreResultExtractor modelScoreResultExtractor;

    /***
     * add model score result to fact list in order to execute rules related with score Result
     * @param order
     * @param facts
     * @return
     */
    @Override
    protected List<Object> addExtendFact(OrdOrder order, List<Object> facts) {
        //添加600评分结果数据
        Optional<ModelScoreResult> scoreResult = modelScoreResultExtractor.extractModel(order);
        if (scoreResult.isPresent()) {
            facts.add(scoreResult.get());
        }
        return facts;
    }

    @Override
    protected FlowEnum getFLowType() {
        return FlowEnum.LABELING_RULE;
    }

    @Override
    protected boolean preFilter(OrdOrder order) {
        return executorUtil.normalFlowFilter(order);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        super.initChains(nonManualExecutionChain, nonManualExecutionChain);
    }

    public void beforeExecuteInternal(OrdOrder order, Map<String, SysAutoReviewRule> allRules, List<Object> facts) {
        riskScoreExecutor.calculateScore(order, facts, ScoreModelEnum.PRODUCT_600);
        riskScoreExecutor.calculateScore(order, facts, ScoreModelEnum.PRODUCT_600_V2);
    }


}
