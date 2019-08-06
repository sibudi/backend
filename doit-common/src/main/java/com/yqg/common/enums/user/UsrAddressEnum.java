package com.yqg.common.enums.user;

public enum UsrAddressEnum {
    HOME(0),//居住
    COMPANY(1),//公司
    SCHOOL(2),//学校
    ORDER(3),//下单位置
    BIRTH(4),//出生位置
    FAMILY(5);//家庭收入来源者工作地址;

    private int type;

    UsrAddressEnum(int  type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}