
start transaction;
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'COMPANY_TEL_MALE','公司外呼and性别','',2,1,15,2095,"V1",34,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'COMPANY_TEL_MALE_MONTHLY_INCOME','公司外呼and性别and月收入','4000000',2,1,15,2100,"V1",35,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'COMPANY_TEL_MALE_MONTHLY_INCOME_EDUCATION_CHILDREN_COUNT','公司外呼and性别&月收入&学历&孩子数量','3000000#0',2,1,15,2105,"V1",3,0);


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'INSTALLPED_APP_DIFFDAYS_OF_LASTEST_COMMIT_TIME','APP最后一次更新时间距提交的天数','',1,3,15,2110,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'INSTALLPED_APP_DIFFDAYS_OF_EARLIEST_COMMIT_TIME','APP最早一次更新时间距提交的天数','',1,3,15,2111,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'INSTALLPED_APP_DIFFDAYS_OF_EARLIEST_LATEST_TIME','APP最后一次更新时间-最早一次更新时间','',1,3,15,2112,"V1",3,0);


-- flow

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'COMPANY_TEL_MALE',
    'COMPANY_TEL_MALE_MONTHLY_INCOME',
    'COMPANY_TEL_MALE_MONTHLY_INCOME_EDUCATION_CHILDREN_COUNT'

    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_100',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'COMPANY_TEL_MALE_MONTHLY_INCOME_EDUCATION_CHILDREN_COUNT'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'LABELING_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'INSTALLPED_APP_DIFFDAYS_OF_LASTEST_COMMIT_TIME',
    'INSTALLPED_APP_DIFFDAYS_OF_EARLIEST_COMMIT_TIME',
    'INSTALLPED_APP_DIFFDAYS_OF_EARLIEST_LATEST_TIME'
    );

-- param


insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'COMPANY_TEL_MALE',
    'COMPANY_TEL_MALE_MONTHLY_INCOME',
    'COMPANY_TEL_MALE_MONTHLY_INCOME_EDUCATION_CHILDREN_COUNT'

    );

insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
and s.ruleDetailType in (
    'COMPANY_TEL_MALE',
    'COMPANY_TEL_MALE_MONTHLY_INCOME',
    'COMPANY_TEL_MALE_MONTHLY_INCOME_EDUCATION_CHILDREN_COUNT'
);

start transaction;

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'AUTO_CALL_FIRST_UNIVERSAL',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'AUTO_CALL_REJECT_OWNER_CALL_INVALID',
    'AUTO_CALL_REJECT_OWNER_EXCEED_LIMIT',
    'AUTO_CALL_REJECT_LINKMAN_VALID_COUNT'
    );


update flowRuleSet set flowName = 'AUTO_CALL_FIRST_UNIVERSAL'
where flowName = 'AUTO_CALL_FIRST_BORROWING' and
  ruleDetailType in (
      'AUTO_CALL_REJECT_OWNER_CALL_INVALID',
      'AUTO_CALL_REJECT_OWNER_EXCEED_LIMIT',
      'AUTO_CALL_REJECT_LINKMAN_VALID_COUNT'
      )



insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'AUTO_CALL_FIRST_PRODUCT600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'COMPANY_TEL_MALE',
    'COMPANY_TEL_MALE_MONTHLY_INCOME',
    'TOTAL_MEMORY_COMPANY_CALL_BANK_CODE_MALE',
    'IZI_PHONEAGE_PHONEVERIFY_MOBILE_LANGUAGE_COMPANY_CALL'
    );





insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'AUTO_CALL_FIRST_PRODUCT100',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
     'COMPANY_TEL_MALE_MONTHLY_INCOME_EDUCATION_CHILDREN_COUNT'
    );



insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'AUTO_CALL_FIRST_NON_MANUAL',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
     'COMPANY_CALL_IOS_FEMALE',
     'COMPANY_CALL_IOS_MALE_MONTHLY_INCOME'
    );






update flowRuleSet set flowName = 'AUTO_CALL_FIRST_UNIVERSAL'
where flowName = 'AUTO_CALL_FIRST_BORROWING' and
  ruleDetailType in (
      'AUTO_CALL_REJECT_OWNER_CALL_INVALID',
      'AUTO_CALL_REJECT_OWNER_EXCEED_LIMIT',
      'AUTO_CALL_REJECT_LINKMAN_VALID_COUNT'
      );
update flowRuleSet set flowName = 'AUTO_CALL_FIRST_PRODUCT600'
where flowName = 'AUTO_CALL_FIRST_BORROWING' and
  ruleDetailType in (
      'COMPANY_TEL_MALE',
    'COMPANY_TEL_MALE_MONTHLY_INCOME',
    'TOTAL_MEMORY_COMPANY_CALL_BANK_CODE_MALE',
    'IZI_PHONEAGE_PHONEVERIFY_MOBILE_LANGUAGE_COMPANY_CALL'
      );

update flowRuleSet set flowName = 'AUTO_CALL_FIRST_PRODUCT100'
where flowName = 'AUTO_CALL_FIRST_BORROWING' and
  ruleDetailType in (
      'COMPANY_TEL_MALE_MONTHLY_INCOME_EDUCATION_CHILDREN_COUNT'
      )   ;

update flowRuleSet set flowName = 'AUTO_CALL_FIRST_NON_MANUAL'
where flowName = 'AUTO_CALL_FIRST_BORROWING' and
  ruleDetailType in (
      'COMPANY_CALL_IOS_FEMALE',
     'COMPANY_CALL_IOS_MALE_MONTHLY_INCOME'
      )   ;

update ruleParam set flowName = 'AUTO_CALL_FIRST_UNIVERSAL'
where flowName = 'AUTO_CALL_FIRST_BORROWING' and
  ruleDetailType in (
      'AUTO_CALL_REJECT_OWNER_CALL_INVALID',
      'AUTO_CALL_REJECT_OWNER_EXCEED_LIMIT',
      'AUTO_CALL_REJECT_LINKMAN_VALID_COUNT'
      );
update ruleParam set flowName = 'AUTO_CALL_FIRST_PRODUCT600'
where flowName = 'AUTO_CALL_FIRST_BORROWING' and
  ruleDetailType in (
      'COMPANY_TEL_MALE',
    'COMPANY_TEL_MALE_MONTHLY_INCOME',
    'TOTAL_MEMORY_COMPANY_CALL_BANK_CODE_MALE',
    'IZI_PHONEAGE_PHONEVERIFY_MOBILE_LANGUAGE_COMPANY_CALL'
      );

update ruleParam set flowName = 'AUTO_CALL_FIRST_PRODUCT100'
where flowName = 'AUTO_CALL_FIRST_BORROWING' and
  ruleDetailType in (
      'COMPANY_TEL_MALE_MONTHLY_INCOME_EDUCATION_CHILDREN_COUNT'
      )   ;

update ruleParam set flowName = 'AUTO_CALL_FIRST_NON_MANUAL'
where flowName = 'AUTO_CALL_FIRST_BORROWING' and
  ruleDetailType in (
      'COMPANY_CALL_IOS_FEMALE',
     'COMPANY_CALL_IOS_MALE_MONTHLY_INCOME'
      )




-- param


insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
and s.ruleDetailType in (
    'COMPANY_TEL_MALE',
    'COMPANY_TEL_MALE_MONTHLY_INCOME',
    'COMPANY_TEL_MALE_MONTHLY_INCOME_EDUCATION_CHILDREN_COUNT',
    'AUTO_CALL_REJECT_OWNER_CALL_INVALID',
    'AUTO_CALL_REJECT_OWNER_EXCEED_LIMIT',
    'AUTO_CALL_REJECT_LINKMAN_VALID_COUNT',
    'TOTAL_MEMORY_COMPANY_CALL_BANK_CODE_MALE',
    'COMPANY_CALL_IOS_FEMALE',
    'COMPANY_CALL_IOS_MALE_MONTHLY_INCOME',
    'IZI_PHONEAGE_PHONEVERIFY_MOBILE_LANGUAGE_COMPANY_CALL'
);