package com.yqg.drools.executor.base;

public enum FlowEnum {
    LABELING_RULE, //打标签规则
    FRAUD_UNIVERSAL_RULE, //欺诈规则&通用规则

    PRODUCT_600,//600rmb规则，120w产品
    PRODUCT_100,//100rmb规则，20w产品
    PRODUCT_100_EXTEND, //100rmb扩展规则
    PRODUCT_50, //50rmb规则，10w产品
    NON_MANUAL_RULE,//免核规则
//    AUTO_CALL_FIRST_BORROWING, //首借外呼
    AUTO_CALL_RE_BORROWING, //复借外呼

    AUTO_CALL_FIRST_UNIVERSAL, // 外呼首借通用规则
    AUTO_CALL_FIRST_PRODUCT600,// 外呼首借600规则
    AUTO_CALL_FIRST_PRODUCT100,// 外呼首借100规则
    AUTO_CALL_FIRST_PRODUCT50,// 外呼首借50规则
    AUTO_CALL_FIRST_NON_MANUAL,//外呼 首借免核

    //AUTO_CALL_RE_UNIVERSAL,// 外呼复借通用规则

    AUTO_CALL_OWNER,//本人外呼

    RISK_SCORE,//评分
}
