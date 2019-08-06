package com.yqg.drools.service;

import com.yqg.service.NonManualReviewService;
import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.drools.model.base.RuleResult;
import com.yqg.service.util.RuleConstants;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdRiskRecord;
import com.yqg.risk.repository.OrderRiskRecordRepository;
import com.yqg.service.externalChannel.service.ExternalChannelDataService;
import com.yqg.service.system.service.SysParamService;
import com.yqg.service.user.service.UserRiskService;
import com.yqg.system.entity.SysAutoReviewRule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/***
 * 指定产品规则
 */
@Service
@Slf4j
public class SpecifiedProductRuleService {
    @Autowired
    private NonManualReviewService nonManualReviewService;
    @Autowired
    private UserRiskService userRiskService;
    @Autowired
    private OrderRiskRecordRepository orderRiskRecordRepository;
    @Autowired
    private SysParamService sysParamService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ExternalChannelDataService externalChannelDataService;


    /***
     * 根据审核结果申请100RMB产品
     * @param resultList
     * @param allRules
     * @param order
     * @return
     * @throws Exception
     */
    public Product100RMBCheckResult apply100RMBProduct(List<RuleResult> resultList, Map<String, SysAutoReviewRule> allRules,
                                                       OrdOrder order) throws Exception{
        if (userRiskService.isSuitableFor100RMBProduct(order)) {
            Product100RMBCheckResult checkResult = this.check100RMBProductCondition(resultList, allRules, order);
            return checkResult;
        }
        return new Product100RMBCheckResult(false, null);
    }



    public void addRuleRecordFor100RMBProduct(Map<String, SysAutoReviewRule> allRules, OrdOrder order, boolean result) {
        SysAutoReviewRule rule = allRules.get(BlackListTypeEnum.SPECIAL_RULE_100_RMB_PRODUCT.getMessage());
        OrdRiskRecord record = new OrdRiskRecord();
        record.setOrderNo(order.getUuid());
        record.setUserUuid(order.getUserUuid());
        record.setRuleRealValue(String.valueOf(result));
        record.setRuleType(rule.getRuleType());
        record.setRuleDetailType(rule.getRuleDetailType());
        record.setRuleDesc(rule.getRuleDesc());
        record.setUuid(UUIDGenerateUtil.uuid());
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        List<OrdRiskRecord> addRuleResult = new ArrayList<>();
        addRuleResult.add(record);
        orderRiskRecordRepository.addRiskRecordList(addRuleResult);
    }

    public boolean hit100RMBProductABTestRule(OrdOrder order){
        boolean limitCheckSuccess = this.hitNonManualRuleTestFor100RMBProduct(order.getUuid());
        return limitCheckSuccess;
    }

//    public boolean isSuitableFor100RMBProduct(OrdOrder order) {
//        boolean switchOpen = is100RMBProductSwitchOpen(order.getUuid());
//        boolean isNotCashCashOrder = !isCashCashOrder(order);
//        //非40w and 非80w产品
//        boolean isAmountSuitable = !order.getAmountApply().equals(new BigDecimal("400000.00"))
//                                  && !order.getAmountApply().equals(new BigDecimal("800000.00"));
//        return switchOpen && isNotCashCashOrder && isAmountSuitable;
//    }
//
//    /**
//     * 是否是cashcash的订单
//     *
//     * @param order
//     * @return
//     */
//    public boolean isCashCashOrder(OrdOrder order) {
//        ExternalOrderRelation externalOrderRelation = externalChannelDataService.getExternalOrderNoByRealOrderNoRelation(order.getUuid());
//        if (externalOrderRelation != null) {
//            return true;
//        }
//        return false;
//    }
//
//
//    private boolean is100RMBProductSwitchOpen(String orderNo) {
//        OrdDeviceInfo deviceInfo = deviceService.getOrderDeviceInfo(orderNo);
//        if (deviceInfo == null) {
//            log.info("the device info is empty, orderNo: {}", orderNo);
//            return false;
//        }
//        String switchOpen;
//        if ("iOS".equals(deviceInfo.getDeviceType())) {
//            //iOS 因版本审核问题一直没上
//            switchOpen = sysParamService.getSysParamValue(SysParamContants.RISK_100RMB_PRODUCT_SWITCH_IOS);
//        } else {
//            switchOpen = sysParamService.getSysParamValue(SysParamContants.RISK_100RMB_PRODUCT_SWITCH_ANDROID);
//        }
//        return "true".equals(switchOpen);
//    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Product100RMBCheckResult {
        private boolean hit100RMB; //符合100RMB产品规则
        //private boolean hitNonManualTestFor100RMB; //符合100RMB产品规则的同时符合测试规则

        private RuleResult rejectRule;// 不符合
    }

    /****
     * 检查订单是否符合100rmb 产品前提条件
     * @param resultList
     * @param allRules
     * @param order
     * @return
     */
    private Product100RMBCheckResult check100RMBProductCondition(List<RuleResult> resultList, Map<String, SysAutoReviewRule> allRules,
                                                                 OrdOrder order) throws Exception{
        //100RMB规则处理
        return  this.onlyHit100RMBProductRules(resultList, allRules);
    }

    private boolean ruleCheckFor100RMBRroductTest(List<RuleResult> resultList, Map<String, SysAutoReviewRule> allRules) {
        List<RuleResult> rejectRules = resultList.stream().filter(elem ->
                elem.isPass() && allRules.get(elem.getRuleName()).getRuleResult() == 2).collect(Collectors.toList());
        //100product 产品改阈值的规则
        List<String> filteredList = Arrays.asList(BlackListTypeEnum.WORK_ADDRESS_INVALID.getMessage(),
                BlackListTypeEnum.COMB_SAMEIPCOUNT_150RMB.getMessage(),
                BlackListTypeEnum.NIGHT_CALL_RATE.getMessage(),
                BlackListTypeEnum.RECENT30_EVENING_CALL_RATE_MALE.getMessage());

        Long rejectedForNormalThreshold = rejectRules.stream().filter(elem -> filteredList.contains(elem.getRuleName())).count();
        return rejectedForNormalThreshold <= 0;
    }

    /***
     * 订单拒绝的通用规则只包含在100rmb产品放开的规则中
     * @param resultList
     * @param allRules
     * @return
     */
    private Product100RMBCheckResult onlyHit100RMBProductRules(List<RuleResult> resultList, Map<String, SysAutoReviewRule> allRules) throws Exception{
        Map<String, SysAutoReviewRule> ruleMapFor100RMB =
                allRules.values()
                        .stream()
                        .filter(elem -> elem.getSpecifiedProduct() != null
                                && SysAutoReviewRule.ExcludedForSpecifiedProduct.PRODUCT_100RMB.getCode() == elem.getSpecifiedProduct())
                        .collect(Collectors.toMap(SysAutoReviewRule::getRuleDetailType, Function.identity()));

        List<RuleResult> rejectRules = resultList.stream().filter(elem ->
                elem.isPass() && allRules.get(elem.getRuleName()).getRuleResult() == 2).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(rejectRules)) {
            log.error("no reject rules , the order is pass");
            throw new Exception("check 100rmb product error");
        }
        //拒绝规则不在100RMB规则中的个数
        Optional<RuleResult> rejectRule =
                rejectRules.stream().filter(elem -> !ruleMapFor100RMB.containsKey(elem.getRuleName())).findFirst();
        if (rejectRule.isPresent()) {
            return new Product100RMBCheckResult(false, rejectRule.get());
        } else {
            return new Product100RMBCheckResult(true, null);
        }
    }



    /***
     * 是否符合100rmb 产品免核自动通过条件
     * @param orderNo
     * @return
     */
    private boolean hitNonManualRuleTestFor100RMBProduct(String orderNo) {
        Long count = nonManualReviewService.nonManualRulesCount(orderNo);
        //检查相应规则的次数
        String remark = RuleConstants.AB_TEST_RULES + RuleConstants.NON_MANUAL_TEST_WITH_100RMB_PRODUCT;
        Integer relatedRuleCount = userRiskService.getABTestOrderIssuedCount(remark);
        if (count >= 3 && relatedRuleCount < 100) {
            return true;
        }
        return false;
    }




}
