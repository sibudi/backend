package com.yqg.drools.executor.reBorrowing;

import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.drools.beans.RuleSetExecutedResult;
import com.yqg.drools.executor.RuleResultService;
import com.yqg.drools.executor.base.BaseExecutionChain;
import com.yqg.drools.executor.base.FlowEnum;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.order.OrdService;
import com.yqg.service.util.RuleConstants;
import com.yqg.system.entity.SysAutoReviewRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReBorrowingProduct600ExtendExecutionChain  extends BaseExecutionChain implements InitializingBean {

    @Autowired
    private RuleResultService ruleResultService;
    @Autowired
    private OrdService ordService;

    @Autowired
    private ReBorrowingProduct400ExtendExecutionChain reBorrowingProduct400ExtendExecutionChain;

    @Override
    protected boolean preFilter(OrdOrder order) {
        if ("3".equals(order.getOrderType())) {
            return false;
        }
        if (order.getBorrowingCount() != 2) {
            return false;
        }
        OrdOrder lastLoan = ordService.getLastSettledLoan(order.getUserUuid());
        return lastLoan.getAmountApply().compareTo(RuleConstants.PRODUCT600) == 0 && order.getAmountApply().compareTo(RuleConstants.PRODUCT600) == 0;
    }

    @Override
    protected FlowEnum getFLowType() {
        return FlowEnum.RE_BORROWING_PRD600_EXTEND;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        super.initChains(null, reBorrowingProduct400ExtendExecutionChain);
    }


    /***
     * after rules executed , check whether the order hit the extend rules , if not . the applyAmount will be decreased in afterRejectResult
     * @param result
     * @param allRules
     * @param order
     * @return
     */
    @Override
    public RuleSetExecutedResult postProcessExecuteResult(RuleSetExecutedResult result, Map<String, SysAutoReviewRule> allRules, OrdOrder order) {
        return super.doPostProcessForExecutedResult(result, allRules, order, BlackListTypeEnum.NO_REBORROWING_PRODUCT_600_EXTEND_RULE_HIT);
    }




//
//    @Override
//    protected void afterRejectResult(Map<String, SysAutoReviewRule> allRules, OrdOrder order) throws Exception {
//        //拒绝原因disabled掉
//        ruleResultService.disabledOrdBlackWithRemark(order.getUuid(), RuleConstants.RE_BORROWING_PRD600TO400);
//        ordService.decreaseOrderToFixAmount(order, RuleConstants.PRODUCT400);
//    }

}

