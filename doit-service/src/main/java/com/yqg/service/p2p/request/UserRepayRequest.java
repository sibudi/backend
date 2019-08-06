package com.yqg.service.p2p.request;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by wanghuaizhou on 2018/9/7.
 */
@Data
public class UserRepayRequest extends P2PInvokeBaseParam{

    private BigDecimal amountApply; //还款总金额
    private String userUuid; //用户id
    private String userName; //用户姓名
    private String creditorNo;  //订单号
    private BigDecimal overdueRate;  //逾期滞纳金
    private BigDecimal overdueFee;  //逾期服务费
    private BigDecimal interest; //利息

    private String repaymentType; // 还款类型 1正常还款 2分期还款 3展期还款
    private Integer periodNo; // 分期还款期数
}
