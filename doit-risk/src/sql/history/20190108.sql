




insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MOBILE_LANGUAGE_MALE_APPLY_TIME_HOUR','手机语言&性别&提交时间','in#6',2,1,15,2040,"V1",35,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MOBILE_LANGUAGE_MALE_APPLY_TIME_HOUR_100RMB','手机语言&性别&提交时间_100RMB','in#4',2,1,15,2045,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'IZI_PHONEAGE_PHONEVERIFY_EDUCATION_MONTHLY_INCOME_FEMALE','IZI在网时长&IZI手机实名&学历&月收入&女性','6#4000000',1,3,15,2050,"V1",34,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'IZI_PHONEAGE_PHONEVERIFY_MEMORY_MALE','IZI在网时长&IZI手机实名认证&手机总内存&男性','6#3',1,3,15,2055,"V1",34,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'IZI_PHONEAGE_PHONEVERIFY_MOBILE_LANGUAGE_COMPANY_CALL','IZI在网时长&IZI手机实名认证&语言&公司外呼','in',2,1,15,2060,"V1",34,0);


-- 流程
insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MOBILE_LANGUAGE_MALE_APPLY_TIME_HOUR'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_100',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MOBILE_LANGUAGE_MALE_APPLY_TIME_HOUR_100RMB'
    );


insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'NON_MANUAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'IZI_PHONEAGE_PHONEVERIFY_EDUCATION_MONTHLY_INCOME_FEMALE',
    'IZI_PHONEAGE_PHONEVERIFY_MEMORY_MALE'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'AUTO_CALL_FIRST_BORROWING',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'IZI_PHONEAGE_PHONEVERIFY_MOBILE_LANGUAGE_COMPANY_CALL'
    );


-- 参数

insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
and s.ruleDetailType in (
'MOBILE_LANGUAGE_MALE_APPLY_TIME_HOUR',
'MOBILE_LANGUAGE_MALE_APPLY_TIME_HOUR_100RMB',
'IZI_PHONEAGE_PHONEVERIFY_EDUCATION_MONTHLY_INCOME_FEMALE',
'IZI_PHONEAGE_PHONEVERIFY_MEMORY_MALE',
'IZI_PHONEAGE_PHONEVERIFY_MOBILE_LANGUAGE_COMPANY_CALL'
);



--  数据异常修复
start transaction ;
update flowRuleSet f set f.flowName = 'NON_MANUAL_RULE01' where f.flowName= 'LABELING_RULE';

update flowRuleSet f set f.flowName = 'LABELING_RULE' where f.flowName= 'NON_MANUAL_RULE'
and f.ruleDetailType not in (
    'IZI_PHONEAGE_PHONEVERIFY_EDUCATION_MONTHLY_INCOME_FEMALE',
    'IZI_PHONEAGE_PHONEVERIFY_MEMORY_MALE'
    );

update flowRuleSet f set f.flowName = 'NON_MANUAL_RULE' where f.flowName= 'NON_MANUAL_RULE01';



start transaction ;

update flowRuleSet s set s.flowName = 'LABELING_RULE',s.remark='从免核规则排除相关规则190109' where s.ruleDetailType in (
    'COMB_2_FEMALE_FREE_PHONE_CHECK',
    'COMB_RECENT30_MISCALL_FEMALE_MARRIAGE_EDUCATION',
    'COMB_FIRST_CONTACT_RECENT90CALL_FEMALE',
    'COMB_DIFFDAYSOFEARLIESTMSG_RECENT90OVEDUESMSG_FEMALE',
    'COMB_RECENT30CALLINRATIO_DIFFOFEARLIESTMSG_NOCALLDAYS_FEMALE',
    'COMB_RECENT90CALLINTIMES_DIFFOFEARLIESTMSG_EDUCATION_FEMALE',
    'COMB_MOBILEUSAGETIMES_DIFFOFEARLIESTMSG_CHILDRENCOUNT_FEMALE',
    'COMB_RECENT90CALLINTIMES_NOCALLDAYS_EDUCATION_MALE',
    'FACEBOOK_COMPANY_CONTAIN',
    'COMB_TAX_NUMBER_RECENT30INRATION_APPCOUNT_MALE',
    'COMB_TAX_NUMBER_RECENT30INRATION_APPCOUNT_FEMALE',
    'CAP_MONTHLY_INCOME_GOJEK_MOBILE_NOT_SAME'
    );



update ruleParam s set s.flowName = 'LABELING_RULE' where s.ruleDetailType in (
    'COMB_2_FEMALE_FREE_PHONE_CHECK',
    'COMB_RECENT30_MISCALL_FEMALE_MARRIAGE_EDUCATION',
    'COMB_FIRST_CONTACT_RECENT90CALL_FEMALE',
    'COMB_DIFFDAYSOFEARLIESTMSG_RECENT90OVEDUESMSG_FEMALE',
    'COMB_RECENT30CALLINRATIO_DIFFOFEARLIESTMSG_NOCALLDAYS_FEMALE',
    'COMB_RECENT90CALLINTIMES_DIFFOFEARLIESTMSG_EDUCATION_FEMALE',
    'COMB_MOBILEUSAGETIMES_DIFFOFEARLIESTMSG_CHILDRENCOUNT_FEMALE',
    'COMB_RECENT90CALLINTIMES_NOCALLDAYS_EDUCATION_MALE',
    'FACEBOOK_COMPANY_CONTAIN',
    'COMB_TAX_NUMBER_RECENT30INRATION_APPCOUNT_MALE',
    'COMB_TAX_NUMBER_RECENT30INRATION_APPCOUNT_FEMALE',
    'CAP_MONTHLY_INCOME_GOJEK_MOBILE_NOT_SAME'
    );


update sysAutoReviewRule s set s.ruleType = '15' where s.ruleDetailType in (
    'COMB_2_FEMALE_FREE_PHONE_CHECK',
    'COMB_RECENT30_MISCALL_FEMALE_MARRIAGE_EDUCATION',
    'COMB_FIRST_CONTACT_RECENT90CALL_FEMALE',
    'COMB_DIFFDAYSOFEARLIESTMSG_RECENT90OVEDUESMSG_FEMALE',
    'COMB_RECENT30CALLINRATIO_DIFFOFEARLIESTMSG_NOCALLDAYS_FEMALE',
    'COMB_RECENT90CALLINTIMES_DIFFOFEARLIESTMSG_EDUCATION_FEMALE',
    'COMB_MOBILEUSAGETIMES_DIFFOFEARLIESTMSG_CHILDRENCOUNT_FEMALE',
    'COMB_RECENT90CALLINTIMES_NOCALLDAYS_EDUCATION_MALE',
    'FACEBOOK_COMPANY_CONTAIN',
    'COMB_TAX_NUMBER_RECENT30INRATION_APPCOUNT_MALE',
    'COMB_TAX_NUMBER_RECENT30INRATION_APPCOUNT_FEMALE',
    'CAP_MONTHLY_INCOME_GOJEK_MOBILE_NOT_SAME'
    );


commit;