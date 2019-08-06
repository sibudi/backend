package com.yqg.drools.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanHistory {
    private Long applyCount; //申请次数
    private Long successLoanCount; //成功借款次数
    private BigDecimal successRatio;//成功借款次数和申请次数占比
    private Long overdueCount;
    private BigDecimal overdueSuccessLoanRatio;// 逾期次数与成功借款次数占比
    private BigDecimal averageOverdueDays;//平均逾期天数
    private BigDecimal averageIntervalDays;// 平均借款间隔天数（借款间隔： 实际还款日到下一次提交订单日）
    private Long overdue1Count; //逾期1天的次数
    private Long overdue2Count;
    private Long overdue3Count;
    private Long overdue4Count;
    private Long overdue5Count;
    private Long overdue6Count;
    private Long overdueMoreThan6Count; //逾期6天以上次数
    private BigDecimal overdue1Ratio; //逾期1天的占比
    private BigDecimal overdue2Ratio;
    private BigDecimal overdue3Ratio;
    private BigDecimal overdue4Ratio;
    private BigDecimal overdue5Ratio;
    private BigDecimal overdue6Ratio;
    private BigDecimal overdueMoreThan6Ratio; //逾期6天以上占比

    public LoanHistory() {
        overdue1Count = 0L;
        overdue2Count = 0L;
        overdue3Count = 0L;
        overdue4Count = 0L;
        overdue5Count = 0L;
        overdue6Count = 0L;
        overdueMoreThan6Count = 0L;
        overdue1Ratio = BigDecimal.ZERO;
        overdue2Ratio = BigDecimal.ZERO;
        overdue3Ratio = BigDecimal.ZERO;
        overdue4Ratio = BigDecimal.ZERO;
        overdue5Ratio = BigDecimal.ZERO;
        overdue6Ratio = BigDecimal.ZERO;
        overdueMoreThan6Ratio = BigDecimal.ZERO;
    }

}
