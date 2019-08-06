package com.yqg.order.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by wanghuaizhou on 2018/1/1.
 */
@Data
@Table("ordPaymentCode")
public class OrdPaymentCode extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -3767615796625641328L;

    private String userUuid; //??id

    private String orderNo; //??id

    private String paymentCode; //????

    private String codeType; // 1 bluePay 2xendit 3CIMB  4DOKU 5 BNI

    private String amountApply; //????

    private String actualRepayAmout; //??????

    private String interest; //??

    private String overDueFee; //?????

    private String penaltyFee; //??

    private String principal; //还款本金

    private String couponUuid; //优惠券uuid
}

