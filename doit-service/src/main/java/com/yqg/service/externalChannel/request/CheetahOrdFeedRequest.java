package com.yqg.service.externalChannel.request;

import lombok.Data;

import java.util.List;

/**
 * Created by wanghuaizhou on 2019/1/8.
 */
@Data
public class CheetahOrdFeedRequest {

    private String orderId; //订单号

    private int productId;

    private int installments;

    private int currentInstallment;

    private int orderStatus;

    private int auditStatus;

    private long disburseTime;

    private long updateTime;

    private List<BillBean> bills;

    @Data
    public static class BillBean {

        private int installmentNumber;

        private int status;

        private long amount;

        private long interestAmount;

        private long fineAmount;

        private long adminAmount;

        private long repaidAmount;

        private String dueDate;

    }
    private int[] returnReason;
}
