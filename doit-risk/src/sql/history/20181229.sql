
  insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
  'COMPANY_CALL_IOS_FEMALE','公司外呼&IOS&女性','',1,3,15,1985,"V1",3,0);

  insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
  'COMPANY_CALL_IOS_MALE_MONTHLY_INCOME','公司外呼&IOS&男性&月收入','5000000',1,3,15,1990,"V1",3,0);

  insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'TOTAL_MEMORY_APPLY_TIME_HOUR_MALE','手机总内存&下单时间&男性','2',2,1,15,1995,"V1",3,0);