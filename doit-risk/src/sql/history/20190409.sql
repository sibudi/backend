
start transaction;


insert into sysAutoReviewRule(ruleData,uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values('Petani#Kurir#Pelayan#Pengemudi#Pembantu rumah tangga',replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'TAX_SCOREMODEL600_POSITIONNAME_MEN','税卡&600产品模型分&非高逾期职业&男性','505',1,3,15,2289,"V1",3,0);

insert into sysAutoReviewRule(ruleData,uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values('Pengemudi#Keamanan#Petani#Pembantu rumah tangga',replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'TAX_SCOREMODEL600_POSITIONNAME_FEMAL','税卡&600产品模型分&非高逾期职业&女性','505',1,3,15,2289,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'APPCOUNT_FOR_ECOMMERCE_IZINAME_IZIONLINETIME_AGE_SEX','电商类APP个数&IZI实名&IZI在网时长&年龄&性别','3#MATCH#6#25#45',1,3,15,2289,"V1",3,0);

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'NON_MANUAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'TAX_SCOREMODEL600_POSITIONNAME_MEN',
    'TAX_SCOREMODEL600_POSITIONNAME_FEMAL',
    'APPCOUNT_FOR_ECOMMERCE_IZINAME_IZIONLINETIME_AGE_SEX'
    );

insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
and s.ruleDetailType in (
   'TAX_SCOREMODEL600_POSITIONNAME_MEN',
    'TAX_SCOREMODEL600_POSITIONNAME_FEMAL',
    'APPCOUNT_FOR_ECOMMERCE_IZINAME_IZIONLINETIME_AGE_SEX'
);

commit;


