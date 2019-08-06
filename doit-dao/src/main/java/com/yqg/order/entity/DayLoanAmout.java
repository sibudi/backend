package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2019/1/21.
 */
@Data
public class DayLoanAmout {

    private String LendDate;
    private String totalAmount;
    private String newTotal;
    private String oldTotal;
    private String oldProportion;
    private String newProportion;

}
