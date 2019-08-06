
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'SMS_OVERDUE_LESSTHAN_15DAYS_COUNT_FEMALE','短信出现逾期10-15（含）天的平台个数&女','0',2,1,15,1875,"V1",3);

//添加remark
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',5,
  'SMS_OVERDUE_LESSTHAN_15DAYS_COUNT','短信出现逾期10-15（含）天的平台个数','0',1,3,15,1875,"V1",3);

update sysAutoReviewRule s set s.ruleDetailType = 'RECENT_30_CALL_TIME_MALE' where s.ruleDetailType = 'RECENT_30_CALL_TIME';


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',4,
  'RECENT_30_CALL_TIME','近30天通话时长','0',1,3,15,1885,"V1",3);



insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',25,
  'AUTO_CALL_REJECT_BACKUP_LINKMAN_VALID_COUNT_100RMB_PRODUCT','备选联系人外呼有效个数-100RMB','3',2,1,15,1880,"V1",3);




update sysAutoReviewRule s set s.ruleDetailType='SMS_OVERDUE_LESSTHAN_15DAYS_COUNT_MALE',
s.ruleDesc = '短信出现逾期10-15（含）天的平台个数&男'
where s.ruleDetailType = 'SMS_OVERDUE_LESSTHAN_15DAYS_COUNT' and disabled=0;


update sysAutoReviewRule s set s.ruleResult = 2 ,s.ruleStatus = 1 where s.ruleDetailType = 'SMS_OVERDUE_MORETHAN_15DAYS_COUNT'
and disabled=0;

update sysAutoReviewRule s set s.ruleResult = 1 ,s.ruleStatus = 3 where s.ruleDetailType = 'CONTACT_COUNT'and disabled=0;

