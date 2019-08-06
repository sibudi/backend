package com.yqg.risk.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Table("orderScoreDetail")
public class OrderScoreDetail extends BaseEntity {
    private String orderNo;
    private String userUuid;
    private String variableName;//变量名
    private String variableThresholdName;//变量对应的阈值分组
    private BigDecimal score;//变量分数
    private String realValue;//实际值
    private String modelName;//模型对应产品(or 模型名称)
    private Integer version;
}
