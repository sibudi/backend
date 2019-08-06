package com.yqg.drools.executor.firstBorrowing;

import com.github.pagehelper.StringUtil;
import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.drools.beans.RuleSetExecutedResult;
import com.yqg.drools.executor.ExecutorUtil;
import com.yqg.drools.executor.RuleResultService;
import com.yqg.drools.executor.base.BaseExecutionChain;
import com.yqg.drools.executor.base.FlowEnum;
import com.yqg.drools.model.DeviceModel;
import com.yqg.drools.model.RUserInfo;
import com.yqg.drools.model.base.RuleResult;
import com.yqg.drools.service.SpecifiedProductRuleService;
import com.yqg.service.user.service.UserRiskService;
import com.yqg.service.util.RuleConstants;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.order.OrdService;
import com.yqg.system.entity.SysAutoReviewRule;
import com.yqg.user.dao.UsrVerifyResultDao;
import com.yqg.user.entity.UsrVerifyResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/***
 * 处理通用的拒绝规则(欺诈规则，其他通用规则)
 */
@Service
@Slf4j
public class UniversalExecutionChain extends BaseExecutionChain implements InitializingBean {
    @Autowired
    private OrdService ordService;
    @Autowired
    private SpecifiedProductRuleService specifiedProductRuleService;

    @Autowired
    private ExecutorUtil executorUtil;

    @Autowired
    private Product600ExecutionChain product600ExecutionChain;
    @Autowired
    private Product50ExecutionChain product50ExecutionChain;
    @Autowired
    private RuleResultService ruleResultService;
    @Autowired
    private UserRiskService userRiskService;
    @Autowired
    private UsrVerifyResultDao usrVerifyResultDao;


    @Override
    protected void afterRejectResult(Map<String, SysAutoReviewRule> allRules, OrdOrder order) throws Exception {
        //拒绝后直接拒绝，暂时不转50
        if(true){
            return;
        }
        List<RuleResult> currentResultList = getRuleResultDetailList();
        if (CollectionUtils.isEmpty(currentResultList)) {
            log.warn("the order is rejected , but there is no rule result detail");
            return;
        }
        if (isHitRuleFor50(currentResultList, allRules) && userRiskService.isSuitableFor100RMBProduct(order)) {
            //拒绝原因disabled掉
            ruleResultService.disabledOrdBlackWithRemark(order.getUuid(), RuleConstants.PRODUCT600TO50_FRAUD);
            ordService.changeOrderTo50RMBProduct(order);
        } else {
            //直接拒绝
        }
    }

    private boolean isHitRuleFor50(List<RuleResult> ruleResults, Map<String, SysAutoReviewRule> allRules) {
        List<RuleResult> rejectRules = ruleResults.stream().filter(elem -> elem.isPass() && allRules.get(elem.getRuleName()).getRuleResult() == 2).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(rejectRules) || rejectRules.size() != 1) {
            return false;
        }
        RuleResult rejectResult = rejectRules.get(0);
        boolean onlyRealNameReject = rejectResult.getRuleName().equals(BlackListTypeEnum.ADVANCE_VERIFY_RULE.getMessage());
        if (onlyRealNameReject && rejectResult.getDesc().equals(RuleConstants.PERSON_NOT_FOUND)) {
            //找不到用户直接转为50
            return true;
        }
        return false;
    }

//    public static void main(String[] args) {
//        List<RuleResult> rejectRules = new ArrayList<>();
//        RuleResult res = new RuleResult();
//        res.setRuleName("ADVANCE_VERIFY_RULE");
//        rejectRules.add(res);
//        String rejectRuleName = rejectRules.get(0).getRuleName();
//        boolean result = !rejectRuleName.equalsIgnoreCase(BlackListTypeEnum.ADVANCE_VERIFY_RULE.getMessage());
//        if (!rejectRuleName.equalsIgnoreCase(BlackListTypeEnum.ADVANCE_VERIFY_RULE.getMessage())) {
//            log.info("the reject rules is not real name verify, {}",rejectRuleName);
//            System.err.println("inner result");
//        }else{
//            System.err.println("outer result");
//        }
//
//    }
//    @Override
//    protected RuleSetExecutedResult abTestAfterReject(OrdOrder order, Map<String, SysAutoReviewRule> allRules, List<Object> facts) {
//
//
//        //仅仅实名拒绝，而且满足部分条件
//        if (CollectionUtils.isEmpty(getRuleResultDetailList())) {
//            log.info("no rules for ab test in universal execution");
//            return DEFAULT_REJECT_RESULT;
//        }
//
//        List<RuleResult> rejectRules =
//                getRuleResultDetailList().stream().filter(elem -> elem.isPass() && allRules.get(elem.getRuleName()).getRuleResult() == 2).collect(Collectors.toList());
//        if (CollectionUtils.isEmpty(rejectRules) || rejectRules.size() != 1) {
//            log.info("not only one rejected rules");
//            return DEFAULT_REJECT_RESULT;
//        }
//        String rejectRuleName = rejectRules.get(0).getRuleName();
//        if (!rejectRuleName.equalsIgnoreCase(BlackListTypeEnum.ADVANCE_VERIFY_RULE.getMessage())) {
//            log.info("the reject rules is not real name verify, {}",rejectRuleName);
//            return DEFAULT_REJECT_RESULT;
//        }
//
//        //只有实名被拒绝并且满足其他条件
//        Optional<Object> rUserInfo = facts.stream().filter(elem->elem instanceof RUserInfo).findFirst();
//        Optional<Object> deviceModel = facts.stream().filter(elem->elem instanceof DeviceModel).findFirst();
//        BigDecimal monthlyIncome = null;
//        Float totalMemory = null;
//        UsrVerifyResult advanceVerifyResult = usrVerifyResultDao.getLatestVerifyResultWithType(order.getUuid(),
//                UsrVerifyResult.VerifyTypeEnum.ADVANCE.getCode());
//        boolean advanceNotFound = advanceVerifyResult!=null && "实名认证失败：PERSON_NOT_FOUND".equals(advanceVerifyResult.getRemark());
//        Integer sex = null;
//        String academic = null;
//        if(rUserInfo.isPresent()){
//            RUserInfo userModel = (RUserInfo)rUserInfo.get();
//            monthlyIncome = userModel.getMonthlyIncome();
//            academic = userModel.getAcademic();
//            sex = userModel.getSex();
//        }
//        if(deviceModel.isPresent()){
//            DeviceModel  deviceInfoModel = (DeviceModel) deviceModel.get();
//            totalMemory = deviceInfoModel.getTotalMemory();
//        }
//        boolean othersConditionHit = monthlyIncome!=null && monthlyIncome.compareTo(new BigDecimal("8000000"))>=0
//                                    && totalMemory!=null && totalMemory>=2
//                                    && StringUtil.isNotEmpty(academic) && Arrays.asList(RUserInfo.EducationEnum.Specialty.getCode(),
//                RUserInfo.EducationEnum.Undergraduate.getCode(),RUserInfo.EducationEnum.GraduateStudent.getCode(),
//                RUserInfo.EducationEnum.Doctor.getCode()).contains(academic)
//                                    && advanceNotFound ;
//        boolean hitTest = false;
//        String disabledRemark = null;
//        if(othersConditionHit && sex!=null && sex == RUserInfo.SexEnum.FEMALE.getCode()){
//            //女
//            disabledRemark = RuleConstants.REAL_NAME_COMB_VERIFY_ABTEST_FEMALE;
//
//
//            //检查测试量：
//            Integer currentCount = userRiskService.getABTestOrderIssuedCount(disabledRemark);
//            if (currentCount != null && currentCount > 150) {
//                log.info("exceed female test count...");
//                hitTest = false;
//            } else {
//                hitTest = true;
//            }
//        }
//        if(othersConditionHit && sex!=null && sex == RUserInfo.SexEnum.MALE.getCode()){
//            //男
//            disabledRemark = RuleConstants.REAL_NAME_COMB_VERIFY_ABTEST_MALE;
//            Integer currentCount = userRiskService.getABTestOrderIssuedCount(disabledRemark);
//            if (currentCount != null && currentCount > 150) {
//                log.info("exceed male test count...");
//                hitTest = false;
//            } else {
//                hitTest = true;
//            }
//        }
//
//        if (hitTest) {
//            log.info("real name ab test pass");
//            //disabled remark
//            ruleResultService.disabledOrdBlackWithRemark(order.getUuid(), disabledRemark);
//            return new RuleSetExecutedResult(true, null);
//        }
//
//
//        return DEFAULT_REJECT_RESULT;
//    }

    @Override
    protected boolean preFilter(OrdOrder order) {
        return executorUtil.normalFlowFilter(order);
    }

    @Override
    protected FlowEnum getFLowType() {
        return FlowEnum.FRAUD_UNIVERSAL_RULE;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        super.initChains(product600ExecutionChain, null);
    }
}
