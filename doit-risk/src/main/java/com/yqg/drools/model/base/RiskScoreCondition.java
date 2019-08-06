package com.yqg.drools.model.base;

import com.yqg.risk.entity.ScoreTemplate;
import com.yqg.drools.model.ScoreModel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Setter
@Getter
public class RiskScoreCondition {
    private Map<String, ScoreTemplate> scoreTemplateMap;

    private Integer version; //模型版本

    private BigDecimal baseScore;

    private BigDecimal totalThresholdScore;

    public boolean isSuitable(ScoreModel.VariableEnum variableEnum) {
        if (scoreTemplateMap == null) {
            return false;
        }
        return scoreTemplateMap.containsKey(variableEnum.name());
    }

    public BigDecimal getVariableScore(ScoreModel.VariableEnum variableEnum) {
        return scoreTemplateMap.get(variableEnum.name()).getScore();
    }

    public String getThresholdValue(ScoreModel.VariableEnum variableEnum) {
        return scoreTemplateMap.get(variableEnum.name()).getThresholdValue();
    }



}
