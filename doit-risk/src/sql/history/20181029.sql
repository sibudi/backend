insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',9,
  'GOJEK_EMAIL_NOT_SAME_AND_MOBILE_NOT_SAME','gojek邮箱是不一致&gojek手机号是不一致','true#true',1,3,15,1835,"V1",2);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
  'HOUSE_WIFE_AND_MARRIAGE_STATUS','身份是家庭主妇但婚姻状态是非已婚','',1,3,15,1840,"V1",2);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
  'SAME_DAY_SAME_IP','同一天内同一个IP的申请次数','3',1,3,15,1845,"V1",2);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'FACEBOOK_AVERAGE_MONTH_COMMENT_FEMALE','facebook月均评论数&女','50',1,3,15,1850,"V1",2);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'男&单身&高中及以下',15,
  'SEX_MARRIAGE_EDUCATION','性别&婚姻状态&学历','',1,3,15,1850,"V1",2);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'FACEBOOK_AVERAGE_MONTH_POST_MALE','facebook月均帖子数&男','10',1,3,15,1855,"V1",2);


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'HIT_COLLECTOR_BLACK_LIST','命中催收黑名单用户身份证号或手机号','',2,1,15,1860,"V1",2);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'MULTI_HIT_COLLECTOR_BLACK_LIST','命中催收黑名单用户身份证号或手机号-复借','',2,1,15,1865,"V1",2);