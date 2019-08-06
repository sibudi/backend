start transaction;

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'FEMALE_EDUCATION_CREDIT_CARD_COMPANY_TEL_IZI_PHONEVERIFY','女性&学历&信用卡类APP个数&公司外呼&IZI实名匹配','0',1,3,15,2165,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'MALE_EDUCATION_CREDIT_CARD_COMPANY_TEL_AGE','男性&学历&信用卡类APP个数&公司外呼&年龄','2#30',1,3,15,2170,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MALE_CREDIT_CARD_MONTHLY_SALARY_COMPANY_TEL','男性&信用卡类APP个数&月收入小于2000RMB&公司外呼无效或可能有效','0#4000000',2,1,15,2175,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MALE_CREDIT_CARD_MONTHLY_SALARY_COMPANY_TEL_IZI_PHONEVERIFY','男性&信用卡类APP个数&月收入小于2000RMB&公司外呼有效&IZI手机实名认证','0#4000000',2,1,15,2180,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MALE_CREDIT_CARD_MONTHLY_SALARY_IZI_PHONEVERIFY','男性&信用卡类APP个数&月收入2000-4000RMB&IZI实名认证NOT_FOUND','0#4000000#8000000',2,1,15,2185,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MALE_CREDIT_CARD_MONTHLY_SALARY_COMPANY_TEL_IZI_PHONEVERIFY_01','男性&信用卡类APP个数&月收入2000-4000RMB&公司外呼&IZI手机实名NOT_MATCH','0#4000000#8000000',2,1,15,2190,"V1",3,0);

-- flow
-- 免核

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'NON_MANUAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'FEMALE_EDUCATION_CREDIT_CARD_COMPANY_TEL_IZI_PHONEVERIFY',
    'MALE_EDUCATION_CREDIT_CARD_COMPANY_TEL_AGE'
    );

-- product_600

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MALE_CREDIT_CARD_MONTHLY_SALARY_COMPANY_TEL',
    'MALE_CREDIT_CARD_MONTHLY_SALARY_COMPANY_TEL_IZI_PHONEVERIFY',
    'MALE_CREDIT_CARD_MONTHLY_SALARY_IZI_PHONEVERIFY',
    'MALE_CREDIT_CARD_MONTHLY_SALARY_COMPANY_TEL_IZI_PHONEVERIFY_01'
    );

-- param

insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
and s.ruleDetailType in (
     'FEMALE_EDUCATION_CREDIT_CARD_COMPANY_TEL_IZI_PHONEVERIFY',
     'MALE_EDUCATION_CREDIT_CARD_COMPANY_TEL_AGE',
     'MALE_CREDIT_CARD_MONTHLY_SALARY_COMPANY_TEL',
     'MALE_CREDIT_CARD_MONTHLY_SALARY_COMPANY_TEL_IZI_PHONEVERIFY',
     'MALE_CREDIT_CARD_MONTHLY_SALARY_IZI_PHONEVERIFY',
     'MALE_CREDIT_CARD_MONTHLY_SALARY_COMPANY_TEL_IZI_PHONEVERIFY_01'
);