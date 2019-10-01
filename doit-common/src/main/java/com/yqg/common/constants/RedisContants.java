package com.yqg.common.constants;

/**
 * @author Jacob
 */
public class RedisContants {

    public static final int REFUND_EXPIRES_SCOND = 3600 * 24 * 2;
    public static final int GOBAL_DAY_SECONDS = 3600 * 24;
    public static final int GOBAL_FIVE_MIN_SECONDS = 60;
    public static final int GOBAL_120_SECONDS = 120;


    public static final String SESSION_PREFIX = "session:";
    public static final int EXPIRES_SECOND = 30 * 30;
    public static final int EXPIRES_COUNT_SECOND = 60 * 24;

    public static final String SESSION_SMS_KEY = "smsKey:";
    public static final String SMS_KEY_INFORBIP = "smsKey:sid";
    public static final String SESSION_SMS_LOGIN_KEY = "smsLoginKey:";
    // ????????
    public static final String SMS_BANK_CADR_LOCK = "smsBankCard:lock:";

    public static final String SESSION_BANK_CARD_COUNT_KEY = "bankCardCountKey:";
    public static final String SESSION_BT_BANK_CARD_COUNT_KEY = "btBankCardCountKey:";

    public static final String SYSTEM_YIMEI_ACCESS_TOKEN = "sys:yimei:token";
    public static final String NUMBERS_OTP_OFF = "numbers:otp:off";

    /**
     * ??????
     */
    public static final String REPEAT_LOCK = "repeatLock:";

    //e.g. order:identity:times:20170608:userId
    public static final String ORDER_IDENTITY_TIMES_FIX = "order:identity:times:";

    //e.g. order:identity:times:userId
    public static final String ORDER_IDENTITY_LOCK_FIX = "order:identity:lock:";

    // ???
    //e.g. order:identity:times:20170608:userId
    public static final String BT_ORDER_IDENTITY_TIMES_FIX = "btOrder:identity:times:";

    //e.g. order:identity:times:userId
    public static final String BT_ORDER_IDENTITY_LOCK_FIX = "btOrder:identity:lock:";

    /**
     * ??
     */
    public static final String ORDER_LOAN_LOCK_FIX = "order:loan:lock:";

    /**
     * ??
     */
    public static final String ORDER_REFUND_LOCK_FIX = "order:refund:lock:";

    /**
     * ????
     */
    public static final String ORDER_REFUND_BF_LOCK_FIX = "order:refund:bf:lock:";

    /**
     * ????
     */
    public static final String ORDER_REFUND_UF_LOCK_FIX = "order:refund:uf:lock:";

    //sum????????
    public static final String ORDER_SUM_LOAN_AMOUNT = "order:sum:loan:amount";

    //sum??????????
    public static final String ORDER_SUM_OMNIACCOUNT_LOAN_TOTAL_AMOUNT = "order:sum:omniaccount:loan:total:amount:";
    //sum???????????
    public static final String ORDER_SUM_KOUDAI_LOAN_TOTAL_AMOUNT = "order:sum:koudai:loan:total:amount:";
    //sum?????????
    public static final String ORDER_SUM_WEIDAI_LOAN_TOTAL_AMOUNT = "order:sum:weidai:loan:total:amount:";

    /**
     * ???
     */
    public static final String ORDER_LOAN_LOCK_NEW = "order:loan:lock:new:";

    /**
     * ???????
     */
    public static final String ORDER_LOAN_REQUEST_AFTER_LOCK = "order:loan:request:after:lock:";

    /**
     * ?????
     */
    public static final String ORDER_PROXY_HOLD_REFUND_LOCK_NEW = "order:proxy:hold:refund:lock:new:";

    //????
    public static final String CACHE_INDEX_KEY = "cache:index:key";
    //???? ?? ??
    public static final String CACHE_INDEX_KEY_NEW = "cache:index:key:new";
    //????
    public static final String CACHE_HOME_KEY = "cache:home:key";
    //????ios
    public static final String CACHE_INDEX_KEY_IOS = "cache:index:key:ios";
    //?????????
    public static final String CACHE_INIT_PRODUCT_LIST_KEY = "cache:init_product_list:key";
    //?????
    public static final String CACHE_PROVINCE_LIST_KEY = "cache:province:list:key";
    //??h5 url ????
    public static final String CACHE_H5_URL_LIST_KEY = "cache:h5:url:list:key";
    // ?????????
    public static final String CACHE_BANK_CARD_LIST_KEY = "cache:bank:card:list:key";
    //????????
    public static final String CACHE_DIC_ITEM_LIST_KEY = "cache:dic:item:list:key";
    //???????
    public static final String CACHE_BANK_DEFINE_LIST_KEY = "cache:bank:define:list:key";
    //??ip??count
    public static final String CACHE_IP_ADDRESS_COUNT_KEY = "count:ip:address:count:key";
    //?????
    public static final String CACHE_ADVERTISING_PAGE_KEY = "cache:advertising:page:key";

    //??????
    public static final String MANAGE_SESSION_PREFIX = "manage:user:session";
    //????????
    public static final String MANAGE_LOAN_SEARCH_PASSWORD = "manage:loan:search:password";
    //??????????
    public static final int MANAGE_SESSION_EXPIRE = 3600 * 5;

    public static final String RANDOMIMAGE_SESSION_KEY = "randomImage:session:";

    //??app????????
    public static final String MANAGE_LOAN_DATA_STATISTICS = "manage:loan:data:statistics";

    //??app??????????
    public static final String MANAGE_TODAY_DATA_STATISTICS = "manage:today:data:statistics";

    //??app??????????
    public static final String MANAGE_YESTERDAY_DATA_STATISTICS = "manage:yesterday:data:statistics";

    //??????
    public static final String SENSITIVE_WORD_PREFIX = "sensitive:word:prefix:";

    //ocr????
    public static final String YITU_OCR_COUNT = "yitu:ocr:count";
    //??ocr????
    public static final String BT_YITU_OCR_COUNT = "btyitu:ocr:count";
    //??????
    public static final String YITU_YITU_COUNT = "yitu:yitu:count";
    //????????
    public static final String RISK_ORDER = "risk:order:fengkong";

    //??????
    public static final String ORDER_INITIAL_LOCK_FIX = "order:initial:lock:";

    /**
     * h5 ?? ??sesion
     */
    public static final String SESSION_H5_USER_ORDER_PREFIX = "session:h5:user:order:prefix:";

    //?? access token session
    public static final String WECHAT_ACCESS_TOKEN_SESSION = "wechat:access:token:session";
    /**
     * ???
     */
    public static final String LOTTERY_DRAW_LOCK = "lottery:draw:lock";
    /**
     * ????
     */
    public static final String LOTTERY_AWARD_LIST = "lottery:award:list";
    //rong360????????????????
    public static final String RONG_360_INFO = "rong:order:error";
    //rong360????????????
    public static final String RONG_360_INFO_COUNT_MAX = "rong:count:";

    /**
     * ?????
     */
    public static final String ORDER_REFUND_LOCK_FIXS = "order:refund:lock:fixs";

    /**
     * ????
     */
    public static final String SAVE_MANGO_ORDER_LIST = "save:mango:orderList";
    public static final String CERTIFICATION_COUNT_KEY = "certification:count:key";

    /**
     * ????
     */
    public static final String RISK_WORD_PREFIX = "risk:words:prefix:";


    /**
     * ????
     */
    public static final String USER_CERTIFICATION_LIST = "user:certification:list:";

    /**
     * ?? ??????gojek????
     */
    public static final String RISK_FENGKONG_GOJEK_AMOUNT = "risk:fengkong:gojek:amount";


    public static final String INIT_HOME_VIEW_RETURN = "init:home:view:return";

    // ??????  mom
    public static final String RISK_REFUSE_JUDGE_MOM = "risk:refuse:judge:mom";
    // ??????  son
    public static final String RISK_REFUSE_JUDGE_SON = "risk:refuse:judge:son";

    //获取h5 url 列表缓存
    public static final String CACHE_H5_CASE2_URL_LIST_KEY = "cache:h5:url:list:cash2key";

    //逾期15天通话记录手机号集合
    public static final String RISK_BLACKLIST_OVERDUE15_CALL_RECORD = "risk:blacklist:overdue15:call_record";
    //逾期15天短信手机号集合
    public static final String RISK_BLACKLIST_OVERDUE15_SHORT_MESSAGE = "risk:blacklist:overdue15:short_message";
    //逾期15天通讯录手机号集合
    public static final String RISK_BLACKLIST_OVERDUE15_CONTACT = "risk:blacklist:overdue15:contact";

    //逾期7天通话记录手机号集合[7,15)
    public static final String RISK_BLACKLIST_OVERDUE7_CALL_RECORD = "risk:blacklist:overdue7:call_record";
    //逾期7天短信手机号集合
    public static final String RISK_BLACKLIST_OVERDUE7_SHORT_MESSAGE = "risk:blacklist:overdue7:short_message";
    //逾期7天通讯录手机号集合
    public static final String RISK_BLACKLIST_OVERDUE7_CONTACT = "risk:blacklist:overdue7:contact";

    //欺诈用户通话记录手机号集合
    public static final String RISK_BLACKLIST_FRAUD_CALL_RECORD = "risk:blacklist:fraud:call_record";
    //欺诈用户短信手机号集合
    public static final String RISK_BLACKLIST_FRAUD_SHORT_MESSAGE = "risk:blacklist:fraud:short_message";
    //欺诈用户通讯录手机号集合
    public static final String RISK_BLACKLIST_FRAUD_CONTACT = "risk:blacklist:fraud:contact";

    //能够下载文件的固定IP
    public static final String CAN_DOWNFILE_IP = "manage:downfile:ips";


    public static final String ORDER_LOAN_ISSUED_SUCCESS_UPDATE="order:loan:issued:success:update"; //贷款放款成功后续处理

    public static final String ORDER_LOAN_PAYMENT_SUCCESS_UPDATE="order:loan:payment:success:update"; //贷款缓存陈宫后续处理


    //600评分模型相关开关
    //public static final String SCORE_MODEL_600_SWITCH_CALCULATE= "score:model:600:switch:calculate"; //是否跑600评分


    public static final String SCORE_MODEL_SWITCH_CALCULATE= "score:model:switch:calculate:"; //是否跑评分开关

    public static final String SCORE_MODEL_600_SWITCH_USE_SCORE = "score:model:600:switch:userScore"; //是否使用分数放过订单

    public static final String SCORE_MODEL_600_TEST_RATE = "score:model:600:test:rate";// 放过的测试占比

    public static final String SCORE_MODEL_600_TEST_MAX_COUNT = "score:model:600:test:maxCount";// 测试放过的单量


    public static final String SCORE_MODEL_SWITCH_USE_SCORE = "score:model:switch:userScore:"; //是否使用分数放过订单


    public static final String SCORE_MODEL_TEST_MAX_COUNT = "score:model:test:maxCount:";// 测试放过的单量

    public static final String SCORE_MODEL_TEST_RATE = "score:model:test:rate:";// 放过的测试占比

    public static final String SCORE_MODEL_LOAN_COUNT = "score:model:loan:count:";// 直接放款免核的数量

    public static final String THIRD_INFINITY_TOKEN = "third:infinity:token";// 调用Infinity 接口Token
    public static final String ORDER_LOAN_COVER_CHARGE_LOCK = "order:loan:charge:lock:";

    public  static final String DIGITAL_SIGN_SWITCH ="digital:sign:switch";//电子签约开关

    public  static final String INVOKE_ASLI_SWITCH ="invoke:ali:swtich";//invoke asli switch
    public  static final String INVOKE_DIGI_SIGN_SWITCH ="invoke:digiSign:switch";//invoke digiSign switch
    public  static final String RE_BORROWING_DIGI_SIGN_START_TIME ="reBorrowing:digi:sign:startTime";//reBorrowing:digi:sign:startTime

    public static final String DIGITAL_SIGN_STATUS_CHECK_INTERVAL ="digital:sign:status:checkInterval";//等待状态前端查询频率

    public static final String DIGITAL_SIGN_SELFIE_SCORE_LIMIT ="digital:sign:selfieScore:limit";//自拍照分数限制



    public static final String TWILIO_TEST_USE = "twilio:test:use"; //测试一天twilio本人外呼开关
    public static final String TWILIO_TEST_MOBILES = "twilio:test:mobile";//逗号分隔所有测试使用twilio的电话

    public static final String COUPON_D_15_SWITCH = "coupon_d_15_switch";//true 开 ， 自动发放优惠券开关（同时控制与对应的外呼通知）
    public static final String COUPON_D_10_SWITCH = "coupon_d_10_switch";//true 开 ， 自动发放优惠券开关（同时控制与对应的外呼通知）
    public static final String COUPON_D_5_SWITCH = "coupon_d_5_switch";//true 开 ， 自动发放优惠券开关（同时控制与对应的外呼通知）

    public static final String STARTUPDATECALLRESULT = "startUpdateCallResult"; //更新twilio锁


    public static final String REVIEW_SIGN_LOCK="review:sigin:lock";// 异步签约中的单不再走审核

    public static final String SECOND_CHECK_LOCK="second:check:lock:";// 异步签约中的单不再走审核


    public static final String REALNAME_VERIFY_PER_DAY_LIMIT = "REALNAME:VERiFY:PERDAY:LIMIT:";

    public static final String TWILIO_CALL_TOTAL_SWITCH = "twilio_call_total_switch";//true 开 ，
    public static final String INFOBIP_CALL_TOTAL_SWITCH = "infobip_call_total_switch";//true 开 ，



    public static final String INFORBIP_OLD_ENDPOINT = "INFORBIP_OLD_ENDPOINT"; //https://api.infobip.com

    public static final String ORDER_COLLECTION_CHECK_QUALITY = "order:collection:check:lock:";



}
