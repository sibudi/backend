package com.yqg.drools.executor;

import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.risk.dao.RiskResultDao;
import com.yqg.drools.beans.RuleSetExecutedResult;
import com.yqg.drools.executor.base.FlowEnum;
import com.yqg.drools.model.base.RuleResult;
import com.yqg.drools.service.RuleResultAnalysisService;
import com.yqg.order.entity.OrdBlack;
import com.yqg.order.entity.OrdBlackTemp;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdRiskRecord;
import com.yqg.risk.repository.OrderRiskRecordRepository;
import com.yqg.system.entity.SysAutoReviewRule;
import lombok.extern.slf4j.Slf4j;

import org.apache.xpath.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@Slf4j
public class RuleResultService {

    @Autowired
    private OrderRiskRecordRepository orderRiskRecordRepository;

    @Autowired
    private RiskResultDao riskResultDao;

    @Autowired
    private RuleResultAnalysisService ruleResultAnalysisService;

    /****
     * 记录ordRiskRecord,ordBlack
     * @param order
     * @param allRules
     * @param ruleResultList
     * @param flowEnum
     */

    @Transactional(rollbackFor = Exception.class)
    public void batchRecordRuleResult(OrdOrder order, Map<String, SysAutoReviewRule> allRules,
                                      List<RuleResult> ruleResultList, FlowEnum flowEnum) {
        List<OrdRiskRecord> orderRiskRecords = new ArrayList<>();
        List<OrdBlack> ordBlacks = new ArrayList<>();
        List<OrdBlackTemp> ordBlackTemps = new ArrayList<>();

        ruleResultList.stream().forEach(elem -> {
            SysAutoReviewRule rule = allRules.get(elem.getRuleName());
            if (rule == null) {
                log.error("the rule name error: ,ruleName: " + elem.getRuleName());
            }
            String respMessage = rule.getRuleDesc() + flowEnum.name();
            String realValue = elem.getRealValue();
            boolean isPassed = elem.isPass();

            //插入ordRiskRecord表
            OrdRiskRecord record = new OrdRiskRecord();
            record.setOrderNo(order.getUuid());
            record.setUserUuid(order.getUserUuid());
            record.setRuleRealValue(realValue);
            record.setRuleType(rule.getRuleType());
            record.setRuleDetailType(rule.getRuleDetailType());
            record.setRuleDesc(respMessage);
            record.setUuid(UUIDGenerateUtil.uuid());
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            record.setRemark("isPassed: " + isPassed);
            orderRiskRecords.add(record);

            //isHitRule
            boolean isHitRule = elem.isPass();

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

    }

    public RuleSetExecutedResult fetchRuleSetResult(Map<String, SysAutoReviewRule> allRules,
                                                    List<RuleResult> ruleResultList, String orderNo){
        return ruleResultAnalysisService.fetchRuleSetResult(allRules,ruleResultList,orderNo);
    }

    public boolean disabledOrdBlackWithRemark(String orderNo, String remark) {
        Integer affectRow = riskResultDao.disableOrdBlackRecord(orderNo, remark);
        return affectRow != null && affectRow > 0;
    }
}
