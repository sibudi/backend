start transaction;

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
       'SAME_IMSI','同一IMSI命中大于阈值','0',1,3,15,2115,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
       'CUSTOM_DEVICE_FINGER_PRINT_APPS','同一android_apps命中大于阈值','0',1,3,15,2115,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
       'CUSTOM_DEVICE_FINGER_PRINT_PHONE_BRAND','同一android_deviceType_totalMemoery_phoneBrand命中大于阈值','0',1,3,15,2115,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
       'CUSTOM_DEVICE_FINGER_PRINT_IP','同一android_deviceType_totalMemoery_ip命中大于阈值','0',1,3,15,2115,"V1",3,0);



insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'LABELING_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'SAME_IMSI',
    'CUSTOM_DEVICE_FINGER_PRINT_APPS',
    'CUSTOM_DEVICE_FINGER_PRINT_PHONE_BRAND',
    'CUSTOM_DEVICE_FINGER_PRINT_IP'
    );


insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
and s.ruleDetailType in (
    'SAME_IMSI',
    'CUSTOM_DEVICE_FINGER_PRINT_APPS',
    'CUSTOM_DEVICE_FINGER_PRINT_PHONE_BRAND',
    'CUSTOM_DEVICE_FINGER_PRINT_IP'
);