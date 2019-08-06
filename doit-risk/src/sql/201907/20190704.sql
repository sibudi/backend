

update ruleParam set thresholdValue = '725' where ruleDetailType = 'MODEL_SCORE_FOR_PRD100' and disabled=0;

update ruleParam set thresholdValue = '30' where ruleDetailType = 'SEX_GOJEKVERIFY_IZIPHONEVERIFY' and disabled=0;

update ruleParam set thresholdValue = '3' where ruleDetailType = 'APP_COUNT_FOR_CREDIT_CARD_PRD100' and disabled=0;

update ruleParam set thresholdValue = '4#1' where ruleDetailType = 'SEX_IZIPHONEVERIFY_IZIPHONEAGE' and disabled=0;

update ruleParam set thresholdValue = '1#6000000' where ruleDetailType = 'MALE_ECOMMERCEAPPCOUNT_SALARY_GOJEKVERIFY' and disabled=0;

update ruleParam set thresholdValue = '30#30' where ruleDetailType = 'MALE_EDUCATION_HASDRIVER_LICENSE_AGE' and disabled=0;

update sysAutoReviewRule set ruleValue = '725' where ruleDetailType = 'MODEL_SCORE_FOR_PRD100' and disabled=0;

update sysAutoReviewRule set ruleValue = '30' where ruleDetailType = 'SEX_GOJEKVERIFY_IZIPHONEVERIFY' and disabled=0;

update sysAutoReviewRule set ruleValue = '3' where ruleDetailType = 'APP_COUNT_FOR_CREDIT_CARD_PRD100' and disabled=0;

update sysAutoReviewRule set ruleValue =  '4#1' where ruleDetailType = 'SEX_IZIPHONEVERIFY_IZIPHONEAGE' and disabled=0;

update sysAutoReviewRule set ruleValue =  '1#6000000' where ruleDetailType = 'MALE_ECOMMERCEAPPCOUNT_SALARY_GOJEKVERIFY' and disabled=0;

update sysAutoReviewRule set ruleValue =  '30#30' where ruleDetailType = 'MALE_EDUCATION_HASDRIVER_LICENSE_AGE' and disabled=0;


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEX_CREDITCARD_APPCOUNT_IZI_PHONEAGE','female&appCountForCreditCard&iziPhoneAge','1#1#3',
       2,1,15,2800,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEX_ECOMMERCE_APPCOUNT_IZI_PHONEVERIFY_MODEL_SCORE_100','female&appCountForEcommerce&iziPhoneVerify&100ModelScore','0#710',
       2,1,15,2805,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEX_SENSITIVE_APPCOUNT_MODEL_SCORE_600_V2','female&appCountForLoan&600ModelScoreV2','0#465',
       2,1,15,2810,
       "V1", 3,0);



insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_100',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'SEX_CREDITCARD_APPCOUNT_IZI_PHONEAGE'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_100',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'SEX_ECOMMERCE_APPCOUNT_IZI_PHONEVERIFY_MODEL_SCORE_100'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_100',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'SEX_SENSITIVE_APPCOUNT_MODEL_SCORE_600_V2'
    );


insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
  and s.ruleDetailType in (
       'SEX_CREDITCARD_APPCOUNT_IZI_PHONEAGE',
       'SEX_ECOMMERCE_APPCOUNT_IZI_PHONEVERIFY_MODEL_SCORE_100',
       'SEX_SENSITIVE_APPCOUNT_MODEL_SCORE_600_V2'
    );

commit;