package com.yqg.common.enums.user;

/**
 * Created by Didit Dwianto on 2017/11/25.
 */
public enum CertificationResultEnum {

    NOT_AUTH(0), //0207060300¡è
    AUTH_SUCCESS(1), //060300¡è06070107
    AUTH_FAILD(2); //060300¡è08¡ì¡ã05

    private int type;

    private CertificationResultEnum(int type) {
        this.setType(type);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
