package com.yqg.order.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by wanghuaizhou on 2019/4/19.
 */
@Data
@Table("ordBill")
public class OrdBill extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -4598164742973217843L;

    private String userUuid;

    private String orderNo;

    private String productUuid;

    private Integer status;

    private String billTerm;  // 账单期数

    private BigDecimal billAmout;

    private Date refundTime;  // 应还款时间

    private Date actualRefundTime;  // 实际还款时间

    private BigDecimal interest;  // 利息

    private BigDecimal overdueFee;  // 逾期服务费

    private BigDecimal overdueRate;  // 逾期费费率
}
