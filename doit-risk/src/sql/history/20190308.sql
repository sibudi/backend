start transaction;
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'CREDIT_CARD_ECOMMERCE_MALE_EDUCATION','信用卡类app个数&电商类app个数&男性&学历','2#0',2,1,15,2195,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'TOTAL_APP_COUNT_MALE_IZI_PHONEVERIFY','首借累计app个数&男性&IZI手机实名','20',2,1,15,2200,"V1",35,0);



insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'CREDIT_CARD_ECOMMERCE_MALE_EDUCATION',
    'TOTAL_APP_COUNT_MALE_IZI_PHONEVERIFY'
    );


insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
and s.ruleDetailType in (
   'CREDIT_CARD_ECOMMERCE_MALE_EDUCATION',
    'TOTAL_APP_COUNT_MALE_IZI_PHONEVERIFY'
);

commit;
