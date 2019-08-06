package com.yqg.drools.service;

import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.risk.dao.FlowRuleSetDao;
import com.yqg.risk.dao.RiskResultDao;
import com.yqg.drools.beans.RuleSetExecutedResult;
import com.yqg.drools.executor.ExecutorUtil;
import com.yqg.drools.extract.BaseExtractor;
import com.yqg.drools.model.*;
import com.yqg.drools.model.base.RuleConditionModel;
import com.yqg.drools.model.base.RuleResult;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.processor.ExecutorPreProcessor;
import com.yqg.drools.service.SpecifiedProductRuleService.Product100RMBCheckResult;
import com.yqg.service.user.service.UserRiskService;
import com.yqg.service.util.RuleConstants;
import com.yqg.order.entity.OrdDeviceInfo;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.order.OrdService;
import com.yqg.system.entity.SysAutoReviewRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/*****
 * @Author zengxiangcai
 * Created at 2018/2/23
 * @Email zengxiangcai@yishufu.com
 * 规则应用服务(每一个规则作为一个大类服务使用)
 ****/

@Service
@Slf4j
public class RuleApplicationService {
    @Autowired
    private FlowRuleSetDao flowRuleSetDao;

    @Autowired
    private List<BaseExtractor> extractorList;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private RuleResultAnalysisService ruleResultAnalysisService;

    @Autowired
    private ExecutorPreProcessor executorPreProcessor;

    @Autowired
    private DeviceService deviceService;
    @Autowired
    private SpecifiedProductRuleService specifiedProductRuleService;
    @Autowired
    private UserRiskService userRiskService;
    @Autowired
    private OrdService ordService;
    @Autowired
    private RiskResultDao riskResultDao;
    @Autowired
    private ExecutorUtil executorUtil;


    List<RuleSetEnum> firstBorrowRuleSet = Arrays
            .asList(RuleSetEnum.YQG_BLACK_LIST, RuleSetEnum.USER_IDENTITY,
                    RuleSetEnum.CONTACT_INFO, RuleSetEnum.USER_CALL_RECORDS, RuleSetEnum.SHORT_MESSAGE,
                    RuleSetEnum.INSTALL_APP, RuleSetEnum.DEVICE_INFO, RuleSetEnum.GOJEK,
                    RuleSetEnum.TOKOPEIDA, RuleSetEnum.FACEBOOK,
                    RuleSetEnum.BLACK_LIST_USER, RuleSetEnum.SPECIAL_RULE, RuleSetEnum.LOAN_INFO);

    List<RuleSetEnum> reBorrowRuleSet = Arrays
            .asList(RuleSetEnum.LATEST_LOAN, RuleSetEnum.RE_BORROWING_CONTACT,
                    RuleSetEnum.RE_BORROWING_CALL_RECORD, RuleSetEnum.RE_BORROWING_SHORT_MESSAGE,
                    RuleSetEnum.RE_BORROWING_INSTALLED_APP, RuleSetEnum.GOJEK, RuleSetEnum.FACEBOOK, RuleSetEnum.DEVICE_INFO,
                    RuleSetEnum.RE_BORROWING_BLACK_LIST_USER, RuleSetEnum.SPECIAL_RULE, RuleSetEnum.LOAN_HISTORY, RuleSetEnum.LOAN_INFO,
                    RuleSetEnum.RE_BORROWING_USER_IDENTITY);

    List<RuleSetEnum> autoCallRuleSet = Arrays
            .asList(RuleSetEnum.AUTO_CALL);

    List<RuleSetEnum> specifiedProduct100RMBRuleSet = Arrays
            .asList(RuleSetEnum.SPECIFIED_PRODUCT_100RMB);



    /***
     * 首借
     * @param order
     * @param allRules
     */
    public RuleSetExecutedResult firstBorrow(OrdOrder order, Map<String, SysAutoReviewRule> allRules)
            throws Exception {
        long startTime = System.currentTimeMillis();
        try {

            List<Object> facts = fetchRuleFacts(order, allRules, firstBorrowRuleSet);

            boolean  preExecuteResult = executorPreProcessor.preExecute(facts,order);
            if (!preExecuteResult) {
                //预处理失败
                log.warn("pre execute false, orderNo: {}", order.getUuid());
                return new RuleSetExecutedResult(true, null, false);
            }
            //设置规则适用条件
            Optional<RuleConditionModel> conditionModel = buildRuleCondition(allRules,order);
            if(conditionModel.isPresent()){
                facts.add(conditionModel.get());
            }

            //首借通用规则执行
            List<RuleResult> resultList = ruleService
                    .executeRules(RuleSetEnum.FIRST_BORROWING, facts, allRules);

            ruleResultAnalysisService.batchRecordRuleResult(order, allRules, resultList);

            RuleSetExecutedResult ruleSetResult = ruleResultAnalysisService
                    .fetchRuleSetResult(allRules, resultList,order.getUuid());
            boolean is100RmbProduct = false;
            //拒绝判定是否符合100rmb 产品
            if(!ruleSetResult.isRuleSetResult()){
                Product100RMBCheckResult applyResult = specifiedProductRuleService.apply100RMBProduct(resultList,allRules,order);
                if(applyResult.isHit100RMB()){
                    //符合100RMB产品，执行100RMB产品自有规则
                    log.info("hit 100 rmb product pre condition");
                    ruleSetResult = execute100RMBProductRules(order,allRules,facts);
                    if(ruleSetResult.isRuleSetResult()){
                        if(specifiedProductRuleService.hit100RMBProductABTestRule(order)){
                            //100rmb产品是否符合测试规则
                            //测试规则
                            ruleSetResult = ruleResultAnalysisService.fetch100RMBProductTestResult(order);
                        }else{
                            is100RmbProduct = true;
                            log.info("record 100rmb product...");
                            String remark = RuleConstants.AB_TEST_RULES + BlackListTypeEnum.SPECIAL_RULE_100_RMB_PRODUCT.getMessage();
                            riskResultDao.disableOrdBlackRecord(order.getUuid(), remark);
                            ordService.changeOrderTo100RMBProduct(order);
                        }
                    }
                }else{
                    //未命中100rmb的，开关没打开还是规则问题？
                    if(applyResult.getRejectRule()!=null){
                        //规则不符disabled原来的规则，新增拒绝规则
                        ruleSetResult = ruleResultAnalysisService.change100RMBProductResult(order,applyResult.getRejectRule(),
                                allRules.get(applyResult.getRejectRule().getRuleName()));
                    }
                }

            }
            //记录订单是否命中100rmb产品，方便更加订单规则明细统计是否命中100rmb产品(为了防止产品重跑多次计算结果不一致，没一个订单每跑一次都记录一次结果)
            specifiedProductRuleService.addRuleRecordFor100RMBProduct(allRules,order,is100RmbProduct);

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



    public RuleSetExecutedResult execute100RMBProductRules(OrdOrder order, Map<String, SysAutoReviewRule> allRules,List<Object> existsFacts) throws Exception {
        long startTime = System.currentTimeMillis();
        try {

            List<Object> facts = fetchRuleFacts(order, allRules, specifiedProduct100RMBRuleSet);
            //将需要的已经计算好的规则参数放入计算中
            boolean existsConditionModel = false;
            for(Object obj: existsFacts){
                if(obj instanceof RUserInfo){
                    facts.add(obj);
                }
                if(obj instanceof UserCallRecordsModel){
                    facts.add(obj);
                }
                if(obj instanceof DeviceModel){
                    facts.add(obj);
                }
                if(obj instanceof RuleConditionModel){
                    facts.add(obj);
                    existsConditionModel = true;
                }
            }
            if(!existsConditionModel){
                Optional<RuleConditionModel> conditionModel = buildRuleCondition(allRules,order);
                if(conditionModel.isPresent()){
                    facts.add(conditionModel.get());
                }
            }

            List<RuleResult> resultList = ruleService
                    .executeRules(RuleSetEnum.SPECIFIED_PRODUCT_100RMB, facts, allRules);

            ruleResultAnalysisService.batchRecordRuleResult(order, allRules, resultList);

            RuleSetExecutedResult ruleSetResult = ruleResultAnalysisService
                    .fetch100RMBProductResult(allRules, resultList, order);


            log.info("execute " + RuleSetEnum.SPECIFIED_PRODUCT_100RMB + " rules with ruleSetResult: "
                    + JsonUtils.serialize(ruleSetResult));

            return ruleSetResult;

        } catch (Exception e) {
            log.error("execute " + RuleSetEnum.SPECIFIED_PRODUCT_100RMB + " rules error, orderNo: " + order
                    .getUuid(), e);
            throw e;
        } finally {
            log.info("{} rules cost: {} ms", RuleSetEnum.SPECIFIED_PRODUCT_100RMB,
                    System.currentTimeMillis() - startTime);
        }
    }


    /***
     * 复借
     * @param order
     * @param allRules
     */
    public RuleSetExecutedResult reBorrow(OrdOrder order, Map<String, SysAutoReviewRule> allRules)
            throws Exception {
        long startTime = System.currentTimeMillis();
        try {

            List<Object> facts = fetchRuleFacts(order, allRules, reBorrowRuleSet);

            //设置规则适用条件
            Optional<RuleConditionModel> conditionModel = buildRuleCondition(allRules,order);
            if(conditionModel.isPresent()){
                //部分规则特殊处理
                conditionModel = executorUtil.filterConditionForReBorrowing(conditionModel, order);
                facts.add(conditionModel.get());
            }

            List<RuleResult> resultList = ruleService
                    .executeRules(RuleSetEnum.RE_BORROWING, facts, allRules);

            ruleResultAnalysisService.batchRecordRuleResult(order, allRules, resultList);

            RuleSetExecutedResult ruleSetResult = ruleResultAnalysisService
                    .fetchRuleSetResult(allRules, resultList,order.getUuid());


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
     * 外呼
     * @param order
     * @param allRules
     */
    public RuleSetExecutedResult  autoCall(OrdOrder order, Map<String, SysAutoReviewRule> allRules)
            throws Exception {
        long startTime = System.currentTimeMillis();
        try {

            List<Object> facts = fetchRuleFacts(order, allRules, autoCallRuleSet);


            List<RuleResult> resultList = ruleService
                    .executeRules(RuleSetEnum.AUTO_CALL, facts, allRules);

            ruleResultAnalysisService.batchRecordRuleResult(order, allRules, resultList);

            RuleSetExecutedResult ruleSetResult = ruleResultAnalysisService
                    .fetchRuleSetResult(allRules, resultList,order.getUuid());


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




    public List<Object> fetchRuleFacts(OrdOrder order, Map<String, SysAutoReviewRule> allRules,
                                        List<RuleSetEnum> ruleSets) throws Exception {
        Optional<KeyConstant> keyConstant = ruleService.getKeyConstants(allRules);
        List<Object> facts = new ArrayList<>();

        for (RuleSetEnum ruleSet : ruleSets) {
            Optional<BaseExtractor> extractor = extractorList.stream()
                    .filter(elem -> elem.filter(ruleSet)).findFirst();
            if (extractor.isPresent()) {
                try {
                    Optional<Object> model = extractor.get()
                            .extractModel(order, keyConstant.get());
                    if (model.isPresent()) {
                        facts.add(model.get());
                    }
                } catch (Exception e) {
                    log.error("extract model error", e);
                    throw e;
                }
            }
        }
        return facts;
    }

    public Optional<RuleConditionModel> buildRuleCondition(Map<String, SysAutoReviewRule> allRules, OrdOrder order) {
        RuleConditionModel model = new RuleConditionModel();
        if (allRules == null || order == null) {
            return Optional.empty();
        }
        OrdDeviceInfo deviceInfo = deviceService.getOrderDeviceInfo(order.getUuid());

        boolean isCashCashOrder = userRiskService.isCashCashOrder(order);

        Map<String, Boolean> ruleConditionMap = allRules.values().stream().collect(Collectors.toMap(SysAutoReviewRule::getRuleDetailType,
                elem -> isRuleNeedToExecute(deviceInfo, isCashCashOrder, elem)));

        model.setRuleExecuteMap(ruleConditionMap);
        return Optional.of(model);
    }

    private boolean isRuleNeedToExecute(OrdDeviceInfo deviceInfo,boolean isCashCashOrder,SysAutoReviewRule ruleConfig){
        SysAutoReviewRule.AppliedTargetEnum configEnum = SysAutoReviewRule.AppliedTargetEnum.enumOfValue(ruleConfig.getAppliedTo());
        if(configEnum==null){
            return true;
        }
        if(deviceInfo==null|| StringUtils.isEmpty(deviceInfo.getDeviceType())){
            return true;
        }
        String deviceType = deviceInfo.getDeviceType();
        switch (configEnum){
            case android:
                return "android".equalsIgnoreCase(deviceType);
            case iOS:
                return "iOS".equalsIgnoreCase(deviceType);
            case android_CashCash:
                return "android".equalsIgnoreCase(deviceType)&&isCashCashOrder;
            case iOS_CashCash:
                return "iOS".equalsIgnoreCase(deviceType)&&isCashCashOrder;
            case both_CashCash:
                return isCashCashOrder;
            case android_NORMAL:
                return "android".equalsIgnoreCase(deviceType)&& !isCashCashOrder;
            case iOS_NORMAL:
                return "iOS".equalsIgnoreCase(deviceType)&&!isCashCashOrder;
            case both_NORMAL:
                return !isCashCashOrder;
            case both:
                return true;
        }
        return true;
    }



}
