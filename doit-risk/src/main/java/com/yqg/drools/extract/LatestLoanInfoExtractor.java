package com.yqg.drools.extract;

import com.yqg.common.utils.DateUtils;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.LastLoan;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.utils.DateUtil;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.risk.dao.LoanLimitRuleResultDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/*****
 * @Author zengxiangcai
 * Created at 2018/1/24
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Service
@Slf4j
public class LatestLoanInfoExtractor implements BaseExtractor<LastLoan> {

    @Autowired
    private OrdDao ordDao;
    @Autowired
    private LoanLimitRuleResultDao loanLimitRuleResultDao;

    @Override
    public boolean filter(RuleSetEnum ruleSet) {
        return RuleSetEnum.LATEST_LOAN.equals(ruleSet);
    }

    @Override
    public Optional<LastLoan> extractModel(OrdOrder order, KeyConstant keyConstant) {

        List<OrdOrder> orders = ordDao.getLastSuccessOrder(order.getUserUuid());
        if (CollectionUtils.isEmpty(orders)) {
            return Optional.empty();
        }

        LastLoan latestLoan = new LastLoan();
        OrdOrder ord = orders.get(0);
        // 上一次成功借款的逾期天数：actualRefundTime(实际还款日期)-refundTime(应还款日期)
        Long diffDay = DateUtils
                .getDiffDaysIgnoreHours(ord.getRefundTime(), ord.getActualRefundTime());

        latestLoan.setOverdueDays(diffDay);
        latestLoan.setBorrowingAmount(ord.getAmountApply());
        latestLoan.setIntervalDays(DateUtil.getDiffDays(ord.getActualRefundTime(), order.getApplyTime()));


        Integer hitCount = loanLimitRuleResultDao.hitRuleCount(ord.getUuid(), "PRODUCT_50");
        latestLoan.setHitIncreaseLoanLimit200RMB(hitCount != null && hitCount > 0);

        return Optional.of(latestLoan);
    }


}
