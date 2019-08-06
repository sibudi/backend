package com.yqg.manage.service.loan;


import com.yqg.order.dao.ManOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author alan
 */
@Component
public class ManOrderCountService {

    @Autowired
    private ManOrderDao manOrderDao;

    /**
     * 通过订单状态统计订单金额*/
    public BigDecimal orderSumByStatus(String status) throws Exception {
        return this.manOrderDao.orderSumByStatus(status);
    }

    /**
     * 通过refundTime和status统计订单总额*/
    public BigDecimal orderSumByRefundTimeStatus(String orderStatus, String Time) throws Exception {
        return this.manOrderDao.orderSumByRefundTimeStatus(orderStatus,Time);
    }

    /**
     * 今日到期应还*/
    public BigDecimal sumOrderExpireShouldPayAmount(String time) throws Exception {
        return this.manOrderDao.sumOrderExpireShouldPayAmount(time);
    }

    /**
     * 当日到期应还订单数*/
    public Integer countOrderExpireShouldPayDaily(String time) throws Exception {
        return this.manOrderDao.countOrderExpireShouldPayDaily(time);
    }

    /**
     * 通过订单状态统计数*/
    public Integer orderCountByStatus(String status) throws Exception {
        return this.manOrderDao.orderCountByStatus(status);
    }

    /**
     * */
    public BigDecimal orderSumByLendingTimeStatus(String orderStatus, String time) throws Exception {
        return this.manOrderDao.orderSumByLendingTimeStatus(orderStatus,time);
    }

    /**
     * 订单数*/
    public Integer orderCountByLendingTimeStatus(String orderStatus, String time) throws Exception {
        return this.manOrderDao.orderCountByLendingTimeStatus(orderStatus,time);
    }

    /**
     * 今日放款用户数*/
    public List<Integer> todayLoanUserCount(String time) throws Exception {
        return this.manOrderDao.todayLoanUserCount(time);
    }

    /**
     * 累计放款用户数*/
    public Integer totalLoanUserCount() throws Exception {
        return this.manOrderDao.totalLoanUserCount();
    }

    /**
     * 今日还款总额*/
    public BigDecimal todayRepaymentAmount(String time) throws Exception {
        return this.manOrderDao.todayRepaymentAmount(time);
    }

    /**
    * 正常还款总额*/
    public BigDecimal todayNormalRepaymentAmount(String time) throws Exception {
        return this.manOrderDao.todayNormalRepaymentAmount(time);
    }

    /**
     * 正常还款订单数*/
    public Integer todayNormalRepaymentCount(String time) throws Exception {
        return this.manOrderDao.todayNormalRepaymentCount(time);
    }

    /**
     * 正常还款用户数*/
    public Integer todayNormalRepaymentUserCount() throws Exception {
        return this.manOrderDao.todayNormalRepaymentUserCount();
    }

    /**
     * 逾期还款总额*/
    public BigDecimal todayOverDueRepaymentAmount() throws Exception {
        return this.manOrderDao.todayOverDueRepaymentAmount();
    }

    /**
     * 今日逾期还款用户数*/
    public Integer todayOverDueRepaymentUserCount() throws Exception {
        return this.manOrderDao.todayOverDueRepaymentUserCount();
    }

    /**
     * 今日提前还款用户数*/
    public Integer todayPrepaymentUserCount(String time) throws Exception {
        return this.manOrderDao.todayPrepaymentUserCount(time);
    }

    /**
     * 今日提前还款订单数*/
    public Integer todayPrepaymentOrderSum(String time) throws Exception {
        return this.manOrderDao.todayPrepaymentOrderSum(time);
    }

    /**
     * 今日逾期用户数*/
    public Integer todayOverDueUserCountResult() throws Exception {
        return this.manOrderDao.todayOverDueUserCountResult();
    }

    /**
     * 今日逾期总额*/
    public BigDecimal todayOverDueAmountSum(String time) throws Exception {
        return this.manOrderDao.todayOverDueAmountSum(time);
    }

    /**
     * 今日复借总额*/
    public BigDecimal todayRepeatOrderSum(String time) throws Exception {
        return this.manOrderDao.todayRepeatOrderSum(time);
    }

    /**
     * 累计复借总额*/
    public BigDecimal totalRepeatOrderSum() throws Exception {
        return this.manOrderDao.totalRepeatOrderSum();
    }

    /**
     * 今日复借用户数*/
    public Integer todayRepeatUserCountResult() throws Exception {
        return this.manOrderDao.todayRepeatUserCountResult();
    }

    /**
     * 累积复借用户数*/
    public Integer orderCountByoverDueDay(Integer maxDay, Integer minDay) throws Exception {
        return this.manOrderDao.orderCountByoverDueDay(maxDay, minDay);
    }

    /**
     * 通过到期日查询今日已还 */
    public Integer todayRepayOrderCountByRefundTime (String time) throws Exception {
        return this.manOrderDao.todayRepayOrderCountByRefundTime(time);
    }
}
