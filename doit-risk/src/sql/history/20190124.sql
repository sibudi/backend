start transaction;

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
       'REBORROWING_IZI_PHONEVERIFY_AGE_MALE','IZI手机实名认证&年龄&性别-复借','28',1,3,15,2120,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
       'REBORROWING_FIRST_LOAN_COMPANY_CALL_TOTAL_MEMORY','公司外呼&总内存-复借','1.8',1,3,15,2125,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
       'REBORROWING_IZI_PHONEAGE_PHONEVERIFY_MALE','IZI手机在网时长&IZI手机实名认证_NOT_MATCH&性别-复借','',1,3,15,2130,"V1",3,0);



insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'AUTO_CALL_RE_BORROWING',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'REBORROWING_FIRST_LOAN_COMPANY_CALL_TOTAL_MEMORY'
    );



insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
and s.ruleDetailType in (
    'REBORROWING_FIRST_LOAN_COMPANY_CALL_TOTAL_MEMORY'
);


start transaction;

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'IZI_PHONEVERIFY_LANGUAGE_AGE_MALE','IZI手机实名认证&男性&语言&年龄','in#30',2,1,15,2135,"V1",3,0);


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'EDUCATION_APPCOUNT_FOR_ECOMMERCE_LINKMAN_CALL_RESULT_FEMALE','女性&学历&电商类APP个数&联系人外呼完全有效占比','4#4',1,3,15,2140,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'EDUCATION_APPCOUNT_FOR_ECOMMERCE_COMPANY_CALL_RESULT_MALE','男性&学历&电商类APP个数&公司外呼','4',1,3,15,2140,"V1",3,0);


insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'IZI_PHONEVERIFY_LANGUAGE_AGE_MALE'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_100',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'IZI_PHONEVERIFY_LANGUAGE_AGE_MALE'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'AUTO_CALL_FIRST_NON_MANUAL',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'EDUCATION_APPCOUNT_FOR_ECOMMERCE_LINKMAN_CALL_RESULT_FEMALE',
    'EDUCATION_APPCOUNT_FOR_ECOMMERCE_COMPANY_CALL_RESULT_MALE'
    );

insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
and s.ruleDetailType in (
    'IZI_PHONEVERIFY_LANGUAGE_AGE_MALE',
    'EDUCATION_APPCOUNT_FOR_ECOMMERCE_LINKMAN_CALL_RESULT_FEMALE',
    'EDUCATION_APPCOUNT_FOR_ECOMMERCE_COMPANY_CALL_RESULT_MALE'
);

update ruleParam set thresholdValue = 'in#25' where flowName = 'PRODUCT_100' and ruleDetailType = 'IZI_PHONEVERIFY_LANGUAGE_AGE_MALE';



