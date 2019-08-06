package com.yqg.drools.model.base;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class RuleConditionModel {

    private Map<String,Boolean> ruleExecuteMap = new HashMap<>();

    public boolean isSuitableForRule(String ruleName){
        if(ruleName == null||ruleExecuteMap == null){
            return false;
        }
        if(ruleExecuteMap.get(ruleName)== null){
            return false;
        }
        return ruleExecuteMap.get(ruleName);
    }
}
