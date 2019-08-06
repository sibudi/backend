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
import com.yqg.service.order.OrdService;
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
 * this execution to execute rules after the 100 rejected rule passed. these rule is additional condition to make sure the order can by pass
 */
@Service
@Slf4j
public class Product100ExtendRuleExecutionChain extends BaseExecutionChain  implements InitializingBean {

    @Autowired
    private UserRiskService userRiskService;
    @Autowired
    private Product50ExecutionChain product50ExecutionChain;
    @Autowired
    private RuleResultService ruleResultService;
    @Autowired
    private ExtendModelExtractor extendModelExtractor;
    @Autowired
    private OrderScoreService orderScoreService;
    @Autowired
    private OrdService ordService;

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
    protected FlowEnum getFLowType() {
        return FlowEnum.PRODUCT_100_EXTEND;
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
        List<RuleResult> flowRuleResultList = this.getRuleResultDetailList();
        if (!CollectionUtils.isEmpty(flowRuleResultList)) {
            Optional<RuleResult> firstPassRule = flowRuleResultList.stream().filter(elem -> elem.isPass()).findFirst();
            if (firstPassRule.isPresent()) {
                //pass
                log.info("pass for 100rmd extend rules");
                return BaseExecutionChain.DEFAULT_PASS_RESULT;
            }
        }
        SysAutoReviewRule rule = allRules.get(BlackListTypeEnum.NO_PRODUCT_100_EXTEND_RULE_HIT.getMessage());
        if (rule != null && rule.getRuleResult() == 2) {
            //reject, insert reject rule
            List<RuleResult> limitResultList = new ArrayList<>();
            RuleResult ruleResult = RuleUtils.buildHitRuleResult(BlackListTypeEnum.NO_PRODUCT_100_EXTEND_RULE_HIT.getMessage(),
                    "true", rule.getRuleDesc()

            );
            limitResultList.add(ruleResult);
            ruleResultService.batchRecordRuleResult(order, allRules, limitResultList, getFLowType());
            return new RuleSetExecutedResult(false, rule);
        } else {
            log.info("not config the product 100 extend rule reject rule");
        }

        return result;

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initChains(null, product50ExecutionChain);
    }


    @Override
    protected void afterRejectResult(Map<String, SysAutoReviewRule> allRules, OrdOrder order) throws Exception {
        //设置评分标记为:
        orderScoreService.setRulePassFlag(order, ScoreModelEnum.PRODUCT_100, false);
        if (userRiskService.isSuitableFor100RMBProduct(order)) {
            //拒绝原因disabled掉
            ruleResultService.disabledOrdBlackWithRemark(order.getUuid(), RuleConstants.PRODUCT50TO80_FOR_EXTEND_RULE);
            ordService.changeOrderTo50RMBProduct(order);
        }
    }
}
