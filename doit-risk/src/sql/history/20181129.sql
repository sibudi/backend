insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'TOTAL_MEMORY_APPLY_TIME_HOUR','手机总内存&提交时间','2#6',2,1,15,1910,"V1",3);


--- 适用于cashcash的规则【doit放开了，但是cashcash仍然执行】
-- android 的
start transaction ;
update sysAutoReviewRule s set s.ruleResult=2,s.ruleStatus=1 ,s.appliedTo=14 where s.appliedTo=1 and s.ruleDetailType in (
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
    'COMB_RECENT30CALLIN_MOBILEUSEDTIMES_FEMALE',
    'COMB_DIFFDAYSOFEARLIESTMSG_MALE',
    'COMB_NOCALLOFFIRSTSECONDLINKMAN_MALE_MARITAL',
    'COMB_MOBILE_CAP_FIRSTLINKEMANRECENT180CALLCOUNT_MALE',
    'RECENT30_EVENING_CALL_RATE_MALE',
    'RECENT90_DISTINCT_CALL_IN_NUMBERS_MALE',
    'FACEBOOK_AVERAGE_MONTH_COMMENT_FEMALE',
    'SMS_OVERDUE_LESSTHAN_15DAYS_COUNT_FEMALE',
    'NIGHT_CALL_RATE_100RMB',
    'RECENT30_EVENING_CALL_RATE_MALE_100RMB',
    'CALL_RECORD_PHONE_IN_OVERDUE15_BLACKLIST',
    'CALL_RECORD_PHONE_IN_OVERDUE7_BLACKLIST',
    'COMB_REBORROW_OVERDUEDAYS_EVENING_RATIO',
    'SHORT_MESSAGE_KEY_INfO_EMPTY'
    );


-- ios 的
update sysAutoReviewRule s set  s.ruleResult=2,s.ruleStatus=1 ,s.appliedTo=24 where s.appliedTo=2 and s.ruleDetailType in (
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
    'COMB_RECENT30CALLIN_MOBILEUSEDTIMES_FEMALE',
    'COMB_DIFFDAYSOFEARLIESTMSG_MALE',
    'COMB_NOCALLOFFIRSTSECONDLINKMAN_MALE_MARITAL',
    'COMB_MOBILE_CAP_FIRSTLINKEMANRECENT180CALLCOUNT_MALE',
    'RECENT30_EVENING_CALL_RATE_MALE',
    'RECENT90_DISTINCT_CALL_IN_NUMBERS_MALE',
    'FACEBOOK_AVERAGE_MONTH_COMMENT_FEMALE',
    'SMS_OVERDUE_LESSTHAN_15DAYS_COUNT_FEMALE',
    'NIGHT_CALL_RATE_100RMB',
    'RECENT30_EVENING_CALL_RATE_MALE_100RMB',
    'CALL_RECORD_PHONE_IN_OVERDUE15_BLACKLIST',
    'CALL_RECORD_PHONE_IN_OVERDUE7_BLACKLIST',
    'COMB_REBORROW_OVERDUEDAYS_EVENING_RATIO',
    'SHORT_MESSAGE_KEY_INfO_EMPTY'
    );

update sysAutoReviewRule s set  s.ruleResult=2,s.ruleStatus=1 ,s.appliedTo=34 where s.appliedTo=3 and s.ruleDetailType in (
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
    'COMB_RECENT30CALLIN_MOBILEUSEDTIMES_FEMALE',
    'COMB_DIFFDAYSOFEARLIESTMSG_MALE',
    'COMB_NOCALLOFFIRSTSECONDLINKMAN_MALE_MARITAL',
    'COMB_MOBILE_CAP_FIRSTLINKEMANRECENT180CALLCOUNT_MALE',
    'RECENT30_EVENING_CALL_RATE_MALE',
    'RECENT90_DISTINCT_CALL_IN_NUMBERS_MALE',
    'FACEBOOK_AVERAGE_MONTH_COMMENT_FEMALE',
    'SMS_OVERDUE_LESSTHAN_15DAYS_COUNT_FEMALE',
    'NIGHT_CALL_RATE_100RMB',
    'RECENT30_EVENING_CALL_RATE_MALE_100RMB',
    'CALL_RECORD_PHONE_IN_OVERDUE15_BLACKLIST',
    'CALL_RECORD_PHONE_IN_OVERDUE7_BLACKLIST',
    'COMB_REBORROW_OVERDUEDAYS_EVENING_RATIO',
    'SHORT_MESSAGE_KEY_INfO_EMPTY'
    );


commit;

-- 漏放开规则
select *from sysAutoReviewRule s where s.ruleDetailType in ('AUTO_CALL_REJECT_BACKUP_LINKMAN_VALID_COUNT',
                                                            'MULTI_AUTO_CALL_REJECT_BACKUP_LINKMAN_VALID_COUNT',
    'AUTO_CALL_REJECT_BACKUP_LINKMAN_VALID_COUNT_100RMB_PRODUCT',
    'COMB_FIRST_CONTACT_AND_SECOND_CONTACT_NOT_IN_CONTACTS',
    'CONTACT_PHONE_IN_OVERDUE15_BLACKLIST');



insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'女性&age<30&孩子数量>1',15,
  'FEMALE_AGE_CHILDREN_COUNT','女性&年龄&孩子数量','30#1',2,1,15,1915,"V1",3);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'男性&age<30&孩子数量>0&本科以下',15,
  'MALE_AGE_CHILDREN_COUNT_EDUCATION','女性&年龄&孩子数量','30#0',2,1,15,1920,"V1",3);



insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'[职业-安保人员/邮差/销售人员/厨师 ]',22,
  'NO_TAX_NUMBER_NO_PAYROLL_POST_MALE','无税卡&未上传工资单&特定职业&男性','Keamanan#Kurir#Staf Penjualan#Koki',2,1,15,1925,"V1",3);
