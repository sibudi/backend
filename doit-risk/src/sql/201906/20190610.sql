start transaction;


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'MOBILE_IS_EMERGENCYTEL_FOR_1THORDER_OVERDUE7_USER','申请人手机号是首借逾期7天及以上用户的联系人号码','',1,3,15,2455,"V1",
       3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'EMERGENCYTEL_IS_EMERGENCYTEL_FOR_1THORDER_OVERDUE7_USER','申请人联系人号码是首借逾期7天及以上用户的联系人号码','',1,3,15,2456,"V1",
       3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'EMERGENCYTEL_IS_1THORDER_OVERDUE7_USER','申请人联系人号码是首借逾期7天及以上用户的本人号码','',1,3,15,2457,"V1",
       3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'MOBILE_IS_EMERGENCYTEL_FOR_2THORDER_OVERDUE7_USER','申请人手机号是第2笔逾期7天及以上用户的联系人号码','',1,3,15,2458,"V1",
       3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'EMERGENCYTEL_IS_EMERGENCYTEL_FOR_2THORDER_OVERDUE7_USER','申请人联系人号码是第2笔逾期7天及以上用户的联系人号码','',1,3,15,2459,"V1",
       3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'EMERGENCYTEL_IS_2THORDER_OVERDUE7_USER','申请人联系人号码是第2笔逾期7天及以上用户的本人号码','',1,3,15,2460,"V1",
       3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'EMERGENCYTEL_IS_EMERGENCYTEL_FOR_3THOR4THORDER_OVERDUE7_USER','申请人联系人号码是第3/4笔逾期7天及以上用户的联系人号码','',1,3,15,2461,"V1",
       3,0);


insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'FRAUD_UNIVERSAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MOBILE_IS_EMERGENCYTEL_FOR_1THORDER_OVERDUE7_USER'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'FRAUD_UNIVERSAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'EMERGENCYTEL_IS_EMERGENCYTEL_FOR_1THORDER_OVERDUE7_USER'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'FRAUD_UNIVERSAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'EMERGENCYTEL_IS_1THORDER_OVERDUE7_USER'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MOBILE_IS_EMERGENCYTEL_FOR_2THORDER_OVERDUE7_USER'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'EMERGENCYTEL_IS_EMERGENCYTEL_FOR_2THORDER_OVERDUE7_USER'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'EMERGENCYTEL_IS_2THORDER_OVERDUE7_USER'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'LABELING_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'EMERGENCYTEL_IS_EMERGENCYTEL_FOR_3THOR4THORDER_OVERDUE7_USER'
    );



insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
  and s.ruleDetailType in (
'MOBILE_IS_EMERGENCYTEL_FOR_1THORDER_OVERDUE7_USER',
'EMERGENCYTEL_IS_EMERGENCYTEL_FOR_1THORDER_OVERDUE7_USER',
'EMERGENCYTEL_IS_1THORDER_OVERDUE7_USER',
'MOBILE_IS_EMERGENCYTEL_FOR_2THORDER_OVERDUE7_USER',
'EMERGENCYTEL_IS_EMERGENCYTEL_FOR_2THORDER_OVERDUE7_USER',
'EMERGENCYTEL_IS_2THORDER_OVERDUE7_USER',
'EMERGENCYTEL_IS_EMERGENCYTEL_FOR_3THOR4THORDER_OVERDUE7_USER'
    );


commit;

update sysAutoReviewRule set ruleResult = 2, ruleStatus = 1 where ruleDetailType = 'WHATSAPP_CONNECT_MALE';