package com.yqg.service.order.response;

import java.io.Serializable;

import lombok.Data;

@Data
public class OrderStatusResponse implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 3628246418334443190L;
    private String orderNo;
    private String orderStep;
    private String orderStatus;
    private String orderStatusMsg;
    private String amountApply;
    private String borrowingTerm;
    private String applyTime;
    private String lendingTime;
    private String refundTime;
}