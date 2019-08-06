insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'COMB_FIRST_CONTACT_RECENT90CALL_FEMALE','第一联系人,近90天,通话时长&女','3000',1,3,15,1240,"V1");


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'COMB_RECENT180RIDETIMES_FEMALE','近180天累计乘车次数&女','5',1,3,15,1245,"V1");


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'COMB_DIFFDAYSOFEARLIESTMSG_RECENT90OVEDUESMSG_FEMALE','女性&最早一条短信时间&近90天逾期短信条数（按号码去重）','270#0',1,3,15,1250,"V1");


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'COMB_SAMEIPCOUNT_MALE','同一天内同一个IP的申请次数&男','1',2,1,15,1255,"V1");


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'COMB_RECENT180RIDETIMES_MAXRIDEFEE_MALE','近180天累计乘车次数&单次乘车的最大费用&男','5#40000',2,1,15,1260,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'COMB_RECENT90FIRSTCONTACTCALL_FACEBOOKNOPOSTMONTHS_MALE','第一联系人,近90天,通话时长&facebook未发帖的月份个数&男','0#1',2,1,15,1265,"V1");


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'COMB_FIRST_CONTACT_AND_SECOND_CONTACT_NOT_IN_CONTACTS','2个紧急联系人均不在通讯录','0',2,1,15,1270,"V1");


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'COMB_RECENT30CALLIN_MOBILEUSEDTIMES_FEMALE','女性&近30天打入通话时长&手机使用时长','300#30',2,1,15,1275,"V1");



insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'COMB_DIFFDAYSOFEARLIESTMSG_MALE','男性&最早一条短信时间距申请时间的天数','0',2,1,15,1280,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'COMB_MOBILEUSEDTIMES_RECENT30NOCONNECTRATIO_MALE','男性&手机使用时长&近30天打入未接通占比','1#50#0.05#0.15',2,1,15,1285,"V1");




update  sysAutoReviewRule set ruleValue= '0.15' where ruleDetailType='RECENT_30_CALL_NO_CONNECT_RATE';

