-- 复借规则增加

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'COMB_REBORROW_MEMORY_EDUCATION_SEX','手机内存&学历& 性别','1',2,1,15,1120,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'COMB_REBORROW_OVERDUEDAYS_EVENING_RATIO','上一次贷款逾期天数-复借>3&近15天内夜间活跃占比-复借>=0.05','3#0.05',2,1,15,1125,"V1");


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'COMB_REBORROW_FIRSTOVERDUEDAYS','首借贷款逾期天数','5',2,1,15,1130,"V1");



update sysAutoReviewRule set ruleCondition ='大于等于',ruleResult='2',ruleStatus='1' where ruleDetailType='MULTI_ALL_SMS_COUNT';


commit;
