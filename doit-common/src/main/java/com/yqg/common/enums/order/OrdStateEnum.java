package com.yqg.common.enums.order;

/**
 * Created by luhong on 2017/11/24.
 */
public enum OrdStateEnum {
    SUBMITTING(1,"待提交"),//待提交
    MACHINE_CHECKING(2,"待机审"),//待机审
    FIRST_CHECK(3,"待初审"),//待初审
    SECOND_CHECK(4,"待复审"),//待复审
    LOANING(5,"待放款"),//待放款
    LOANING_DEALING(6,"放款处理中"),//放款处理中
    RESOLVING_NOT_OVERDUE(7,"待还款（未逾期）"),//待还款（未逾期）
    RESOLVING_OVERDUE(8,"待还款（已逾期）"),//待还款（已逾期）
    //    RESOLVED_DEALING(9,"还款处理中"),//还款处理中     （产品说不需要这种状态）
    RESOLVED_NOT_OVERDUE(10,"正常已还款"),// 正常已还款。订单生命周期完成 (正常完成)
    RESOLVED_OVERDUE(11,"逾期已还款"),// 逾期已还款。订单生命周期完成 (正常完成)

    MACHINE_CHECK_NOT_ALLOW(12,"机审不通过"),// 机审不通过 (异常完成)
    FIRST_CHECK_NOT_ALLOW(13,"初审不通过"),// 初审不通过(异常完成)
    SECOND_CHECK_NOT_ALLOW(14,"复审不通过"),//复审不通过 (异常完成)
    CANCEL(15,"取消"), // 取消。因为审核不通过被取消，订单生命周期完成(异常完成)
    LOAN_FAILD(16,"放款失败"),// 放款失败

    WAIT_CALLING(17,"等待外呼"),//等待外呼
    WAITING_CALLING_AFTER_FIRST_CHECK(18,"初审后等待外呼"),//订单初审后等待外呼
    WAITING_CONFIRM(19,"降额后等待用户确认"),//降额后等待用户确认

    WAITING_SIGN_CONTRACT(20,"待签约");//等待签约


    private int code;
    private String message;

    private OrdStateEnum(int code, String message) {
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
