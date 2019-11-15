package com.yqg.drools.executor.base;


public enum ApplicationFlowEnum {
    LOAN_LIMIT_FIRST_RE_BORROWING, //第一次复借贷款额度判断,对外提供api需要的数据

    LOAN_LIMIT_FIRST_RE_BORROWING_AFTER_SUBMIT,//提交后额度判定
}
