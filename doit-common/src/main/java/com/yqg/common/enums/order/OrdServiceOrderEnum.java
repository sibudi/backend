package com.yqg.common.enums.order;

/**
 * Created by wanghuaizhou on 2019/5/6.
 */
public enum OrdServiceOrderEnum {

    INIT(1,"初始化"),       // Init
    LOAN(2,"待打款"),       // Pending Payment (after the original order has been successfully sent)
    LOANING(3,"放款中"),    // Loaning
    LOAN_SUCCESS(4,"打款成功"),     // Successfully hit money
    LOAN_FAILD(5,"打款失败"),       // Failed to make money
    TRANS_SUCCESS(6,"转账成功");    // Transfer successful

    private int code;
    private String message;

    private OrdServiceOrderEnum(int code, String message) {
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
