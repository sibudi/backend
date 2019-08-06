start transaction;
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
  'GOJEK_CASH_PAY_COUNT','现金支付次数','0',1,3,15,1945,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
  'GOJEK_SAME_MOBILE_SAME_EMAIL_SAME_ADDRESS_FEMALE','gojek手机号一致&gojek邮箱一致&乘车地匹配公司地址同时出现&性别','',1,3,15,1950,"V1",3,0);


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
  'LANGUAGE_MONTHLY_SALARY','手机语言&月收入','en#10000000',1,3,15,1955,"V1",3,0);

  insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'NO_TAXNUMBER_APP_COUNT','无税卡&首借累计APP个数','15',2,1,15,1960,"V1",3,0);

  insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'WORK_ADDRESS_NOT_VALID_NORMAL_IOS','工作城市不佳-IOS','',2,1,15,1965,"V1",25,0);

  commit;

