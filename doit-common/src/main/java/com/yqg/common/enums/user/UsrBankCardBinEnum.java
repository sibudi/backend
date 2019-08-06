package com.yqg.common.enums.user;

/**
 * ???bin?????????0
 * Created by Didit Dwianto on 2017/11/26.
 */
public enum UsrBankCardBinEnum {
    PENDING(1),// 1 待验证
    SUCCESS(2),// 2 成功
    FAILED(3),// 3 失败
    ;
    private int type;

    UsrBankCardBinEnum(int  type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
