package com.yqg.common.enums.system;

/**
 * Created by wanghuaizhou on 2018/9/6.
 */
public enum LoanDisburseTypeEnum {

    PAYDAYLOAN("PAYDAYLOAN"),// do-it 放款
    P2P("P2P"),// p2p 放款
    BONUS("BONUS"),//活动奖励提现
    PRE_SERVICE_FEE("PRE_SERVICE_FEE"); // do-it 放款手续费

    private String type;

    private LoanDisburseTypeEnum(String type) {
        this.setType(type);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
