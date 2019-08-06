package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/11/13.
 */
@Data
public class FraudRule {

    private String date;
    private String fraudRule;
    private String hitOrd;
    private String rejectedOrd;
    private String fraudRuleRejectRate;
}
