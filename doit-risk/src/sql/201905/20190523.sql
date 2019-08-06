insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'COMB_SAMEIPCOUNT_150RMB','同一天内同一个IP的申请次数','5',2,1,15,3000,"V1",3);

  insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'COMB_SAMEIPCOUNT_600RMB','同一天内同一个IP的申请次数','3',2,1,15,1255,"V1");

  insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_100',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'COMB_SAMEIPCOUNT_150RMB'
    );
 insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'COMB_SAMEIPCOUNT_600RMB'
    );

insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
  and s.ruleDetailType in (
    'COMB_SAMEIPCOUNT_150RMB',
    'COMB_SAMEIPCOUNT_600RMB'
    );


update sysAutoReviewRule set disabled=1 where ruleDetailType in ('COMB_SAMEIPCOUNT_MALE','COMB_SAMEIPCOUNT_MALE_100RMB');
update ruleParam set disabled=1 where ruleDetailType in ('COMB_SAMEIPCOUNT_MALE','COMB_SAMEIPCOUNT_MALE_100RMB');
update flowRuleSet set disabled=1 where ruleDetailType in ('COMB_SAMEIPCOUNT_MALE','COMB_SAMEIPCOUNT_MALE_100RMB');

update sysAutoReviewRule set disabled=1 where ruleDetailType in ('REBORROWING_IZI_PHONEVERIFY_AGE_MALE','REBORROWING_FIRST_LOAN_COMPANY_CALL_TOTAL_MEMORY','REBORROWING_IZI_PHONEAGE_PHONEVERIFY_MALE');
update ruleParam set disabled=1 where ruleDetailType in ('REBORROWING_IZI_PHONEVERIFY_AGE_MALE','REBORROWING_FIRST_LOAN_COMPANY_CALL_TOTAL_MEMORY','REBORROWING_IZI_PHONEAGE_PHONEVERIFY_MALE');
update flowRuleSet set disabled=1 where ruleDetailType in ('REBORROWING_IZI_PHONEVERIFY_AGE_MALE','REBORROWING_FIRST_LOAN_COMPANY_CALL_TOTAL_MEMORY','REBORROWING_IZI_PHONEAGE_PHONEVERIFY_MALE');

update sysAutoReviewRule set disabled=1 where ruleDetailType in ('COMB_3_CHILDRENAMOUNT');
update ruleParam set disabled=1 where ruleDetailType in ('COMB_3_CHILDRENAMOUNT');
update flowRuleSet set disabled=1 where ruleDetailType in ('COMB_3_CHILDRENAMOUNT');

update sysAutoReviewRule set ruleResult = 1, ruleStatus = 3 where ruleDetailType in ('FEMALE_AGE_CHILDREN_COUNT','NO_TAX_NUMBER_NO_PAYROLL_POST_MALE','SAME_WORK_ADDRESS_AND_HOME_ADDRESS_EXISTS');
