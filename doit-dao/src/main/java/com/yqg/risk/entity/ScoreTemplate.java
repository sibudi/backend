package com.yqg.risk.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Table("scoreTemplate")
public class ScoreTemplate extends BaseEntity {
    private String modelName;
    private String variableName;
    private String thresholdName;
    private String thresholdValue;
    private BigDecimal score;
    private Integer version;
    private BigDecimal baseScore;
    private BigDecimal totalThresholdScore;
}
