package com.yqg.manage.enums;

/**
 * Author: tonggen
 * Date: 2018/8/27
 * time: 下午5:35
 */
public enum ContactModeEnum {

    CONTACT_MODE_WA(1,"WA"),
    CONTACT_MODE_PHONE(2,"电话"),
    CONTACT_MODE_SMS(3,"短信"),
    CONTACT_MODE_OTHER(4,"其他");
    ContactModeEnum(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private int type;

    private String message;
}
