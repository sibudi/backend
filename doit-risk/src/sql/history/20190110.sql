start transaction;
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'MONTHLY_INCOME_GOJEK_MAX_DISTANCE_FEMALE','月收入&单次乘车最长距离&性别','8000000#10',1,3,15,2065,"V1",35,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'COMPANY_TEL_COMPANY_NAME_MALE','公司号码&公司名称&性别','08#pt',2,1,15,2070,"V1",3,0);

-- 通用拒绝规则
insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'FRAUD_UNIVERSAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'COMPANY_TEL_COMPANY_NAME_MALE'
    );

-- 免核规则
insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'NON_MANUAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MONTHLY_INCOME_GOJEK_MAX_DISTANCE_FEMALE'
    );


-- 参数


insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
and s.ruleDetailType in (
'COMPANY_TEL_COMPANY_NAME_MALE',
'MONTHLY_INCOME_GOJEK_MAX_DISTANCE_FEMALE'
);




