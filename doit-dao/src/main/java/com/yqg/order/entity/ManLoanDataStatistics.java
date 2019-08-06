package com.yqg.order.entity;


import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author alan
 */
@Data
@Table("manLoanDataStatistics")
public class ManLoanDataStatistics extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 8892424707234108808L;


    private String loaningAmount;

    private String dueAmount;

    private String dueCount;

    private String overDueAmount;

    private String overDueCount;

    private String notPaidAmount;

    private String notPaidCount;

    private String loaningRate;

    private String expireShouldPayAmount;

    private String shouldPaidRate;

    private String expectBadDebtRate;

    private String expectBadDebtAmount;


}
