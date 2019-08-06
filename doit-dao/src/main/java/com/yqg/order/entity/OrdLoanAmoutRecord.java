package com.yqg.order.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Didit Dwianto on 2018/1/31.
 */
@Data
@Table("ordLoanAmoutRecord")
public class OrdLoanAmoutRecord extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 8032838419320327535L;

    private String userUuid;//

    private String orderNo;//

    private String actualLoanAmout;//

    private BigDecimal serviceFee;//

    private String loanChannel;//
}
