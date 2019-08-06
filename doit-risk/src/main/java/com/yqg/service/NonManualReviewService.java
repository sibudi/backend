package com.yqg.service;

import com.yqg.risk.dao.RiskResultDao;
import com.yqg.order.entity.OrdRiskRecord;
import com.yqg.risk.repository.OrderRiskRecordRepository;
import com.yqg.service.user.service.UserRiskService;
import com.yqg.system.entity.SysAutoReviewRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*****
 * @Author zengxiangcai
 * Created at 2018/6/20
 * @Email zengxiangcai@yishufu.com
 * 免核相关规则
 ****/


@Service
@Slf4j
public class NonManualReviewService {



    @Autowired
    private UserRiskService userRiskService;

    @Autowired
    private RiskResultDao riskResultDao;

    @Autowired
    private OrderRiskRecordRepository orderRiskRecordRepository;

    private Map<String, String> withoutManualReviewRules;

    @PostConstruct
    public void init() {
        List<SysAutoReviewRule> nonManualReviewRules = riskResultDao.getRulesWithType(22);
        if (CollectionUtils.isEmpty(nonManualReviewRules)) {
            return;
        }
        withoutManualReviewRules = nonManualReviewRules.stream().collect(Collectors.toMap(SysAutoReviewRule::getRuleDetailType, elem -> "true"));

    }


    /****
     * 是否免初审和复审订单
     * @param orderNo
     * @return
     */
    public boolean isNonManualReviewOrder(String orderNo) {
        if (withoutManualReviewRules == null) {
            return false;
        }

//        if (userRiskService.isUserHitRuleForVerifyScore(orderNo)) {
//            //命中人脸比对分规则，不可免复审初审
//            log.info("hit the face verify rule, orderNo: {}", orderNo);
//            return false;
//        }
        //实名失败的保险卡家庭卡订单
        boolean insuranceCardPass = userRiskService.onlyRealNameVerifyFailedWithInsuranceCard(orderNo);
        if(insuranceCardPass){
            log.info("hit the face insurance card pass rule, orderNo: {}", orderNo);
            return false;
        }
        boolean familyCardPass = userRiskService.onlyRealNameVerifyFailedWithFamilyCard(orderNo);
        if(familyCardPass){
            log.info("hit the face family card pass rule, orderNo: {}", orderNo);
            return false;
        }

        List<String> nonManualRules = new ArrayList<>(withoutManualReviewRules.keySet());
        List<OrdRiskRecord> ruleResultList = orderRiskRecordRepository.selectRuleResultByRuleNames(orderNo, nonManualRules);
        if (CollectionUtils.isEmpty(ruleResultList)) {
            return false;
        }
        return ruleResultList.stream().filter(
                elem -> withoutManualReviewRules.containsKey(elem.getRuleDetailType())
                        && withoutManualReviewRules.get(elem.getRuleDetailType())
                        .equalsIgnoreCase(elem.getRuleRealValue())
        ).findFirst().isPresent();
    }

    public Long nonManualRulesCount(String orderNo){
        List<String> nonManualRules = new ArrayList<>(withoutManualReviewRules.keySet());
        List<OrdRiskRecord> ruleResultList = orderRiskRecordRepository.selectRuleResultByRuleNames(orderNo, nonManualRules);
        if (CollectionUtils.isEmpty(ruleResultList)) {
            return 0L;
        }
        return ruleResultList.stream().filter(  elem -> withoutManualReviewRules.containsKey(elem.getRuleDetailType())
                && withoutManualReviewRules.get(elem.getRuleDetailType())
                .equalsIgnoreCase(elem.getRuleRealValue())
        ).count();
    }

}
