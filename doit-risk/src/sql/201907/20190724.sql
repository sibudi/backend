start transaction;



insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
       'LAST_LOAN_AMOUNT_OVERDUEDAYS_BORROWING_COUNT','上一笔复借200RBM并且逾期',
       '400000#0#3',2,1,15,3135,"V1", 3,0);



insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'RE_BORROWING_UNIVERSAL',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled=0 and s.ruleDetailType in (
   'LAST_LOAN_AMOUNT_OVERDUEDAYS_BORROWING_COUNT'
);



insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled=0 and s.ruleDetailType in (
   'LAST_LOAN_AMOUNT_OVERDUEDAYS_BORROWING_COUNT'
);



