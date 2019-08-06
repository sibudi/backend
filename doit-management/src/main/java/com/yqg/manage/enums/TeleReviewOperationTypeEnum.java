package com.yqg.manage.enums;

/**
 * ??????
 */
public enum TeleReviewOperationTypeEnum {

    NOT_PASS(1,"未接通","Tidak tersambung"),
    PASS(2,"接通","Tersambung");

    TeleReviewOperationTypeEnum(int type, String message, String messageInn) {
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

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageInn() {
        return messageInn;
    }

    public void setMessageInn(String messageInn) {
        this.messageInn = messageInn;
    }

    private int type;

    private String message;

    private String messageInn;

}
