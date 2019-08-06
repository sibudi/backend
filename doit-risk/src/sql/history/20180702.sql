insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
  'CASH2_FACE_PLUS_PLUS_SCORE','face++人脸识别相似度','小于',
  '70',2,1,15,1335,"V1");

commit;