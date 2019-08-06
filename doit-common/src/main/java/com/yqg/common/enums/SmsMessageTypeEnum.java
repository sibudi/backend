package com.yqg.common.enums;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
public enum SmsMessageTypeEnum {
    REGISTER(1),//????
    LOGIN(2);//????

    private Integer type;

    SmsMessageTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
