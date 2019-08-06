package com.yqg.order.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Didit Dwianto on 2018/2/2.
 */
@Data
@Table("ordRepayAmoutRecord")
public class OrdRepayAmoutRecord extends BaseEntity implements Serializable{

    private static final long serialVersionUID = 1360828775742012626L;

    private String userUuid;// ??uuid

    private String orderNo;// ????

    private String actualRepayAmout;// ??????

    private String interest;// ??

    private String overDueFee;// ?????

    private String penaltyFee;// ??

    private String repayChannel;// ????  1 bluePay  2 xendit

    private String transactionId;// ??????

    private String repayMethod; // ????  ?ATM alfamart bni bri others?
}
