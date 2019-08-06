package com.yqg.service.externalChannel.utils;

/**
 * Created by wanghuaizhou on 2018/3/12.
 */
public enum Cash2OrdCheckResultEnum {

    CHECK_PASS(10,"审批通过"),
    WAITING_CONFIRM(15,"降额待确认"),
    IN_CHECKING(20,"审批进行中"),
    CHECK_NEED_DATA(30,"审批需重填资料"),
    CHECK_NOT_PASS(40,"审批不通过");


    private int code;
    private String message;

    private Cash2OrdCheckResultEnum(int code, String message) {
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
