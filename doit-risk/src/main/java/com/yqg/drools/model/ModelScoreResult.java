package com.yqg.drools.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 模型评分结果
 */

@Getter
@Setter
public class ModelScoreResult {

    private BigDecimal product600Score;

    private BigDecimal product100Score;

    private BigDecimal product50Score;

    private BigDecimal product600ScoreV2;
}
