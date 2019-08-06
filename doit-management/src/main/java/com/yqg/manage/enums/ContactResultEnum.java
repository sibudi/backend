package com.yqg.manage.enums;

/**
 * Author: tonggen
 * Date: 2018/8/27
 * time: 下午5:35
 */
public enum ContactResultEnum {

    CONTACT_YES(1,"接通,承诺还款"),
    CONTACT_NO_PORMISE(2,"接通,可联系跳票"),
    EMPTY_MOBLILE(3,"空号"),
    NO_CONTRACT(4,"拒接"),
    NO_RESPONSE_CONTACT(5,"无人接听，通话中"),
    SHUT_OFF(6,"关机"),
    NOT_USE(7,"停机"),
    OTHER(8,"其他"),
    NO_WA(9,"无WA账号"),
    SEND_NO_LOOK(10,"已发，未看"),
    SEE_NO_RESOPNSE(11,"看了，未回复"),
    SEND_NO_SEE(12,"看了，可能跳票"),
    SEE_PROMISE(13,"看了，承诺还款"),
    CANT_SEE(14,"屏蔽"),
    CONTACT_OTHER(15,"其他"),
    SEND_NO_RESPONSE(16,"已发，未回"),
    SEND_PROMISE(17,"已发，承诺还款"),
    CONTACT_SEND_NOTRANSEFR(18,"已发，可联跳票"),
    CONTACT_TRANSFER1(19,"未发送"),
    CONTACT_NOTRANSFER2(20,"发送不成功"),
    CONTACT_SEND_NORESPONSE3(21,"其他"),
    CONTACT_SEND_RESPONSE4(22,"进行详情备注，通过什么方式联系，具体详情备注"),
    CONTACT_SEND_TRANSFER5(23,"接通，同意转告"),
    CONTACT_SEND_RESPONSE6(24,"接通，不同意转告"),
    CONTACT_SEND_TRANSFER7(25,"接通，不认识/已离职/无此人");

    ContactResultEnum(int type, String message) {
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
