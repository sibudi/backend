package com.yqg.system.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * ??????
 * Created by Didit Dwianto on 2017/11/25.
 */
@Data
@Table("sysBankBasicInfo")
public class SysBankBasicInfo extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 7079330160093864284L;

    private String bankName;//????
    private String bankCode;//????
    private Integer isUsed;//????(1=??0=?)
    private Date protectStartTime;//??????
    private Date protectEndTime;//??????
    private BigDecimal singleLimit;//????
    private BigDecimal oneDayLimit;//????
}
