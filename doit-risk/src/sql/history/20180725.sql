update sysAutoReviewRule s set s.ruleResult=2,s.ruleStatus=1 where ruleDetailType in ('SMS_OVERDUE_MORETHAN_15DAYS_COUNT','SMS_OVERDUE_LESSTHAN_15DAYS_COUNT')
and s.disabled=0;

update sysAutoReviewRule s set s.diabled=0 and s.disabled=1 where s.ruleDetailType= 'SMS_OVERDUE_COUNT_THIRTY_DAY' and s.disabled=0;



insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',5,
  'SMS_RECENT30_OVERDUE_DISTINCT_COUNT','近30天逾期短信条数（按号码去重）','大于','1',1,3,15,426,"V1");

commit;