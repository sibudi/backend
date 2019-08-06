package com.yqg.service.order.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by wanghuaizhou on 2019/4/20.
 */
@Data
public class OrdBillResponse {

    private Integer status;

    private String billNo;

    private String billTerm;  // 账单期数

    private String billAmount;

    private String refundTime;  // 应还款时间

    private String interest;  // 利息

    private String overdueFee = "0";  // 逾期服务费

    private String penalty = "0";  // 逾期滞纳金

    private String totalAmount = "0";  // 本期应还总金额
}
