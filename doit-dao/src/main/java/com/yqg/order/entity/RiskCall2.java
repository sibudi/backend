package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/9/12.
 */
@Data
public class RiskCall2 {
    private String cusNewCallDate;
    private String validCallOrd;
    private String invalidNumOrd;
    private String opeRejectOrd;
    private String notExistNumOrd;
    private String overTimesOrd;
    private String callRejectOrd;
    private String notExistNumRate;
    private String invalidNumRejectRate;
    private String opeRejectRate;
    private String overTimesRate;
    private String callRejectRate;
}
