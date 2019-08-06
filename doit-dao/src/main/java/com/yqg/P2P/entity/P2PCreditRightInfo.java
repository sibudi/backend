package com.yqg.P2P.entity;


import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * investment table
 * Created by Didit Dwianto on 2018/02/27.
 */
@Table("p2pCreditRightInfo")
@Data
public class P2PCreditRightInfo extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -8993628754298851445L;

    private String creditRightCode;//orderNo
    private String borrowerName;
    private String borrowerBankCode;
    private String borrowerBankName;
    private String borrowerBankNumberNo;
    private String borrowerBankCardName;
    private String borrowerIdCard;
    private Integer borrowingTerm;
    private BigDecimal borrowingAmount;
    private BigDecimal borrowingCutInterestAmount;
    private String productId;
    private String productName;
    private BigDecimal interestRate;
    private BigDecimal anticipatedIncome;
    private Integer creditRightStatus;
    private String userUuid;
    private String investorsName;
    private Date applyTime;
    private Date refundTime;
    private Date actualRefundTime;
    private String borrowingUseInfo;
}