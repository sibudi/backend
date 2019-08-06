
start transaction;
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
       'MALE_MARRIAGE_EDUCATION_DRIVER_LICENSE','男性&单身&学历&年龄&未上传驾驶证','35',2,1,15,2260,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
       'MALE_WHATSAPP_NOT_SAME_TAX_NUMBER_AGE_MONTHLY_INCOME','男性&WhatsApp账号不一致&提交税卡&年龄&月收入','30#4000000',2,1,15,2265,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
       'MALE_WHATSAPP_NOT_SAME_TAX_NUMBER','男性&WhatsApp账号不一致&未提交税卡','',2,1,15,2270,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
       'MALE_WHATSAPP_SAME_TAX_NUMBER_IZI_PHONEVERIFY_MONNTHLY_INCOME_MARRIAGE_AGE','男性&WhatsApp账号一致&未提交税卡&IZI手机实名NOTMATCH&月收入&单身&年龄','4000000#30',2,
       1,15,2255,"V1",34,0);


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
       'WHATSAPP_NOT_SAME_TAX_NUMBER_IZI_PHONEVERIFY_AGE','WhatsApp账号一致&未提交税卡&IZI手机实名NOTFOUND&年龄','30',2,1,15,2275,"V1",3,0);



insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_50',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MALE_MARRIAGE_EDUCATION_DRIVER_LICENSE',
    'MALE_WHATSAPP_NOT_SAME_TAX_NUMBER_AGE_MONTHLY_INCOME',
    'MALE_WHATSAPP_NOT_SAME_TAX_NUMBER',
    'MALE_WHATSAPP_SAME_TAX_NUMBER_IZI_PHONEVERIFY_MONNTHLY_INCOME_MARRIAGE_AGE',
    'WHATSAPP_NOT_SAME_TAX_NUMBER_IZI_PHONEVERIFY_AGE'
    );


insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
and s.ruleDetailType in (
    'MALE_MARRIAGE_EDUCATION_DRIVER_LICENSE',
    'MALE_WHATSAPP_NOT_SAME_TAX_NUMBER_AGE_MONTHLY_INCOME',
    'MALE_WHATSAPP_NOT_SAME_TAX_NUMBER',
    'MALE_WHATSAPP_SAME_TAX_NUMBER_IZI_PHONEVERIFY_MONNTHLY_INCOME_MARRIAGE_AGE',
    'WHATSAPP_NOT_SAME_TAX_NUMBER_IZI_PHONEVERIFY_AGE'
);

commit;