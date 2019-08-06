package com.yqg.drools.executor.autoCall;

import com.yqg.drools.executor.base.BaseExecutionChain;
import com.yqg.drools.executor.base.FlowEnum;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.util.RuleConstants;
import com.yqg.system.entity.SysAutoReviewRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class FirstBorrowingAutoCallProduct100ExecutionChain extends BaseExecutionChain implements InitializingBean {

    @Override
    protected void afterRejectResult(Map<String, SysAutoReviewRule> allRules, OrdOrder order) throws Exception {

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
        super.initChains(null, null);
    }
}
