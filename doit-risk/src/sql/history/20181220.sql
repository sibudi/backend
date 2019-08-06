  insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'IZI_PHONEAGE_PHONEVERIFY_APPCOUNT_LANGUAGE','IZI在网时长&IZI手机实名认证&首借累计APP个数&手机语言','40#in',1,3,15,1970,"V1",3,0);


  insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'MALE_APPCOUNT_MONTH_SALARY_IZI_PHONEAGE_PHONEVERIFY','男性&首借累计App个数&月收入&IZI在网时长&IZI实名','40#5000000',1,3,15,1975,"V1",3,0);

  insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'FEMALE_APPCOUNT_MONTH_SALARY_IZI_PHONEAGE_PHONEVERIFY','女性&首借累计App个数&月收入&IZI在网时长&IZI实名','40#6000000',1,3,15,1980,"V1",3,0);