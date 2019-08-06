insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'RECENT30_EVENING_CALL_RATE_MALE','近30天内夜间活跃占比&男','0.05',2,1,15,1530,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'RECENT30_CALL_IN_MISSED_RATE_RECENT30_CALL_COUNT','近30天打入未接通占比&近30天通话次数','0.1#10',2,1,15,1535,"V1");


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'NO_TAX_NUMBER_APP_COUNT_MALE','无税号&首借累计app个数&男','20',2,1,15,1540,"V1");


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'NO_TAX_NUMBER_APP_COUNT_FEMALE','无税号&首借累计app个数&女','15',2,1,15,1545,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'DISTINCT_CONTACT_COUNT_RECENT90_CALLIN_DISTINCT_NUMBERS','去重通讯录个数&近90天被叫号码去重个数','100#10',2,1,15,1550,"V1");



update sysAutoReviewRule s set s.updateTime = now(),s.ruleValue='0.1',s.ruleCondition='大于等于' where s.ruleDetailType='NIGHT_CALL_RATE'
and s.disabled=0;


-- 外呼

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',25,
  'AUTO_CALL_REJECT_OWNER_CALL_INVALID','本人号码外呼拒绝','',2,1,15,1555,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',25近30天内夜间活跃占比,
  'AUTO_CALL_REJECT_OWNER_EXCEED_LIMIT','本人号码外呼超过次数','',2,1,15,1560,"V1");


INSERT INTO doit.sysParam (disabled, uuid, createUser, updateUser, createTime, updateTime, remark, sysKey, sysValue, description)
VALUES (0, '', 0, 0, now(), now(), '', 'risk:auto_call:switch', 'true', '机审外呼开关');



