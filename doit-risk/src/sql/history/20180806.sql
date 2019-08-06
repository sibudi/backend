

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
  'HAS_INSURANCE_CARD','有税卡','','',1,3,15,1435,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
  'COMB_TAX_NUMBER_RECENT30INRATION_APPCOUNT_MALE','有税卡&近30天呼入占比&首借累计app个数&男','','0.3#40',1,3,15,1440,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
  'COMB_TAX_NUMBER_RECENT30INRATION_APPCOUNT_FEMALE','有税卡&近30天呼入占比&首借累计app个数&女','','0.3#40',1,3,15,1445,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'MULTI_IMEI_IS_FRAUD_USER','申请人imei命中欺诈用户黑名单-复借','','',1,3,15,1450,"V1");