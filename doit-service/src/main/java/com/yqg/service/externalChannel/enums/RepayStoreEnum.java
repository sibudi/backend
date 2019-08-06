package com.yqg.service.externalChannel.enums;

/**
 * Created by wanghuaizhou on 2018/6/29.
 */
// 对cashcash支持的便利店还款
public enum  RepayStoreEnum {

    ALFAMART(2001),//Alfamart
    INDOMARET(2002),//Indomaret
    LAWSON(2003),//lawson
    CIRCKE_K(2004);//circle K

    private Integer type;

    RepayStoreEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
