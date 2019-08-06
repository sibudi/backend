start transaction;
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
       'MALE_IZI_PHONEVERIFY_PHONEAGE','男性&IZI手机实名&IZI在网时长','6',2,1,15,2225,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
       'MALE_IZI_PHONEVERIFY_PHONEAGE_AGE','男性&IZI手机实名&IZI在网时长&年龄','6#25',2,1,15,2230,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
       'MALE_MONTHLY_INCOME_AGE_EDUCATION_HAS_CREDIT_CARD','男性&月收入&年龄&学历&未上传驾驶证&未上传信用卡','8000000#30',2,1,15,2235,"V1",3,0);



insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_100',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MALE_IZI_PHONEVERIFY_PHONEAGE',
    'MALE_IZI_PHONEVERIFY_PHONEAGE_AGE',
    'MALE_MONTHLY_INCOME_AGE_EDUCATION_HAS_CREDIT_CARD'
    );


insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
and s.ruleDetailType in (
     'MALE_IZI_PHONEVERIFY_PHONEAGE',
    'MALE_IZI_PHONEVERIFY_PHONEAGE_AGE',
    'MALE_MONTHLY_INCOME_AGE_EDUCATION_HAS_CREDIT_CARD'
);

commit;







start transaction;
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
       'NO_GOJEK_IZI_PHONEAGE_AGE_MALE','未验证gojek&IZI在网时长&年龄&男性','6#30',2,1,15,2240,"V1",35,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
       'NO_GOJEK_IZI_PHONEAGE_FEMALE_WHATSAPP_MOBILE_NOT_SAME','未验证gojek&IZI在网时长&whatsapp账号不是本人手机号&女性','6',2,1,15,2245,"V1",35,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
       'NO_GOJEK_IZI_PHONEAGE_MALE_EDUCATION','未验证gojek&IZI在网时长&男性&学历','6',2,1,15,2250,"V1",34,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
       'NO_GOJEK_IZI_PHONEAGE_FEMALE_EDUCATION','未验证gojek&IZI在网时长&女性&学历','',2,1,15,2255,"V1",34,0);



insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'NO_GOJEK_IZI_PHONEAGE_AGE_MALE',
    'NO_GOJEK_IZI_PHONEAGE_FEMALE_WHATSAPP_MOBILE_NOT_SAME',
    'NO_GOJEK_IZI_PHONEAGE_MALE_EDUCATION',
    'NO_GOJEK_IZI_PHONEAGE_FEMALE_EDUCATION'
    );


insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
and s.ruleDetailType in (
    'NO_GOJEK_IZI_PHONEAGE_AGE_MALE',
    'NO_GOJEK_IZI_PHONEAGE_FEMALE_WHATSAPP_MOBILE_NOT_SAME',
    'NO_GOJEK_IZI_PHONEAGE_MALE_EDUCATION',
    'NO_GOJEK_IZI_PHONEAGE_FEMALE_EDUCATION'
);

commit;