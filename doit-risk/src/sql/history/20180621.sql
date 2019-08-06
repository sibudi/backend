insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'COMB_MOBILE_LANG_AND_CAP','手机语言&手机总容量[免核]','en#25',1,3,15,1290,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'COMB_MOBILE_CAP_FEMALE_EDUCATION','手机总容量&女&学历[免核]','25',1,3,15,1295,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'COMB_MOBILE_CAP_MALE_MARITAL_EDUCATION_DIFFDAYSOFLASTCALL_RECENT30CALLIN','男性&婚姻&学历&最后通话&30天打入时长&手机容量[免核]','0#600#25',1,3,15,1300,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'COMB_NOCALLOFFIRSTSECONDLINKMAN_MALE_MARITAL','第一，第二联系人均无通话记录&男&单身','',2,1,15,1305,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'COMB_MOBILE_CAP_FIRSTLINKEMANRECENT180CALLCOUNT_MALE','第一联系人,近180天,通话次数&手机总内存&男','0#1',2,1,15,1310,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'COMB_MOBILE_CAP_MARITAL_EDUCATION','婚姻&手机总容量&学历','5',2,1,15,1310,"V1");


update sysAutoReviewRule set ruleDesc ='facebook公司名称不一致命中false' where ruleDetailType='FACEBOOK_COMPANY_NOT_CONTAIN'

 commit;
