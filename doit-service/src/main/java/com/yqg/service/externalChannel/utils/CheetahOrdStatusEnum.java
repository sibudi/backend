package com.yqg.service.externalChannel.utils;

/**
 * Created by wanghuaizhou on 2019/1/8.
 */
public enum CheetahOrdStatusEnum {

    SUBMITTING(80,"待补充资料"),     //待补充材料
    PASS_CHECK(100,"审批通过"),     //审批通过的状态
    IN_CHECKING(90,"审批中"),   //审批中
    WAIT_CONFIRM(109,"降额待确认"),   //  降额待确认
    NOT_PASS_CHECK(110,"审批不通过"),  //审批不通过
    CANCLE(161,"贷款取消"),         //贷款取消
    LOAN_FAILD(169,"放款失败"), //放款失败
    LOAN_SUCCESS(170,"放款成功"),//放款成功
    /**
     * 1. 款项必须已成功打至用户卡中才可推送放款成功；
     2. 进入放款成功的订单必须推送还款结果，且仅能流转至：200（到期结清）、180（已逾期），若用户已展期订单状态仍为170即可，但需更新还款计划；

     * */
    IN_REPAYMENT(175,"还款中"),  // 还款中  应该没有这个选项
    /**
     1. 仅针对多期产品，才有该状态；
     2. 当某期逾期后结清时，并还需要还款剩余款项，则需要更新订单状态为该状态；
     */
    OVERDUE(180,"逾期"),   //  逾期订单
    REPAY_SUCCESS(200,"贷款结清");    // 用户还清全部欠款



    private int code;
    private String message;

    private CheetahOrdStatusEnum(int code, String message) {
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
