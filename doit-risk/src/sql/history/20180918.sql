-- 30天改为15天


update sysAutoReviewRule set disabled=1,updateTime = now(),remark='逾期30天规则改为逾期15天规则'
where ruleDetailType in ('IMEI_IN_OVERDUE30_BLACK_LIST_USER',
'ID_CARD_IN_OVERDUE30_BLACK_LIST_USER',
'MOBILE_IN_OVERDUE30_BLACK_LIST',
'BANKCARD_NUMBER_IN_OVERDUE30_BLACK_LIST_USER_BANKCARD',
'EMERGENCY_TEL_IS_OVERDUE30_BLACK_LIST_USER_EMERGENCY_TEL') ;


update sysAutoReviewRule set disabled=1,updateTime = now(),remark='统计次数转为新规则 EMERGENCY_TEL_IN_OVERDUE15_BLACKLIST'
where ruleDetailType ='EMERGENCY_TEL_IN_OVERDUE15_BLACK_LIST_USER_COUNT';


update sysAutoReviewRule set disabled=1,updateTime = now(),remark='统计次数转为新规则(30转15) CONTACT_PHONE_IN_OVERDUE15_BLACKLIST'
where ruleDetailType ='CONTACT_HIT_OVERDUE30USERS_COUNT';


-- 统计次数的已经有拒绝规则，disabled

update sysAutoReviewRule set disabled=1,updateTime = now(),remark='已有拒绝规则 CONTACT_PHONE_IN_OVERDUE15_BLACKLIST'
where ruleDetailType ='CONTACT_PHONES_IN_OVERDUE15_BLACK_LIST_USER_COUNT';

update sysAutoReviewRule set disabled=1,updateTime = now(),remark='已有拒绝规则 MOBILE_IN_OVERDUE15_BLACK_LIST_USER_EMERGENCY_TEL'
where ruleDetailType ='MOBILE_IS_EMERGENCYTEL_FOR_OVERDUE30USERS';

update sysAutoReviewRule set disabled=1,updateTime = now(),remark='已有拒绝规则 EMERGENCY_TEL_IN_OVERDUE15_BLACKLIST'
where ruleDetailType ='EMERGENCYTEL_IS_OVERDUE30USERS';

update sysAutoReviewRule set disabled=1,updateTime = now(),remark='已有拒绝规则 CALL_RECORD_PHONE_IN_OVERDUE15_BLACKLIST'
where ruleDetailType ='CALL_RECORD_HIT_OVERDUE30USERS_COUNT';




insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'IMEI_IN_OVERDUE15_BLACKLIST','申请手机IMEI命中逾期15天以上用户IMEI黑名单','',2,1,15,1595,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'ID_CARD_IN_OVERDUE15_BLACKLIST','申请人身份证命中逾期15天以上用户身份证号黑名单','',2,1,15,1600,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'MOBILE_IN_OVERDUE15_BLACKLIST','申请手机号命中逾期15天以上用户手机号黑名单','',2,1,15,1605,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'BANKCARD_IN_OVERDUE15_BLACKLIST','申请人银行卡命中逾期15天以上用户银行卡号黑名单','',2,1,15,1610,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_OVERDUE15_BLACKLIST','申请人的紧急联系人手机号码是逾期15天以上用户的紧急联系人','',2,1,15,1615,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'EMERGENCY_TEL_IN_OVERDUE15_BLACKLIST','紧急联系人手机号码是逾期15天以上用户','',2,1,15,1620,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'MOBILE_IN_CONTACT_OF_OVERDUE15_BLACKLIST','申请手机号命中逾期15天用户的通讯录','',2,1,15,1625,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'MOBILE_IN_CALL_RECORD_OF_OVERDUE15_BLACKLIST','申请手机号命中逾期15天用户的通话记录','',2,1,15,1630,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'MOBILE_IN_SHORT_MSG_OF_OVERDUE15_BLACKLIST','申请手机号命中逾期15天用户的短信对象','',2,1,15,1635,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'CONTACT_PHONE_IN_OVERDUE15_BLACKLIST','通讯录命中逾期15天黑名单','',2,1,15,1640,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'CALL_RECORD_PHONE_IN_OVERDUE15_BLACKLIST','通话记录对象命中逾期15天及以上黑名单','',2,1,15,1645,"V1");


-- 复借-30天改为15天

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
  'MULTI_MOBILE_IN_OVERDUE15_BLACK_LIST','借款人手机号命中逾期15天以上黑名单用户','',1,3,15,1020,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,ruleCondition)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
  'MULTI_CONTACT_PHONE_IN_OVERDUE15_COUNT','通讯录对象命中逾期15天以上黑名单用户次数','1',1,3,15,1025,"V1",'大于等于');

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,ruleCondition)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
  'MULTI_CALL_RECORD_PHONE_IN_OVERDUE15_COUNT','通话记录号码命中逾期15天以上黑名单次数','1',1,3,15,1030,"V1",'大于等于');



update sysAutoReviewRule set disabled=1,updateTime = now(),remark='30天转为15天，规则==> MULTI_MOBILE_IN_OVERDUE15_BLACK_LIST'
where ruleDetailType ='MULTI_TELEPHONE_30_COUNT';

update sysAutoReviewRule set disabled=1,updateTime = now(),remark='30天转为15天，规则==> MULTI_CONTACT_PHONE_IN_OVERDUE15_COUNT'
where ruleDetailType ='MULTI_CONTACT_30_COUNT';

update sysAutoReviewRule set disabled=1,updateTime = now(),remark='30天转为15天，规则==> MULTI_CALL_RECORD_PHONE_IN_OVERDUE15_COUNT'
where ruleDetailType ='MULTI_TELEPHONE_30_BLACKLIST_COUNT';


-- ruleType迁移
update sysAutoReviewRule s set s.ruleType = 24 where ruleDetailType in ('DEVICEID_IS_OVERDUE30USERS','MOBILE_IN_OVERDUE15_BLACK_LIST_USER_EMERGENCY_TEL');

update sysAutoReviewRule s set s.ruleType = 20 where ruleDetailType in ('MULTI_IMEI_IS_FRAUD_USER');


//remark


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'CALL_RECORD_PHONE_IN_OVERDUE7_BLACKLIST','通话记录对象命中逾期7天及以上黑名单','',2,1,15,1650,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'IMEI_IN_OVERDUE7_BLACKLIST','申请手机IMEI命中逾期7天以上用户IMEI黑名单','',2,1,15,1655,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'ID_CARD_IN_OVERDUE7_BLACKLIST','申请人身份证命中逾期7天以上用户身份证号黑名单','',2,1,15,1660,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'MOBILE_IN_OVERDUE7_BLACKLIST','申请手机号命中逾期7天以上用户手机号黑名单','',2,1,15,1665,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'BANKCARD_IN_OVERDUE7_BLACKLIST','申请人银行卡命中逾期7天以上用户银行卡号黑名单','',2,1,15,1670,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_OVERDUE7_BLACKLIST','申请人的紧急联系人手机号码是逾期7天以上用户的紧急联系人','',2,1,15,1675,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'MOBILE_IS_EMERGENCY_TEL_FOR_OVERDUE7_BLACK','申请人手机号码是逾期7天以上用户的紧急联系人','',2,1,15,1680,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'EMERGENCY_TEL_IN_OVERDUE7_BLACKLIST','申请人的紧急联系人手机号码是逾期7天以上用户','',2,1,15,1685,"V1");






