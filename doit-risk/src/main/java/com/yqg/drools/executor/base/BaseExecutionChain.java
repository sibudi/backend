package com.yqg.drools.executor.base;


import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.common.utils.JsonUtils;
import com.yqg.drools.model.ModelScoreResult;
import com.yqg.drools.utils.JsonUtil;
import com.yqg.drools.utils.RuleUtils;
import com.yqg.ojk.entity.OjkDataTotal;
import com.yqg.risk.dao.FlowRuleSetDao;
import com.yqg.risk.entity.RuleParam;
import com.yqg.drools.beans.RuleSetExecutedResult;
import com.yqg.drools.executor.ExecutorUtil;
import com.yqg.drools.executor.RuleResultService;
import com.yqg.drools.model.base.RuleConditionModel;
import com.yqg.drools.model.base.RuleResult;
import com.yqg.drools.service.RuleService;
import com.yqg.order.dao.OrdBlackDao;
import com.yqg.order.entity.OrdBlack;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.order.OrdService;
import com.yqg.system.entity.SysAutoReviewRule;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@Getter
public class BaseExecutionChain {

    @Autowired
    private FlowRuleSetDao flowRuleSetDao;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private RuleResultService ruleResultService;
    @Autowired
    private ExecutorUtil executorUtil;
    @Autowired
    private OrdBlackDao ordBlackDao;
    @Autowired
    private OrdService ordService;

    protected BaseExecutionChain passExecutionChain;

    protected BaseExecutionChain rejectExecutionChain;

    private List<RuleResult> ruleResultDetailList = null;

    public static RuleSetExecutedResult DEFAULT_REJECT_RESULT  = new RuleSetExecutedResult(false,null);
    public static RuleSetExecutedResult DEFAULT_PASS_RESULT  = new RuleSetExecutedResult(true,null);

    protected void initChains(BaseExecutionChain passChain,BaseExecutionChain rejectChain){
        this.passExecutionChain = passChain;
        this.rejectExecutionChain = rejectChain;
    }


    public static void main(String[] args) {
        List<Object> facts = new ArrayList<>();
        ModelScoreResult res = new ModelScoreResult();
        res.setProduct600ScoreV2(new BigDecimal("100"));
        res.setProduct600Score(new BigDecimal("120"));
       // res.setProduct100Score(new BigDecimal("744"));
        facts.add(res);
        test1(facts);
    }

    public  static void  test1(List<Object> facts){



        if(process100(facts)){
            System.err.println("flow 100 extend process");
            proce100Extend(facts);
        }
    }

    private static boolean process100(List<Object> facts){

      //  facts = facts.stream().filter(elem->!(elem instanceof ModelScoreResult)).collect(Collectors.toList());
        Optional<Object> fact  = facts.stream().filter(elem-> elem instanceof ModelScoreResult).findFirst();
        if(fact.isPresent()){
            facts.remove(fact.get());
        }
//        int index = -1;
//        for(int i=0;i<facts.size(); i++){
//            if(facts.get(i) instanceof ModelScoreResult){
//                index = i;
//            }
//        }

        System.err.println("flow 100 add");
        ModelScoreResult res = new ModelScoreResult();
        res.setProduct600ScoreV2(new BigDecimal("100"));
        res.setProduct600Score(new BigDecimal("120"));
        res.setProduct100Score(new BigDecimal("744"));
//        if(index!=-1){
//            facts.remove(index);
//        }
        facts.add(res);

        System.err.println("not extend: "+ JsonUtils.serialize(facts));
        return true;
    }

    private static boolean proce100Extend(List<Object> facts){
        System.err.println("extend: "+ JsonUtils.serialize(facts));
        return true;
    }




    public RuleSetExecutedResult execute(OrdOrder order, Map<String, SysAutoReviewRule> allRules, List<Object> facts) throws Exception {
        log.info("current flow to be executed: {}",getFLowType());
        //common condition
        //Janhsen: check if order in manual blacklist 
        if(hasRejectRules(order.getUuid())){
            return null;
        }
        if (preFilter(order)) {
            RuleSetExecutedResult executedResult = executeInternal(order, allRules, facts);
            if (executedResult.isRuleSetResult()) {
                RuleSetExecutedResult passCheck = afterPassResult(allRules, order, facts);
                if (passCheck != null && !passCheck.isRuleSetResult()) {
                    //拒绝则直接拒掉
                    return passCheck;
                }
                //the amountApply maybe modified in afterPassResult method, need to get the new order info.
                order = ordService.getOrderByOrderNo(order.getUuid());
                if (passExecutionChain != null) {
                    RuleSetExecutedResult tmp = passExecutionChain.execute(order, allRules, facts);
                    executedResult = tmp != null ? tmp : executedResult;
                }
            } else {
                RuleSetExecutedResult abTestResult = abTestAfterReject(order,allRules,facts);
                if(abTestResult.isRuleSetResult()){
                    //AB test返回成功，则按照ABTest操作,不执行后续的流程
                    log.info("abTest success");
                    return abTestResult;
                }
                afterRejectResult(allRules, order);
                //the amountApply maybe modified in afterRejectResult method, need to get the new order info.
                order = ordService.getOrderByOrderNo(order.getUuid());
                if (rejectExecutionChain != null) {
                    RuleSetExecutedResult tmp = rejectExecutionChain.execute(order, allRules, facts);
                    executedResult = tmp != null ? tmp : executedResult;
                }
            }
            return executedResult;
        }else{
            //如果不符合当前可跑的规则，跑其下面的pass 规则
            if(passExecutionChain!=null){
                RuleSetExecutedResult nextFlowResult = passExecutionChain.execute(order,allRules,facts);
                return nextFlowResult;
            }
        }
        return null;
    }

    private boolean hasRejectRules(String orderNo){
        OrdBlack searchParam = new OrdBlack();
        searchParam.setDisabled(0);
        searchParam.setOrderNo(orderNo);
        List<OrdBlack> searchResult = ordBlackDao.scan(searchParam);
        if(CollectionUtils.isEmpty(searchResult)){
            return false;
        }
        return true;
    }

    private RuleSetExecutedResult executeInternal(OrdOrder order, Map<String, SysAutoReviewRule> allRules, List<Object> facts){

        beforeExecuteInternal(order, allRules, facts);

        //add extend fact for this flow
        facts = addExtendFact(order, facts);

        FlowEnum currentFlow = getFLowType();
        //设置哪些规则需要执行
        List<SysAutoReviewRule> ruleList = flowRuleSetDao.getExecutedRulesByFlowName(Arrays.asList(currentFlow.name()));
        Map<String, SysAutoReviewRule> configMap = ruleList.stream().collect(Collectors.toMap(SysAutoReviewRule::getRuleDetailType,
                Function.identity()));

        if (CollectionUtils.isEmpty(ruleList)) {
            return null;
        }
        //设置可执行规则
        facts = addRuleCondition(configMap, order, facts);
        //设置规则参数和规则顺序
        List<RuleParam> ruleParams = flowRuleSetDao.getRuleParamsByFlowName(Arrays.asList(currentFlow.name()));
        Map<String, String> ruleParamMap = ruleParams.stream().collect(Collectors.toMap(RuleParam::getRuleDetailType, RuleParam::getThresholdValue));
        for (Map.Entry<String, SysAutoReviewRule> item : configMap.entrySet()) {
            item.getValue().setRuleValue(ruleParamMap.get(item.getKey()));
        }
        //调用执行
        List<RuleResult> resultList = ruleService.executeRules(currentFlow, facts, configMap);
        //结果保存
        ruleResultService.batchRecordRuleResult(order, allRules, resultList,currentFlow);

        //返回拒绝的规则===》分析是拒绝还是通过
        RuleSetExecutedResult ruleSetResult = ruleResultService
                .fetchRuleSetResult(allRules, resultList,order.getUuid());
        //记录当前运行的规则结果明细，方便后续处理
        ruleResultDetailList = resultList;

        afterExecuteInternal(order, allRules, facts);

        ruleSetResult = postProcessExecuteResult(ruleSetResult, allRules,order);
        return ruleSetResult;
    }

    protected boolean preFilter(OrdOrder order) {
        if (getFLowType() == null) {
            return false;
        }
        return true;
    }

    protected FlowEnum getFLowType(){
        return null;
    }


    protected void afterRejectResult(Map<String, SysAutoReviewRule> allRules, OrdOrder order) throws Exception{
        //TODO add some action after the flow is reject
    }

    private List<Object> addRuleCondition(Map<String, SysAutoReviewRule> allRules, OrdOrder order, List<Object> factList) {
        factList = factList.stream().filter(elem -> !(elem instanceof RuleConditionModel)).collect(Collectors.toList());

        Optional<RuleConditionModel> ruleCondition = executorUtil.buildRuleCondition(allRules, order);

        if (ruleCondition.isPresent()) {

            factList.add(ruleCondition.get());
        }
        return factList;
    }

    protected RuleSetExecutedResult afterPassResult(Map<String, SysAutoReviewRule> allRules, OrdOrder order, List<Object> facts) throws Exception{
        //TODO add ABTest info for some flow
        return new RuleSetExecutedResult(true,null);
    }

    //订单ABTest
    protected RuleSetExecutedResult abTestAfterReject(OrdOrder order, Map<String, SysAutoReviewRule> allRules, List<Object> facts) {
        return DEFAULT_REJECT_RESULT;
    }

    //在execution 内部执行前，执行
    protected void beforeExecuteInternal(OrdOrder order, Map<String, SysAutoReviewRule> allRules, List<Object> facts) {

    }

    //在execution 内部执行后，执行
    protected void afterExecuteInternal(OrdOrder order, Map<String, SysAutoReviewRule> allRules, List<Object> facts) {

    }

    // add extend fact for specified flow
    protected List<Object> addExtendFact(OrdOrder order,List<Object> facts){
        return facts;
    }

    // after the flow executed change the normal for specified situation
    protected RuleSetExecutedResult postProcessExecuteResult(RuleSetExecutedResult result, Map<String, SysAutoReviewRule> allRules, OrdOrder order) {
        return result;
    }


    protected RuleSetExecutedResult doPostProcessForExecutedResult(RuleSetExecutedResult result, Map<String, SysAutoReviewRule> allRules,
                                                                   OrdOrder order, BlackListTypeEnum typeEnum) {
        List<RuleResult> flowRuleResultList = this.getRuleResultDetailList();
        if (!CollectionUtils.isEmpty(flowRuleResultList)) {
            Optional<RuleResult> firstPassRule = flowRuleResultList.stream().filter(elem -> elem.isPass()).findFirst();
            if (firstPassRule.isPresent()) {
                //pass
                log.info("not hit rule: {}", typeEnum);
                return BaseExecutionChain.DEFAULT_PASS_RESULT;
            }
        }
        SysAutoReviewRule rule = allRules.get(typeEnum.getMessage());
        if (rule != null && rule.getRuleResult() == 2) {
            log.info("hit reject rule: {}",typeEnum);
            //reject, insert reject rule
            List<RuleResult> limitResultList = new ArrayList<>();
            RuleResult ruleResult = RuleUtils.buildHitRuleResult(typeEnum.getMessage(),
                    "true", rule.getRuleDesc()

            );
            limitResultList.add(ruleResult);
            ruleResultService.batchRecordRuleResult(order, allRules, limitResultList, getFLowType());
            return new RuleSetExecutedResult(false, rule);
        } else {
            log.info("not config reject rule: {}", typeEnum);
        }
        return result;

    }

}
