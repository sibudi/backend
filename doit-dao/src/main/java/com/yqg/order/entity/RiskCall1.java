package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/9/12.
 */
@Data
public class RiskCall1 {
    private String cusNewCallDate;
    private String calledNoReportOrd;
    private String calledFailedOrd;
    private String validCallOrd;
    private String connectOrd;
    private String keepCallingOrd;
    private String callRejectOrd;
    private String connectRate;
}
