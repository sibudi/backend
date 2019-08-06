
start transaction;

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',27,
       'RISK_SCORE_600_FOR_PRD100','600产品评分对100产品','430',2,1,15,2335,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',27,
       'RISK_SCORE_100_FOR_PRD100_MALE','男性&100产品评分对100产品','675',2,1,15,2340,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',27,
       'RISK_SCORE_100_FOR_PRD100_FEMALE','女性&100产品评分对100产品','665',2,1,15,2345,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',27,
       'RISK_SCORE_600_FOR_PRD50_MALE','男性&600产品评分对50产品','430',2,1,15,2350,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',27,
       'RISK_SCORE_600_FOR_PRD50_FEMALE','女性&600产品评分对50产品','425',2,1,15,2355,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',27,
       'IZIPHONEAGE','IZI手机在网时长返回值为PHONENUMBER_NOT_FOUND','',2,1,15,2355,"V1",3,0);



insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_100',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'RISK_SCORE_600_FOR_PRD100',
    'RISK_SCORE_100_FOR_PRD100_MALE',
    'RISK_SCORE_100_FOR_PRD100_FEMALE',
    'IZIPHONEAGE'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_50',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'RISK_SCORE_600_FOR_PRD50_MALE',
    'RISK_SCORE_600_FOR_PRD50_FEMALE',
    'IZIPHONEAGE'
    );


insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
and s.ruleDetailType in (
    'RISK_SCORE_600_FOR_PRD100',
    'RISK_SCORE_100_FOR_PRD100_MALE',
    'RISK_SCORE_100_FOR_PRD100_FEMALE',
    'IZIPHONEAGE',
    'RISK_SCORE_600_FOR_PRD50_MALE',
    'RISK_SCORE_600_FOR_PRD50_FEMALE'
);

commit;