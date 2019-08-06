package com.yqg.drools.model.base;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ScoreRuleResult {
    private String variableName;//变量名
    private String variableThresholdName;//变量对应的阈值分组
    private BigDecimal score;//变量分数
    private String realValue;//实际值
    private String modelName;//模型对应产品(or 模型名称)

    public void addToResultList(List list) {
        list.add(this);
    }
}



