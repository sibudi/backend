start transaction;
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'EMERGENCY_TEL_IN_FIRST_BORROW_600_IZI_PHONEVERIFY','紧急联系人是首借600已还款用户&IZI手机实名MATCH','0',1,3,15,2215,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
       'HISTORY_SUBMIT_COUNT_MALE','提交订单数&男性','3',2,1,15,2220,"V1",3,0);



insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'NON_MANUAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'EMERGENCY_TEL_IN_FIRST_BORROW_600_IZI_PHONEVERIFY'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'HISTORY_SUBMIT_COUNT_MALE'
    );


insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
and s.ruleDetailType in (
    'EMERGENCY_TEL_IN_FIRST_BORROW_600_IZI_PHONEVERIFY',
    'HISTORY_SUBMIT_COUNT_MALE'
);

commit;



update  sysAutoReviewRule s set s.disabled=1 ,s.remark='监管需要将相关规则下线20190314' where s.disabled = 0 and (ruleType  in (3,4,5)
or ruleDesc like '%短信%'
or ruleDesc like '%通话%'
or ruleDesc like '%通讯%'
or ruleDesc like '%呼入%'
or ruleDesc like '%呼出%'
or ruleDesc like '%主叫%'
or ruleDesc like '%被叫%'
or ruleDesc like '%接通%'
or ruleDesc like '%去重被叫号码%'
or ruleDesc like '%夜间%'
or ruleDesc like '%被叫号码%'
or ruleDetailType like '%IMEI%'
or ruleDetailType like '%DEVICE_ID%'
) ;
-- order by ruleType

-- and ruleType !=20
;


update flowRuleSet a set a.disabled=1 ,a.remark='监管需要将相关规则下线20190314'
where ruleDetailType in (select s.ruleDetailType from sysAutoReviewRule s where s.disabled=1);







