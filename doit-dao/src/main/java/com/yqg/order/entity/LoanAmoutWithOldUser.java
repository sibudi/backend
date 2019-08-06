package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2019/1/22.
 */
@Data
public class LoanAmoutWithOldUser {

    private String LendDate;
    private String OldTotalAmount;
    private String Old80;
    private String Old200;
    private String Old400;
    private String Old600;
    private String Old750;
    private String Old1000;
    private String otherAmount;
    private String proportion80;
    private String proportion200;
    private String proportion400;
    private String proportion600;
    private String proportion750;
    private String proportion1000;
    private String otherAmountProportion;
}
