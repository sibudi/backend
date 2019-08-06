
start transaction;

update sysAutoReviewRule set appliedTo = 3 where ruleDetailType = 'IZIPHONEVERIFY_WHATSAPPANDOWNERTELSAME_SEX';

update sysAutoReviewRule set disabled=1 where disabled=0 and ruleDetailType in ('ORDERSMALLDIRECT_RISK_SCORE_600_MEAL','ORDERSMALLDIRECT_RISK_SCORE_600_FEMEAL');


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',27,
       'ORDER_SMALL_DIRECT','下单的小区为空','',2,1,15,2430,"V1",
       3,0);


insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_100',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'ORDER_SMALL_DIRECT'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_50',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'ORDER_SMALL_DIRECT'
    );



insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
  and s.ruleDetailType in (
'ORDER_SMALL_DIRECT'
    );


commit;


