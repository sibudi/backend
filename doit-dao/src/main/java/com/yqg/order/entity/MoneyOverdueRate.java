package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/7/4.
 */
@Data
public class MoneyOverdueRate {

    private String date;
    private String orders;
    private String repayOrder;
    private String ordOverDueRate;
    private String dueAmount;
    private String repayAmount;
    private String amtOverDueRate;
}
