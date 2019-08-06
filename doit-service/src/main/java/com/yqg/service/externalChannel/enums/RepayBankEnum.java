package com.yqg.service.externalChannel.enums;

/**
 * Created by wanghuaizhou on 2018/6/29.
 */
// 对cashcash支持的银行 还款
public enum RepayBankEnum {
    BNI(1001),//BNI
    BRI(1002),//Indomaret
    MANDIRI(1003),//lawson
    BCA(1004),//lawson
    Permata(1005),//lawson
    OtherBanks(1006);//circle K

    private Integer type;

    RepayBankEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
