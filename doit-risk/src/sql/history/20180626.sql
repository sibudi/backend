

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'免初审和复审',22,
  'COMB_RECENT30CALLINRATIO_DIFFOFEARLIESTMSG_NOCALLDAYS_FEMALE','近30天呼入占比&最早一条短信时间距申请时间的天数&近90天无通话记录总天数&女',
  '0.3#200#40',1,3,15,1315,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'免初审和复审',22,
  'COMB_RECENT90CALLINTIMES_DIFFOFEARLIESTMSG_EDUCATION_FEMALE','近90天打入通话次数&最早一条短信时间距申请时间的天数&学历&女',
  '100#200',1,3,15,1320,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'免初审和复审',22,
  'COMB_MOBILEUSAGETIMES_DIFFOFEARLIESTMSG_CHILDRENCOUNT_FEMALE','手机使用时长（单位：天）&最早一条短信时间距申请时间的天数&孩子数量&女',
  '50#200#1#3',1,3,15,1325,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'免初审和复审',22,
  'COMB_RECENT90CALLINTIMES_NOCALLDAYS_EDUCATION_MALE','近90天打入通话次数&近90天无通话记录总天数&学历&男',
  '80#20',1,3,15,1330,"V1");

update sysAutoReviewRule s set s.ruleDesc='性别&年龄&第一，第二均有通话记录&去重通讯录个数' ,s.ruleValue = '25#46#185',s.updateTime = now()
where s.ruleDetailType = 'COMB_2_FEMALE_FREE_PHONE_CHECK';

commit;