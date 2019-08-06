package com.yqg.common.enums.order;

/**
 * Created by wanghuaizhou on 2019/5/6.
 */
public enum OrdServiceOrderEnum {

    INIT(1,"初始化"),//初始化
    LOAN(2,"待打款"),// 待打款(原订单打款成功后)
    LOANING(3,"放款中"),// 放款中
    LOAN_SUCCESS(4,"打款成功"),//打款成功
    LOAN_FAILD(5,"打款失败"),//打款失败
    TRANS_SUCCESS(6,"转账成功");//转账成功

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
