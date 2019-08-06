package com.yqg.system.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by wanghuaizhou on 2019/6/24.
 */
@Data
@Table("sysProductChannel")
public class SysProductChannel extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1182043287172431297L;

    private String productUuid;

    private BigDecimal borrowingAmount;

    private Integer borrowingTerm;

    private Integer productType;

    private BigDecimal dayRate;

    private BigDecimal channel;
}
