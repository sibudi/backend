package com.yqg.drools.model.base;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoanLimitRuleResult {
    private String ruleName;
    private Boolean pass;
    private String ruleDesc;

    public static LoanLimitRuleResult buildResult(String ruleName,Boolean pass,String desc){
        LoanLimitRuleResult result = new LoanLimitRuleResult();
        result.setPass(pass);
        result.setRuleDesc(desc);
        result.setRuleName(ruleName);
        return result;
    }

    public void addToList(List<Object> list){
        list.add(this);
    }
}
