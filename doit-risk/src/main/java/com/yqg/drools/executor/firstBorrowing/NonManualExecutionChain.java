package com.yqg.drools.executor.firstBorrowing;

import com.yqg.drools.executor.base.BaseExecutionChain;
import com.yqg.drools.executor.base.FlowEnum;
import com.yqg.order.entity.OrdOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NonManualExecutionChain extends BaseExecutionChain  implements InitializingBean {

    @Autowired
    private UniversalExecutionChain universalExecutionChain;

    @Override
    protected FlowEnum getFLowType(){
        return FlowEnum.NON_MANUAL_RULE;
    }

    public boolean preFilter(OrdOrder order) {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.initChains(universalExecutionChain, universalExecutionChain);
    }

}
