insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
  'FIRST_LINKMAN_EXISTS','第一紧急联系人相同','',1,3,15,1695,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
  'EMAIL_EXISTS','邮箱相同','',1,3,15,1700,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
  'SAME_WORK_ADDRESS_AND_ORDER_ADDRESS_EXISTS','工作地址&下单位置','',1,3,15,1705,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
  'SAME_HOME_ADDRESS_AND_ORDER_ADDRESS_EXISTS','居住地址&下单位置','',1,3,15,1710,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
  'SAME_WORK_ADDRESS_AND_HOME_ADDRESS_EXISTS','工作地址&居住地址','',1,3,15,1715,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
  'HAS_FAMILY_CARD','有家庭卡','',1,3,15,1720,"V1");


CREATE INDEX idx_email ON usrWorkDetail (email);

CREATE INDEX idx_email ON usrHouseWifeDetail (email);

CREATE INDEX idx_email ON usrStudentDetail (email);

CREATE INDEX idx_detail_small_direct ON usrAddressDetail (detailed, smallDirect);

DROP INDEX idx_addressType ON usrAddressDetail;