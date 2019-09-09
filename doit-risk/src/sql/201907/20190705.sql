-- 历史复借规则迁移到RE_BORROWING_UNIVERSAL

start transaction;

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'RE_BORROWING_UNIVERSAL',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled=0 and (s.ruleDetailType like 'MULTI%' or s.ruleDetailType like '%REBORROW%')
    and s.ruleDetailType not in ('MULTI_AUTO_CALL_REJECT_LINKMAN_VALID_COUNT','MULTI_AUTO_CALL_REJECT_BACKUP_LINKMAN_VALID_COUNT');


insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled=0 and (s.ruleDetailType like 'MULTI%' or s.ruleDetailType like '%REBORROW%')
    and s.ruleDetailType not in ('MULTI_AUTO_CALL_REJECT_LINKMAN_VALID_COUNT','MULTI_AUTO_CALL_REJECT_BACKUP_LINKMAN_VALID_COUNT');



-- 新的600extend规则


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEX_OVERDUEDAYS','男&提前还款天数(0,28]','0#-28',
       1,3,15,3001,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEX_REPYATODAY_COLLEAGE_MODELV1','男&当天还款&学历大专及以上&模型分V1(490,505]&月收入>=4000','490#505#8000000',
       1,3,15,3002,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEX_REPAYTODAY_LASTGETPAY_WANOTSAMEWITHPHONE','男&当天还款&上一笔第一次催收到还款<1h&WA账号与手机号不一致','1',
       1,3,15,3003,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEX_REPAYTODAY_NOTCHECK_HASDRIVERLICENSE_MONTHINCOME_AGE','男&当天还款&免审核&有驾驶证&月收入>=5000&年龄>=35','10000000#35',
       1,3,15,3004,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEX_REPAYTODAY_NOCOLLECTION_HASDIRVER_AGE','男&当天还款&首借未催收&有驾驶证&年龄>=35','35',
       1,3,15,3005,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEX_REPAYTODAY_NOCOLLECTION_NOTCHECKGOJEK_AGE','男&当天还款&首借未催收&未验证gojek&年龄>=35','35',
       1,3,15,3006,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEX_REPAYTODAY_NOCOLLECTION_CHECKGOJEK_AGE','男&当天还款&首借未催收&验证gojek&年龄>=30','30',
       1,3,15,3007,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEX_REPAYTODAY_NOCOLLECTION_NOTCHECK','男&当天还款&首借未催收&免审核','',
       1,3,15,3008,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEX_REPAYTODAY_NOCOLLECTION_PERSONCHECK_EDUCATION','男&当天还款&首借未催收&人工审核&大专及以上','',
       1,3,15,3009,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEX_REPAYTODAY_AGREEPHONEANDWA_NOTCHECK_INTERVALDAYS','男&当天还款&WA与手机号一致&免审核&借款间隔天数in(3,5)','3#5',
       1,3,15,3010,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEX_REPAYTODAY_EDUCATION_AGE_HASDIRVIER_MEMORY','男&当天还款&本科&年龄[40,45)&有驾驶证&手机总内存>=3GB','40#45#3',
       1,3,15,3011,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEX_REPAYTODAY_BRAND','男&当天还款&手机品牌in(Apple,Honor,Sony,lge)','',
       1,3,15,3012,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEX_REPAYTODAY_CHECKGOJIEK_INTERVALDAYS','男&当天还款&验证gojek&借款间隔天数>4','4',
       1,3,15,3013,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEX_REPAYBEFORE','女&提前还款天数(0,25]','0#-25',
       1,3,15,3014,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEX_REPAYTODAY_INTERVALDAYS','女&当天还款&借款间隔天数>5','5',
       1,3,15,3015,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEXMEMAL_REPAYTODAY_NOTCHECK_HASDRIVER_MONTHINCOME','女&当天还款&免审核&有驾驶证&月收入>=5000','10000000',
       1,3,15,3016,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEXMEMAL_REPAYTODAY_NOCHECK_LASTREPAYTIME_INTERVALDAYS','女&当天还款&免审核&上一笔催收到还款<=3h&借款间隔天数=1','3#1',
       1,3,15,3017,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEXMEMAL_REPAYTODAY_NOCOLLECTION_INTERVALDAYS','女&当天还款&首借未催收&借款间隔天数(0,3]','0#3',
       1,3,15,3018,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEXMEMAL_REPAYTODAY_NOCOLLECTION_HASDIRVIVER_MEMORY','女&当天还款&免审核&有驾驶证&总容量>=55GB','55',
       1,3,15,3019,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEXMEMAL_REPAYTODAY_NOCOLLECTION_WAAGGRENPHONE_GOJEK_NODIRVIER_AGE','女&当天还款&免审核&首借未催收&WA账号与手机号一致&gojek&没有驾驶证&年龄[25,45)','55',
       1,3,15,3020,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEXMEMAL_REPAYTODAY_NOCOLLECTION_NOTDIRCER_AGE','女&当天还款&首借未催收&没有驾驶证&年龄>=40或[30,35)','40#30#35',
       1,3,15,3021,
       "V1", 3,0);


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEXMEMAL_REPAYTODAY_NOTCHECK_HASDRIVER_HASCIRECARD','女&当天还款&免审核&有驾驶证&有信用卡','',
       1,3,15,3022,
       "V1", 3,0);


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEXMEMAL_REPAYTODAY_WAAGGREMENTPHONE_NOTCHECK_INTERVDAYS','女&当天还款&WA与手机号一致&免审核&借款间隔天数=4','4',
       1,3,15,3023,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEXMEMAL_REPAYTODAY_COMPANYPASS_HASCARD_NOTGOJEK','女&当天还款&公司通过&有信用卡&未验证gojek','',
       1,3,15,3024,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEXMEMAL_REPAYTODAY_PHONEBRAND','女&当天还款&手机品牌in(Apple,Honor,Sony)','',
       1,3,15,3025,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEXMEMAL_REPAYTODAY_WAAGGREMENTPHONE_HASDIRVERS_NOSALARY_APPCOUNT','女&当天还款&WA与手机号一致&有驾驶证&没有工资单&票务类APP个数=2','2',
       1,3,15,3026,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'SEXMEMAL_REPAYTODAY_AGE_ALLAPPCOUNT','女&当天还款&年龄[35,40)&首借累计APP<10','35#40#10',
       1,3,15,3027,
       "V1", 3,0);


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'NO_REBORROWING_PRODUCT_600_EXTEND_RULE_HIT','not hit extend rule for reBorrowing product600',
       '',2,1,15,3101,"V1", 3,0);

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'RE_BORROWING_PRD600_EXTEND',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled=0 and s.ruleDetailType in (
    'SEX_OVERDUEDAYS',
    'SEX_REPYATODAY_COLLEAGE_MODELV1',
    'SEX_REPAYTODAY_LASTGETPAY_WANOTSAMEWITHPHONE',
    'SEX_REPAYTODAY_NOTCHECK_HASDRIVERLICENSE_MONTHINCOME_AGE',
    'SEX_REPAYTODAY_NOCOLLECTION_HASDIRVER_AGE',
    'SEX_REPAYTODAY_NOCOLLECTION_NOTCHECKGOJEK_AGE',
    'SEX_REPAYTODAY_NOCOLLECTION_CHECKGOJEK_AGE',
    'SEX_REPAYTODAY_NOCOLLECTION_NOTCHECK',
    'SEX_REPAYTODAY_NOCOLLECTION_PERSONCHECK_EDUCATION',
    'SEX_REPAYTODAY_AGREEPHONEANDWA_NOTCHECK_INTERVALDAYS',
    'SEX_REPAYTODAY_EDUCATION_AGE_HASDIRVIER_MEMORY',
    'SEX_REPAYTODAY_BRAND',
    'SEX_REPAYTODAY_CHECKGOJIEK_INTERVALDAYS',
    'SEX_REPAYBEFORE',
    'SEX_REPAYTODAY_INTERVALDAYS',
    'SEXMEMAL_REPAYTODAY_NOTCHECK_HASDRIVER_MONTHINCOME',
    'SEXMEMAL_REPAYTODAY_NOCHECK_LASTREPAYTIME_INTERVALDAYS',
    'SEXMEMAL_REPAYTODAY_NOCOLLECTION_INTERVALDAYS',
    'SEXMEMAL_REPAYTODAY_NOCOLLECTION_HASDIRVIVER_MEMORY',
    'SEXMEMAL_REPAYTODAY_NOCOLLECTION_WAAGGRENPHONE_GOJEK_NODIRVIER_AGE',
    'SEXMEMAL_REPAYTODAY_NOCOLLECTION_NOTDIRCER_AGE',
    'SEXMEMAL_REPAYTODAY_NOTCHECK_HASDRIVER_HASCIRECARD',
    'SEXMEMAL_REPAYTODAY_WAAGGREMENTPHONE_NOTCHECK_INTERVDAYS',
    'SEXMEMAL_REPAYTODAY_COMPANYPASS_HASCARD_NOTGOJEK',
    'SEXMEMAL_REPAYTODAY_PHONEBRAND',
    'SEXMEMAL_REPAYTODAY_WAAGGREMENTPHONE_HASDIRVERS_NOSALARY_APPCOUNT',
    'SEXMEMAL_REPAYTODAY_AGE_ALLAPPCOUNT',
    'NO_REBORROWING_PRODUCT_600_EXTEND_RULE_HIT'
       );


insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled=0 and s.ruleDetailType in (
    'SEX_OVERDUEDAYS',
    'SEX_REPYATODAY_COLLEAGE_MODELV1',
    'SEX_REPAYTODAY_LASTGETPAY_WANOTSAMEWITHPHONE',
    'SEX_REPAYTODAY_NOTCHECK_HASDRIVERLICENSE_MONTHINCOME_AGE',
    'SEX_REPAYTODAY_NOCOLLECTION_HASDIRVER_AGE',
    'SEX_REPAYTODAY_NOCOLLECTION_NOTCHECKGOJEK_AGE',
    'SEX_REPAYTODAY_NOCOLLECTION_CHECKGOJEK_AGE',
    'SEX_REPAYTODAY_NOCOLLECTION_NOTCHECK',
    'SEX_REPAYTODAY_NOCOLLECTION_PERSONCHECK_EDUCATION',
    'SEX_REPAYTODAY_AGREEPHONEANDWA_NOTCHECK_INTERVALDAYS',
    'SEX_REPAYTODAY_EDUCATION_AGE_HASDIRVIER_MEMORY',
    'SEX_REPAYTODAY_BRAND',
    'SEX_REPAYTODAY_CHECKGOJIEK_INTERVALDAYS',
    'SEX_REPAYBEFORE',
    'SEX_REPAYTODAY_INTERVALDAYS',
    'SEXMEMAL_REPAYTODAY_NOTCHECK_HASDRIVER_MONTHINCOME',
    'SEXMEMAL_REPAYTODAY_NOCHECK_LASTREPAYTIME_INTERVALDAYS',
    'SEXMEMAL_REPAYTODAY_NOCOLLECTION_INTERVALDAYS',
    'SEXMEMAL_REPAYTODAY_NOCOLLECTION_HASDIRVIVER_MEMORY',
    'SEXMEMAL_REPAYTODAY_NOCOLLECTION_WAAGGRENPHONE_GOJEK_NODIRVIER_AGE',
    'SEXMEMAL_REPAYTODAY_NOCOLLECTION_NOTDIRCER_AGE',
    'SEXMEMAL_REPAYTODAY_NOTCHECK_HASDRIVER_HASCIRECARD',
    'SEXMEMAL_REPAYTODAY_WAAGGREMENTPHONE_NOTCHECK_INTERVDAYS',
    'SEXMEMAL_REPAYTODAY_COMPANYPASS_HASCARD_NOTGOJEK',
    'SEXMEMAL_REPAYTODAY_PHONEBRAND',
    'SEXMEMAL_REPAYTODAY_WAAGGREMENTPHONE_HASDIRVERS_NOSALARY_APPCOUNT',
    'SEXMEMAL_REPAYTODAY_AGE_ALLAPPCOUNT',
    'NO_REBORROWING_PRODUCT_600_EXTEND_RULE_HIT'
       )
;


--新的400 extend规则


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MALE_OVERDUEDAYS_HAS_DRIVERLICENSE_LASTLOAN_NOCOLLECTION','男&当天还款&有驾驶证&首借未被催收','0',1,3,15,3101,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MALE_OVERDUEDAYS_HAS_DRIVERLICENSE_APPLY_INTERVALDAYS','男&当天还款&有驾驶证&借款间隔天数','0#3',1,3,15,3102,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MALE_OVERDUEDAYS_NO_DRIVERLICENSE_APPLY_INTERVALDAYS','男&当天还款&没有驾驶证&借款间隔天数','0#2#4',1,3,15,3103,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MALE_OVERDUEDAYS_MONTHLY_INCOME','男&当天还款&月收入','0#10000000',1,3,15,3104,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MALE_OVERDUEDAYS_MONTHLY_INCOME_DIFFHOURSOFCOLLECTION_AND_REFUND_AGE','男&当天还款&月收入[2000,5000)&上一笔第一次催收到还款<1h&年龄[25,35)',
       '0#4000000#10000000#1#25#35',1,3,15,3105,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MALE_OVERDUEDAYS_MONTHLY_INCOME_DIFFHOURSOFCOLLECTION_AND_REFUND_AGE_HAS_DRIVERLICENSE','男&当天还款&月收入[2000,5000)&有驾驶证&上一笔第一次催收到还款<1h&年龄[40,45)',
       '0#4000000#10000000#1#40#45',1,3,15,3106,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MALE_OVERDUEDAYS_HAS_DRIVERLICENSE_RELIGION','男&当天还款&有驾驶证&宗教in(天主教,印度教)',
       '0',1,3,15,3107,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MALE_OVERDUEDAYS_HAS_DRIVERLICENSE_POSITION','男&当天还款&有驾驶证&职业in(个体供应商,医务人员,工程师,老师)',
       '0',1,3,15,3108,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MALE_OVERDUEDAYS_LOANPASSTYPE_GOJEVERIFIED_SAMEOF_WHATSAPPANDMOBILE','男&当天还款&免审核&验证gojek&WA与手机号不一致',
       '0#1',1,3,15,3109,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MALE_OVERDUEDAYS_LOANPASSTYPE_SAMEOF_WHATSAPPANDMOBILE_HASPASSPORT_AGE','男&当天还款&免审核&WA与手机号一致&有护照&年龄[30,45)',
       '0#1#30#45',1,3,15,3110,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MALE_OVERDUEDAYS_SAMEOF_WHATSAPPANDMOBILE_HASPASSPORT_AGE','男&当天还款&WA与手机号一致&有护照&年龄[30,35)',
       '0#30#35',1,3,15,3111,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MALE_OVERDUEDAYS_HASPASSPORT_TOTAL_SPACE','男&当天还款&没有护照&总容量>=60GB',
       '0#60',1,3,15,3112,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'FEMALE_OVERDUEDAYS_HASDRIVERLICENSE_GOJEKVERIFIED','女&当天还款&有驾驶证&验证gojek',
       '0',1,3,15,3113,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'FEMALE_OVERDUEDAYS_HASDRIVERLICENSE_APPLY_INTERVALDAYS','女&当天还款&有驾驶证&借款间隔天数',
       '0#1',1,3,15,3113,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'FEMALE_OVERDUEDAYS_HASDRIVERLICENSE_EDUCATION','女&当天还款&有驾驶证&大专及以上',
       '0',1,3,15,3114,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'FEMALE_OVERDUEDAYS_HASDRIVERLICENSE_MONTHLY_INCOME','女&当天还款&没有驾驶证&月收入',
       '0#10000000',1,3,15,3115,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'FEMALE_OVERDUEDAYS_HASDRIVERLICENSE_MOBILE_LANGUAGE','女&当天还款&有驾驶证&手机语言',
       '0#en',1,3,15,3116,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'FEMALE_OVERDUEDAYS_HASDRIVERLICENSE_AGE','女&当天还款&有驾驶证&年龄',
       '0#30#35#45',1,3,15,3101,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'FEMALE_OVERDUEDAYS_HASDRIVERLICENSE_AGE_MONTHLY_INCOME','女&当天还款&年龄&没有驾驶证&月收入',
       '0#40#6000000#8000000',1,3,15,3117,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'FEMALE_OVERDUEDAYS_AGE_MONTHLY_INCOME','女&当天还款&年龄&月收入',
       '0#45#4000000#6000000',1,3,15,3118,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'FEMALE_OVERDUEDAYS_AGE_MONTHLY_INCOME_HAS_DRIVERLICENSE_DIFFHOURSOFCOLLECTION_AND_REFUND','女&当天还款&月收入&没有驾驶证&上一笔第一次催收到还款&年龄',
       '0#25#45#4000000#10000000#1',1,3,15,3119,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'FEMALE_OVERDUEDAYS_HAS_DRIVERLICENSE_POSITION','女&当天还款&没有驾驶证&职业in(个体供应商,公务员)',
       '0',1,3,15,3120,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'FEMALE_OVERDUEDAYS_LOANPASSTYPE_LASTLOAN_NO_COLLECTION_AGE','女&当天还款&免审核&首借未被催收&年龄',
       '0#1#30',1,3,15,3121,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'FEMALE_OVERDUEDAYS_LOANPASSTYPE_HAS_SALARY_PIC_GOJEVERIFIED_MONTHLY_INCOME','女&当天还款&免审核&没有工资单&验证gojek&月收入',
       '0#1#4000000',1,3,15,3122,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'FEMALE_OVERDUEDAYS_LOANPASSTYPE_HAS_SALARY_PIC_MONTHLY_INCOME','女&当天还款&免审核&没有工资单&月收入',
       '0#1#8000000',1,3,15,3123,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'FEMALE_OVERDUEDAYS_LOANPASSTYPE_HAS_SALARY_PIC_HAS_DRIVERLICENSE_SAMEOF_WHATSAPP_AND_MOBILE','女&当天还款&免审核&WA与手机号一致&没有工资单&有驾驶证',
       '0#1',1,3,15,3124,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'FEMALE_OVERDUEDAYS_LOANPASSTYPE_HAS_PASSPORT_SAMEOF_WHATSAPP_AND_MOBILE_AGE','女&当天还款&免审核&WA与手机号一致&没有护照&年龄',
       '0#1#35',1,3,15,3125,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'FEMALE_OVERDUEDAYS_HAS_PASSPORT_SAMEOF_WHATSAPP_AND_MOBILE_AGE','女&当天还款&WA与手机号一致&有护照&年龄',
       '0#30#35',1,3,15,3126,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'FEMALE_OVERDUEDAYS_DIFFHOURSOFCOLLECTION_AND_REFUND','女&当天还款&借款间隔天数',
       '0#2',1,3,15,3127,"V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'NO_REBORROWING_PRODUCT_400_EXTEND_RULE_HIT','not hit extend rule for reBorrowing product400',
       '',2,1,15,3128,"V1", 3,0);




insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'RE_BORROWING_PRD400_EXTEND',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled=0 and s.ruleDetailType in (
   'MALE_OVERDUEDAYS_HAS_DRIVERLICENSE_LASTLOAN_NOCOLLECTION',
   'MALE_OVERDUEDAYS_HAS_DRIVERLICENSE_APPLY_INTERVALDAYS',
   'MALE_OVERDUEDAYS_NO_DRIVERLICENSE_APPLY_INTERVALDAYS',
   'MALE_OVERDUEDAYS_MONTHLY_INCOME',
   'MALE_OVERDUEDAYS_MONTHLY_INCOME_DIFFHOURSOFCOLLECTION_AND_REFUND_AGE',
   'MALE_OVERDUEDAYS_MONTHLY_INCOME_DIFFHOURSOFCOLLECTION_AND_REFUND_AGE_HAS_DRIVERLICENSE',
   'MALE_OVERDUEDAYS_HAS_DRIVERLICENSE_RELIGION',
   'MALE_OVERDUEDAYS_HAS_DRIVERLICENSE_POSITION',
   'MALE_OVERDUEDAYS_LOANPASSTYPE_GOJEVERIFIED_SAMEOF_WHATSAPPANDMOBILE',
   'MALE_OVERDUEDAYS_LOANPASSTYPE_SAMEOF_WHATSAPPANDMOBILE_HASPASSPORT_AGE',
   'MALE_OVERDUEDAYS_SAMEOF_WHATSAPPANDMOBILE_HASPASSPORT_AGE',
   'MALE_OVERDUEDAYS_HASPASSPORT_TOTAL_SPACE',
   'FEMALE_OVERDUEDAYS_HASDRIVERLICENSE_GOJEKVERIFIED',
   'FEMALE_OVERDUEDAYS_HASDRIVERLICENSE_APPLY_INTERVALDAYS',
   'FEMALE_OVERDUEDAYS_HASDRIVERLICENSE_EDUCATION',
   'FEMALE_OVERDUEDAYS_HASDRIVERLICENSE_MONTHLY_INCOME',
   'FEMALE_OVERDUEDAYS_HASDRIVERLICENSE_MOBILE_LANGUAGE',
   'FEMALE_OVERDUEDAYS_HASDRIVERLICENSE_AGE',
   'FEMALE_OVERDUEDAYS_HASDRIVERLICENSE_AGE_MONTHLY_INCOME',
   'FEMALE_OVERDUEDAYS_AGE_MONTHLY_INCOME',
   'FEMALE_OVERDUEDAYS_AGE_MONTHLY_INCOME_HAS_DRIVERLICENSE_DIFFHOURSOFCOLLECTION_AND_REFUND',
   'FEMALE_OVERDUEDAYS_HAS_DRIVERLICENSE_POSITION',
   'FEMALE_OVERDUEDAYS_LOANPASSTYPE_LASTLOAN_NO_COLLECTION_AGE',
   'FEMALE_OVERDUEDAYS_LOANPASSTYPE_HAS_SALARY_PIC_GOJEVERIFIED_MONTHLY_INCOME',
   'FEMALE_OVERDUEDAYS_LOANPASSTYPE_HAS_SALARY_PIC_MONTHLY_INCOME',
   'FEMALE_OVERDUEDAYS_LOANPASSTYPE_HAS_SALARY_PIC_HAS_DRIVERLICENSE_SAMEOF_WHATSAPP_AND_MOBILE',
   'FEMALE_OVERDUEDAYS_LOANPASSTYPE_HAS_PASSPORT_SAMEOF_WHATSAPP_AND_MOBILE_AGE',
   'FEMALE_OVERDUEDAYS_HAS_PASSPORT_SAMEOF_WHATSAPP_AND_MOBILE_AGE',
   'FEMALE_OVERDUEDAYS_DIFFHOURSOFCOLLECTION_AND_REFUND',
   'NO_REBORROWING_PRODUCT_400_EXTEND_RULE_HIT'

       );


insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled=0 and s.ruleDetailType in (
   'MALE_OVERDUEDAYS_HAS_DRIVERLICENSE_LASTLOAN_NOCOLLECTION',
   'MALE_OVERDUEDAYS_HAS_DRIVERLICENSE_APPLY_INTERVALDAYS',
   'MALE_OVERDUEDAYS_NO_DRIVERLICENSE_APPLY_INTERVALDAYS',
   'MALE_OVERDUEDAYS_MONTHLY_INCOME',
   'MALE_OVERDUEDAYS_MONTHLY_INCOME_DIFFHOURSOFCOLLECTION_AND_REFUND_AGE',
   'MALE_OVERDUEDAYS_MONTHLY_INCOME_DIFFHOURSOFCOLLECTION_AND_REFUND_AGE_HAS_DRIVERLICENSE',
   'MALE_OVERDUEDAYS_HAS_DRIVERLICENSE_RELIGION',
   'MALE_OVERDUEDAYS_HAS_DRIVERLICENSE_POSITION',
   'MALE_OVERDUEDAYS_LOANPASSTYPE_GOJEVERIFIED_SAMEOF_WHATSAPPANDMOBILE',
   'MALE_OVERDUEDAYS_LOANPASSTYPE_SAMEOF_WHATSAPPANDMOBILE_HASPASSPORT_AGE',
   'MALE_OVERDUEDAYS_SAMEOF_WHATSAPPANDMOBILE_HASPASSPORT_AGE',
   'MALE_OVERDUEDAYS_HASPASSPORT_TOTAL_SPACE',
   'FEMALE_OVERDUEDAYS_HASDRIVERLICENSE_GOJEKVERIFIED',
   'FEMALE_OVERDUEDAYS_HASDRIVERLICENSE_APPLY_INTERVALDAYS',
   'FEMALE_OVERDUEDAYS_HASDRIVERLICENSE_EDUCATION',
   'FEMALE_OVERDUEDAYS_HASDRIVERLICENSE_MONTHLY_INCOME',
   'FEMALE_OVERDUEDAYS_HASDRIVERLICENSE_MOBILE_LANGUAGE',
   'FEMALE_OVERDUEDAYS_HASDRIVERLICENSE_AGE',
   'FEMALE_OVERDUEDAYS_HASDRIVERLICENSE_AGE_MONTHLY_INCOME',
   'FEMALE_OVERDUEDAYS_AGE_MONTHLY_INCOME',
   'FEMALE_OVERDUEDAYS_AGE_MONTHLY_INCOME_HAS_DRIVERLICENSE_DIFFHOURSOFCOLLECTION_AND_REFUND',
   'FEMALE_OVERDUEDAYS_HAS_DRIVERLICENSE_POSITION',
   'FEMALE_OVERDUEDAYS_LOANPASSTYPE_LASTLOAN_NO_COLLECTION_AGE',
   'FEMALE_OVERDUEDAYS_LOANPASSTYPE_HAS_SALARY_PIC_GOJEVERIFIED_MONTHLY_INCOME',
   'FEMALE_OVERDUEDAYS_LOANPASSTYPE_HAS_SALARY_PIC_MONTHLY_INCOME',
   'FEMALE_OVERDUEDAYS_LOANPASSTYPE_HAS_SALARY_PIC_HAS_DRIVERLICENSE_SAMEOF_WHATSAPP_AND_MOBILE',
   'FEMALE_OVERDUEDAYS_LOANPASSTYPE_HAS_PASSPORT_SAMEOF_WHATSAPP_AND_MOBILE_AGE',
   'FEMALE_OVERDUEDAYS_HAS_PASSPORT_SAMEOF_WHATSAPP_AND_MOBILE_AGE',
   'FEMALE_OVERDUEDAYS_DIFFHOURSOFCOLLECTION_AND_REFUND',
   'NO_REBORROWING_PRODUCT_400_EXTEND_RULE_HIT'
       )
;