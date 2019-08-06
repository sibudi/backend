update sysAutoReviewRule s set s.disabled=1 where ruleDetailType in (
'MOBILE_IN_OVERDUE15_BLACK_LIST_USER_EMERGENCY_TEL',
'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_OVERDUE15_BLACKLIST',
'EMERGENCY_TEL_IN_OVERDUE15_BLACKLIST',
'MOBILE_IS_EMERGENCYTEL_FOR_1THORDER_OVERDUE7_USER',
'EMERGENCYTEL_IS_1THORDER_OVERDUE7_USER',
'EMERGENCYTEL_IS_EMERGENCYTEL_FOR_1THORDER_OVERDUE7_USER',
'EMERGENCYTEL_IS_2THORDER_OVERDUE7_USER',
'EMERGENCYTEL_IS_EMERGENCYTEL_FOR_2THORDER_OVERDUE7_USER',
'MOBILE_IS_EMERGENCYTEL_FOR_2THORDER_OVERDUE7_USER',
'EMERGENCYTEL_IS_EMERGENCYTEL_FOR_3THOR4THORDER_OVERDUE7_USER',
'WHATSAPP_CONNECT_MALE'
) and disabled=0;

update sysAutoReviewRule s set s.ruleResult=1,s.ruleStatus =3 where ruleDetailType in (
'MALE_AGE_CHILDREN_COUNT_EDUCATION',
'SAME_WORK_ADDRESS_AND_ORDER_ADDRESS_EXISTS'
) and disabled=0;


start transaction;


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'MOBILE_IS_EMERGENCY_TEL_FOR_1TH_UNSETTLED_OVERDUE7USER','申请人手机号是(首借逾期7天及以上并且当前逾期未还)用户的联系人号码','',2,1,15,2490, "V1", 3,0);


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'EMERGENCY_TEL_IS_OWNER_FOR_1TH_UNSETTLED_OVERDUE7USER','申请人联系人号码是(首借逾期7天及以上并且当前逾期未还用户)的本人号码','',2,1,15,2491, "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_1TH_UNSETTLED_OVERDUE7USER','申请人联系人号码是(首借逾期7天及以上并且当前逾期未还用户)的联系人号码','',2,1,15,2492, "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'EMERGENCY_TEL_IS_OWNER_FOR_2TH_UNSETTLED_OVERDUE7USER','申请人联系人号码是(第2笔逾期7天及以上并且当前逾期未还用户)的本人号码','',2,1,15,2493, "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_2TH_UNSETTLED_OVERDUE7USER','申请人联系人号码是(第2笔逾期7天及以上并且当前逾期未还用户)的联系人号码','',2,1,15,2494, "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'MOBILE_IS_EMERGENCY_TEL_FOR_2TH_UNSETTLED_OVERDUE7USER','申请人手机号是(第2笔逾期7天及以上用户并且当前逾期未还用户)的联系人号码','',2,1,15,2495, "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_3OR4TH_UNSETTLED_OVERDUE7USER','申请人联系人号码是(第3/4笔逾期7天及以上并且当前逾期未还用户)的联系人号码','',1,3,15,2496, "V1", 3,0);




insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'MOBILE_IS_EMERGENCY_TEL_FOR_1TH_SETTLED_OVERDUE7USER','申请人手机号是(首借逾期7天及以上并且当前逾期已还)用户的联系人号码','',1,3,15,2497, "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'EMERGENCY_TEL_IS_OWNER_FOR_1TH_SETTLED_OVERDUE7USER','申请人联系人号码是(首借逾期7天及以上并且当前逾期已还用户)的本人号码','',1,3,15,2498, "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_1TH_SETTLED_OVERDUE7USER','申请人联系人号码是(首借逾期7天及以上并且当前逾期已还用户)的联系人号码','',1,3,15,2499, "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'EMERGENCY_TEL_IS_OWNER_FOR_2TH_SETTLED_OVERDUE7USER','申请人联系人号码是(第2笔逾期7天及以上并且当前逾期已还用户)的本人号码','',1,3,15,2500, "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_2TH_SETTLED_OVERDUE7USER','申请人联系人号码是(第2笔逾期7天及以上并且当前逾期已还用户)的联系人号码','',1,3,15,2501, "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'MOBILE_IS_EMERGENCY_TEL_FOR_2TH_SETTLED_OVERDUE7USER','申请人手机号是(第2笔逾期7天及以上用户并且当前逾期已还用户)的联系人号码','',1,3,15,2502, "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
       'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_3OR4TH_SETTLED_OVERDUE7USER','申请人联系人号码是(第3/4笔逾期7天及以上并且当前逾期已还用户)的联系人号码','',1,3,15,2503, "V1", 3,0);


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
       'MALE_OWNER_IZIWHATSAPP_IZIPHONEVERIFY','男性&本人手机号未开通whatsapp&IZI手机实名','',2,1,15,2505, "V1", 3,0);





insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'FRAUD_UNIVERSAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MOBILE_IS_EMERGENCY_TEL_FOR_1TH_UNSETTLED_OVERDUE7USER'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MALE_OWNER_IZIWHATSAPP_IZIPHONEVERIFY'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'FRAUD_UNIVERSAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'EMERGENCY_TEL_IS_OWNER_FOR_1TH_UNSETTLED_OVERDUE7USER'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'FRAUD_UNIVERSAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_1TH_UNSETTLED_OVERDUE7USER'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'EMERGENCY_TEL_IS_OWNER_FOR_2TH_UNSETTLED_OVERDUE7USER'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_2TH_UNSETTLED_OVERDUE7USER'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MOBILE_IS_EMERGENCY_TEL_FOR_2TH_UNSETTLED_OVERDUE7USER'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'LABELING_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_3OR4TH_UNSETTLED_OVERDUE7USER'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'LABELING_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MOBILE_IS_EMERGENCY_TEL_FOR_1TH_SETTLED_OVERDUE7USER'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'LABELING_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'EMERGENCY_TEL_IS_OWNER_FOR_1TH_SETTLED_OVERDUE7USER'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'LABELING_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_1TH_SETTLED_OVERDUE7USER'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'LABELING_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'EMERGENCY_TEL_IS_OWNER_FOR_2TH_SETTLED_OVERDUE7USER'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'LABELING_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_2TH_SETTLED_OVERDUE7USER'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'LABELING_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MOBILE_IS_EMERGENCY_TEL_FOR_2TH_SETTLED_OVERDUE7USER'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'LABELING_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_3OR4TH_SETTLED_OVERDUE7USER'
    );




insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
  and s.ruleDetailType in (
       'MOBILE_IS_EMERGENCY_TEL_FOR_1TH_UNSETTLED_OVERDUE7USER',
    'EMERGENCY_TEL_IS_OWNER_FOR_1TH_UNSETTLED_OVERDUE7USER',
    'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_1TH_UNSETTLED_OVERDUE7USER',
    'EMERGENCY_TEL_IS_OWNER_FOR_2TH_UNSETTLED_OVERDUE7USER',
    'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_2TH_UNSETTLED_OVERDUE7USER',
    'MOBILE_IS_EMERGENCY_TEL_FOR_2TH_UNSETTLED_OVERDUE7USER',
    'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_3OR4TH_UNSETTLED_OVERDUE7USER',
    'MOBILE_IS_EMERGENCY_TEL_FOR_1TH_SETTLED_OVERDUE7USER',
    'EMERGENCY_TEL_IS_OWNER_FOR_1TH_SETTLED_OVERDUE7USER',
    'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_1TH_SETTLED_OVERDUE7USER',
    'EMERGENCY_TEL_IS_OWNER_FOR_2TH_SETTLED_OVERDUE7USER',
    'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_2TH_SETTLED_OVERDUE7USER',
    'MOBILE_IS_EMERGENCY_TEL_FOR_2TH_SETTLED_OVERDUE7USER',
    'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_3OR4TH_SETTLED_OVERDUE7USER',
    'MALE_OWNER_IZIWHATSAPP_IZIPHONEVERIFY'
    );



commit;