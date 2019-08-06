create table if not exists flowRuleSet
(
	id int auto_increment
		primary key,
	uuid varchar(64) default '' null,
	flowName varchar(100) default '' null comment '流程规则名称',
	ruleDetailType varchar(256) default '' null comment '规则名(对应sysAutoReviewRule中对应字段)',
	createTime timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
	createUser int default '0' null,
	updateTime timestamp default CURRENT_TIMESTAMP not null,
	updateUser int default '0' null,
	remark varchar(512) null,
	disabled tinyint default '0' null,
	constraint flowRuleSet_uuid_uindex
		unique (uuid)
)
comment '流程规则集'
;

create table ruleParam
(
	id int null,
	flowName varchar(100) null,
	ruleDetailType varchar(256) null,
	createTime timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
	createUser int default '0' null,
	updateTime timestamp default CURRENT_TIMESTAMP not null,
	updateUser int default '0' null,
	uuid varchar(64) default '' null,
	disabled tinyint default '0' null,
	thresholdValue varchar(20000) null
)
;



-- 新增规则
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'TOTAL_MOBILE_CAP_MALE','手机总容量&男性','5',2,1,15,2000,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MALE_NO_TAXNUMBER_APPCOUNT','男性&无税卡&首借累计app个数','15',2,1,15,2005,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'MALE_EDUCATION_MARRIAGE_APPLY_TIME_HOUR','男性&学历&单身&下单时间','15',2,1,15,2010,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'APPCOUNT_MALE','累计App个数&男性','10',2,1,15,2015,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
       'APPLY_TIME_HOUR_MALE','下单时间在凌晨[0-5)点&男性','',2,1,15,2020,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'CAP_MONTHLY_INCOME_GOJEK_MOBILE_NOT_SAME','手机总容量&收入&gojek手机号不一致','25#6000000#false',2,1,15,2025,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'CAP_MARRIAGE_FEMALE_GOJEK_MOBILE_NOT_SAME','手机总容量&婚姻&性别&gojek手机号不一致','25#false',2,1,15,2030,"V1",3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
       'FLOW_MAX_ISSUED_LIMIT','超过该流程放款数量','',2,1,15,2035,"V1",3,0);

update sysAutoReviewRule s set s.ruleResult = 2 ,s.ruleStatus=1 where ruleDetailType ='IP_ADDRESS_COUNT' and s.disabled =0;

-- 欺诈规则
insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'FRAUD_UNIVERSAL_RULE',s.ruleDetailType,now(),
now(),'FRAUD' from sysAutoReviewRule s where s.disabled = 0 and s.ruleDetailType in (
    'USER_DEVICE_ID',
    'MOBILE_LANGUAGE',
    'DEVICE_ID_IN_BLACK_LIST',
     'USER_DEVICE_IMEI',
     'IMEI_IN_BLACK_LIST',
     'MOBILE_IN_OVERDUE15_BLACK_LIST_USER_EMERGENCY_TEL',
     'MOBILE_IS_FRAUD_USER_EMERGENCY_TEL',
     'MOBILE_IS_FRAUD_USER_CALL_RECORD_COUNT',
     'MOBILE_IN_FRAUD_USER',
     'ID_CARD_NO_IN_FRAUD_USER',
     'HIT_FRAUD_USER_INFO',
     'IMEI_IN_OVERDUE15_BLACKLIST',
     'ID_CARD_IN_OVERDUE15_BLACKLIST',
     'MOBILE_IN_OVERDUE15_BLACKLIST',
     'BANKCARD_IN_OVERDUE15_BLACKLIST',
     'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_OVERDUE15_BLACKLIST',
     'EMERGENCY_TEL_IN_OVERDUE15_BLACKLIST',
     'MOBILE_IN_CONTACT_OF_OVERDUE15_BLACKLIST',
     'MOBILE_IN_CALL_RECORD_OF_OVERDUE15_BLACKLIST',
     'MOBILE_IN_SHORT_MSG_OF_OVERDUE15_BLACKLIST',
     'IMEI_IN_OVERDUE7_BLACKLIST',
     'ID_CARD_IN_OVERDUE7_BLACKLIST',
     'MOBILE_IN_OVERDUE7_BLACKLIST',
     'BANKCARD_IN_OVERDUE7_BLACKLIST',
     'EMERGENCY_TEL_IS_EMERGENCY_TEL_FOR_OVERDUE7_BLACKLIST',
     'MOBILE_IS_EMERGENCY_TEL_FOR_OVERDUE7_BLACK',
     'EMERGENCY_TEL_IN_OVERDUE7_BLACKLIST',
     'HIT_SENSITIVE_USER_INFO',
     'HIT_COLLECTOR_BLACK_LIST'
    ) and disabled =0 ;

-- 通用规则
insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'FRAUD_UNIVERSAL_RULE',s.ruleDetailType,now(),
now(),'UNIVERSAL' from sysAutoReviewRule s where s.disabled = 0 and s.ruleDetailType in (
    'AGE_LOW',
    'AGE_UP',
    'PROVINCE_IN_EARTHQUAKE_AREA',
    'CITY_IN_EARTHQUAKE_AREA',
    'ADVANCE_VERIFY_RULE'
    ) and disabled =0 ;

-- 标签规则
insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'NON_MANUAL_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
                                           and s.ruleResult = 1
and s.ruleDetailType not like 'MULT%' and s.ruleDetailType not like '%REBO%'

-- 免核规则
insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'LABELING_RULE',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.ruleType = 22 and disabled=0;

-- 600 特有规则
insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_600',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'WORK_ADDRESS_INVALID',
    'WORK_ADDRESS_NOT_VALID_NORMAL',
    'WORK_ADDRESS_NOT_VALID_NORMAL_IOS',
    'NO_TAX_NUMBER_NO_PAYROLL_POST_MALE',
    'IZI_PHONEAGE_PHONEVERIFY_APPCOUNT_LANGUAGE',
    'COMB_SAMEIPCOUNT_MALE',
    'COMB_MOBILE_CAP_MARITAL_EDUCATION',
    'EMAIL_EXISTS',
    'FIRST_LINKMAN_EXISTS',
    'SAME_WORK_ADDRESS_AND_ORDER_ADDRESS_EXISTS',
    'SAME_HOME_ADDRESS_AND_ORDER_ADDRESS_EXISTS',
    'SAME_WORK_ADDRESS_AND_HOME_ADDRESS_EXISTS',
    'FEMALE_AGE_CHILDREN_COUNT',
    'MALE_AGE_CHILDREN_COUNT_EDUCATION',
    'COMB_3_CHILDRENAMOUNT',
    'MOBILE_JAILBREAK',
    'PHONE_BRAND',
    'TOTAL_MOBILE_CAP_MALE');

-- 100 特有规则

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_100',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'WORK_ADDRESS_INVALID_100RMB',
    'COMB_SAMEIPCOUNT_MALE_100RMB',
    'TOTAL_MEMORY_APPLY_TIME_HOUR_MALE',
    'IZI_PHONEAGE_PHONEVERIFY_APPCOUNT_LANGUAGE',
    'MALE_NO_TAXNUMBER_APPCOUNT',
    'MALE_EDUCATION_MARRIAGE_APPLY_TIME_HOUR'
    );

-- 50 特有规则

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'PRODUCT_50',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'TOTAL_MOBILE_CAP_MALE',
    'APPLY_TIME_HOUR_MALE',
    'IP_ADDRESS_COUNT',
    'IZI_PHONEAGE_PHONEVERIFY_APPCOUNT_LANGUAGE',
    'APPCOUNT_MALE'
    );


-- 外呼规则

-- 首借外呼

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'AUTO_CALL_FIRST_BORROWING',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'AUTO_CALL_REJECT_LINKMAN_VALID_COUNT',
    'AUTO_CALL_REJECT_OWNER_CALL_INVALID',
    'AUTO_CALL_REJECT_OWNER_EXCEED_LIMIT',
    'TOTAL_MEMORY_COMPANY_CALL_BANK_CODE_MALE',
    'COMPANY_CALL_IOS_FEMALE',
    'COMPANY_CALL_IOS_MALE_MONTHLY_INCOME'
    );

-- 复借外呼

insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark)
select replace(uuid(), '-', ''),'AUTO_CALL_RE_BORROWING',s.ruleDetailType,now(),
       now(),'' from sysAutoReviewRule s where s.disabled = 0
and s.ruleDetailType in (
    'MULTI_AUTO_CALL_REJECT_LINKMAN_VALID_COUNT',
    'MULTI_AUTO_CALL_REJECT_BACKUP_LINKMAN_VALID_COUNT'
    );




-- 阈值处理：
insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid)
select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '')
from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType
where s.disabled = 0;

-- 特殊规则阈值：
update ruleParam p set p.thresholdValue = '20#in'
where p.ruleDetailType = 'IZI_PHONEAGE_PHONEVERIFY_APPCOUNT_LANGUAGE' and p.flowName = 'PRODUCT_50';

update sysAutoReviewRule set specifiedProduct = 12 where ruleDetailType = 'TOTAL_MEMORY_COMPANY_CALL_BANK_CODE_MALE';


INSERT INTO doit.sysProduct (id, uuid, disabled, createTime, updateTime, createUser, updateUser, remark, productCode, borrowingAmount, 
borrowingTerm, dueFeeRate, dueFee, interestRate, interest, overdueFee, overdueRate1, overdueRate2, productLevel, productOrder) VALUES (200, '200', 1,
 '2018-11-13 16:06:32', '2018-11-13 16:06:32', 0, 0, '降额产品-zengxiangcai', '200', 100000.00, 7, 0.20, 20000.00, 0.36, 700.00, 40000.00, 0.01, 0.02, 1, 8);
INSERT INTO doit.sysProduct (id, uuid, disabled, createTime, updateTime, createUser, updateUser, remark, productCode, borrowingAmount, 
borrowingTerm, dueFeeRate, dueFee, interestRate, interest, overdueFee, overdueRate1, overdueRate2, productLevel, productOrder) VALUES (201, '201', 1,
 '2018-11-13 16:06:32', '2018-11-13 16:06:32', 0, 0, '降额产品-zengxiangcai', '201', 100000.00, 8, 0.20, 20000.00, 0.36, 800.00, 40000.00, 0.01, 0.02, 1, 8);
INSERT INTO doit.sysProduct (id, uuid, disabled, createTime, updateTime, createUser, updateUser, remark, productCode, borrowingAmount, 
borrowingTerm, dueFeeRate, dueFee, interestRate, interest, overdueFee, overdueRate1, overdueRate2, productLevel, productOrder) VALUES (202, '202', 1,
 '2018-11-13 16:06:32', '2018-11-13 16:06:32', 0, 0, '降额产品-zengxiangcai', '202', 100000.00, 9, 0.20, 20000.00, 0.36, 900.00, 40000.00, 0.01, 0.02, 1, 8);
INSERT INTO doit.sysProduct (id, uuid, disabled, createTime, updateTime, createUser, updateUser, remark, productCode, borrowingAmount, 
borrowingTerm, dueFeeRate, dueFee, interestRate, interest, overdueFee, overdueRate1, overdueRate2, productLevel, productOrder) VALUES (203, '203', 1,
 '2018-11-13 16:06:32', '2018-11-13 16:06:32', 0, 0, '降额产品-zengxiangcai', '203', 100000.00, 10, 0.20, 20000.00, 0.36, 1000.00, 40000.00, 0.01, 0.02, 1, 8);
INSERT INTO doit.sysProduct (id, uuid, disabled, createTime, updateTime, createUser, updateUser, remark, productCode, borrowingAmount, 
borrowingTerm, dueFeeRate, dueFee, interestRate, interest, overdueFee, overdueRate1, overdueRate2, productLevel, productOrder) VALUES (204, '204', 1,
 '2018-11-13 16:06:32', '2018-11-13 16:06:32', 0, 0, '降额产品-zengxiangcai', '204', 100000.00, 11, 0.20, 20000.00, 0.36, 1100.00, 40000.00, 0.01, 0.02, 1, 8);
INSERT INTO doit.sysProduct (id, uuid, disabled, createTime, updateTime, createUser, updateUser, remark, productCode, borrowingAmount, 
borrowingTerm, dueFeeRate, dueFee, interestRate, interest, overdueFee, overdueRate1, overdueRate2, productLevel, productOrder) VALUES (205, '205', 1,
 '2018-11-13 16:06:32', '2018-11-13 16:06:32', 0, 0, '降额产品-zengxiangcai', '205', 100000.00, 12, 0.20, 20000.00, 0.36, 1200.00, 40000.00, 0.01, 0.02, 1, 8);
INSERT INTO doit.sysProduct (id, uuid, disabled, createTime, updateTime, createUser, updateUser, remark, productCode, borrowingAmount, 
borrowingTerm, dueFeeRate, dueFee, interestRate, interest, overdueFee, overdueRate1, overdueRate2, productLevel, productOrder) VALUES (206, '206', 1,
 '2018-11-13 16:06:32', '2018-11-13 16:06:32', 0, 0, '降额产品-zengxiangcai', '206', 100000.00, 13, 0.20, 20000.00, 0.36, 1300.00, 40000.00, 0.01, 0.02, 1, 8);
INSERT INTO doit.sysProduct (id, uuid, disabled, createTime, updateTime, createUser, updateUser, remark, productCode, borrowingAmount, 
borrowingTerm, dueFeeRate, dueFee, interestRate, interest, overdueFee, overdueRate1, overdueRate2, productLevel, productOrder) VALUES (207, '207', 1,
 '2018-11-13 16:06:32', '2018-11-13 16:06:32', 0, 0, '降额产品-zengxiangcai', '207', 100000.00, 14, 0.20, 20000.00, 0.36, 1400.00, 40000.00, 0.01, 0.02, 1, 8);