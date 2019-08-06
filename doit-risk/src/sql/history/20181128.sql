update sysAutoReviewRule s set s.ruleResult = 1 ,s.ruleStatus = 3,s.updateTime=now(),s.remark='监管需要放开' where s.ruleDetailType in (
    'CONTACT_SENSITIVI_COUNT',
    'PHONE_USED_DAY',
    'RECENT_30_CALL_TIME_MALE',
    'RECENT_30_CALL_COUNT',
    'RECENT_30_CALL_IN_TIME',
    'CONTACT_LAST_TIME',
    'RECENT_30_CALL_NO_CONNECT_RATE',
    'NIGHT_CALL_RATE',
    'RECENT_30_CALL_RATE',
    'USER_CALL_RECORDS_KEY_INfO_EMPTY',
    'SMS_ALL_COUNT',
    'SMS_THIRTY_COUNT',
    'SMS_REFUSE_COUNT',
    'SMS_OVERDUE_MORETHAN_15DAYS_COUNT',
    'SMS_OVERDUE_LESSTHAN_15DAYS_COUNT_MALE',
    'SMS_OVERDUE15_COUNT_MALE',
    'SMS_OVERDUE15_COUNT_FEMALE',
    'SMS_PHONES_IN_OVERDUE15_BLACK_LIST_USER',
    'COMB_SEX_RECENT30_MISSEDCALLRATE',
    'COMB_SEX_RECENT30_MISSEDCALLINDATE',
    'COMB_MOBILEUSAGETIME_MALE_MARRIAGE',
    'COMB_FIRST_CONTACT_AND_SECOND_CONTACT_NOT_IN_CONTACTS',
    'COMB_RECENT30CALLIN_MOBILEUSEDTIMES_FEMALE',
    'COMB_DIFFDAYSOFEARLIESTMSG_MALE',
    'COMB_NOCALLOFFIRSTSECONDLINKMAN_MALE_MARITAL',
    'COMB_MOBILE_CAP_FIRSTLINKEMANRECENT180CALLCOUNT_MALE',
    'RECENT30_EVENING_CALL_RATE_MALE',
    'DISTINCT_CONTACT_PHONE_MALE',
    'DISTINCT_CONTACT_PHONE_FEMALE',
    'RECENT90_DISTINCT_CALL_IN_NUMBERS_MALE',
    'FACEBOOK_AVERAGE_MONTH_COMMENT_FEMALE',
    'SMS_OVERDUE_LESSTHAN_15DAYS_COUNT_FEMALE',
    'NIGHT_CALL_RATE_100RMB',
    'RECENT30_EVENING_CALL_RATE_MALE_100RMB',
    'CALL_RECORD_PHONE_IN_OVERDUE15_BLACKLIST',
    'CALL_RECORD_PHONE_IN_OVERDUE7_BLACKLIST',
    'COMB_REBORROW_OVERDUEDAYS_EVENING_RATIO'
    ) and disabled = 0;





