package com.yqg.manage.service.order.response;

import lombok.Data;

/**
 * Author: tonggen
 * Date: 2019/4/23
 * time: 5:27 PM
 */
@Data
public class OrderTermBillResponse {

    private Integer status;

    private String billNo;

    private String billTerm;  // 账单期数

    private String billAmount;

    private String refundTime;  // 应还款时间

    private String interest;  // 利息

    private String overdueFee = "0";  // 逾期服务费

    private String penalty = "0";  // 逾期滞纳金

    private String totalAmount = "0";  // 本期应还总金额

    private String actualRepayAmout = "0"; //实际还款

}
