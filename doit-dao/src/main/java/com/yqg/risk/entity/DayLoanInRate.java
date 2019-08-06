package com.yqg.risk.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/9/5.
 */
@Data
public class DayLoanInRate {

    private String createtime;
    private String lendUser;
    private String unableUser;
    private String greenUser;
    private String borUser;
    private String borRate;
}
