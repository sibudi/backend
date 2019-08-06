
start transaction;


insert into sysAutoReviewRule(ruleData,uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values('SUMATERA BARAT#LAMPUNG#BENGKULU#SUMATERA SELATAN#SULAWESI UTARA#JAMBI',replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'HOME_PROVINCE_MALE_PRODUCT600','居住地（省）高风险&男性&600产品模型分','505',2,1,15,2290,"V1",3,0);

insert into sysAutoReviewRule(ruleData,uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values('RIAU#SULAWESI UTARA#SUMATERA SELATAN#NUSA TENGGARA TIMUR',replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'HOME_PROVINCE_FEMALE_PRODUCT600','居住地（省）高风险&女性&600产品模型分','505',2,1,15,2290,"V1",3,0);

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'HOME_PROVINCE_MALE_PRODUCT600',
    'HOME_PROVINCE_FEMALE_PRODUCT600'
    );

insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
and s.ruleDetailType in (
   'HOME_PROVINCE_MALE_PRODUCT600',
    'HOME_PROVINCE_FEMALE_PRODUCT600'
);

commit;


