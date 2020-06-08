package com.yqg.common.enums.system;

/**
 * ???????
 * Created by Didit Dwianto on 2017/11/26.
 */
public enum SysThirdLogsEnum {
    SMS_SERVICE(1,"短信服务"),
    ADVANC(2, "advance"),
    YITU(3, "依图"),
    PAY_SERVICE(4, "支付服务"),
    KABIN_CHECK(5, "卡bin校验"),
    COMMIT_LOAN(6, "提交放款"),
    CHEAK_LOAN(7, "查询放款"),
    COMMIT_REPAY(8, "还款申请"),
    CHEAK_REPAY(9, "查询还款"),
    INACTVE_ORDER(10, "使mk订单无效化"),
    PAY_CHECK(11,"放款复查"),
    SEND_ORDER(12,"推单到理财端"),
    TAX_NUMBER_VERIFY(13,"税号实名验证"),
    ASLI_VERIFY(14,"asli实名验证"),
    SELFIE_VERIFY(15,"自拍照验证"),
    SERVERFEE_TRANSFER(16,"服务费转账"),
    FDC_INQUIRY(23,"FDC INQUIRY");

    private int code;
    private String message;

    private SysThirdLogsEnum(int code,String message) {
        this.code=code;
        this.message=message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
