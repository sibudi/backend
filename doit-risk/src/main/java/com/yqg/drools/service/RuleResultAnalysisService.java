package com.yqg.drools.service;

import com.yqg.service.ABTestRuleService;
import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.risk.dao.RiskResultDao;
import com.yqg.drools.beans.RuleSetExecutedResult;
import com.yqg.drools.model.base.RuleResult;
import com.yqg.service.util.RuleConstants;
import com.yqg.order.entity.OrdBlack;
import com.yqg.order.entity.OrdBlackTemp;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdRiskRecord;
import com.yqg.risk.repository.OrderRiskRecordRepository;
import com.yqg.service.order.OrdService;
import com.yqg.system.entity.SysAutoReviewRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 ****/

@Service
@Slf4j
public class RuleResultAnalysisService {

    @Autowired
    private RiskResultDao riskResultDao;
    @Autowired
    private OrderRiskRecordRepository orderRiskRecordRepository;
    @Autowired
    private ABTestRuleService abTestRuleService;
    @Autowired
    private OrdService ordService;


    @Transactional(rollbackFor = Exception.class)
    public void batchRecordRuleResult(OrdOrder order, Map<String, SysAutoReviewRule> allRules,
                                      List<RuleResult> ruleResultList) {
        List<OrdRiskRecord> orderRiskRecords = new ArrayList<>();
        List<OrdBlack> ordBlacks = new ArrayList<>();
        List<OrdBlackTemp> ordBlackTemps = new ArrayList<>();

        ruleResultList.stream().forEach(elem -> {
            SysAutoReviewRule rule = allRules.get(elem.getRuleName());
            if (rule == null) {
                log.error("the rule name error: ,ruleName: " + elem.getRuleName());
            }
            String respMessage;
            String realValue;
            if (elem.getRuleName().equalsIgnoreCase(BlackListTypeEnum.DATA_EMPTY.name())) {
                respMessage =
                        StringUtils.isEmpty(elem.getDesc()) ? elem.getRealValue() : elem.getDesc();
                realValue = StringUtils.isEmpty(elem.getRealValue()) ? elem.getDesc()
                        : elem.getRealValue();
            } else {
                respMessage = rule.getRuleDesc();
                realValue = elem.getRealValue();
            }
            //插入ordRiskRecord表
            OrdRiskRecord record = new OrdRiskRecord();
            record.setOrderNo(order.getUuid());
            record.setUserUuid(order.getUserUuid());
            record.setRuleRealValue(realValue);

            record.setRuleType(rule.getRuleType());
            record.setRuleDetailType(rule.getRuleDetailType());
            record.setRuleDesc(rule.getRuleDesc());
            record.setUuid(UUIDGenerateUtil.uuid());
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            orderRiskRecords.add(record);

            //isHitRule
            boolean isHitRule = elem.isPass() && !elem.getRuleName()
                    .equalsIgnoreCase(BlackListTypeEnum.DATA_EMPTY.getMessage());

            //插入ordBlack表
            if (rule.getRuleStatus() == 1 && isHitRule) {
                OrdBlack ordBlack = new OrdBlack();
                ordBlack.setOrderNo(order.getUuid());
                ordBlack.setUserUuid(order.getUserUuid());
                ordBlack.setResponseMessage(respMessage);
                ordBlack.setUuid(UUIDGenerateUtil.uuid());
                ordBlack.setRuleHitNo(rule.getRuleType() + "-" + rule.getRuleDetailType());
                ordBlack.setRuleRealValue(elem.getRealValue());
                ordBlack.setRuleValue(rule.getRuleValue());
                ordBlack.setRuleRejectDay(rule.getRuleRejectDay());

                ordBlacks.add(ordBlack);
            } else if (rule.getRuleStatus() == 3 && isHitRule) {
                //插入ordBlackTemp表
                OrdBlackTemp ordBlackTemp = new OrdBlackTemp();
                ordBlackTemp.setOrderNo(order.getUuid());
                ordBlackTemp.setUserUuid(order.getUserUuid());
                ordBlackTemp.setResponseMessage(respMessage);
                ordBlackTemp.setUuid(UUIDGenerateUtil.uuid());
                ordBlackTemp.setRuleHitNo(rule.getRuleType() + "-" + rule.getRuleDetailType());
                ordBlackTemp.setRuleRealValue(elem.getRealValue());
                ordBlackTemp.setRuleValue(rule.getRuleValue());
                ordBlackTemps.add(ordBlackTemp);
            }


        });
        if (!CollectionUtils.isEmpty(orderRiskRecords)) {
            orderRiskRecordRepository.addRiskRecordList(orderRiskRecords);
        }
        if (!CollectionUtils.isEmpty(ordBlacks)) {
            //只插入第一条
            riskResultDao.addBlackList(Arrays.asList(ordBlacks.get(0)));
        }
        if (!CollectionUtils.isEmpty(ordBlackTemps)) {
            riskResultDao.addBlackTempList(ordBlackTemps);
//            orderBlackTempRepository.addBlackTempList(ordBlackTemps);
        }


    }

    /***
     * 初审复审规则结果判定【check是否拒绝用户】
     * @param allRules
     * @param ruleResultList
     * @return
     */
    public RuleSetExecutedResult fetchRuleSetResult(Map<String, SysAutoReviewRule> allRules,
                                                    List<RuleResult> ruleResultList, String orderNo) {
        List<RuleResult> rejectRules = fetchRejectedRules(allRules, ruleResultList);


        if (CollectionUtils.isEmpty(rejectRules)) {
            return new RuleSetExecutedResult(true, null);
        }

        Optional<RuleResult> hitSpecialRule = hitSpecialRuleWithRealNameVerifyFailed(rejectRules, ruleResultList, orderNo);
        if (hitSpecialRule.isPresent()) {
            //移除ordBlack中的拒绝记录【后台显示会使用到保险卡规则】
            riskResultDao.disableOrdBlackRecord(orderNo, RuleConstants.ONLY_REAL_NAME_VERIFY_FAILED_AND_HIT_SPECIAL_RULE_REMAK +
                    hitSpecialRule.get().getRuleName());
            //机审通过
            return new RuleSetExecutedResult(true, null);
        } else {
            //未命中特殊规则，看是否命中abtest规则
            log.info("start ab test");
            Optional<ABTestRuleService.ABTestModel> abTestResult = abTestRuleService.getTheHitABTestModel(ruleResultList, allRules);
            if (abTestResult.isPresent()) {
                //取得命中的reject规则:
                List<ABTestRuleService.ABTestRule> hitConfigRules = abTestResult.get().getTheRejectedRuleList().stream()
                        .filter(configItem -> rejectRules.stream()
                                .filter(rejectItem -> rejectItem.getRuleName().equalsIgnoreCase(configItem.getRuleName())).findFirst().isPresent())
                        .collect(Collectors.toList());

                String remark = RuleConstants.AB_TEST_RULES + hitConfigRules.get(0).getRuleName();
                riskResultDao.disableOrdBlackRecord(orderNo, remark);
                return new RuleSetExecutedResult(true, null);
            }
        }
        return new RuleSetExecutedResult(false, allRules.get(rejectRules.get(0).getRuleName()));

    }

    private List<RuleResult> fetchRejectedRules(Map<String, SysAutoReviewRule> allRules, List<RuleResult> ruleResultList) {
        return ruleResultList.stream().filter(elem -> {
                SysAutoReviewRule rule = allRules.get(elem.getRuleName());
                boolean isHitRule = elem.isPass() && !elem.getRuleName()
                        .equalsIgnoreCase(BlackListTypeEnum.DATA_EMPTY.getMessage());
                if (isHitRule && rule.getRuleStatus() == 1 && rule.getRuleResult() == 2) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
    }

    /**
     * 处理测试规则
     * @param order
     * @return
     */
    public RuleSetExecutedResult fetch100RMBProductTestResult(OrdOrder order) {
        log.info("hit 100RMB rule with non manual rule test");
        String remark = RuleConstants.AB_TEST_RULES + RuleConstants.NON_MANUAL_TEST_WITH_100RMB_PRODUCT;
        riskResultDao.disableOrdBlackRecord(order.getUuid(), remark);
        return new RuleSetExecutedResult(true, null);
    }

    /**
     * 记录不符合100rmb产品原因
     * @param order
     * @return
     */
    public RuleSetExecutedResult change100RMBProductResult(OrdOrder order,RuleResult ruleResult,SysAutoReviewRule ruleConfig) {
        log.info("change the reject result for not suitable for 100RMB product");
        //String remark = RuleConstants.AB_TEST_RULES + RuleConstants.NON_MANUAL_TEST_WITH_100RMB_PRODUCT;
        riskResultDao.disableOrdBlackRecord(order.getUuid(),"");
        //新增reject 规则
        OrdBlack ordBlack = new OrdBlack();
        ordBlack.setOrderNo(order.getUuid());
        ordBlack.setUserUuid(order.getUserUuid());
        ordBlack.setResponseMessage(ruleConfig.getRuleDesc());
        ordBlack.setUuid(UUIDGenerateUtil.uuid());
        ordBlack.setRuleHitNo(ruleConfig.getRuleType() + "-" + ruleConfig.getRuleDetailType());
        ordBlack.setRuleRealValue(ruleResult.getRealValue());
        ordBlack.setRuleValue(ruleConfig.getRuleValue());
        ordBlack.setRuleRejectDay(ruleConfig.getRuleRejectDay());
        riskResultDao.addBlackList(Arrays.asList(ordBlack));
        return new RuleSetExecutedResult(false, ruleConfig);
    }

    /***
     * 处理100RMB产品自由规则执行结果
     * @param allRules
     * @param ruleResultList
     * @param order
     * @return
     */
    public RuleSetExecutedResult fetch100RMBProductResult(Map<String, SysAutoReviewRule> allRules,
                                                          List<RuleResult> ruleResultList, OrdOrder order) throws Exception {
        //100rmb 产品通过？
        List<RuleResult> rejectedRules = fetchRejectedRules(allRules, ruleResultList);
        if (CollectionUtils.isEmpty(rejectedRules)) {
            //100rmb产品通过
            log.info("pass 100rmb additional rules");
            return new RuleSetExecutedResult(true, null);
        } else {
            //100rmb产品reject，通用规则也reject了，这样会导致ordBlack中有两条reject的记录,disabled掉前一条
            List<OrdBlack> ordBlackList = riskResultDao.getOrderBlackList(order.getUuid());
            if (!CollectionUtils.isEmpty(ordBlackList) && ordBlackList.size() > 1) {
                Integer id = ordBlackList.stream().min(Comparator.comparing(OrdBlack::getId)).get().getId();
                riskResultDao.disableOrdBlackRecordById(id, "100-product-冗余");
            }
            RuleResult firstRejectRule = rejectedRules.get(0);
            return new RuleSetExecutedResult(false, allRules.get(firstRejectRule.getRuleName()));
        }
    }

    /***
     * 仅命中实名拒绝一条规则且命中其他的一些特殊规则(实名拒绝不能是黑名单拒绝的)
     * @param rejectRules
     * @return
     */
    private Optional<RuleResult> hitSpecialRuleWithRealNameVerifyFailed(List<RuleResult> rejectRules, List<RuleResult> allRuleResults, String orderNo) {
        if (rejectRules.size() != 1) {
            return Optional.empty();
        }
        RuleResult firstRuleResult = rejectRules.get(0);
        if (!firstRuleResult.getRuleName().equals(BlackListTypeEnum.ADVANCE_VERIFY_RULE.getMessage())) {
            return Optional.empty();
        }
        if (RuleConstants.REAL_NAME_TAX_NUMBER_FRAUD_USER_DESC.equals
                (firstRuleResult.getRealValue())) {
            return Optional.empty();
        }
        //仅实名拒绝的并且命中指定的特殊规则并且不是欺诈用户，则移除ordBlack表中的黑名单规则
        Optional<RuleResult> specialRule = allRuleResults.stream()
                .filter(elem -> specialRules.contains(elem.getRuleName())
                        && elem.getRealValue().equals("true"))
                .findFirst();
        if (specialRule.isPresent()) {
            return specialRule;
        }

        Optional<RuleResult> hitInsuranceCardRule = allRuleResults.stream()
                .filter(elem -> BlackListTypeEnum.HAS_INSURANCE_CARD.getMessage().equals(elem.getRuleName())
                        && elem.getRealValue().equals("true")).findFirst();
        if (hitInsuranceCardRule.isPresent()) {
            log.info("hit insurance_card");
            return hitInsuranceCardRule;
        }

        Optional<RuleResult> hitFamilyCardRule = allRuleResults.stream()
                .filter(elem -> BlackListTypeEnum.HAS_FAMILY_CARD.getMessage().equals(elem.getRuleName())
                        && elem.getRealValue().equals("true")).findFirst();
        if (hitInsuranceCardRule.isPresent()) {
            log.info("hit family card");
            return hitFamilyCardRule;
        }


        return Optional.empty();

    }


    /***
     * 实名未通过任然不拒绝订单的规则
     */
    private final static List<String> specialRules = Arrays.asList(BlackListTypeEnum.COMB_RECENT30CALLINRATIO_DIFFOFEARLIESTMSG_NOCALLDAYS_FEMALE
                    .getMessage(),
            BlackListTypeEnum.COMB_RECENT90CALLINTIMES_DIFFOFEARLIESTMSG_EDUCATION_FEMALE.getMessage(),
            BlackListTypeEnum.COMB_MOBILE_LANG_AND_CAP.getMessage());


}
