package com.yqg.order.entity;

import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Didit Dwianto on 2018/1/6.
 */
@Data
public class OrderRepayAmountRecordExtended extends BaseEntity implements Serializable{
    private static final long serialVersionUID = 1360828775742012626L;

    private String userUuid;

    private String orderNo;

    private String actualRepayAmout;

    private String interest;

    private String overDueFee;

    private String penaltyFee;

    private BigDecimal actualDisbursedAmount;
    
    private BigDecimal serviceFee;

    private String repayChannel;//  1 BLUEPAY  2 XENDIT 3 CIMB 4 DOKU 5 BNI 6 OVO (from ordPaymentCode.codeType)

    private String transactionId;

    private String repayMethod; // ALFAMART,INDOMARET, BCA, MANDIRI, BNI, BRI, CIMB, PERMATA, OTHERS, OVO, MANUAL (from T_LPAY_DEPOSIT_METHOD.deposit_method)

    private String status;

    private String orderType; //OrdTypeEnum 0 NORMAL 1 DELAY 2 DELAY_PAID 3 STAGING
    
    private String parentOrderNo; //If 0 NORMAL -> Same as orderNo. If 3 STAGING -> Parent order no in ordOrder

    private Date lendingTime;
}