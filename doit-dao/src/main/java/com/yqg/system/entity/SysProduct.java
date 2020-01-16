package com.yqg.system.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**?????
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@Table("sysProduct")
public class SysProduct extends BaseEntity implements Serializable{
    private static final long serialVersionUID = -9037125086074319334L;
    // ????
    private String productCode;
    // ????
    private BigDecimal borrowingAmount;
    // ????
    private Integer borrowingTerm;
    // ????
    private BigDecimal dueFeeRate;
    // ???
    private BigDecimal dueFee;
    //??
    private BigDecimal interestRate;
    //??
    private BigDecimal interest;
    //?????
    private BigDecimal overdueFee;
    // ????1??
    private BigDecimal overdueRate1;
    // ????2??
    private BigDecimal overdueRate2;
    // ????
    private Integer productLevel;
    //??
    private Integer productOrder;

    //每期还款金额  只有分期产品才有此字段  此时borrowingTerm 单位为月
    private BigDecimal termAmount;

    //below 100 is monthly, greater 100 is daily, greater 200 is weekly, greater 300 is yearly
    private Integer productType;

    private BigDecimal dayRate;

    private String rate1;
    private String rate2;
    private String rate3;
    private String rate4;
    private String rate5;
    private String rate6;
    
}
