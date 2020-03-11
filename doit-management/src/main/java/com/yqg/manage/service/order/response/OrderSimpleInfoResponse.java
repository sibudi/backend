package com.yqg.manage.service.order.response;

import lombok.Data;
import java.util.Date;

import com.yqg.service.pay.response.RepayResponse;

@Data
public class OrderSimpleInfoResponse {
    private String orderUuid;
    private String realName;
    private String userUuid;
    private String emailAddress;
    private String userMobileNo;
    private Date applyTime;
    private String orderStatusName;
    private Integer orderStatusId;
    private String orderTypeName;
    private Integer orderTypeId;
    private String amountApply;
    private String overdueFee;
    private String overdueMoney;
    private String shouldPayAmount;
    private String actualPayAmount;
    private String extendServiceFee;
}
