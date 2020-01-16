package com.yqg.common.enums.order;

/**
 * Created by Janhsen on 2019/12/18.
 */
public enum ChannelTypeEnum {

    KUDO_INSTALLMENT_1200K(84,"Kudo installment with 1.2M product"),
    KUDO_NORMAL_1200K(81,"Kudo 30 days with 1.2M product");

    private int code;
    private String message;

    private ChannelTypeEnum(int code, String message) {
        this.code=code;
        this.message=message;
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
