
start transaction;


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'FEMALE_WHATSAPP_OPEN_UPDATETIME_AVATAR','女别&本人手机号开通whatsapp&状态更新时间&有头像','2#20190101',1,3,15,2485, "V1", 3,0);



insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'MALE_WHATSAPP_OPEN_UPDATETIME_AVATAR','男别&本人手机号开通whatsapp&状态更新时间&有头像','1#20180301',1,3,15,2486, "V1", 3,0);



insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'FEMAIL_WHATSAPP_OPEN_UPDATETIME_AVATAR_DRIVERLICENSE','女性&本人手机号开通whatsapp&状态更新时间&有头像&上传了驾驶证','2#20190101#20190301',1,3,15,2487, "V1", 3,0);


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'MALE_WHATSAPP_OPEN_UPDATETIME_AVATAR_EDUCATION','男性&本人手机号开通whatsapp&状态更新时间&有头像&学历','1#20171231#20190101',1,3,15,2488, "V1", 3,0);




insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'NON_MANUAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'FEMALE_WHATSAPP_OPEN_UPDATETIME_AVATAR'
    );


insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'NON_MANUAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MALE_WHATSAPP_OPEN_UPDATETIME_AVATAR'
    );


insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'NON_MANUAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'FEMAIL_WHATSAPP_OPEN_UPDATETIME_AVATAR_DRIVERLICENSE'
    );


insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'NON_MANUAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MALE_WHATSAPP_OPEN_UPDATETIME_AVATAR_EDUCATION'
    );



insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
  and s.ruleDetailType in (
'FEMALE_WHATSAPP_OPEN_UPDATETIME_AVATAR',
'MALE_WHATSAPP_OPEN_UPDATETIME_AVATAR',
'FEMAIL_WHATSAPP_OPEN_UPDATETIME_AVATAR_DRIVERLICENSE',
'MALE_WHATSAPP_OPEN_UPDATETIME_AVATAR_EDUCATION'
    );


commit;




