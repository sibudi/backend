package com.yqg.service.order.response;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/9/20.
 */
@Data
public class PaymentProofResponse {

    private String orderNo;// 订单号
    private String loanDate; //打款日期
    private String platformName;//平台名称
    private String userName;//借款人姓名
    private String userAccount;//收款账户
    private String loanAmout;//打款金额
    private String loanStatus = "1";  //打款状态 1 成功
    private String loanBank;  // 打款银行

    private String loanIconUrl;  // 打款渠道的icon
}
