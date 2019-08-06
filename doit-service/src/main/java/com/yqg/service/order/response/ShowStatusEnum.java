package com.yqg.service.order.response;

import lombok.Getter;

@Getter
public enum ShowStatusEnum {
    INIT_STAGE("0"),//待新增订单-->对应与没有处理中的订单
    SUBMITTING_STAGE("1"),//待提交-->orderStatus = 1
    //审核中--> orderStatus = 2,3,4,5,6,12,17,18(待机审，待初审、复审，待放款，放款处理中，待外呼)
    REVIEWING_STAGE("2"),
    NORMAL_REPAYMENT_STAGE("3"),//正常还款阶段-->orderStatus = 7
    OVERDUE_REPAYMENT_STAGE("4"),//逾期还款阶段-->orderStatus = 8
    REJECTED_STAGE("6"),//用户处于拒绝限制阶段-->orderStatus = 12,13,14,并且没有超出限制天数
    LOAN_FAILED_STAGE("7"),//放款失败阶段-->orderStatus= 16
    LOAN_CONFIRMING_STAGE("8"),//待用户确认-->orderStatus = 19
    DIGI_SIGN_STAGE("9"),//待激活签约-->orderStatus = 20
    ;
    //12状态拒绝时间小于1h，显示2待审核，超过1小时显示其他，拒绝时间已经超过限制的天数，显示0可新增订单，其他情况显示6，拒绝
    ShowStatusEnum(String code) {
        this.code = code;
    }
    private String code;
}
