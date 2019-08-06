package com.yqg.drools.executor;

import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.model.base.RuleConditionModel;
import com.yqg.drools.service.DeviceService;
import com.yqg.drools.service.RuleApplicationService;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdDeviceInfo;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.user.service.UserRiskService;
import com.yqg.service.util.RuleConstants;
import com.yqg.system.entity.SysAutoReviewRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExecutorUtil {

    public final List<BigDecimal> excludeApplyAmounts = Arrays.asList(RuleConstants.PRODUCT100,RuleConstants.PRODUCT50);

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private UserRiskService userRiskService;
    @Autowired
    private OrdDao ordDao;


    private List<String> ownerAutoCallRules = Arrays.asList(BlackListTypeEnum.AUTO_CALL_REJECT_OWNER_CALL_INVALID.getMessage(),
            BlackListTypeEnum.AUTO_CALL_REJECT_OWNER_EXCEED_LIMIT.getMessage());



    public boolean normalFlowFilter(OrdOrder order){
        /**
         * 订单当前是已经是100rmb或者50rmb产品，不需要执行该规则。
         * 非降额产品跑的规则
         */
        if (CollectionUtils.isEmpty(excludeApplyAmounts)) {
            return true;
        }
        if (order.getAmountApply() != null) {
            Long count = excludeApplyAmounts.stream().filter(elem -> elem.compareTo(order.getAmountApply())==0).count();
            return count > 0 ? false : true;
        }
        return true;
    }


    public Optional<RuleConditionModel> buildRuleCondition(Map<String, SysAutoReviewRule> allRules, OrdOrder order) {
        RuleConditionModel model = new RuleConditionModel();
        if (allRules == null || order == null) {
            return Optional.empty();
        }
        OrdDeviceInfo deviceInfo = deviceService.getOrderDeviceInfo(order.getUuid());

        boolean isCashCashOrder = userRiskService.isCashCashOrder(order);
        boolean isCheetahOrder = userRiskService.isCheetahOrder(order);

        Map<String, Boolean> ruleConditionMap = allRules.values().stream().collect(Collectors.toMap(SysAutoReviewRule::getRuleDetailType,
                elem -> isRuleNeedToExecute(deviceInfo, isCashCashOrder,isCheetahOrder, elem,order)));

        model.setRuleExecuteMap(ruleConditionMap);

        return Optional.of(model);
    }

    private boolean isRuleNeedToExecute(OrdDeviceInfo deviceInfo,boolean isCashCashOrder, boolean isCheetahOrder,SysAutoReviewRule ruleConfig,
                                        OrdOrder order){
        //状态18的时候只进行本人外呼其他规则不执行
        if(order.getStatus() == OrdStateEnum.WAITING_CALLING_AFTER_FIRST_CHECK.getCode()){
            if(!ownerAutoCallRules.contains(ruleConfig.getRuleDetailType())){
                return false;
            }
        }
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
            case android_CHEETAH:
                return "android".equalsIgnoreCase(deviceType)&&isCheetahOrder;
            case iOS_CHEETAH:
                return "iOS".equalsIgnoreCase(deviceType)&&isCheetahOrder;
            case both_CHEETAH:
                return isCheetahOrder;
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


    public Optional<RuleConditionModel> filterConditionForReBorrowing(Optional<RuleConditionModel> conditionModel, OrdOrder order) {
        if (!conditionModel.isPresent()) {
            return conditionModel;
        }
        List<OrdOrder> successOrders = ordDao.getLastSuccessOrder(order.getUserUuid());
        if (CollectionUtils.isEmpty(successOrders)) {
            return conditionModel;
        }
        successOrders = successOrders.stream().filter(elem -> !"1".equals(elem.getOrderType())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(successOrders)) {
            return conditionModel;
        }

        RuleConditionModel model = conditionModel.get();
        boolean firstOrder100RMB = false;
        Optional<OrdOrder> firstOrder = successOrders.stream().min(Comparator.comparing(OrdOrder::getCreateTime));
        if (firstOrder.isPresent() && firstOrder.get().getAmountApply().compareTo(RuleConstants.PRODUCT100) == 0) {
            firstOrder100RMB = true;
        }

        boolean has400RMBOrder = successOrders.stream().filter(elem -> elem.getAmountApply().compareTo(RuleConstants.PRODUCT400) == 0).count() > 0;

        boolean currentOrder600RMB = order.getAmountApply().compareTo(RuleConstants.PRODUCT600) == 0;


        Boolean isExecute = model.getRuleExecuteMap().get(BlackListTypeEnum.REBORROWING_IZI_PHONEVERIFY_AGE_MALE.getMessage());
        if (isExecute != null && isExecute == true) {
            //首次借款是100，当前是600并且中间没有400，可执行该规则
            boolean toExecute = firstOrder100RMB && !has400RMBOrder && currentOrder600RMB;
            model.getRuleExecuteMap().put(BlackListTypeEnum.REBORROWING_IZI_PHONEVERIFY_AGE_MALE.getMessage(), toExecute);
        }
        isExecute = model.getRuleExecuteMap().get(BlackListTypeEnum.REBORROWING_FIRST_LOAN_COMPANY_CALL_TOTAL_MEMORY.getMessage());
        if (isExecute != null && isExecute == true) {
            //首次借款是100，当前是600并且中间没有400，可执行该规则
            boolean toExecute = firstOrder100RMB && !has400RMBOrder && currentOrder600RMB;
            model.getRuleExecuteMap().put(BlackListTypeEnum.REBORROWING_FIRST_LOAN_COMPANY_CALL_TOTAL_MEMORY.getMessage(), toExecute);
        }
        isExecute = model.getRuleExecuteMap().get(BlackListTypeEnum.REBORROWING_IZI_PHONEAGE_PHONEVERIFY_MALE.getMessage());
        if (isExecute != null && isExecute == true) {
            //首次借款是100，当前是600并且中间有400，可执行该规则
            boolean toExecute = firstOrder100RMB && has400RMBOrder && currentOrder600RMB;
            model.getRuleExecuteMap().put(BlackListTypeEnum.REBORROWING_IZI_PHONEAGE_PHONEVERIFY_MALE.getMessage(), toExecute);
        }
        return Optional.of(model);
    }


}
