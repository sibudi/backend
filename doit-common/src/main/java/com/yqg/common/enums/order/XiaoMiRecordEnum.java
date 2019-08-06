package com.yqg.common.enums.order;

/**
 * Created by wanghuaizhou on 2018/7/25.
 */
public enum XiaoMiRecordEnum {


    APPROVED("01","贷款成功"),
    REJECTED("02","授信/贷款拒绝"),
    ELIGIBLE("03","授信成功"),
    MANUAL("04","进人工"),
    LEAVE("05","用户流失");

    private String code;
    private String message;

    private XiaoMiRecordEnum(String code, String message) {
        this.code=code;
        this.message=message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
