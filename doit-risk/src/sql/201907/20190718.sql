start transaction;


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
       'LAST_LOAN_AMOUNT_CURRENT_AMOUNT_MALE_OVERDUEDAYS_HAS_PASSPORT','男&当天还款&有护照',
       '300000#160000#2#0',2,1,15,3130,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
       'LAST_LOAN_AMOUNT_CURRENT_AMOUNT_MALE_OVERDUEDAYS_NO_PASSPORT_AGE','男&当天还款&没有护照&25<=年龄<30',
       '300000#160000#2#0#25#30',2,1,15,3130,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
       'LAST_LOAN_AMOUNT_CURRENT_AMOUNT_FEMALE_OVERDUEDAYS_AGE','女&当天还款&年龄<25或>=45',
       '300000#160000#2#0#25#45',2,1,15,3130,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
       'LAST_LOAN_AMOUNT_CURRENT_AMOUNT_FEMALE_OVERDUEDAYS_AGE_MONTHLY_INCOME','女&当天还款&25<=年龄<45&月收入<2000',
       '300000#160000#2#0#25#45#4000000',2,1,15,3130,"V1", 3,0);



insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'RE_BORROWING_UNIVERSAL',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled=0 and s.ruleDetailType in (
   'LAST_LOAN_AMOUNT_CURRENT_AMOUNT_MALE_OVERDUEDAYS_HAS_PASSPORT',
   'LAST_LOAN_AMOUNT_CURRENT_AMOUNT_MALE_OVERDUEDAYS_NO_PASSPORT_AGE',
   'LAST_LOAN_AMOUNT_CURRENT_AMOUNT_FEMALE_OVERDUEDAYS_AGE',
   'LAST_LOAN_AMOUNT_CURRENT_AMOUNT_FEMALE_OVERDUEDAYS_AGE_MONTHLY_INCOME'
);



insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled=0 and s.ruleDetailType in (
   'LAST_LOAN_AMOUNT_CURRENT_AMOUNT_MALE_OVERDUEDAYS_HAS_PASSPORT',
   'LAST_LOAN_AMOUNT_CURRENT_AMOUNT_MALE_OVERDUEDAYS_NO_PASSPORT_AGE',
   'LAST_LOAN_AMOUNT_CURRENT_AMOUNT_FEMALE_OVERDUEDAYS_AGE',
   'LAST_LOAN_AMOUNT_CURRENT_AMOUNT_FEMALE_OVERDUEDAYS_AGE_MONTHLY_INCOME'
);