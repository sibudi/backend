package com.yqg.drools.executor.autoCall;

import com.yqg.drools.executor.base.BaseExecutionChain;
import com.yqg.drools.executor.base.FlowEnum;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.util.RuleConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FirstBorrowingAutoCallProduct50ExecutionChain  extends BaseExecutionChain implements InitializingBean {

    @Override
    public boolean preFilter(OrdOrder order) {
        if (order.getAmountApply().compareTo(RuleConstants.PRODUCT50) != 0) {
            return false;
        }
        return true;
    }

    @Override
    protected FlowEnum getFLowType() {
        return FlowEnum.AUTO_CALL_FIRST_PRODUCT50;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.initChains(null, null);
    }
}
