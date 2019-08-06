package com.yqg.enums;


import lombok.Getter;

/***
 * 规则类别
 */

@Getter
public enum RuleTypeEnum {

    AUTO_CALL_RULES(25);

    RuleTypeEnum(int code) {
        this.code = code;
    }

    private int code;
}
