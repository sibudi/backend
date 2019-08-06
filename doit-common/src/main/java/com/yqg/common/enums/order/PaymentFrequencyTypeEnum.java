package com.yqg.common.enums.order;

/**
 * Created by Didit Dwianto on 2018/2/27.
 */
public enum PaymentFrequencyTypeEnum {
    DAILY(1, "daily"),// daily
    WEEKLY(2, "weekly"), // weekly
    MONTHLY(3, "monthly"), //monthly
    QUARTER(4, "quarter"), // quarter
    SEMESTER(5, "semester"), // semester
    YEARLY(6, "yearly"); // yearly

    private Integer code;
    private String message;

    private PaymentFrequencyTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
