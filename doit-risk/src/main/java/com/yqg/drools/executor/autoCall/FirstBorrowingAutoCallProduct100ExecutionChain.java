package com.yqg.drools.executor.autoCall;

import com.yqg.drools.executor.RuleResultService;
import com.yqg.drools.executor.base.BaseExecutionChain;
import com.yqg.drools.executor.base.FlowEnum;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.order.OrdService;
import com.yqg.service.user.service.UserRiskService;
import com.yqg.service.util.RuleConstants;
import com.yqg.system.entity.SysAutoReviewRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class FirstBorrowingAutoCallProduct100ExecutionChain extends BaseExecutionChain implements InitializingBean {

    @Autowired
    private FirstBorrowingAutoCallProduct50ExecutionChain autoCallProduct50ExecutionChain;

    @Autowired
    private RuleResultService ruleResultService;
    @Autowired
    private OrdService ordService;
    @Autowired
    private UserRiskService userRiskService;

    @Override
    protected void afterRejectResult(Map<String, SysAutoReviewRule> allRules, OrdOrder order) throws Exception {

        if (userRiskService.isSuitableFor100RMBProduct(order)) {
            //拒绝原因disabled掉
            ruleResultService.disabledOrdBlackWithRemark(order.getUuid(), RuleConstants.PRODUCT150TO80);
            ordService.changeOrderTo50RMBProduct(order);
        }
    }

    @Override
    public boolean preFilter(OrdOrder order) {
        if (order.getAmountApply().compareTo(RuleConstants.PRODUCT100) != 0) {
            return false;
        }
        return true;
    }

    @Override
    protected FlowEnum getFLowType() {
        return FlowEnum.AUTO_CALL_FIRST_PRODUCT100;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.initChains(null, autoCallProduct50ExecutionChain);
    }
}
