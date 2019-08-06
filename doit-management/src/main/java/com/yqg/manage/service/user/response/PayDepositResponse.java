package com.yqg.manage.service.user.response;

import lombok.Data;

import java.util.Date;

/**
 * Author: tonggen
 * Date: 2018/9/12
 * time: 下午4:11
 */
@Data
public class PayDepositResponse {

    private String externalId;
    private String customerName;
    private String transactionIdStatus;
    private String depositAmount ;
    private String depositStatus;
    private String depositChannel;
    private String paymentCode;
    private Date createTime;
    private Date updateTime;
}
