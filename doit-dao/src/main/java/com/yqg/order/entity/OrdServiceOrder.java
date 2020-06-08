package com.yqg.order.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table("ordServiceOrder")
public class OrdServiceOrder extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -8281308566092454568L;

    private String userUuid;//用户id
    private String orderNo;//原订单编号
    private BigDecimal serviceFee;//放款所需服务费
    private String disburseChannel;//放款通道
    // refer to OrdServiceOrderEnum
    private Integer status;
    private Date loanTime; // 放款日期
}
