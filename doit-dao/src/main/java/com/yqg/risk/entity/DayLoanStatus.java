package com.yqg.risk.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/9/5.
 */
@Data
public class DayLoanStatus {

    private String date;
    private String orders;
    private String newOrder;
    private String oldOrder;
    private String nor;
    private String lendAmount;
    private String newAmount;
    private String oldAmount;
    private String billOrders;
    private String billAmount;
    private String extension;
    private String extAmount;

}
