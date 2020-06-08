package com.yqg.common.constants;

import static org.apache.poi.sl.usermodel.TableCell.BorderEdge.left;

/**
 * @author niebiaofei
 *
 */
public class SysParamContants {

    //e.g. ??????
    public static final String TONGDUN_SCORE_LIMIT = "tongdun:score:limit";

    //e.g. ???????
    public static final String YITU_SCORE_LIMIT = "yitu:score:limit";
    // ????????
    public static final String PRODUCT_INTRO_URL = "product:intro:url";
    // ????????
    public static final String PRODUCT_TARGET_URL = "product:target:url";
    //ip??count
    public static final String IP_ADDRESS_COUNT = "ip:address:count";

    //????????
    public static final String LOAN_OFF_NO = "loan:off:no";
    // BCA 打款开关
    public static final String LOAN_OFF_NO_BCA = "loan:off:no:bca";
    // BNI 打款开关
    public static final String LOAN_OFF_NO_BNI = "loan:off:no:bni";
    // CIMB 打款开关
    public static final String LOAN_OFF_NO_CIMB = "loan:off:no:cimb";

    public static final String LOAN_ACTIVITY_OFF_NO = "loan:acticity:off:no";
    //??????????????
    public static final String LOAN_BF_UF_OFF_NO = "loan:bf:uf:off:no";
    //??????????
    public static final String LOAN_TEMP_OFF_NO = "loan:temp:off:no";
    //????????
    public static final String RISK_OFF_NO = "risk:off:no";
    //?????????
    public static final String OVERDUE_OFF_NO = "overdue:off:no";
    //???15??????
    public static final String REFUND_REMIND_OFF_NO_15 = "refund:remind:off:no:15";
    //????11???????
    public static final String OVERDUE_REMIND_OFF_NO_11 = "overdue:remind:off:no:11";
    //???????3???????
    public static final String REFUND_BEFORE_REMIND_OFF_NO_10 = "refund:before:remind:off:no:10";
    //????????
    public static final String RECONCILIATION_OFF_NO = "reconciliation:off:no";
    //?????????????
    public static final String SMS_CODE_REMIND_OFF_NO = "sms:code:remind:off:no";
    //??????
    public static final String AUTO_REFUND_OFF_NO = "auto:refund:off:no";
    //????????
    public static final String AUTO_BF_REFUND_OFF_NO = "auto:bf:refund:off:no";
    //???????
    public static final String PUSH_SMS_REMIND_PERSON = "push:sms:remind:person";
    //????????
    public static final String PUSH_SMS_LOTTERY_DRAW_REMIND = "push:sms:lottery:draw:remind";

    //?????
    public static final String NOT_IN_LOAN_SECTION = "not:in:loan:section";

    // CIMB的放款数量
    public static final String LOAN_CIMB_COUNT = "loan:count:cimb";

    // p2p推单个数
    public static final String LOAN_P2P_COUNT = "loan:count:p2p";

    public static final String LOAN_P2P_COUNT_BCA = "loan:count:p2p:bca";

    public static final String LOAN_P2P_COUNT_BNI = "loan:count:p2p:bni";

    public static final String LOAN_P2P_COUNT_CIMB = "loan:count:p2p:cimb";

    //??????????
    public static final String LOAN_REMIND_RELVEANT_PERSON  = "loan:remind:relevant:person";
    //?????????
    public static final String LOAN_WATING_OFF_NO  = "loan:wating:off:no";
    //???????????
    public static final String LOAN_TEMP_WATING_OFF_NO  = "loan:temp:wating:off:no";
    //???????????
    public static final String LOAN_BF_WATING_OFF_NO  = "loan:bf:wating:off:no";
    //?????
    public static final String TONGDUN_CONDITIONS  = "tongdun:conditions";
    //?????
    public static final String ZHIMA_CONDITIONS  = "zhima:conditions";

    //??????????
    public static final String ORDER_MANUAL_REFUND_PASSWORD = "order:manual:refund:password";
    //????????
    public static final String LOAN_SYS_TOTAL_ACCOUNT = "loan:sys:total:account";
    //?????????
    public static final String LOAN_OMNIACCOUNT_SYS_DAY_TOTAL_ACCOUNT = "loan:omniaccount:sys:day:total:account";
    //????-??????
    public static final String LOAN_KOUDAI_DAY_MAX_AMOUNT = "loan:koudai:day:max:amount";
    //??-??????
    public static final String LOAN_WEIDAI_DAY_MAX_AMOUNT = "loan:weidai:day:max:amount";
    //????-??????????
    public static final String LOAN_KOUDAI_ORDER_STATUS_SYNC_TASK="loan:koudai:orderStatus:syncTask";

    //???????????
    public static final String WATING_REFUND_BF_OFF_NO  = "wating:refund:bf:off:no";
    //???????????uf
    public static final String WATING_REFUND_UF_OFF_NO  = "wating:refund:uf:off:no";
    //?????????
    public static final String LOAN_RANDOM_OFF_NO  = "loan:random:off:no";
    //??????????????
    public static final String LOAN_PAY_CAPITAL_RANDOM_OFF_NO  = "loan:pay:capital:random:off:no";
    //???????,?????
    public static final String LOAN_PAY_CAPITAL_THIRD_RATIO  = "loan:pay:capital:third:ratio";

    //?????????
    public static final String ORDER_PROXY_HOLD_REFUND_PASSWORD  = "order:proxy:hold:refund:password";

    /**
     * ?????????????
     */
    public static final String ORDER_PROXY_HOLD_SUCCESS_MOBILE  = "order:proxy:hold:success:mobile:off";

    //ios??????
    public static final String ORDER_IOS_VERSION_OFF_NO  = "order:ios:version:off:no";

    //????????
    public static final String ADMIN_SEND_SMS_PASSOWORD = "admin:send:sms:password";

    //??????
    public static final String ADMIN_MANDAO_SMS_CONTENT = "admin:mandao:sms:content";

    //????????
    public static final String SYSTEM_ZHIMA_FEEDBACK_DATA_OFF_NO = "system:zhima:feedback:data:off:no";

    //?360????????
    public static final String RISK_RONG360OFF_NO = "risk:rong360off:no";

    //????????
    public static final String SYSTEM_SEND_SMS_LIST = "system:send:sms:list";
    //???sql
    public static final String SYSTEM_EXECUTE_SQL = "system:execute:sql";

    //???????????????
    public static final String SYSTEM_SMS_TYPE = "system:sms:type";

    //??????(????)
    public static final String SYS_KOUDAI_SERVICE_FEE_RATE_OTHER = "sys:koudai:service:fee:rate:other";
    //??????(???)
    public static final String SYS_DAZHNAGU_SERVICE_FEE_RATE_OTHER = "sys:dazhanghu:service:fee:rate:other";
    //??????(??)
    public static final String SYS_WEIDAI_SERVICE_FEE_RATE_OTHER = "sys:weidai:service:fee:rate:other";
    //????(????)
    public static final String SYS_KOUDAI_INTEREST_RATE_OTHER = "sys:koudai:interest:rate:other";
    //????(???)
    public static final String SYS_DAZHANGHU_INTEREST_RATE_OTHER = "sys:dazhanghu:interest:rate:other";
    //????(??)
    public static final String SYS_WEIDAI_INTEREST_RATE_OTHER = "sys:weidai:interest:rate:other";
    //????
    public static final String SYS_INTEREST_RATE_OUR = "sys:interest:rate:our";
    //?????(????)
    public static final String SYS_KOUDAI_LOAN_HAND_LING_CHARGE = "sys:koudai:loan:hand:ling:charge";

    //H5????????????
    public static final String H5_ZHIMA_AUTH_SUCCESS_URL = "h5:zhima.auth:success.url";
    //H5????????????
    public static final String H5_ZHIMA_AUTH_FAILD_URL = "h5:zhima.auth:faild.url";

    //???????????????
    public static final String SMS_COLLECT_MESSAGE_PERSON = "sms:collect:message:person";
    //????????????
    public static final String MAIL_RECEIVERS_OVERDUE = "mail:receivers:overdue";
    //????????????????
    public static final String MAIL_RECEIVERS_CHANNELCHANGERATE = "mail:receivers:channelchangerate";
    //??????????????
    public static final String MAIL_RECEIVERS_PREPAYMENT = "mail:receivers:prepayment";
    //??????????
    public static final String MAIL_RECEIVERS_BIGDATA = "mail:receivers:bigdata";
    //???????????
    public static final String MAIL_RECEIVERS_REFUSED = "mail:receivers:refused";
    //????????????????
    public static final String MAIL_RECEIVERS_TotalAccount = "mail:receivers:totalAccount";
    //??????????????????
    public static final String SYS_OMNIACCOUNT_KOUDAI_PASSWORD  = "sys:omniaccount:koudai:password";
    //?????????????
    public static final String SYS_RISK_LOAN_SMS_PERSON  = "sys:risk:loan:sms:person";
    //????????
    public static final String SYS_RISK_LOAN_VALUE  = "sys:risk:loan:value";

    //?????????????IP
    public static final String SYS_OMNIACCOUNT_VALID_IPADDRESS  = "sys:omniaccount:valid:ipAddress";
    //??????????????IP
    public static final String SYS_KOUDAI_VALID_IPADDRESS  = "sys:koudai:valid:ipAddress";

    /**
     * ????????
     */
    public static final String LOAN_DOWN_BANK_LIST="loan:down:banklist";
    //??????????
    public static final String RISK_XJBK_OFF_NO = "risk:xjbkRiskOff:no";
    //????????
    public static final String XJBK_OFF_NO = "risk:xjbkoff:no";
    //???????
    public static final String MAIL_RECEIVERS_LATEFEES = "mail:receivers:latefees";


    //??????
    public static final String SYSTEM_WEIDAI_OPEN_ACCOUNT_OFF_NO = "system:weidai:openAccount:off:no";
    //??????
    public static final String SYSTEM_WEIDAI_TENDER_OFF_NO = "system:weidai:tender:off:no";
    //???
    public static final String SYSTEM_ZHIMA_SCORE = "system:zhima:score";

    //??????
    public static final String USER_CERTIFICATION_ON = "user:certification:on";
    //???? ????????
    public static final String NUMBER_OF_ORDERS = "number:of:orders";
    //????????
    public static final String NUMBER_OF_IDENTITY = "number:of:identity";

    //??app??
    public static final String UPLOAD_APPS_OFF_ON = "upload:apps:off:on";
    //??app?????????iOS??????
    public static final String UPLOAD_APPS_DATA = "upload:apps:data";

    //????????????
    public static final String NUMBER_OF_CERTIFICATE = "number:of:certificate";

    //????????? ??????
    public static final String CHEAK_LOANING_OFF_NO = "cheak:loaning:off:no";
    //??????????? ??????
    public static final String CHEAK_REPAY_OFF_NO = "cheak:repay:off:no";

    //????????  ????????
    public static final String  MAIL_RECEIVERS_SMSBALANCE = "mail:receivers:smsBalance";
    //????????  ????????
    public static final String  SMS_RECEIVERS_SMSBALANCE = "sms:receivers:smsBalance";
    // ???????????
    public static final String  SMS_RISK_EXCEPTION = "sms:risk:exception";

    //????????  ????????
    public static final String  MAIL_RECEIVERS_LOANBALANCE = "mail:receivers:loanBalance";
    //??????????  ????????
    public static final String  SMS_RECEIVERS_LOANBALANCE = "sms:receivers:loanBalance";

    //?????????? ????????
    public static final String MAIL_RECEIVERS_PAY_CHECK = "mail:receivers:pay:check";

    // ?????
    public static final String  RISK_MAIL_RECEIVERS_KEYINDEX = "risk:mail:receivers:keyindex";


    //???? ??8? ??????????
    public static final String REFUND_BEFORE_REMIND_OFF_NO_8 = "refund:before:remind:off:no:8";

    //???? ??4? ??????????
    public static final String REFUND_BEFORE_REMIND_OFF_NO_16 = "refund:before:remind:off:no:16";

    //?? ??4? ??????????
    public static final String REFUND_AFTER_REMIND_OFF_NO_16 = "refund:after:remind:off:no:16";

    // ????
    public static final String  TELEPHONE_MAIL_RECEIVERS_BACK = "telephone:mail:receivers:back";
    // ????
    public static final String  REMINDER_MAIL_RECEIVERS = "mail:receivers:reminder";
    // doit????????????
    public static final String  REMINDER_MAIL_KEYINDEX = "mail:receivers:fengkong:keyIndex";

    // doit????????????
    public static final String  REMINDER_MAIL_RISKINMAIL = "mail:receivers:fengkong:riskInMail";

    public static final String  REMINDER_MAIL_CALLOUT_MAIL = "mail:receivers:fengkong:callOut";

    // Doit新老用户每日逾期率
    public static final String  DAY_OVERDUE_RATE = "mail:receivers:fengkong:dayOverdueRate";

    // ?????????
    public static final String  REMINDER_AND_CHECK = "mail:receivers:fengkong:reminderAndCheck";

    // d-1d-2????
    public static final String  REMINDER_MAIL_OVERDUE_USERS = "mail:receivers:operation:overdueUsers";

    public static final String  REMINDER_MAIL_OVERDUERATE_FOR_OPERATION = "mail:receivers:operation:overdueRate";

    // ???????????
    public static final String  DELAYORDER_OF_NUMBER = "delayOrder:of:number";
    // ???? ?????
    public static final String  DELAYORDER_OF_GRANULARITY = "delayOrder:of:granularity";
    // ???? ???
    public static final String  DELAYORDER_OF_FEE = "delayOrder:of:fee";

    // 每天发送待放款用户短信的个数
    public static final String  NUMBER_OF_SMSCOUNT = "number:of:smsCount";

    // 审核人员每日审核状况
    public static final String  REVIEWER_DAY = "mail:receivers:dayReview";

    // 运营日报
    public static final String  OPERATION_DAY = "mail:receivers:operation";

    // 催收时报
    public static final String  COLLECTION_HOUR = "mail:receivers:collection";

    // 催收时报
    public static final String  COLLECTION_HOUR_QI = "mail:receivers:collection:QI";

    //保险卡需求限制次数开关，true的话会进行限制，false不限制
    public static final String RISK_INSURANCE_CARD_RULE_LIMIT_SWITCH = "risk:insurance_card_rule_limit_switch";

    public static final String RISK_ABTEST_CONFIG = "risk:ABTestRule:config";

    //一级邀请佣金
    public static final String ACTIVITY_INVITE_LV1 = "activity:invite:lv1";
    //二级邀请佣金
    public static final String ACTIVITY_INVITE_LV2 = "activity:invite:lv2";
    //最低提现金额
    public static final String ACTIVITY_CASEOUT_MIN = "activity:caseout:min";
    //账户锁
    public static final String ACTIVITY_ACCOUNT = "lock:activity:ACCOUNT:";


    //ON if true
    public static final String RISK_AUTO_CALL_SWITCH = "risk:auto_call:switch";
    //use whitelisting  if true
    public static final String RISK_AUTO_CALL_WHITELIST_SWITCH = "risk:auto_call:whitelist:switch";
    //comma separated whitelisted number
    public static final String RISK_AUTO_CALL_WHITELIST_NUMBER = "risk:auto_call:whitelist:number";

    // 提额产品开关
    public static final String PRODUCT_LEVEL_OFF_NO = "product:level:off:no";


    public static final String RISK_100RMB_PRODUCT_SWITCH_IOS = "risk:100rmb_product:switch:iOS";//100rmb 产品ios开关
    public static final String RISK_100RMB_PRODUCT_SWITCH_ANDROID = "risk:100rmb_product:switch:android";//100rmb 产品android开关

    // 推单开关
    public static final String PAY_ISSUING_TO_P2P_SWITCH = "pay:issuing:to:p2p:switch";

    // 每日放款金额  和 每日放款限制
    public static final String LOAN_ACCOUNT_LIMIT = "loan:amount:limit";
    public static final String LOAN_ACCOUNT_NOW = "loan:amount:now";


    // 首页产品费率
    public static final String PRODUCT_RATE_RATE1 = "product:rate:rate1";
    public static final String PRODUCT_RATE_RATE2 = "product:rate:rate2";
    public static final String PRODUCT_RATE_RATE3 = "product:rate:rate3";
    public static final String PRODUCT_RATE_RATE4 = "product:rate:rate4";
    public static final String PRODUCT_RATE_RATE5 = "product:rate:rate5";
    public static final String PRODUCT_RATE_RATE6 = "product:rate:rate6";

    // 首页金额回收率
    public static final String SYSTEM_REPAY_RATE = "system:repay:rate";

    //check is default product is staging. 1 for new user only, 9 for all user
    public static final String SYSTEM_DEFAULT_IS_INSTALLMENT = "system:default:isinstallment";
    public static final String SYSTEM_DEFAULT_INSTALLMENT_PRODUCT = "system:default:installment:product";
    //rdn repayment channel
    public static final String RDN_REPAYMENT_CHANNELS = "rdn:repayment:channel";        //Also used by biak-rest
    public static final String RDN_NOTIFICATION_EMAIL_TO = "rdn:notification:email:to"; //Also used by biak-rest
    public static final String RDN_NOTIFICATION_EMAIL_CC = "rdn:notification:email:cc"; //Also used by biak-rest

}

