package com.yqg.common.enums.order;

/**
 * Created by Didit Dwianto on 2018/2/27.
 */
public enum paymentTypeEnum {

    AMORTIZATION(1, "amortization"),// amortization
    BULLET_PAYMENT(2, "bullet payment"), // bullet payment
    DISCOUNT(3, "discount"), //discount
    GRACE_PERIOD(4, "grace period"), // grace period
    OTHER(5, "other"); // other

    private Integer code;
    private String message;

    private paymentTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
