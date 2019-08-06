

update sysAutoReviewRule set ruleValue = '2',updateTime = now() where ruleDetailType='AUTO_CALL_REJECT_LINKMAN_VALID_COUNT';
update sysAutoReviewRule set ruleValue = '5',updateTime = now() where ruleDetailType='AUTO_CALL_REJECT_BACKUP_LINKMAN_VALID_COUNT';



insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',25,
  'MULTI_AUTO_CALL_REJECT_LINKMAN_VALID_COUNT','紧急联系人外呼有效个数小于阈值-复借','1',2,1,15,1586,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',25,
  'MULTI_AUTO_CALL_REJECT_BACKUP_LINKMAN_VALID_COUNT','备选联系人外呼有效个数小于阈值-复借','3',2,1,15,1587,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
  'IS_IOS','是iOS设备','',1,3,15,1690,"V1");