package com.yqg.drools.executor.autoCall;

import com.yqg.drools.executor.base.BaseExecutionChain;
import com.yqg.drools.executor.base.FlowEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
public class OwnerAutoCallExecutionChain  extends BaseExecutionChain implements InitializingBean {

    @Override
    protected FlowEnum getFLowType() {
        return FlowEnum.AUTO_CALL_OWNER;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.initChains(null, null);
    }
}
