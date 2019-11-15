package com.yqg.drools.model.base;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class LoanLimitRuleResult {
    private String ruleName;
    private Boolean pass;
    private String ruleDesc;

    private BigDecimal resultAmount; // the result amount for this rule

    public static LoanLimitRuleResult buildResult(String ruleName,Boolean pass,String desc){
        LoanLimitRuleResult result = new LoanLimitRuleResult();
        result.setPass(pass);
        result.setRuleDesc(desc);
        result.setRuleName(ruleName);
        return result;
    }

    public static LoanLimitRuleResult buildResult(String ruleName, Boolean pass, String desc, BigDecimal amount){
        LoanLimitRuleResult result = new LoanLimitRuleResult();
        result.setPass(pass);
        result.setRuleDesc(desc);
        result.setRuleName(ruleName);
        result.setResultAmount(amount);
        return result;
    }

    public void addToList(List<Object> list){
        list.add(this);
    }
}
