package com.yqg.drools.executor.autoCall;

import com.yqg.drools.executor.base.BaseExecutionChain;
import com.yqg.drools.executor.base.FlowEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReBorrowingAutoCallExecutionChain extends BaseExecutionChain implements InitializingBean {


    @Override
    protected FlowEnum getFLowType() {
        return FlowEnum.AUTO_CALL_RE_BORROWING;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.initChains(null, null);
    }
}
