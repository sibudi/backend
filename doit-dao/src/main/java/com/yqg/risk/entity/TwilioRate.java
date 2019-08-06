package com.yqg.risk.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2019/6/23.
 */
@Data
public class TwilioRate {

    private String date;
    private String callPhase;
    private String totalCall;
    private String connectCall;
    private String connectRate;
    private String noAnswerRate;
    private String failedRate;
    private String busyRate;
    private String callCost;

}
