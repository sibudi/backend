package com.yqg.common.enums;

/**
 * @author  Didit Dwianto
 */
public enum UsrTypeEnum {
    REGISTER(1), LOGIN(2);
    private int type;

    private UsrTypeEnum(int type) {
        this.setType(type);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
