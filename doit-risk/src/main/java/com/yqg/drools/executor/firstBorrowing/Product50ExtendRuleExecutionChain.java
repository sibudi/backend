package com.yqg.drools.executor.firstBorrowing;

import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.drools.beans.RuleSetExecutedResult;
import com.yqg.drools.executor.RuleResultService;
import com.yqg.drools.executor.base.BaseExecutionChain;
import com.yqg.drools.executor.base.FlowEnum;
import com.yqg.drools.extract.ExtendModelExtractor;
import com.yqg.drools.model.ExtendModel;
import com.yqg.drools.model.ModelScoreResult;
import com.yqg.drools.model.base.RuleResult;
import com.yqg.drools.service.OrderScoreService;
import com.yqg.drools.utils.RuleUtils;
import com.yqg.enums.ScoreModelEnum;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.user.service.UserRiskService;
import com.yqg.service.util.RuleConstants;
import com.yqg.system.entity.SysAutoReviewRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/***
 * this execution to execute rules after the 50 rejected rule passed. these rule is additional condition to make sure the order can by pass
 */
@Service
@Slf4j
public class Product50ExtendRuleExecutionChain extends BaseExecutionChain implements InitializingBean {

    @Autowired
    private UserRiskService userRiskService;
    @Autowired
    private RuleResultService ruleResultService;
    @Autowired
    private ExtendModelExtractor extendModelExtractor;
    @Autowired
    private OrderScoreService orderScoreService;

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
    protected FlowEnum getFLowType() {
        return FlowEnum.PRODUCT_50_EXTEND;
    }


    @Override
    protected List<Object> addExtendFact(OrdOrder order, List<Object> facts) {
        //添加100评分结果数据
        Optional<ExtendModel> extendModel = extendModelExtractor.extractModel(order);
        if (extendModel.isPresent()) {
            //可能之前已经有这个对象
            Optional<Object> fact = facts.stream().filter(elem -> elem instanceof ExtendModel).findFirst();
            if (fact.isPresent()) {
                facts.remove(fact.get());
            }
            facts.add(extendModel.get());
        }
        return facts;
    }


    @Override
    public RuleSetExecutedResult postProcessExecuteResult(RuleSetExecutedResult result, Map<String, SysAutoReviewRule> allRules, OrdOrder order) {
        return super.doPostProcessForExecutedResult(result, allRules, order, BlackListTypeEnum.NO_PRODUCT_50_EXTEND_RULE_HIT);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initChains(null, null);
    }

//
//    @Override
//    protected void afterRejectResult(Map<String, SysAutoReviewRule> allRules, OrdOrder order) throws Exception {
//        //设置评分标记为:
//        orderScoreService.setRulePassFlag(order, ScoreModelEnum.PRODUCT_50, false);
//    }
}
