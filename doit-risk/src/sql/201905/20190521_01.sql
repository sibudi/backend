start transaction;

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'MOBILE_IS_EMERGENCYTEL_FOR_OVERDUELESSTHAN5_USERS_IZI_SEX','用户是某个首借600已还款用户的联系人& IZI手机实名MATCH & 性别','',1,3,15,2380,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'IZI_PHONEAGE_PHONEVERIFY_APPCOUNT_LANGUAGE_100','IZI在网时长&IZI手机实名认证&首借累计APP个数&手机语言','40#in',1,3,15,1970,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'IZI_PHONEVERIFY_LANGUAGE_AGE_MALE_100','IZI手机实名认证&男性&语言&年龄','in#25',2,1,15,2135,"V1",3,0);



insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'NON_MANUAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MOBILE_IS_EMERGENCYTEL_FOR_OVERDUELESSTHAN5_USERS_IZI_SEX'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_100',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'IZI_PHONEAGE_PHONEVERIFY_APPCOUNT_LANGUAGE_100'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_100',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'IZI_PHONEVERIFY_LANGUAGE_AGE_MALE_100'
    );


insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
and s.ruleDetailType in (
    'MOBILE_IS_EMERGENCYTEL_FOR_OVERDUELESSTHAN5_USERS_IZI_SEX',
    'IZI_PHONEAGE_PHONEVERIFY_APPCOUNT_LANGUAGE_100',
    'IZI_PHONEVERIFY_LANGUAGE_AGE_MALE_100'
);


update sysAutoReviewRule set disabled=1 ,updateTime = now() where ruleDetailType='MOBILE_LANGUAGE_MALE_APPLY_TIME_HOUR_100RMB';
update flowRuleSet set disabled=1 ,updateTime = now() where ruleDetailType='MOBILE_LANGUAGE_MALE_APPLY_TIME_HOUR_100RMB';
update ruleParam set disabled=1 ,updateTime = now() where ruleDetailType='MOBILE_LANGUAGE_MALE_APPLY_TIME_HOUR_100RMB';

update sysAutoReviewRule set ruleValue = '435' where ruleDetailType = 'RISK_SCORE_600_FOR_PRD100';
update ruleParam set thresholdValue='435' where ruleDetailType = 'RISK_SCORE_600_FOR_PRD100';

update sysAutoReviewRule set ruleValue = '675' where ruleDetailType = 'RISK_SCORE_100_FOR_PRD100_FEMALE';
update ruleParam set thresholdValue='675' where ruleDetailType = 'RISK_SCORE_100_FOR_PRD100_FEMALE';


update sysAutoReviewRule set disabled=1 ,updateTime = now() where ruleDetailType='MALE_MONTHLY_INCOME_AGE_EDUCATION_HAS_CREDIT_CARD';
update flowRuleSet set disabled=1 ,updateTime = now() where ruleDetailType='MALE_MONTHLY_INCOME_AGE_EDUCATION_HAS_CREDIT_CARD';
update ruleParam set disabled=1 ,updateTime = now() where ruleDetailType='MALE_MONTHLY_INCOME_AGE_EDUCATION_HAS_CREDIT_CARD';

update sysAutoReviewRule set ruleDetailType='WHATSAPP_NOT_SAME_GOJEK_DRIVERLICENSE_PAYROLL' ,
ruleDesc='whatsapp账号不是本人手机号&未验证gojek&未上传驾驶证&未上传工资单', updateTime = now() where
ruleDetailType='MALE_WHATSAPP_NOT_SAME_GOJEK_DRIVERLICENSE_PAYROLL';

update flowRuleSet set ruleDetailType='WHATSAPP_NOT_SAME_GOJEK_DRIVERLICENSE_PAYROLL' ,updateTime = now() where ruleDetailType='MALE_MONTHLY_INCOME_AGE_EDUCATION_HAS_CREDIT_CARD';
update ruleParam set ruleDetailType='WHATSAPP_NOT_SAME_GOJEK_DRIVERLICENSE_PAYROLL' , updateTime = now() where ruleDetailType='MALE_MONTHLY_INCOME_AGE_EDUCATION_HAS_CREDIT_CARD';

commit;

