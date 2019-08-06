package com.yqg.risk.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(value = "loanLimitRuleResult")
public class LoanLimitRuleResultEntity extends BaseEntity {
    private String userUUid;
    private String orderNo;
    private String ruleName;
    private Boolean pass;
    private String ruleDesc;
    private String productType;
}
