
start transaction;


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MALE_EDUCATION_DIFFMINS_OF_CREATE_SUBMIT_DIFFMINS_OF_STEP1TO2_DRIVERLICENSE_GOJEK','男性&学历&注册到提交时间&填写信息时长&未上传驾驶证&未验证gojek','60#10',2,1,15,2285,
       "V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
       'MALE_WHATSAPP_NOT_SAME_GOJEK_DRIVERLICENSE_PAYROLL','男性&whatsapp账号不是本人手机号&未验证gojek&未上传驾驶证&未上传工资单','',2,1,15,2290,
       "V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
       'MALE_GOJEK_AGE_BANKCODE','男性&未验证gojek&年龄&银行','30',2,1,15,2290,
       "V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',25,
       'FEMALE_GOJEK_AGE_COMP_TEL_BANK_CODE','女性&未验证gojek&年龄&公司外呼&银行&cashcash','30',2,1,15,2295,
       "V1",34,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',25,
       'MALE_GOJEK_AGE_COMP_TEL','男性&未验证gojek&年龄&公司外呼','30',2,1,15,2300,
       "V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MALE_EDUCATION_BORROWING_PURPOSE_DRIVERLICENSE_GOJEK','男性&学历&借款用途&未上传驾驶证&未验证gojek','',2,1,15,2305,
       "V1",3,0);



insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_100',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MALE_EDUCATION_DIFFMINS_OF_CREATE_SUBMIT_DIFFMINS_OF_STEP1TO2_DRIVERLICENSE_GOJEK',
    'MALE_WHATSAPP_NOT_SAME_GOJEK_DRIVERLICENSE_PAYROLL',
    'MALE_GOJEK_AGE_BANKCODE',
    'MALE_EDUCATION_BORROWING_PURPOSE_DRIVERLICENSE_GOJEK'
    );


insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'AUTO_CALL_FIRST_PRODUCT100',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'FEMALE_GOJEK_AGE_COMP_TEL_BANK_CODE',
    'MALE_GOJEK_AGE_COMP_TEL'
    );



insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
and s.ruleDetailType in (
   'MALE_EDUCATION_DIFFMINS_OF_CREATE_SUBMIT_DIFFMINS_OF_STEP1TO2_DRIVERLICENSE_GOJEK',
    'MALE_WHATSAPP_NOT_SAME_GOJEK_DRIVERLICENSE_PAYROLL',
    'MALE_GOJEK_AGE_BANKCODE',
    'FEMALE_GOJEK_AGE_COMP_TEL_BANK_CODE',
    'MALE_GOJEK_AGE_COMP_TEL',
    'MALE_EDUCATION_BORROWING_PURPOSE_DRIVERLICENSE_GOJEK'
);

commit;


