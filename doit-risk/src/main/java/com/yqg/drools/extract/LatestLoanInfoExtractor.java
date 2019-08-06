package com.yqg.drools.extract;

import com.yqg.common.utils.DateUtils;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.LastLoan;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

    @Override
    public boolean filter(RuleSetEnum ruleSet) {
        return RuleSetEnum.LATEST_LOAN.equals(ruleSet);
    }

    @Override
    public Optional<LastLoan> extractModel(OrdOrder order, KeyConstant keyConstant) {

        List<OrdOrder> orders = ordDao.getLastSuccessOrder(order.getUserUuid());
        if (CollectionUtils.isEmpty(orders)) {
            return Optional.empty();
        }else {
            LastLoan latestLoan = new LastLoan();
            OrdOrder ord = orders.get(0);
            // 上一次成功借款的逾期天数：actualRefundTime(实际还款日期)-refundTime(应还款日期)
            Long diffDay = DateUtils
                    .getDiffDaysIgnoreHours(ord.getRefundTime(), ord.getActualRefundTime());

            latestLoan.setOverdueDays(diffDay);
            latestLoan.setBorrowingAmount(ord.getAmountApply());

//            latestLoan.setCurrentBorrowCount(order.getBorrowingCount());
            return Optional.of(latestLoan);
        }

    }


}
