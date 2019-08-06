insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'DISTINCT_CONTACT_PHONE_MALE','去重通讯录个数&男','50',2,1,15,1565,"V1");
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'DISTINCT_CONTACT_PHONE_FEMALE','去重通讯录个数&女','30',2,1,15,1570,"V1");
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'RECENT90_DISTINCT_CALL_IN_NUMBERS_MALE','近90天去重被叫号码个数&男','10',2,1,15,1575,"V1");



update  sysAutoReviewRule s set ruleResult=2, ruleStatus=1 where s.ruleDetailType in ('RECENT30_EVENING_CALL_RATE_MALE','HIT_FRAUD_USER_INFO','MULTI_HIT_FRAUD_USER_INFO');

