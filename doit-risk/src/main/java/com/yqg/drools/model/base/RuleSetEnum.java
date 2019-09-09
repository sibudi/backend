package com.yqg.drools.model.base;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 * 规则集说明，和kmodule.xml对应
 *
 ****/


public enum RuleSetEnum {
    TOKOPEIDA,
    GOJEK,
    YQG_BLACK_LIST,
    DEVICE_INFO,
    CONTACT_INFO,
    INSTALL_APP,
    //CALL_INFO,
    SHORT_MESSAGE,
    USER_IDENTITY,
    USER_CALL_RECORDS,// 用户通话记录
    FACEBOOK,
    BLACK_LIST_USER,//黑名单用户

    FIRST_BORROWING,
    RE_BORROWING,
    LATEST_LOAN,
    RE_BORROWING_CONTACT,//复借通讯录
    RE_BORROWING_CALL_RECORD,//复借通话记录
    RE_BORROWING_SHORT_MESSAGE,//复借短信
    RE_BORROWING_INSTALLED_APP,//复借手机安装的app
    RE_BORROWING_BLACK_LIST_USER,//黑名单用户
    RE_BORROWING_USER_IDENTITY,//复借用户信息
    LAST_LOAN_FOR_EXTEND,//扩展规则使用的上笔借款

    SPECIAL_RULE,//特殊规则

    AUTO_CALL,//机审后外呼
    AUTO_CALL_USER,//外呼用户信息
    AUTO_CALL_INSTALLED_APP,//外呼app信息
    AUTO_CALL_OWNER,//本人外呼



    LOAN_HISTORY,//历史贷款申请

    SPECIFIED_PRODUCT_100RMB,//100RMB产品自由规则
    LOAN_INFO,//订单贷款信息


    TEST;//测试
}
