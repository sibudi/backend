package com.yqg.drools.service;

import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.beans.RuleSetExecutedResult;
import com.yqg.drools.executor.ExecutorUtil;
import com.yqg.drools.executor.autoCall.FirstBorrowingAutoCallUniversalExecutionChain;
import com.yqg.drools.executor.autoCall.OwnerAutoCallExecutionChain;
import com.yqg.drools.executor.autoCall.ReBorrowingAutoCallExecutionChain;
import com.yqg.drools.executor.base.FlowEnum;
import com.yqg.drools.executor.firstBorrowing.LabelExecutionChain;
import com.yqg.drools.executor.firstBorrowing.Product100ExecutionChain;
import com.yqg.drools.executor.loanLimit.LoanLimitExecutor;
import com.yqg.drools.executor.reBorrowing.ReBorrowingUniversalExecutionChain;
import com.yqg.drools.extract.BaseExtractor;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.base.LoanLimitRuleResult;
import com.yqg.drools.model.base.RuleConditionModel;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.processor.ExecutorPreProcessor;
import com.yqg.drools.service.response.ApiBaseResponse;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.user.service.UserRiskService;
import com.yqg.service.util.RuleConstants;
import com.yqg.system.entity.SysAutoReviewRule;
import com.yqg.system.entity.SysAutoReviewRule.ExcludedForSpecifiedProduct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@Slf4j
public class ApplicationService {

    /***
     * 流程对应的参数分类
     */
    private List<RuleSetEnum> firstBorrowRuleSet = Arrays
            .asList(RuleSetEnum.YQG_BLACK_LIST, RuleSetEnum.USER_IDENTITY,
                    RuleSetEnum.CONTACT_INFO, RuleSetEnum.USER_CALL_RECORDS, RuleSetEnum.SHORT_MESSAGE,
                    RuleSetEnum.INSTALL_APP, RuleSetEnum.DEVICE_INFO, RuleSetEnum.GOJEK,
                    RuleSetEnum.TOKOPEIDA, RuleSetEnum.FACEBOOK,
                    RuleSetEnum.BLACK_LIST_USER, RuleSetEnum.SPECIAL_RULE, RuleSetEnum.LOAN_INFO,
                    RuleSetEnum.SPECIFIED_PRODUCT_100RMB);

    private List<RuleSetEnum> reBorrowRuleSet = Arrays
            .asList(RuleSetEnum.LATEST_LOAN, RuleSetEnum.RE_BORROWING_CONTACT,
                    RuleSetEnum.RE_BORROWING_CALL_RECORD, RuleSetEnum.RE_BORROWING_SHORT_MESSAGE,
                    RuleSetEnum.RE_BORROWING_INSTALLED_APP, RuleSetEnum.GOJEK, RuleSetEnum.FACEBOOK, RuleSetEnum.DEVICE_INFO,
                    RuleSetEnum.RE_BORROWING_BLACK_LIST_USER, RuleSetEnum.SPECIAL_RULE, RuleSetEnum.LOAN_HISTORY, RuleSetEnum.LOAN_INFO,
                    RuleSetEnum.RE_BORROWING_USER_IDENTITY, RuleSetEnum.LAST_LOAN_FOR_EXTEND);


    /***
     * 外呼流程对应的规则
     */
    private List<RuleSetEnum> autoCallRuleSet = Arrays
            .asList(RuleSetEnum.AUTO_CALL, RuleSetEnum.AUTO_CALL_USER);

    private List<RuleSetEnum> autoCallRuleSetForOwner = Arrays
            .asList(RuleSetEnum.AUTO_CALL_OWNER);

    private static List<String> PRD_100_LOAN_LIMIT_RULES = Arrays.asList("prd100_rule_01","prd100_rule_07","prd100_rule_08","prd100_rule_09",
            "prd100_rule_10",
            "prd100_rule_11");


    private List<String> ownerAutoCallRules = Arrays.asList(BlackListTypeEnum.AUTO_CALL_REJECT_OWNER_CALL_INVALID.getMessage(),
            BlackListTypeEnum.AUTO_CALL_REJECT_OWNER_EXCEED_LIMIT.getMessage());


    @Autowired
    private ExecutorPreProcessor executorPreProcessor;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private List<BaseExtractor> extractorList;

    @Autowired
    private LabelExecutionChain labelExecutionChain;
    @Autowired
    private Product100ExecutionChain product100ExecutionChain;
    @Autowired
    private FirstBorrowingAutoCallUniversalExecutionChain firstBorrowingAutoCallUniversalExecutionChain;
    @Autowired
    private ReBorrowingAutoCallExecutionChain reBorrowingAutoCallExecutionChain;
    @Autowired
    private OwnerAutoCallExecutionChain ownerAutoCallExecutionChain;

    @Autowired
    private RuleApplicationService ruleApplicationService;

    @Autowired
    private ExecutorUtil executorUtil;
    @Autowired
    private UserRiskService userRiskService;
    @Autowired
    private LoanLimitExecutor loanLimitExecutor;

    @Autowired
    private ReBorrowingUniversalExecutionChain reBorrowingUniversalExecutionChain;

    /***
     * 首借申请
     * @param order
     * @param allRules
     * @return
     * @throws Exception
     */
    public RuleSetExecutedResult apply(OrdOrder order, Map<String, SysAutoReviewRule> allRules) throws Exception {
        long startTime = System.currentTimeMillis();
        try {

            List<Object> facts = fetchRuleFacts(order, allRules, firstBorrowRuleSet);

            //check if the order need to be executed with rules according to the exists facts
            boolean preExecuteResult = executorPreProcessor.preExecute(facts, order);
            if (!preExecuteResult) {
                //预处理失败
                log.warn("pre execute false, orderNo: {}", order.getUuid());
                return new RuleSetExecutedResult(true, null, false);
            }
            RuleSetExecutedResult ruleSetResult = null;

            if (order.getAmountApply().equals(RuleConstants.PRODUCT100) && userRiskService.isSuitableFor100RMBProduct(order)) {
                //首借100rmb产品应该是从复审过来的，直接调用100rmb产品的规则进行处理
                ruleSetResult = product100ExecutionChain.execute(order, allRules, facts);
            } else {
                ruleSetResult = labelExecutionChain.execute(order, allRules, facts);
            }


            log.info("execute " + RuleSetEnum.FIRST_BORROWING + " rules with ruleSetResult: "
                    + JsonUtils.serialize(ruleSetResult));

            return ruleSetResult;

        } catch (Exception e) {
            log.error("execute " + RuleSetEnum.FIRST_BORROWING + " rules error, orderNo: " + order
                    .getUuid(), e);
            throw e;
        } finally {
            log.info("{} rules cost: {} ms", RuleSetEnum.FIRST_BORROWING,
                    System.currentTimeMillis() - startTime);
        }

    }

    /***
     * 复借
     * @param order
     * @param allRules
     */
    public RuleSetExecutedResult applyForReBorrowing(OrdOrder order, Map<String, SysAutoReviewRule> allRules) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            List<Object> facts = fetchRuleFacts(order, allRules, reBorrowRuleSet);

            RuleSetExecutedResult ruleSetResult = reBorrowingUniversalExecutionChain.execute(order, allRules, facts);
            log.info("execute " + RuleSetEnum.RE_BORROWING + " rules with ruleSetResult: "
                    + JsonUtils.serialize(ruleSetResult));
            return ruleSetResult;
        } catch (Exception e) {
            log.error("execute " + RuleSetEnum.RE_BORROWING + " rules error, orderNo: " + order
                    .getUuid(), e);
            throw e;
        } finally {
            log.info("{} rules cost: {} ms", RuleSetEnum.RE_BORROWING,
                    System.currentTimeMillis() - startTime);
        }
    }


    /***
     * Outbound rules
     * @param order
     * @param allRules
     */
    public RuleSetExecutedResult autoCall(OrdOrder order, Map<String, SysAutoReviewRule> allRules)
            throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            List<Object> facts = fetchRuleFacts(order, allRules, autoCallRuleSet);
            RuleSetExecutedResult ruleSetResult = null;
            if (order.getBorrowingCount() == 1) {
                ruleSetResult = firstBorrowingAutoCallUniversalExecutionChain.execute(order, allRules, facts);
            } else {
                ruleSetResult = reBorrowingAutoCallExecutionChain.execute(order, allRules, facts);
            }
            log.info("execute " + RuleSetEnum.AUTO_CALL + " rules with ruleSetResult: "
                    + JsonUtils.serialize(ruleSetResult));

            return ruleSetResult;

        } catch (Exception e) {
            log.error("execute " + RuleSetEnum.AUTO_CALL + " rules error, orderNo: " + order
                    .getUuid(), e);
            throw e;
        } finally {
            log.info("{} rules cost: {} ms", RuleSetEnum.AUTO_CALL,
                    System.currentTimeMillis() - startTime);
        }
    }

    /***
     * 本人外呼规则
     * @param order
     * @param allRules
     */
    public RuleSetExecutedResult autoCallForOwner(OrdOrder order, Map<String, SysAutoReviewRule> allRules)
            throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            List<Object> facts = fetchRuleFacts(order, allRules, autoCallRuleSetForOwner);

            RuleSetExecutedResult ruleSetResult = ownerAutoCallExecutionChain.execute(order, allRules, facts);

            log.info("execute " + FlowEnum.AUTO_CALL_OWNER + " rules with ruleSetResult: "
                    + JsonUtils.serialize(ruleSetResult));

            return ruleSetResult;

        } catch (Exception e) {
            log.error("execute " + FlowEnum.AUTO_CALL_OWNER + " rules error, orderNo: " + order
                    .getUuid(), e);
            throw e;
        } finally {
            log.info("{} rules cost: {} ms", FlowEnum.AUTO_CALL_OWNER,
                    System.currentTimeMillis() - startTime);
        }
    }


    public List<Object> fetchRuleFacts(OrdOrder order, Map<String, SysAutoReviewRule> allRules,
                                       List<RuleSetEnum> ruleSets) throws Exception {
        KeyConstant keyConstant = ruleService.getKeyConstants(allRules).get();
        List<Object> facts = new ArrayList<>();


        for (RuleSetEnum ruleSet : ruleSets) {
            //找到对应的提取参数的类
            Optional<BaseExtractor> extractor = extractorList.stream()
                    .filter(elem -> elem.filter(ruleSet)).findFirst();
            if (!extractor.isPresent()) {
                continue;
            }
            try {
                Optional<Object> model = extractor.get().extractModel(order, keyConstant);
                if (model.isPresent()) {
                    facts.add(model.get());
                }
            } catch (Exception e) {
                log.error("extract model error", e);
                throw e;
            }

        }
        return facts;
    }


    public ApiBaseResponse checkUserLoanLimit(String userUuid) {
        ApiBaseResponse response = ApiBaseResponse.createResponse();
        if (StringUtils.isEmpty(userUuid)) {
            return response.withCode("-1").withMessage("param is empty");
        }

        List<LoanLimitRuleResult> results = loanLimitExecutor.checkUserLoanLimit(userUuid);
        Map<String, String> respMap = new HashMap<>();
        response.withData(respMap);
        boolean hit = false;

        if (!CollectionUtils.isEmpty(results)) {
            hit =
                    results.stream().filter(elem -> elem.getPass() && (PRD_100_LOAN_LIMIT_RULES.contains(elem.getRuleName()) || elem.getRuleName().startsWith("prd50_"))).findFirst().isPresent();
        }
        respMap.put("result", hit ? "HIT" : "NOT_HIT");

        //save result

        log.info("response: {}", JsonUtils.serialize(response));
        return response;
    }



    private Optional<RuleConditionModel> buildAutoCallRunnableRuleCondition(Map<String, SysAutoReviewRule> toRunningRules, OrdOrder order) {
        //设置执行条件，
        //设置规则适用条件
        Optional<RuleConditionModel> conditionModel = executorUtil.buildRuleCondition(toRunningRules, order);

        /***
         * 首借外呼可能区分不同的金额，
         * 根据specified字段排除
         */
        if (!conditionModel.isPresent()) {
            return conditionModel;
        }
        Map<String, Boolean> conditionMap = conditionModel.get().getRuleExecuteMap();
        for (Map.Entry<String, Boolean> entity : conditionMap.entrySet()) {
            if (entity.getValue() != null && entity.getValue() == false) {
                //已经不执行
                continue;
            }
            SysAutoReviewRule rule = toRunningRules.get(entity.getKey());
            ExcludedForSpecifiedProduct excluded =
                    ExcludedForSpecifiedProduct.enumFromCode(rule.getSpecifiedProduct());
            switch (excluded) {
                case PRODUCT_50RMB:
                    conditionMap.put(entity.getKey(), !order.getAmountApply().equals(RuleConstants.PRODUCT50));
                    break;
                case PRODUCT_100RMB:
                    conditionMap.put(entity.getKey(), !order.getAmountApply().equals(RuleConstants.PRODUCT100));
                    break;
                case PRODUCT_600RMB:
                    conditionMap.put(entity.getKey(), !order.getAmountApply().equals(RuleConstants.PRODUCT600));
                    break;
                case PRODUCT_50_100:
                    conditionMap.put(entity.getKey(),
                            !order.getAmountApply().equals(RuleConstants.PRODUCT100) && !order.getAmountApply().equals(RuleConstants.PRODUCT50));
                    break;

            }

            //状态18的时候只进行本人外呼其他规则不执行
            if (order.getStatus() == OrdStateEnum.WAITING_CALLING_AFTER_FIRST_CHECK.getCode()) {
                if (!ownerAutoCallRules.contains(entity.getKey())) {
                    conditionMap.put(entity.getKey(), false);
                }
            }
        }


        return conditionModel;

    }


}
