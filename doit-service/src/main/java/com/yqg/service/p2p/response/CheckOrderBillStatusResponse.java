package com.yqg.service.p2p.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by wanghuaizhou on 2019/7/8.
 */
@Data
public class CheckOrderBillStatusResponse {

    private String id;

    //债权编号
    private String creditorNo;
    //放款时间
    private Date lendingTime;
    //'应还款时间'
    private Date refundIngTime;
    //'实际还款时间'
    private Date refundActualTime;
    //应还款金额
    private BigDecimal refundIngAmount;
    //实际还款金额
    private BigDecimal amountActual;
    //还款状态(1.待还款，2.还款处理中, 3.已还款，4.逾期已还款)
    private Integer status;
    //'期数'
    private Integer periodNo;

}
