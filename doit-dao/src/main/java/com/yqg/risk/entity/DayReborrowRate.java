package com.yqg.risk.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2019/6/23.
 */
@Data
public class DayReborrowRate {

    private String repayDay;
    private String toRepay;
    private String toLend;
    private String rate;
}
