
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,ruleData)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',23,
  'PROVINCE_IN_EARTHQUAKE_AREA','省份属于震区','','',2,1,15,1455,"V1",'Nusa Tenggara Barat');



