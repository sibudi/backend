package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2019/1/22.
 */
@Data
public class OrdPassCountWithStage {

    private String applyDate;
    private String applyNum;
    private String commmitNum;
    private String autoCheckPass;
    private String intoFC;
    private String FCfinish;
    private String FCPassOutCall;
    private String intoSC;
    private String SCfinish;
    private String SCpass;
    private String riskPass;
    private String BlackListMultiHeadNum;
    private String riskBlacklistPassNum;
    private String lendNum;
    private String LendSuccessNum;

}
