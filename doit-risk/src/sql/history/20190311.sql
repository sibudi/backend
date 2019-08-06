start transaction;
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
       'ID_CARD_BIRTHDAY_NOT_SAME_WITH_USER_DETAIL','身份证号对应的生日与用户填写的不一致','',1,3,15,2205,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
       'ID_CARD_SEX_NOT_SAME_WITH_USER_DETAIL','身份证号对应的性别与用户填写的不一致','',1,3,15,2210,"V1",3,0);



insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'LABELING_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'ID_CARD_BIRTHDAY_NOT_SAME_WITH_USER_DETAIL',
    'ID_CARD_SEX_NOT_SAME_WITH_USER_DETAIL'
    );


insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
and s.ruleDetailType in (
    'ID_CARD_BIRTHDAY_NOT_SAME_WITH_USER_DETAIL',
    'ID_CARD_SEX_NOT_SAME_WITH_USER_DETAIL'
);

commit;
