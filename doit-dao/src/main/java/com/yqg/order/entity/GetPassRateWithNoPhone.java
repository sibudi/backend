package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/6/25.
 */
@Data
public class GetPassRateWithNoPhone {

    private String date;
    private String ruleDesc;
    private String commitNum;
    private String autoCheckPassNum;
    private String nupofree;
    private String commitrate;
    private String autocheckrate;
}
