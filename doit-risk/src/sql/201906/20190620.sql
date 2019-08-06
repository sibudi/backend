update sysAutoReviewRule s set s.ruleValue = 685 where ruleDetailType in ( 'RISK_SCORE_100_FOR_PRD100_FEMALE','RISK_SCORE_100_FOR_PRD100_MALE');


update ruleParam s set s.thresholdValue = 685 where ruleDetailType in ( 'RISK_SCORE_100_FOR_PRD100_FEMALE','RISK_SCORE_100_FOR_PRD100_MALE');


start transaction;


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MALE_ECOMMERCE_CREDIT_CARD_APPCOUNT_IZIPHONEAGE_GOJEK_MODEL50SCORE','男性&电商类app个数&信用卡类app个数&IZI在网时长&未验证gojek&50产品模型评分','1#0#680',1,3,15,2475,
       "V1",
       3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MALE_ECOMMERCE_CREDIT_CARD_APPCOUNT_IZIPHONEVERIFY_GOJEK_MODEL50SCORE','男性&电商类app个数&信用卡类app个数&IZI实名认证&未验证gojek&50产品模型评分','1#0#680',1,3,15,
       2480,"V1",
       3,0);


insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_50',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MALE_ECOMMERCE_CREDIT_CARD_APPCOUNT_IZIPHONEAGE_GOJEK_MODEL50SCORE'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_50',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MALE_ECOMMERCE_CREDIT_CARD_APPCOUNT_IZIPHONEVERIFY_GOJEK_MODEL50SCORE'
    );


insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
  and s.ruleDetailType in (
'MALE_ECOMMERCE_CREDIT_CARD_APPCOUNT_IZIPHONEAGE_GOJEK_MODEL50SCORE',
'MALE_ECOMMERCE_CREDIT_CARD_APPCOUNT_IZIPHONEVERIFY_GOJEK_MODEL50SCORE'
    );


commit;

