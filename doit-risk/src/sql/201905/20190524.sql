start transaction;

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
       'MALE_IZI_WHATSAPP_RESULT','男性&IZI验证本人手机号未开通WhatsApp','',2,1,15,2400,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
       'FEMALE_IZI_WHATSAPP_IZI_PHONEVERIFY','女性&IZI验证本人手机号未开通WhatsApp&ZI手机实名认证','',2,1,15,2405,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
       'FEMALE_IZI_WHATSAPP_IZI_PHONEVERIFY_IZI_LINKMAN_WHATSAPP','女性&ZI本人WhatsApp&ZI手机实名认证&IZI联系人WhatsAppw未开通个数','0',2,1,15,2410,"V1",
       3,0);


insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_100',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MALE_IZI_WHATSAPP_RESULT'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_100',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'FEMALE_IZI_WHATSAPP_IZI_PHONEVERIFY'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_50',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MALE_IZI_WHATSAPP_RESULT'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_50',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'FEMALE_IZI_WHATSAPP_IZI_PHONEVERIFY_IZI_LINKMAN_WHATSAPP'
    );

insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
  and s.ruleDetailType in (
    'MALE_IZI_WHATSAPP_RESULT',
    'FEMALE_IZI_WHATSAPP_IZI_PHONEVERIFY',
    'FEMALE_IZI_WHATSAPP_IZI_PHONEVERIFY_IZI_LINKMAN_WHATSAPP'
    );


commit;