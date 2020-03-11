package com.yqg.common.enums.order;

/**
 * Created by wanghuaizhou on 2019/4/19.
 */
public enum OrderTypeEnum {

    NORMAL("0","普通订单"),
    DELAY("1","展期订单"),
    DELAY_PAID("2","展期已还款"),
    STAGING("3","分期");

    private String code;
    private String message;

    private OrderTypeEnum(String code, String message) {
        this.code=code;
        this.message=message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static OrderTypeEnum getEnum(String code) {
        for (OrderTypeEnum e : values()) {
            if (e.code.equalsIgnoreCase(code)) {
                return e;
            }
        }
        return null;
    }
}
