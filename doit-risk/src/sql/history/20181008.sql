insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,ruleData)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',23,
  'CITY_IN_EARTHQUAKE_AREA','城市属于震区','','',2,1,15,1720,"V1",'Kabupaten Donggala#Kabupaten Parigi Moutong#Kabupaten Sigi#Kota Palu');





