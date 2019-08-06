--
-- insert into sysDicItem(uuid,createTime,updateTime,dicId, dicItemValue,dicItemName,`language`)
-- select UUID(),now(),now(),52,ruleData,'sensitiveWords','ID' from  sysAutoReviewRule where ruleDetailType = 'CONTACT_SENSITIVI_COUNT';
--
-- insert into sysDicItem(uuid,createTime,updateTime,dicId, dicItemValue,dicItemName,`language`)
-- select UUID(),now(),now(),52,ruleData,'interrelatedWords','ID' from  sysAutoReviewRule where ruleDetailType = 'SMS_SAME_COUNT';
--
-- insert into sysDicItem(uuid,createTime,updateTime,dicId, dicItemValue,dicItemName,`language`)
-- select UUID(),now(),now(),52,ruleData,'overdueWords','ID' from  sysAutoReviewRule where ruleDetailType = 'SMS_OVERDUE_COUNT';
--
-- insert into sysDicItem(uuid,createTime,updateTime,dicId, dicItemValue,dicItemName,`language`)
-- select UUID(),now(),now(),52,ruleData,'negativeWords','ID' from  sysAutoReviewRule where ruleDetailType = 'SMS_NEGATIVE_COUNT';
--
-- insert into sysDicItem(uuid,createTime,updateTime,dicId, dicItemValue,dicItemName,`language`)
-- select UUID(),now(),now(),52,ruleData,'rejectWords','ID' from  sysAutoReviewRule where ruleDetailType = 'SMS_REFUSE_COUNT';
--
-- insert into sysDicItem(uuid,createTime,updateTime,dicId, dicItemValue,dicItemName,`language`)
-- select UUID(),now(),now(),52,ruleData,'relativeWords','ID' from  sysAutoReviewRule where ruleDetailType = 'CONTACT_RELATIVE_COUNT';
--
-- insert into sysDicItem(uuid,createTime,updateTime,dicId, dicItemValue,dicItemName,`language`)
-- select UUID(),now(),now(),52,ruleData,'loanAppWords','ID' from  sysAutoReviewRule where ruleDetailType = 'USER_INSTALLED_SENSITIVE_APP_COUNT';
--
-- insert into sysDicItem(uuid,createTime,updateTime,dicId, dicItemValue,dicItemName,`language`)
-- select UUID(),now(),now(),52,ruleData,'jarkatAddressWords','ID' from  sysAutoReviewRule where ruleDetailType = 'WORK_ADDRESS_INVALID';
--
-- insert into sysDicItem(uuid,createTime,updateTime,dicId, dicItemValue,dicItemName,`language`)
-- select UUID(),now(),now(),52,ruleData,'jarkatTelNumbers','ID' from  sysAutoReviewRule where ruleDetailType = 'COMPANY_TEL_NOT_IN_JAKARTA';
--
-- insert into sysDicItem(uuid,createTime,updateTime,dicId, dicItemValue,dicItemName,`language`)
-- select UUID(),now(),now(),52,ruleData,'smsRuleBody','ID' from  sysAutoReviewRule where ruleDetailType = 'SMS_OVERDUE_MORETHAN_15DAYS_COUNT';
--
-- insert into sysDicItem(uuid,createTime,updateTime,dicId, dicItemValue,dicItemName,`language`)
-- select UUID(),now(),now(),52,ruleData,'earthquakeArea','ID' from  sysAutoReviewRule where ruleDetailType = 'PROVINCE_IN_EARTHQUAKE_AREA';
--
-- insert into sysDicItem(uuid,createTime,updateTime,dicId, dicItemValue,dicItemName,`language`)
-- select UUID(),now(),now(),52,ruleData,'earthquakeCity','ID' from  sysAutoReviewRule where ruleDetailType = 'CITY_IN_EARTHQUAKE_AREA';
--
-- insert into sysDicItem(uuid,createTime,updateTime,dicId, dicItemValue,dicItemName,`language`)
-- select UUID(),now(),now(),52,ruleData,'jarkatAddressWordsFor100Rmb','ID' from  sysAutoReviewRule where ruleDetailType = 'WORK_ADDRESS_INVALID_100RMB';
--
-- insert into sysDicItem(uuid,createTime,updateTime,dicId, dicItemValue,dicItemName,`language`)
-- select UUID(),now(),now(),52,ruleData,'jarkatAddressWordsNormal','ID' from  sysAutoReviewRule where ruleDetailType = 'WORK_ADDRESS_NOT_VALID_NORMAL';
--
-- insert into sysDicItem(uuid,createTime,updateTime,dicId, dicItemValue,dicItemName,`language`)
-- select UUID(),now(),now(),52,ruleData,'overDuePositionMan','ID' from  sysAutoReviewRule where ruleDetailType = 'TAX_SCOREMODEL600_POSITIONNAME_MEN';
--
-- insert into sysDicItem(uuid,createTime,updateTime,dicId, dicItemValue,dicItemName,`language`)
-- select UUID(),now(),now(),52,ruleData,'overDuePositionFeMen','ID' from  sysAutoReviewRule where ruleDetailType = 'TAX_SCOREMODEL600_POSITIONNAME_FEMAL';
--
-- insert into sysDicItem(uuid,createTime,updateTime,dicId, dicItemValue,dicItemName,`language`)
-- select UUID(),now(),now(),52,ruleData,'homeProviceMan','ID' from  sysAutoReviewRule where ruleDetailType = 'HOME_PROVINCE_MALE_PRODUCT600';
--
-- insert into sysDicItem(uuid,createTime,updateTime,dicId, dicItemValue,dicItemName,`language`)
-- select UUID(),now(),now(),52,ruleData,'homeProviceFeMen','ID' from  sysAutoReviewRule where ruleDetailType = 'HOME_PROVINCE_FEMALE_PRODUCT600';





insert into sysAutoReviewRule(ruleData,uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values('SUMATERA SELATAN#LAMPUNG#RIAU#BENGKULU#SUMATERA UTARA',replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'HOME_PROVINCE_MALE_PRODUCT600_150','居住地（省）高风险&男性&600产品模型分(150)','460',2,1,15,2300,"V1",3,0);

insert into sysAutoReviewRule(ruleData,uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values('GORONTALO#SUMATERA SELATAN#RIAU#SULAWESI UTARA',replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'HOME_PROVINCE_FEMALE_PRODUCT600_150','居住地（省）高风险&女性&600产品模型分(150)','460',2,1,15,2300,"V1",3,0);

insert into sysAutoReviewRule(ruleData,uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values('JAMBI#LAMPUNG#KALIMANTAN TENGAH#ACEH',replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'HOME_PROVINCE_MALE_PRODUCT600_80','居住地（省）高风险&男性&600产品模型分(80)','460',2,1,15,2300,"V1",3,0);

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_100',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'HOME_PROVINCE_MALE_PRODUCT600_150'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_100',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'HOME_PROVINCE_FEMALE_PRODUCT600_150'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_50',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'HOME_PROVINCE_MALE_PRODUCT600_80'
    );

insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
and s.ruleDetailType in (
   'HOME_PROVINCE_MALE_PRODUCT600_150',
    'HOME_PROVINCE_FEMALE_PRODUCT600_150',
    'HOME_PROVINCE_MALE_PRODUCT600_80'
);