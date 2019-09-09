package com.yqg.drools.executor.reBorrowing;

import com.yqg.drools.beans.RuleSetExecutedResult;
import com.yqg.drools.executor.base.BaseExecutionChain;
import com.yqg.drools.executor.base.FlowEnum;
import com.yqg.drools.executor.loanLimit.LoanLimitExecutor;
import com.yqg.drools.model.base.LoanLimitRuleResult;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.order.OrdService;
import com.yqg.service.user.service.UserRiskService;
import com.yqg.service.util.RuleConstants;
import com.yqg.system.entity.SysAutoReviewRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ReBorrowingUniversalExecutionChain extends BaseExecutionChain implements InitializingBean {
    @Autowired
    private ReBorrowingProduct600ExtendExecutionChain reBorrowingProduct600ExtendExecutionChain;
    @Autowired
    private LoanLimitExecutor loanLimitExecutor;
    @Autowired
    private OrdService ordService;
    @Autowired
    private UserRiskService userRiskService;

    @Override
    protected boolean preFilter(OrdOrder order) {
        return order.getBorrowingCount()>1;
    }

    @Override
    protected FlowEnum getFLowType() {
        return FlowEnum.RE_BORROWING_UNIVERSAL;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        super.initChains(reBorrowingProduct600ExtendExecutionChain, null);
    }


    protected RuleSetExecutedResult afterPassResult(Map<String, SysAutoReviewRule> allRules, OrdOrder order, List<Object> facts) throws Exception {
        //跑规则判断降额提额
        LoanLimitRuleResult approvedAmount = loanLimitExecutor.getApprovalLoanLimit(order, facts);
        if (approvedAmount != null && approvedAmount.getResultAmount().compareTo(order.getAmountApply()) != 0) {

            ordService.decreaseOrderToFixAmount(order, approvedAmount.getResultAmount());
            userRiskService.decreaseUserLoanLimit(order, RuleConstants.PRODUCT50.compareTo(approvedAmount.getResultAmount()) == 0 ? -5 : -3,
                    approvedAmount.getRuleDesc());
        }
        return new RuleSetExecutedResult(true, null);
    }
}
