CREATE TABLE `loanLimitRuleResult` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userUuid` varchar(45) DEFAULT NULL,
  `orderNo` varchar(45) DEFAULT NULL,
  `ruleName` varchar(50) DEFAULT NULL,
  `pass` tinyint(4) DEFAULT NULL,
  `ruleDesc` varchar(200) DEFAULT NULL,
  `productType` varchar(50) DEFAULT NULL,
  `remark` varchar(200) DEFAULT NULL,
  `uuid` varchar(45) DEFAULT NULL,
  `disabled` tinyint(4) DEFAULT '0',
  `createTime` datetime DEFAULT NULL,
  `updateTime` datetime DEFAULT NULL,
  `createUser` int(11) DEFAULT '0',
  `updateUser` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `loanLimitRuleResult_idxUserUuid` (`userUuid`),
  KEY `loanLimitRuleResult_idxOrderNo` (`orderNo`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='降额规则结果表';



update sysAutoReviewRule set disabled =1,updateTime=now() where disabled=0 and ruleDetailType in ('MOBILE_LANGUAGE_MALE_APPLY_TIME_HOUR','SCORE_MODEL_PRODUCT_600');


start transaction;

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'APPLY_TIME_MALE_IZI_PHONEVERIFY','提交时间&男性&IZI手机实名','6',2,1,15,2415,"V1",
       3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',27,
       'RISK_SCORE_600_V2_MALE','600模型分V2版&男性','430',2,1,15,2420,"V1",
       3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',27,
       'RISK_SCORE_600_V2_FEMALE','600模型分V2版&女性','435',2,1,15,2425,"V1",
       3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',27,
       'RISK_SCORE_600_V2_FEMALE_IZI_PHONEVERIFY','600模型分V2版&女性&IZI手机实名认证','435#445',2,1,15,2425,"V1",
       3,0);


insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'APPLY_TIME_MALE_IZI_PHONEVERIFY'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'RISK_SCORE_600_V2_MALE'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'RISK_SCORE_600_V2_FEMALE'
    );

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'RISK_SCORE_600_V2_FEMALE_IZI_PHONEVERIFY'
    );

insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0
  and s.ruleDetailType in (
'APPLY_TIME_MALE_IZI_PHONEVERIFY',
'RISK_SCORE_600_V2_MALE',
'RISK_SCORE_600_V2_FEMALE',
'RISK_SCORE_600_V2_FEMALE_IZI_PHONEVERIFY'
    );



update flowRuleSet set disabled=1 where disabled=0 and ruleDetailType = 'TOTAL_MOBILE_CAP_MALE' and flowName='PRODUCT_50';

update flowRuleSet set disabled=1 where disabled=0 and ruleDetailType = 'IZI_PHONEAGE_PHONEVERIFY_APPCOUNT_LANGUAGE' and flowName='PRODUCT_50';

update flowRuleSet set disabled=1 where disabled=0 and ruleDetailType = 'APPCOUNT_MALE' and flowName='PRODUCT_50';

commit;

