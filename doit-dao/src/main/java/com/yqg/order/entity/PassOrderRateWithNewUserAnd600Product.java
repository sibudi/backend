package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2019/1/21.
 */
@Data
public class PassOrderRateWithNewUserAnd600Product {

    private String applyDate;
    private String commitRate;
    private String autoCheckPassRate;
    private String manFCPassRate;
    private String selfCallPassRate;
    private String SCPassRate;
    private String riskPassRate;
    private String riskandBlackListPassRate;
    private String noMCRateInACpass;
    private String noMCrateInCommit;
    private String BlackListMuliHeadRate;
    private String lendSuccessRate;

}
