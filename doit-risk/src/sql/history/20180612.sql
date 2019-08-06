
update sysAutoReviewRule set ruleType = 20 where ruleDetailType IN ('COMB_REBORROW_MEMORY_EDUCATION_SEX','COMB_REBORROW_OVERDUEDAYS_EVENING_RATIO','COMB_REBORROW_FIRSTOVERDUEDAYS');

-- 数据修复
update ordRiskRecord o set o.ruleRealValue='null'
where ruleDetailType='MAX_COM_ADDR_TIME' and ruleRealValue<>'NULL' and cast(ruleRealValue as UNSIGNED )>40000;


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
  'MULTI_SMS_RECENT30_COUNT','短信30天条数','0',1,3,15,1135,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
  'MULTI_CALLIN_NOTRECEIVED_RATIO_RECENT30','近30天打入未接通占比','0',1,3,15,1140,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
  'MULTI_CALLIN_NOTRECEIVED_COUNT_RECENT30','近30天打入未接通次数','0',1,3,15,1145,"V1");


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
  'MULTI_NOTCONNECTED_COUNT_RECENT30','近30天内未接通电话次数','0',1,3,15,1155,"V1");


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
  'MULTI_NOTCONNECTED_RATIO_RECENT30','近30天内未接通电话占比','0',1,3,15,1160,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
  'MULTI_TOTAL_DURATION_RECENT30','近30天通话时长','0',1,3,15,1165,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
  'MULTI_TOTAL_TIMES_RECENT30','近30天通话次数','0',1,3,15,1170,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
  'MULTI_CALLIN_DURATION_RECENT30','近30天打入通话时长','0',1,3,15,1175,"V1");


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
  'MULTI_CALLOUT_PHONES_RECENT30','近30天主叫号码数','0',1,3,15,1180,"V1");


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
  'MULTI_CALLIN_PHONES_RECENT30','近30天被叫号码数','0',1,3,15,1185,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
  'MULTI_NOCONNECT_DAYS_RECENT180','180天内的无通话天数','0',1,3,15,1190,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
  'MULTI_CONNECT_FIRSTCONTACT_TIMES_RECENT30','近30天与第一联系人通话次数','0',1,3,15,1195,"V1");


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
  'MULTI_CONNECT_SECONDCONTACT_TIMES_RECENT30','近30天与第二联系人通话次数','0',1,3,15,1200,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
  'MULTI_SAME_IP_APPLYTIMES','同一天内同一个IP的申请次数','0',1,3,15,1205,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
  'MULTI_GOJEK_SAMEPHONE','gojek手机号是否一致','0',1,3,15,1210,"V1");


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
  'MULTI_GOJEK_MAXFEE','单次乘车的最大费用','0',1,3,15,1215,"V1");


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
  'MULTI_FACEBOOK_MONTH_AVERAGE_REMARDS','facebook月均评论数','0',1,3,15,1220,"V1");


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
  'MULTI_TOTAL_APPS','累计app个数','0',1,3,15,1225,"V1");


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
  'MULTI_SAME_DEVICE_HITCOUNT','同一设备号命中2人及以','0',1,3,15,1230,"V1");


  commit;





