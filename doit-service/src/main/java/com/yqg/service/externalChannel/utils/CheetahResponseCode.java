package com.yqg.service.externalChannel.utils;

import lombok.Getter;

/**
 * Created by wanghuaizhou on 2018/12/26.
 */
@Getter
public enum CheetahResponseCode {

    CODE_OK_0(0,"success"),
    PARAM_ERROR_1001(1001,"参数错误"), //参数错误
    ORDER_STATES_ERROR_1002(1002,"贷款订单)状态错误"), //(贷款订单)状态错误
    IMAGE_ERROR_1021(1021,"图片错误"), //图片错误
    AUTH_FAILD_2001(2001,"认证失败"), //认证失败
    NO_VAILID_PRODUCT(2002,"konfigurasi produk tidak terdaftar"),//产品配置不存在
    SERVER_INTERNAL_ERROR_3001(3001,"服务器内部错误"), //服务器内部错误
    NOT_ALLOW_LOAN_ERROR_11000(11000,"不可借款"), //不可借款
    BASIC_INFO_ERROR_12000(12000,"基础信息错误"),  //基础信息错误
    TELE_NUMBER_FORMAT_ERROR_12001(12001,"手机号格式错误"),  //手机号格式错误
    IDCARD_NUMBER_FOMAT_ERROR_12002(12002,"身份证格式错误"), //身份证格式错误
    SUPPLY_INFO_ERROR_13000(13000,"补充信息错误"), //补充信息错误
    BANK_ACCOUNT_NOT_CONSISTENT_13001(13001,"银行账号名与用户全名不一致"), //银行账号名与用户全名不一致
    ORDER_NOT_EXIT_14001(14001,"订单不存在"),//订单不存在
    ORDER_INFO_NOT_COMPLETE_14002(14002,"订单资料不全"),//订单资料不全
    DEVICE_INFO_ERROR_15001(15001,"设备信息不正确"),  // 设备信息不正确
    ORDER_NOT_COMPLETE_16001(16001,"用户有未完成订单"),  //用户有未完成订单
    USER_IN_REFUSE_LIMIT_TIME_16002(16002,"用户仍在拒绝期限内"),  //用户仍在拒绝期限内
    ;

    CheetahResponseCode(int code,String message){
        this.code=code;
        this.message=message;
    }
    private String message;
    private int code;

}
