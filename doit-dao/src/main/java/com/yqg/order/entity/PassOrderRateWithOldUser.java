package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2019/1/21.
 */
@Data
public class PassOrderRateWithOldUser {

    private String applyDate;
    private String OldApplyNum;
    private String CommitNum;
    private String RiskPassNum;
    private String LendNum;
    private String LendSuccessNum;
    private String commitRate;
    private String riskPassRate;
    private String LendSuccessRate;

}
