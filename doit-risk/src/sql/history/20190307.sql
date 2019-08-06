start transaction;

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
       'CUSTOM_DEVICE_FINGER_PRINT_DEVICE_NAME_MEMORY_CPU','设备信息组合(androidId等)','0',2,1,15,471,"V1",3,0);


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
       'CUSTOM_DEVICE_FINGER_PRINT_MAC_ADDRESS_WITHOUT_ANDROIDID','设备信息组合(无androidId等)','0',2,1,15,472,"V1",3,0);


insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'FRAUD_UNIVERSAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'CUSTOM_DEVICE_FINGER_PRINT_DEVICE_NAME_MEMORY_CPU',
    'CUSTOM_DEVICE_FINGER_PRINT_MAC_ADDRESS_WITHOUT_ANDROIDID'
    );


insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
and s.ruleDetailType in (
    'CUSTOM_DEVICE_FINGER_PRINT_DEVICE_NAME_MEMORY_CPU',
    'CUSTOM_DEVICE_FINGER_PRINT_MAC_ADDRESS_WITHOUT_ANDROIDID'
);

commit;
