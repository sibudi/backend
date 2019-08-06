package com.yqg.manage.enums;

/**
 * ??????
 */
public enum OperationTypeEnum {

//    THEN_DIAL_LATTER(1,"稍后再拨","Telepon beberapa saat lagi"),
//    PASS(2,"通过","lulus"),
//    CANNOT_BE_AUDITED(3,"无法审核","Tidak bisa memutuskan"),
//    REFUSE(4,"拒绝","tolak");

    NOT_PASS(2,"未接通","Tidak tersambung"),
    PASS(1,"接通","Tersambung");

    OperationTypeEnum(int type, String message, String messageInn) {
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
