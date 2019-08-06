package com.yqg.drools.executor.autoCall;

import com.yqg.drools.executor.base.BaseExecutionChain;
import com.yqg.drools.executor.base.FlowEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FirstBorrowingAutoCallUniversalExecutionChain extends BaseExecutionChain implements InitializingBean {

    @Autowired
    private FirstBorrowingAutoCallProduct600ExecutionChain firstBorrowingAutoCallProduct600ExecutionChain;

    @Override
    protected FlowEnum getFLowType() {
        return FlowEnum.AUTO_CALL_FIRST_UNIVERSAL;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.initChains(firstBorrowingAutoCallProduct600ExecutionChain, null);
    }
}
