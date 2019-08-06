package com.yqg.mongo.entity;

import com.yqg.base.data.condition.BaseMongoEntity;
import com.yqg.base.mongo.anaotations.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("loanLimitRuleResult")
public class LoanLimitRuleResultMongo extends BaseMongoEntity {
    private String ruleName;
    private Boolean pass;
    private String ruleDesc;
    private String productType;
}
