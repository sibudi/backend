start transaction;


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
       'MULTI_LAST_LOAN_AMOUNT_OVERDUE_CURRENT_AMOUNT_BORROWING_COUNT','last_loan_amount&current_loan_amount&last_loan_overdue_borrowing_count',
       '160000#160000#0#3',
       2,1,15,2635,
       "V1", 3,0);


commit;


start transaction;


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'NO_PRODUCT_50_EXTEND_RULE_HIT','no additional rules hited for product50','',2,1,15,2640, "V1", 3,0);



insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'PRD50_SEX_GOJEKVERIFIED_HAS_SALARY_PIC_LOAN_APPCOUNT','male&gojekVerified&hasSalaryPic&loanAppCount','1',1,3,15,2645, "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'PRD50_CREDITCARD_APPCOUNT','creditCardAppCount','2',1,3,15,2650, "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'PRD50_SEX_ECOMMERCE_APPCOUNT_CREDITCARD_APPCOUNT_GOJEKVERIFIED','male&ecommerceAppCount&creditCardAppCount&gojekVerified','1#1',1,3,15,2655,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'PRD50_SEX_IZIPHONEVERIFY_IZIPHONEAGE','female&iziPhoneVerify&iziPhoneAge','5',1,3,15,2660, "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'PRD50_SEX_PRODUCT100_MODELSCORE','male&product600ModelScore','710',1,3,15,2665, "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'PRD50_PRODUCT600V2_MODELSCORE','product600ModelScoreV2','495',1,3,15,2670, "V1", 3,0);


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'PRD50_SEX_PRODUCT600V1_MODELSCORE','male&product600ModelScoreV1','490',1,3,15,2675, "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'PRD50_SEX_IZIPHONEVERIFY_IZIPHONEAGE_HAS_DRIVING_LICENSE_APPCOUNT_LOAN_ECOMMERCE',
       'male&iziPhoneVerify&iziPhoneAge&driverLicense&loanAppCount&eCommerceAppCount','4#1#1',1,3,
       15,2680, "V1", 3,0);


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'PRD50_SEX_PRODUCT100_MODELSCORE_CREDITCARD_APPCOUNT_HAS_SALARY_PIC','male&product100ModelScore&creditCardAppCount&salaryPic','690#710#1',1,
       3,15,2685, "V1",
       3,0);


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'PRD50_SEX_EDUCATION_INCOME_HAS_TAXCARD','male&education&monthlyIncome&hasTaxNumber','7000000',1,3,15,2690, "V1", 3,0);



insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'PRD50_SEX_ECOMMERCE_APPCOUNT_IZI_PHONEAGE','male&ecommerceAppCount&iziPhoneAge','4#4',1,3,15,2695, "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'PRD50_HIT_NON_MANUAL_RULES','hit nonManualRules for prd50','',1,3,15,2700, "V1", 3,0);





insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_50_EXTEND',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'NO_PRODUCT_50_EXTEND_RULE_HIT',
    'PRD50_SEX_GOJEKVERIFIED_HAS_SALARY_PIC_LOAN_APPCOUNT',
    'PRD50_CREDITCARD_APPCOUNT',
    'PRD50_SEX_ECOMMERCE_APPCOUNT_CREDITCARD_APPCOUNT_GOJEKVERIFIED',
    'PRD50_SEX_IZIPHONEVERIFY_IZIPHONEAGE',
    'PRD50_SEX_PRODUCT100_MODELSCORE',
    'PRD50_PRODUCT600V2_MODELSCORE',
    'PRD50_SEX_PRODUCT600V1_MODELSCORE',
    'PRD50_SEX_IZIPHONEVERIFY_IZIPHONEAGE_HAS_DRIVING_LICENSE_APPCOUNT_LOAN_ECOMMERCE',
    'PRD50_SEX_PRODUCT100_MODELSCORE_CREDITCARD_APPCOUNT_HAS_SALARY_PIC',
    'PRD50_SEX_EDUCATION_INCOME_HAS_TAXCARD',
    'PRD50_SEX_ECOMMERCE_APPCOUNT_IZI_PHONEAGE',
    'PRD50_HIT_NON_MANUAL_RULES'
    );



insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
  and s.ruleDetailType in (
  'NO_PRODUCT_50_EXTEND_RULE_HIT',
    'PRD50_SEX_GOJEKVERIFIED_HAS_SALARY_PIC_LOAN_APPCOUNT',
    'PRD50_CREDITCARD_APPCOUNT',
    'PRD50_SEX_ECOMMERCE_APPCOUNT_CREDITCARD_APPCOUNT_GOJEKVERIFIED',
    'PRD50_SEX_IZIPHONEVERIFY_IZIPHONEAGE',
    'PRD50_SEX_PRODUCT100_MODELSCORE',
    'PRD50_PRODUCT600V2_MODELSCORE',
    'PRD50_SEX_PRODUCT600V1_MODELSCORE',
    'PRD50_SEX_IZIPHONEVERIFY_IZIPHONEAGE_HAS_DRIVING_LICENSE_APPCOUNT_LOAN_ECOMMERCE',
    'PRD50_SEX_PRODUCT100_MODELSCORE_CREDITCARD_APPCOUNT_HAS_SALARY_PIC',
    'PRD50_SEX_EDUCATION_INCOME_HAS_TAXCARD',
    'PRD50_SEX_ECOMMERCE_APPCOUNT_IZI_PHONEAGE',
    'PRD50_HIT_NON_MANUAL_RULES'
    );


commit;