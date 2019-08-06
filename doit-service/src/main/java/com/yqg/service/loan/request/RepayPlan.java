package com.yqg.service.loan.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by wanghuaizhou on 2019/7/3.
 */
@Data
public class RepayPlan {

    private Integer periodNo;
    //放款时间
    private Date lendingTime;
    //'应还款时间'
    private Date refundIngTime;
    //应还款金额
    private BigDecimal refundIngAmount;
}
