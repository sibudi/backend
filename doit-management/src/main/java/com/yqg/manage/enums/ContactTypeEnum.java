package com.yqg.manage.enums;

/**
 * Author: tonggen
 * Date: 2018/8/27
 * time: 下午5:35
 */
public enum ContactTypeEnum {

    HIMSELEF_PHONE(1,"本人电话"),
    HIMSELEF_WA(2,"本人WA"),
    CONTACT1(3,"联系人1"),
    CONTACT2(4,"联系人2"),
    CONTACT3(5,"联系人3"),
    CONTACT4(6,"联系4"),
    CONTACT_RECORD(7,"通讯录"),
    CALL_RECORD(8,"通话记录");

    ContactTypeEnum(int type, String message) {
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
