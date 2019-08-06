package com.yqg.common.enums.order;

/**
 * Created by wanghuaizhou on 2019/4/19.
 */
public enum OrdBillStatusEnum {

    RESOLVING(1,"待还款"),//待还款
    RESOLVING_OVERDUE(2,"逾期待还款"),//逾期待还款
    RESOLVED(3,"已还款"),//已还款
    RESOLVED_OVERDUE(4,"逾期已还款");//逾期已还款

    private int code;
    private String message;

    private OrdBillStatusEnum(int code, String message) {
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
