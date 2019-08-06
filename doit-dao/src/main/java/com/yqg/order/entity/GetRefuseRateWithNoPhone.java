package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/6/25.
 */
@Data
public class GetRefuseRateWithNoPhone {

    private String date;
    private String ruleDesc;
    private String hitsnum;
    private String rejectnum;
    private String autocheckrate;
}
