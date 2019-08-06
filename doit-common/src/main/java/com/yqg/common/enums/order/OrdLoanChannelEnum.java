package com.yqg.common.enums.order;

/**
 * Created by Didit Dwianto on 2018/1/31.
 */
public enum OrdLoanChannelEnum {

    XENDIT(1,"XENDIT"),// XENDIT
    CIMB(2,"CIMB"),   //CIMB
    DANAMON(3,"DANAMON"),// DANAMON
    BCA(4,"BCA"); // BCA

    private int code;
    private String message;

    private OrdLoanChannelEnum(int code, String message) {
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
