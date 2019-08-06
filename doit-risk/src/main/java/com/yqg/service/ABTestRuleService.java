package com.yqg.service;

import com.yqg.common.constants.SysParamContants;
import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.common.utils.JsonUtils;
import com.yqg.drools.model.base.RuleResult;
import com.yqg.drools.utils.JsonUtil;
import com.yqg.service.util.RuleConstants;
import com.yqg.service.system.service.SysParamService;
import com.yqg.service.user.service.UserRiskService;
import com.yqg.system.entity.SysAutoReviewRule;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 部分规则进行AB test验证逻辑开发
 */
@Service
@Slf4j
public class ABTestRuleService {

    private List<ABTestModel> abTestModelConfig;

    @Autowired
    private SysParamService sysParamService;

    @Autowired
    private UserRiskService userRiskService;

    @PostConstruct
    public void init() {
        //db配置中查询进行abtest的规则配置
        String param = sysParamService.getSysParamValue(SysParamContants.RISK_ABTEST_CONFIG);
        if(StringUtils.isEmpty(param)){
            return;
        }
        abTestModelConfig = JsonUtil.toList(param,ABTestModel.class);
    }


    public Optional<ABTestModel> getTheHitABTestModel(List<RuleResult> allRuleResults, Map<String, SysAutoReviewRule> allRules) {
        if (CollectionUtils.isEmpty(abTestModelConfig)) {
            return Optional.empty();
        }
        if (CollectionUtils.isEmpty(allRuleResults)) {
            return Optional.empty();
        }

        Optional<ABTestModel> hitModel = abTestModelConfig.stream()
                                                          .filter(elem -> elem.onlyMatchTheConfigRejectRule(allRuleResults, allRules)
                                                                          && elem.matchAllRules(allRuleResults))
                                                          .findFirst();

        //未命中
        if(!hitModel.isPresent()){
            return hitModel;
        }
        //check limit count-->检查放款的数量
        Integer sumCount = 0;
        for (ABTestRule abTestRule : hitModel.get().getTheRejectedRuleList()) {
            Integer count = userRiskService.getABTestOrderIssuedCount(RuleConstants.AB_TEST_RULES + abTestRule.getRuleName());
            if (count != null && count > 0) {
                sumCount += count;
            }
        }
        if(sumCount>=hitModel.get().getIssuedLimitCount()){
            log.info("exceed ABTest limit , rule: {}", JsonUtils.serialize(hitModel.get().getTheRejectedRuleList()));
            //已经超过上限
            return Optional.empty();
        }
        //没有超过上限可以放开进行放款--命中
        return hitModel;
    }


    public static void main(String[] args) {
         List<ABTestModel> models = new ArrayList<>();
        //公司电话是雅加达等放开城市，但公司地址不属于放开城市
        ABTestModel workAddress = new ABTestModel();

        ABTestRule hitRule = new ABTestRule();
        hitRule.setRuleName(BlackListTypeEnum.COMPANY_TEL_NOT_IN_JAKARTA.getMessage());
        hitRule.setRealValue("true"); //公司电话属于雅加达，，需要命中

        List<ABTestRule> hitList= new ArrayList<>();
        hitList.add(hitRule);

        //拒绝规则--》工作地址
        ABTestRule rule = new ABTestRule();
        rule.setRuleName(BlackListTypeEnum.WORK_ADDRESS_INVALID.getMessage());
        rule.setRealValue("true");

        workAddress.setTheRejectedRuleList(Arrays.asList(rule));
        workAddress.setHitRules(hitList);
        workAddress.setIssuedLimitCount(50);
        models.add(workAddress);


        //公司电话不是雅加达等放开城市，但公司地址属于放开城市
        ABTestModel tel = new ABTestModel();

        ABTestRule hitRuleTel = new ABTestRule();
        hitRuleTel.setRuleName(BlackListTypeEnum.WORK_ADDRESS_INVALID.getMessage());
        hitRuleTel.setRealValue("false"); //工作地址属于雅加达--》需要命中

        List<ABTestRule> hitListTel= new ArrayList<>();
        hitListTel.add(hitRuleTel);

        //拒绝规则是“公司电话”
        ABTestRule ruleTel= new ABTestRule();
        ruleTel.setRuleName(BlackListTypeEnum.COMPANY_TEL_NOT_IN_JAKARTA.getMessage());
        ruleTel.setRealValue("false");

        tel.setTheRejectedRuleList(Arrays.asList(ruleTel));
        tel.setHitRules(hitListTel);
        tel.setIssuedLimitCount(50);
        models.add(tel);


        //实名未通过且未提交保险卡
        ABTestModel  insu = new ABTestModel();

        ABTestRule hitRuleInsu = new ABTestRule();
        hitRuleInsu.setRuleName(BlackListTypeEnum.HAS_INSURANCE_CARD.getMessage());
        hitRuleInsu.setRealValue("false"); //无保险卡

        List<ABTestRule> hitListInsu= new ArrayList<>();
        hitListInsu.add(hitRuleInsu);

        //拒绝规则"实名未通过通过"
        ABTestRule ruleInsu= new ABTestRule();
        ruleInsu.setRuleName(BlackListTypeEnum.ADVANCE_VERIFY_RULE.getMessage());
        //ruleInsu.setRealValue("false");

        insu.setTheRejectedRuleList(Arrays.asList(ruleInsu));
        insu.setHitRules(hitListInsu);
        insu.setIssuedLimitCount(50);
        models.add(insu);



        //无通讯录，是ios设备
        ABTestModel ios = new ABTestModel();

        ABTestRule ioshitRule = new ABTestRule();
        ioshitRule.setRuleName(BlackListTypeEnum.IS_IOS.getMessage());
        ioshitRule.setRealValue("true"); //公

        List<ABTestRule> iosHitList= new ArrayList<>();
        iosHitList.add(ioshitRule);

        //拒绝规则--》工作地址
        ABTestRule contactRule = new ABTestRule();
        contactRule.setRuleName(BlackListTypeEnum.USER_CALL_RECORDS_KEY_INfO_EMPTY.getMessage());
        contactRule.setRealValue("true");

        ios.setTheRejectedRuleList(Arrays.asList(contactRule));
        ios.setHitRules(iosHitList);
        ios.setIssuedLimitCount(50);
        models.add(ios);



        System.err.println(JsonUtils.serialize(models));

    }


    @Getter
    @Setter
    public static class ABTestRule{
        private String ruleName;
        private String realValue;
    }

    /***
     * 符合AB test规则的流量：
     *  1) 拒绝规则只有 theOnlyRejectRule
     *  2) hitRules中所有规则在订单机审后都命中
     *  3) 放款的单不超过 issuedLimitCount
     */

    @Getter
    @Setter
    public static class ABTestModel {
        //必须命中的规则[theOnlyRejectRule除外的其他规则]
        List<ABTestRule> hitRules;
        //支持放开多条命中的拒绝规则
        List<ABTestRule> theRejectedRuleList;

        Integer issuedLimitCount;//放款订单上限限制

        /***
         * 仅仅命中AB test定义的拒绝规则
         * @param orderRuleResults
         * @param allRules
         * @return
         */
        public boolean onlyMatchTheConfigRejectRule(List<RuleResult> orderRuleResults, Map<String, SysAutoReviewRule> allRules) {
            if (CollectionUtils.isEmpty(theRejectedRuleList)) {
                return false;
            }
            List<RuleResult> rejectRules = orderRuleResults.stream().filter(elem -> {
                SysAutoReviewRule rule = allRules.get(elem.getRuleName());
                if (rule.getRuleResult() == 2 && rule.getRuleStatus() == 1 && elem.isPass()) {
                    //命中拒绝单
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
//
//            if(rejectRules.size()!=1){
//                return false;
//            }

            if(rejectRules.size()>theRejectedRuleList.size()){
                //命中的拒绝规则比AB test的规则多
                return  false;
            }
            //不在ABTest 中的拒绝规则个数
            Long notInABTestRejectRuleCount = rejectRules.stream().filter(elem -> theRejectedRuleList.stream()
                                                                   .filter(abTest -> abTest.getRuleName().equalsIgnoreCase(elem.getRuleName())).count() <= 0)
                                .count();
            if(notInABTestRejectRuleCount>0){
                return false;
            }
            //拒绝的规则都在ABTest规则的规则中，则
            return true;
        }

        /***
         * 订单审核结果命中所有需要的规则
         * @param orderRuleResults
         * @return
         */
        public boolean matchAllRules(List<RuleResult> orderRuleResults) {
            if (CollectionUtils.isEmpty(hitRules)) {
                return false;
            }
            if (CollectionUtils.isEmpty(orderRuleResults)) {
                return false;
            }
            //hitRules有规则不在orderRuleResults中
            Long notHitCount = hitRules.stream().filter(elem -> {
                //hitRule中規則不在orderResult中
                return orderRuleResults.stream().filter(e1 -> e1.getRuleName().equals(elem.getRuleName()) && e1.getRealValue().equals(elem.getRealValue()))
                        .count() == 0;
            }).count();
            return notHitCount ==0;
        }
    }
}
