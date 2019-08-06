package com.yqg.common.enums.order;

/**
 * Created with IntelliJ IDEA.
 * User: Jacob
 * Date: 07/02/2018
 * Time: 3:57 PM
 */
public enum PayStatusEnum {
    SUCCEED(1,"????"),
    FAILED(2,"????");

    private int code;
    private String message;

    PayStatusEnum(int code, String message){
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
