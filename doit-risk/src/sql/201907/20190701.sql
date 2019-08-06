start transaction;


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SMALL_DIRECT_MODEL_SCORE_V2_MALE','order_small_direct_empty&product60ScoreModelV2&male','465',2,1,15,2585,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SMALL_DIRECT_MODEL_SCORE_V2_FEMALE','order_small_direct_empty&product600ScoreModelV2&female','490',2,1,15,2590,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'FEMALE_OWNER_WHATSAPP_CREDITCARD_APPCOUNT','female&owner_whats_app&appCountForCreditCard','0',2,1,15,2595,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'FEMALE_OWNER_WHATSAPP_ECOMMERCE_APPCOUNT','female&owner_whats_app&appCountForEcommerce','2',2,1,15,2600,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'MALE_MODEL_SCORE_V2','product600ScoreModelV2&male','510',1,3,15,2605,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'FEMALE_MODEL_SCORE_V2','product600ScoreModelV2&female','515',1,3,15,2610,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'GOJEK_VERIFIED','gojekVerified','',1,3,15,2615,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'OWNER_WHATSAPP_MODEL_SCORE_V2','product600ScoreModelV2&ownerWhatsAppOpen','505',1,3,15,2620,
       "V1", 34,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'FEMALE_MODEL_SCORE_V2_TICKET_APPCOUNT_GOJEKVERIFIED','female&product600ScoreModelV2&appCountForTicket&gojekNotVerified','445#465#2',2,1,15,
       2625,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'FEMALE_MODEL_SCORE_V2_TICKET_APPCOUNT_GOJEKVERIFIED_EDUCATION','female&product600ScoreModelV2&appCountForTicket&gojekVerified&education','445#465#0',
       2,1,15,2630,
       "V1", 3,0);




insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'SMALL_DIRECT_MODEL_SCORE_V2_MALE'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'SMALL_DIRECT_MODEL_SCORE_V2_FEMALE'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'FEMALE_OWNER_WHATSAPP_CREDITCARD_APPCOUNT'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'FEMALE_OWNER_WHATSAPP_ECOMMERCE_APPCOUNT'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'FEMALE_MODEL_SCORE_V2_TICKET_APPCOUNT_GOJEKVERIFIED'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'FEMALE_MODEL_SCORE_V2_TICKET_APPCOUNT_GOJEKVERIFIED_EDUCATION'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'NON_MANUAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MALE_MODEL_SCORE_V2'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'NON_MANUAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'FEMALE_MODEL_SCORE_V2'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'NON_MANUAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'GOJEK_VERIFIED'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'NON_MANUAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'OWNER_WHATSAPP_MODEL_SCORE_V2'
    );



insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
  and s.ruleDetailType in (
       'SMALL_DIRECT_MODEL_SCORE_V2_MALE',
    'SMALL_DIRECT_MODEL_SCORE_V2_FEMALE',
    'FEMALE_OWNER_WHATSAPP_CREDITCARD_APPCOUNT',
    'FEMALE_OWNER_WHATSAPP_ECOMMERCE_APPCOUNT',
    'MALE_MODEL_SCORE_V2',
    'FEMALE_MODEL_SCORE_V2',
    'GOJEK_VERIFIED',
    'OWNER_WHATSAPP_MODEL_SCORE_V2',
    'FEMALE_MODEL_SCORE_V2_TICKET_APPCOUNT_GOJEKVERIFIED',
    'FEMALE_MODEL_SCORE_V2_TICKET_APPCOUNT_GOJEKVERIFIED_EDUCATION'
    );


update  flowRuleSet s set s.flowName='FRAUD_UNIVERSAL_RULE',updateTime=now() where s.ruleDetailType in ('SAME_BANK_CARD_COUNT',
'EMERGENCY_TEL_IN_MULTI_USER_BANK_CARD');



update ruleParam s  set s.flowName='FRAUD_UNIVERSAL_RULE',updateTime=now() where s.ruleDetailType in ('SAME_BANK_CARD_COUNT',
'EMERGENCY_TEL_IN_MULTI_USER_BANK_CARD');


update sysAutoReviewRule set disabled=1,updateTime=now() where disabled=0 and ruleDetailType = 'RISK_SCORE_600_V2_FEMALE_IZI_PHONEVERIFY';

update ruleParam s set s.thresholdValue = 445,updateTime=now() where ruleDetailType = 'RISK_SCORE_600_V2_FEMALE';
update sysAutoReviewRule s set s.ruleValue = 445,updateTime=now() where ruleDetailType = 'RISK_SCORE_600_V2_FEMALE';






