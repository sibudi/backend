package com.yqg.drools.service;

import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.executor.base.ApplicationFlowEnum;
import com.yqg.drools.executor.base.FlowEnum;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.base.*;
import com.yqg.order.dao.OrdDeviceInfoDao;
import com.yqg.system.dao.SysAutoReviewRuleDao;
import com.yqg.system.entity.SysAutoReviewRule;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RuleService {

    @Autowired
    private SysAutoReviewRuleDao sysAutoReviewRuleDao;

    private KieContainer kieContainer;

    @Autowired
    private OrdDeviceInfoDao ordDeviceInfoDao;

    @PostConstruct
    public void init() {
        KieServices ks = KieServices.Factory.get();
        kieContainer = ks.getKieClasspathContainer();
    }


    /***
     * 查询所有规则
     * @return
     */
    public Map<String, SysAutoReviewRule> getAllRules() {
        // 获取规则列表(1 有效 3测试)
        List<SysAutoReviewRule> rulesList = this.sysAutoReviewRuleDao
            .reviewRuleList();
        if (CollectionUtils.isEmpty(rulesList)) {
            return new HashMap<>();
        }
        return rulesList.stream().collect(Collectors.toMap(SysAutoReviewRule::getRuleDetailType,
            Function.identity()));
    }




    /***
     *
     * @param ruleSetEnum： 访问的规则集
     * @param facts：规则用到的入参
     * @param allRules 所有配置的规则(含有规则参数)
     * @return
     */
    public List<RuleResult> executeRules(RuleSetEnum ruleSetEnum, List<Object> facts,
        Map<String, SysAutoReviewRule> allRules) {
        String printJson="";
        for(Object fact: facts){
            //remove ruleConditionModel print
           if(fact instanceof RuleConditionModel){
               continue;
           }
            printJson+=JsonUtils.serialize(fact)+" | ";
        }
        log.info("rule: {} executed start ,facts: {}", ruleSetEnum.name(),
                printJson);

        long startTime = System.currentTimeMillis();

        //通用的信息
        List<Object> commonList = addCommonFacts(allRules);
        facts.addAll(commonList);

        //获取kSession
        StatelessKieSession kSession = kieContainer.newStatelessKieSession(ruleSetEnum.name());

        //定义规则集返回结果
        List<RuleResult> results = new ArrayList<>();

        kSession.setGlobal("ruleSetResultList", results);

        kSession.execute(facts);

        log.info("rule: {} executed,cost= {} ms,  result: {}", ruleSetEnum.name(),
            System.currentTimeMillis() - startTime, JsonUtils.serialize(results));
        if (CollectionUtils.isEmpty(results) && !RuleSetEnum.AUTO_CALL.equals(ruleSetEnum) && !RuleSetEnum.SPECIFIED_PRODUCT_100RMB.equals(ruleSetEnum)) {
            log.warn(" the matched rule is Empty");
            throw new RuntimeException("the match rule is empty");
        }
        return results;
    }

    /***
     *
     * @param flowEnum： 访问的规则集
     * @param facts：规则用到的入参
     * @param allRules 所有配置的规则(含有规则参数)
     * @return
     */
    public List<RuleResult> executeRules(FlowEnum flowEnum, List<Object> facts,
                                         Map<String, SysAutoReviewRule> allRules) {
        logFacts(facts);
        long startTime = System.currentTimeMillis();

        //通用的信息
        List<Object> commonList = addCommonFacts(allRules);
        facts.addAll(commonList);
        //获取kSession
        StatelessKieSession kSession = kieContainer.newStatelessKieSession(flowEnum.name());

        //定义规则集返回结果
        List<RuleResult> results = new ArrayList<>();

        kSession.setGlobal("ruleSetResultList", results);

        kSession.execute(facts);

        log.info("flowName: {} executed,cost= {} ms,  result: {}", flowEnum.name(),
                System.currentTimeMillis() - startTime, JsonUtils.serialize(results));

        if (CollectionUtils.isEmpty(results) && !flowEnum.name().startsWith("AUTO_CALL")) {
            log.warn(" the matched rule is Empty");
            throw new RuntimeException("the match rule is empty");
        }
        return results;
    }

    public List<ScoreRuleResult> executeRiskScore(FlowEnum flowEnum, List<Object> facts) {
        logFacts(facts);
        long startTime = System.currentTimeMillis();

        //获取kSession
        StatelessKieSession kSession = kieContainer.newStatelessKieSession(flowEnum.name());

        //定义规则集返回结果
        List<ScoreRuleResult> results = new ArrayList<>();


        kSession.setGlobal("scoreResultList", results);

        kSession.execute(facts);

        log.info("flowName: {} executed,cost= {} ms,  result: {}", flowEnum.name(),
                System.currentTimeMillis() - startTime, JsonUtils.serialize(results));

        return results;
    }


    public List<LoanLimitRuleResult> executeLoanLimit(ApplicationFlowEnum application, List<Object> facts){
        logFacts(facts);
        long startTime = System.currentTimeMillis();

        //获取kSession
        StatelessKieSession kSession = kieContainer.newStatelessKieSession(application.name());

        //定义规则集返回结果
        List<LoanLimitRuleResult> results = new ArrayList<>();

        kSession.setGlobal("limitRuleResultList", results);

        kSession.execute(facts);

        log.info("applicationName: {} executed,cost= {} ms,  result: {}", application.name(),
                System.currentTimeMillis() - startTime, JsonUtils.serialize(results));

        return results;
    }



    private void logFacts(List<Object> facts){
        List<Object> logFacts = facts.stream().filter(elem-> !isConditionMap(elem)).collect(Collectors.toList());
        log.info("the facts is: {}", JsonUtils.serialize(logFacts));
    }

    private boolean isConditionMap(Object obj){
        if(obj instanceof RuleConditionModel){
            return true;
        }
        if(obj instanceof RiskScoreCondition){
            return true;
        }
        return false;
    }


    /***
     * 公用的规则数据
     * 特殊词汇，规则阈值参数
     * @param allRules
     * @return
     */
    private List<Object> addCommonFacts(Map<String, SysAutoReviewRule> allRules) {
        List<Object> ruleFacts = new ArrayList<>();
//        //KeyConstant 关键的常量数据
//        Optional<KeyConstant> keyConstant = getKeyConstants(allRules);
//        if (keyConstant.isPresent()) {
//            ruleFacts.add(keyConstant.get());
//        }

        //规则参数：
        Map<String, String> ruleValeMap = getRuleValue(allRules);
        //规则顺序
        Map<String, Integer> orderMap = getRuleOrder(allRules);

        if (!CollectionUtils.isEmpty(ruleValeMap) || !CollectionUtils.isEmpty(orderMap)) {
            RuleThresholdValues values = new RuleThresholdValues();
            values.setThresholdMap(ruleValeMap);
            values.setOrderMap(orderMap);
            values.setRuleDescMap(getRuleDescMap(allRules));
            ruleFacts.add(values);
        }

        return ruleFacts;
    }

    private Map<String, String> getRuleDescMap(Map<String, SysAutoReviewRule> ruleMap) {
        return ruleMap.values().stream().filter(elem -> elem.getRuleSequence() != null)
            .collect(
                Collectors
                    .toMap(SysAutoReviewRule::getRuleDetailType,
                        SysAutoReviewRule::getRuleDesc));
    }

    /**
     * 获取规则阈值参数
     */
    private Map<String, String> getRuleValue(Map<String, SysAutoReviewRule> ruleMap) {
        return ruleMap.values().stream().filter(elem -> StringUtils.isNotEmpty(elem.getRuleValue()))
            .collect(
                Collectors
                    .toMap(SysAutoReviewRule::getRuleDetailType, SysAutoReviewRule::getRuleValue));
    }

    /***
     * 规则顺序
     * @param ruleMap
     * @return
     */
    private Map<String, Integer> getRuleOrder(Map<String, SysAutoReviewRule> ruleMap) {
        return ruleMap.values().stream().filter(elem -> elem.getRuleSequence() != null)
            .collect(
                Collectors
                    .toMap(SysAutoReviewRule::getRuleDetailType,
                        SysAutoReviewRule::getRuleSequence));
    }


    /****
     * 设置关键词常量{@link com.yqg.drools.model.KeyConstant}
     */
    public Optional<KeyConstant> getKeyConstants(Map<String, SysAutoReviewRule> ruleMap) {
        //暂时通过以后规则查找,后续可单独设置

        KeyConstant keyConstant = new KeyConstant();
        //敏感词
        keyConstant.setSensitiveWords(
            getNonEmptyRuleData(Arrays.asList(BlackListTypeEnum.CONTACT_SENSITIVI_COUNT), ruleMap));

        //同业词
        keyConstant.setInterrelatedWords(
            getNonEmptyRuleData(Arrays.asList(BlackListTypeEnum.SMS_SAME_COUNT), ruleMap));

        //逾期词
        keyConstant.setOverdueWords(
            getNonEmptyRuleData(Arrays.asList(BlackListTypeEnum.SMS_OVERDUE_COUNT), ruleMap));

        //负面词
        keyConstant.setNegativeWords(
            getNonEmptyRuleData(Arrays.asList(BlackListTypeEnum.SMS_NEGATIVE_COUNT), ruleMap));

        //拒绝词
        keyConstant.setRejectWords(
            getNonEmptyRuleData(Arrays.asList(BlackListTypeEnum.SMS_REFUSE_COUNT), ruleMap));

        //亲属词
        keyConstant.setRelativeWords(
            getNonEmptyRuleData(Arrays.asList(BlackListTypeEnum.CONTACT_RELATIVE_COUNT), ruleMap));

        //贷款app名称词汇
        keyConstant.setLoanAppWords(
            getNonEmptyRuleData(Arrays.asList(BlackListTypeEnum.USER_INSTALLED_SENSITIVE_APP_COUNT),
                ruleMap));

        //雅加达地区地址
        keyConstant.setJarkatAddressWords(
            getNonEmptyRuleData(Arrays.asList(BlackListTypeEnum.WORK_ADDRESS_INVALID), ruleMap));

        //雅加达地区号码
        keyConstant.setJarkatTelNumbers(
            getNonEmptyRuleData(Arrays.asList(BlackListTypeEnum.COMPANY_TEL_NOT_IN_JAKARTA),
                ruleMap));

        // 短信：逾期天数关键词
        keyConstant.setSmsRuleBody(
            getNonEmptyRuleData(Arrays.asList(BlackListTypeEnum.SMS_OVERDUE_MORETHAN_15DAYS_COUNT),
                ruleMap));

        //省份震区
        keyConstant.setEarthquakeArea(
                getNonEmptyRuleData(Arrays.asList(BlackListTypeEnum.PROVINCE_IN_EARTHQUAKE_AREA),
                        ruleMap));
        keyConstant.setEarthquakeCity(
                getNonEmptyRuleData(Arrays.asList(BlackListTypeEnum.CITY_IN_EARTHQUAKE_AREA),
                        ruleMap));

        //100RMB产品雅加达地址
        keyConstant.setJarkatAddressWordsFor100Rmb(
                getNonEmptyRuleData(Arrays.asList(BlackListTypeEnum.WORK_ADDRESS_INVALID_100RMB),
                        ruleMap));

        keyConstant.setJarkatAddressWordsNormal(
                getNonEmptyRuleData(Arrays.asList(BlackListTypeEnum.WORK_ADDRESS_NOT_VALID_NORMAL),
                        ruleMap)); //新开放城市(适用于非cashcash渠道)

        //男性高逾期职业
        keyConstant.setOverDuePositionMan(
                getNonEmptyRuleData(Arrays.asList(BlackListTypeEnum.TAX_SCOREMODEL600_POSITIONNAME_MEN),
                        ruleMap));

        //女性高逾期职业
        keyConstant.setOverDuePositionFeMen(
                getNonEmptyRuleData(Arrays.asList(BlackListTypeEnum.TAX_SCOREMODEL600_POSITIONNAME_FEMAL),
                        ruleMap));

        //男性高风险地区
        keyConstant.setHomeProviceMan(
                getNonEmptyRuleData(Arrays.asList(BlackListTypeEnum.HOME_PROVINCE_MALE_PRODUCT600),
                        ruleMap));

        //女性高风险地区
        keyConstant.setHomeProviceFeMen(
                getNonEmptyRuleData(Arrays.asList(BlackListTypeEnum.HOME_PROVINCE_FEMALE_PRODUCT600),
                        ruleMap));

        //男性高风险地区
        keyConstant.setHomeProviceMan150(
                getNonEmptyRuleData(Arrays.asList(BlackListTypeEnum.HOME_PROVINCE_MALE_PRODUCT600_150),
                        ruleMap));

        //女性高风险地区
        keyConstant.setHomeProviceFeMen150(
                getNonEmptyRuleData(Arrays.asList(BlackListTypeEnum.HOME_PROVINCE_FEMALE_PRODUCT600_150),
                        ruleMap));

        //男性高风险地区
        keyConstant.setHomeProviceMan80(
                getNonEmptyRuleData(Arrays.asList(BlackListTypeEnum.HOME_PROVINCE_MALE_PRODUCT600_80),
                        ruleMap));

        //app分类
        KeyConstant.AppCategoryEnum appCategorys[] = KeyConstant.AppCategoryEnum.values();
        Map<KeyConstant.AppCategoryEnum,String> categoryMap = new HashMap<>();
        for (KeyConstant.AppCategoryEnum item : appCategorys) {
            categoryMap.put(item,getNonEmptyRuleData(Arrays.asList(item.getRelatedRuleName()),
                    ruleMap));
        }
       keyConstant.setAppCategoryKeyWords(categoryMap);
        return Optional.of(keyConstant);
    }

    /***
     * 对敏感词，同业词等多个规则公用的信息，目前是每个规则都有配置，取其中一个非空的数据即可
     * @param rules
     * @param allRules
     * @return
     */
    private String getNonEmptyRuleData(List<BlackListTypeEnum> rules,
        Map<String, SysAutoReviewRule> allRules) {
        Optional<BlackListTypeEnum> ruleEnum = rules.stream().filter(elem -> {
            SysAutoReviewRule rule = allRules.get(elem.getMessage());
            if (rule != null && StringUtils.isNotEmpty(rule.getRuleData())) {
                return true;
            }
            return false;
        }).findFirst();
        if (ruleEnum.isPresent()) {
            return allRules.get(ruleEnum.get().getMessage()).getRuleData();
        }
        return null;
    }


//    private String getSensitiveTerms(String keyConstant) {
//
//    }

}
