
start transaction;
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'WHATSAPP_SAME_WITH_MOBILE_FEMALE_AGE_EDUCATION','whatsapp账号是否一致&性别&年龄&学历','40',1,3,15,2075,"V1",35,0);


-- flow

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'NON_MANUAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'WHATSAPP_SAME_WITH_MOBILE_FEMALE_AGE_EDUCATION'
    );

-- param

insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
and s.ruleDetailType in (
'WHATSAPP_SAME_WITH_MOBILE_FEMALE_AGE_EDUCATION'
);

commit;

start transaction;

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'WHATSAPP_IN_OVERDUE7_BLACKLIST','申请人的va号码命中逾期7天用户的号码','',1,3,15,2076,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'WHATSAPP_IN_OVERDUE7_EMERGENCY_LINKMAN','申请人的va号码命中逾期7天用户紧急联系人','',1,3,15,2077,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'WHATSAPP_IN_OVERDUE7_CALL_RECORDS','申请人的va号码命中逾期7天用户通话记录','',1,3,15,2078,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'WHATSAPP_IN_OVERDUE7_CONTACTS','申请人的va号码命中逾期7天用户通讯录','',1,3,15,2079,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'WHATSAPP_IN_OVERDUE7_SHORTMESSAGE','申请人的va号码命中逾期7天用户的短信','',1,3,15,2080,"V1",3,0);


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'EMERGENCY_LINKMAN_IN_FRAUD_USER_EMERGENCY_LINKMAN','申请人的紧急联系人号码命中欺诈用户的紧急联系人','',1,3,15,2081,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'EMERGENCY_LINKMAN_IN_FRAUD_USER_CALL_RECORDS','申请人的紧急联系人号码命中欺诈用户的通话记录','',1,3,15,2082,"V1",3,0);


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'EMERGENCY_LINKMAN_IN_FRAUD_USER_CONTACTS','申请人的紧急联系人号码命中欺诈用户的通讯录','',1,3,15,2083,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'EMERGENCY_LINKMAN_IN_FRAUD_USER_SHORTMESSAGE','申请人的紧急联系人号码命中欺诈用户的短信','',1,3,15,2084,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'EMERGENCY_LINKMAN_IN_FRAUD_USER_WHATSAPP','申请人的紧急联系人号码命中欺诈用户的va','',1,3,15,2085,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'COMPANY_TEL_IN_OVERDUE7_BLACKLIST','申请人公司电话命中逾期7天用户的公司电话','',1,3,15,2086,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'COMPANY_ADDRESS_IN_OVERDUE7_BLACKLIST','申请人公司地址命中逾期7天用户的公司地址','',1,3,15,2087,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'HOME_ADDRESS_IN_OVERDUE7_BLACKLIST','申请人居住地址命中逾期7天用户的居住地址','',1,3,15,2088,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'COMPANY_TEL_IN_FRAUD_USER_BLACKLIST','申请人公司电话命中欺诈用户的公司电话','',1,3,15,2089,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'COMPANY_ADDRESS_IN_FRAUD_USER_BLACKLIST','申请人公司地址命中欺诈用户的公司地址','',1,3,15,2090,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'HOME_ADDRESS_IN_FRAUD_USER_BLACKLIST','申请人居住地址命中欺诈用户的居住地址','',1,3,15,2091,"V1",3,0);


insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'LABELING_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.createTime>='2019-01-14 16:27:05'
