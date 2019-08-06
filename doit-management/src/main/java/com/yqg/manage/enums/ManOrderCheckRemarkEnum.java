package com.yqg.manage.enums;

/**
 *
 */
public enum ManOrderCheckRemarkEnum {
    //优秀通过
    PASS_EXCELLENT(1,"优秀","Best"),
    //良好通过
    PASS_FINE(2,"良好","Baik"),
    //一般通过
    PASS_COMMONLY(3,"一般","Normal"),
    //资质不符
    NOT_PASS_INCOMPETENCE(4,"资质不符","tidak cocok kualifikasi"),
    //信息不对称
    NOT_PASS_INFO_ASYMMETRY(5,"信息不对称","informasi tidak sama"),

    //存疑
    NOT_PASS_INFO_QUESTION(6,"存疑","diragukan");

    ManOrderCheckRemarkEnum(int type, String message, String messageInn) {
        this.type = type;
        this.message = message;
        this.messageInn = messageInn;
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

    public String getMessageInn() {
        return messageInn;
    }

    public void setMessageInn(String messageInn) {
        this.messageInn = messageInn;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private int type;

    private String message;

    private String messageInn;

}
