package com.yqg.drools.executor.autoCall;

import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.drools.executor.ExecutorUtil;
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
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class FirstBorrowingAutoCallProduct600ExecutionChain extends BaseExecutionChain implements InitializingBean {
    @Autowired
    private FirstBorrowingAutoCallProduct100ExecutionChain firstBorrowingAutoCallProduct100ExecutionChain;

    @Autowired
    private FirstBorrowingAutoCallNonManualExecutionChain firstBorrowingAutoCallNonManualExecutionChain;

    @Autowired
    private ExecutorUtil executorUtil;
    @Autowired
    private UserRiskService userRiskService;
    @Autowired
    private RuleResultService ruleResultService;
    @Autowired
    private OrdService ordService;

//    @Override
//    protected void afterRejectResult(Map<String, SysAutoReviewRule> allRules, OrdOrder order) throws Exception {
//        //600产品拒绝记录金额转到100，调用100的规则
//        if (userRiskService.isSuitableFor100RMBProduct(order) && order.getStatus() == OrdStateEnum.WAIT_CALLING.getCode()) {
//            //拒绝原因disabled掉
//            ruleResultService.disabledOrdBlackWithRemark(order.getUuid(), RuleConstants.PRODUCT600TO150);
//            ordService.changeOrderTo100RMBProduct(order);
//        }
//    }
    public boolean preFilter(OrdOrder order) {
        //600 产品才跑
        return executorUtil.normalFlowFilter(order);
    }

    @Override
    protected FlowEnum getFLowType() {
        return FlowEnum.AUTO_CALL_FIRST_PRODUCT600;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.initChains(null, null);
    }
}
