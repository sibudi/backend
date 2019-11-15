package com.yqg.service.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class RuleConstants {
    public static final String REAL_NAME_TAX_NUMBER_FRAUD_USER_DESC = "姓名税卡相同命中欺诈黑名单";

    //仅仅实名失败并且命中了可以忽略实名失败的规则，对应ordBlack remark
    public static final String ONLY_REAL_NAME_VERIFY_FAILED_AND_HIT_SPECIAL_RULE_REMAK = "ONLY_REAL_NAME_VERIFY_FAILED:";

    //进行ABTest放款的单
    public static final String AB_TEST_RULES = "AB_TEST_RULES:";

    //100RMB产品如果符合3条免核规则，则任然保留测试原申请金额进行测试
    public static final String NON_MANUAL_TEST_WITH_100RMB_PRODUCT = "NON_MANUAL_TEST_WITH_100RMB_PRODUCT";


    public static final String NEED_RE_FILL_LINKMAN = "NEED_RE_FILL_LINKMAN";


    public static final int ORDER_OWNER_CALL_LIMIT = 10;
    public static final int MAX_SEND_NUMBERS_PER_TIME = 10;
    public static final int REJECT_CALL_TIMES = 3;

//    public static final String PRODUCT600TO50_FRAUD = "PRODUCT600TO50_FRAUD"; //身份证号NOT_FOUND 转50
//    public static final String PRODUCT600TO100 = "PRODUCT600TO100";  //600 产品转100
//    public static final String PRODUCT600TO100_SENIOR_REVIEW = "PRODUCT600TO100_SENIOR_REVIEW"; //复审600转100
//    public static final String PRODUCT100TO50 = "PRODUCT100TO50"; //100转50

    public static final String PRODUCT600TO50_FRAUD = "PRODUCT600TO50_FRAUD"; //身份证号NOT_FOUND 转50
    public static final String PRODUCT600TO150 = "PRODUCT600TO150";  //600 产品转100
    public static final String PRODUCT600TO150_SENIOR_REVIEW = "PRODUCT600TO150_SENIOR_REVIEW"; //复审600转100
    public static final String PRODUCT150TO80 = "PRODUCT150TO80"; //100转50


    public static final String RE_BORROWING_PRD600TO400 = "RE_BORROWING_PRD600TO400";//复借600转400
    public static final String RE_BORROWING_PRD400TO200 = "RE_BORROWING_PRD400TO200";//复借400转200

    public static final String PRODUCT50TO80_FOR_EXTEND_RULE = "PRODUCT50TO80_FOR_EXTEND_RULE"; //100转50特殊规则未命中

    public static final String PRODUCT600TO50 = "PRODUCT600TO50";

    public static final String PRODUCT600WITHRISKSCORE = "PRODUCT600_WITH_RISK_SCORE";
    public static final String PRODUCT100WITHRISKSCORE = "PRODUCT100_WITH_RISK_SCORE";
    public static final String PRODUCT600_V2WITHRISKSCORE = "PRODUCT600_V2_WITH_RISK_SCORE";

    public static final String MODEL_RISK_SCORE_PASS_REMARK = "PASS_WITH_RISK_SCORE:";

    public static final BigDecimal PRODUCT100 = new BigDecimal("300000.00");
    public static final BigDecimal PRODUCT600 = new BigDecimal("1200000.00");
    public static final BigDecimal PRODUCT50 = new BigDecimal("160000.00");
    public static final BigDecimal PRODUCT100_V0 = new BigDecimal("200000.00");
    public static final BigDecimal PRODUCT50_V0 = new BigDecimal("100000.00");
    public static final BigDecimal PRODUCT400 = new BigDecimal("800000.00");
    public static final BigDecimal PRODUCT200 = new BigDecimal("400000.00");

    public static final String PERSON_NOT_FOUND = "实名认证失败：PERSON_NOT_FOUND";

    public static final String AUTO_CALL_RERUN = "auto resend perday";


    //public static final BigDecimal PRODUCT_600_MODEL_BASE_SCORE = new BigDecimal("479.290");
    public static final BigDecimal PRODUCT_600_MODEL_THRESHOLD_SCORE = new BigDecimal("490.000");


    public static final String REAL_NAME_COMB_VERIFY_ABTEST_FEMALE = "REAL_NAME_COMB_VERIFY_ABTEST_FEMALE";
    public static final String REAL_NAME_COMB_VERIFY_ABTEST_MALE = "REAL_NAME_COMB_VERIFY_ABTEST_MALE";


    public static final String REJECT_REASON_NOT_NON_MANUAL = "REJECT_REASON_NOT_NON_MANUAL";


    public static Map<String, String> ordBlackRemarks = new HashMap<>();

    static {
        ordBlackRemarks.put("PRODUCT_600", PRODUCT600WITHRISKSCORE);
        ordBlackRemarks.put("PRODUCT_100", PRODUCT100WITHRISKSCORE);
        ordBlackRemarks.put("PRODUCT_600_V2", PRODUCT600_V2WITHRISKSCORE);
    }
}
