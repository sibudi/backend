package com.yqg.order.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by wanghuaizhou on 2018/5/2.
 */
@Data
@Table("ordDelayRecord")
public class OrdDelayRecord extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 7569029601784817368L;

    private String userUuid;// 用户uuid

    private String orderNo;// 订单编号

    private BigDecimal repayNum;// 本次还款金额

    private BigDecimal interest;   // 利息

    private BigDecimal delayFee;   // 展期服务费

    private String overDueFee;   // 逾期服务费

    private String penaltyFee;   // 逾期滞纳金

    private Integer delayDay;// 申请展期天数

    private String type;// 1 订单待展期 2 订单部分还款成功 展期成功

    private String delayOrderNo;// 展期订单号

}
