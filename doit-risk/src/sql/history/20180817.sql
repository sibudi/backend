
-- 实名验证记录表
create table usrVerifyResult
(
	id int auto_increment comment '主键'
		primary key,
	userUuid varchar(50) default '' null comment '用户id',
	orderNo varchar(50) default '' null comment '订单号',
	verifyType tinyint default '0' null comment '1:ktp 2:advance 3:税卡',
	verifyResult tinyint null comment '0:准备验证1：验证通过 2：验证不通过',
	response varchar(500) default '' null comment '第三方响应结果',
	remark varchar(128) default '' null,
	uuid varchar(50) default '' null,
	disabled tinyint default '0' null,
	updateUser int default '0' null,
	createUser int default '0' null,
	updateTime datetime null,
	createTime datetime null
)
comment '第三方验证结果表' engine=InnoDB
;



-- AB test 配置
insert into sysParam(remark,sysKey,sysValue,description,createTime,updateTime)
values(
    '规则ABTest放款测试配置',
  'risk:ABTestRule:config',
  '[{"hitRules":[{"ruleName":"COMPANY_TEL_NOT_IN_JAKARTA","realValue":"true"}],"theOnlyRejectRule":{"ruleName":"WORK_ADDRESS_INVALID",
  "realValue":"true"},"issuedLimitCount":50},{"hitRules":[{"ruleName":"WORK_ADDRESS_INVALID","realValue":"false"}],"theOnlyRejectRule":{"ruleName":"COMPANY_TEL_NOT_IN_JAKARTA","realValue":"false"},"issuedLimitCount":50}]',
  'ABTest 规则配置',
    now(),
  now()
);


-- 新增规则
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
  'NON_REVIEW_POSITION_NAMES','职业是医务人员或老师或公务员','Staf medis#Guru#Pegawai negeri',1,3,15,1460,"V1");
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
  'NON_REVIEW_RELIGION','宗教是天主教或印度教','Kristen Katoli#Hindu',1,3,15,1465,"V1");
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',22,
  'MALE_SALARY_MEMORY','男&月收入&手机内存','8000000#3',1,3,15,1470,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
  'MOBILE_JAILBREAK','已越狱','',2,1,15,1475,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',4,
  'CALL_RECORD_RECENT30_DISTINCT_NUMBERS','近30天通话号码去重个数','',1,3,15,1475,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',4,
  'CALL_RECORD_RECENT90_DISTINCT_NUMBERS','近90天通话号码去重个数','',1,3,15,1475,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',4,
  'CALL_RECORD_RECENT30_CALL_IN_DISTINCT_NUMBERS','近30天被叫号码去重个数','',1,3,15,1475,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',4,
  'CALL_RECORD_RECENT90_CALL_IN_DISTINCT_NUMBERS','近90天被叫号码去重个数','',1,3,15,1475,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',4,
  'CALL_RECORD_RECENT90_STRANGE_NOT_CONNECT_RATIO','近90天通话记录中陌生号码打入未接通占比','',1,3,15,1475,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',5,
  'SMS_OVERDUE15_COUNT_MALE','命中逾期大于15天的短信条数&男','0',2,1,15,445,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',5,
  'SMS_OVERDUE15_COUNT_FEMALE','命中逾期大于15天的短信条数&女','1',2,1,15,446,"V1");

-- 修改规则
-- 改为打标签
update doit.sysAutoReviewRule s set s.ruleResult = 1 ,s.ruleStatus=3,s.updateTime =now() where s.disabled=0 and s.ruleDetailType in (
'COMB_MALE_MARRIAGE_EDUCATION',
'SCHOOL_ADDRESS_INVALID',
'COMB_MOBILEUSEDTIMES_RECENT30NOCONNECTRATIO_MALE',
'SMS_OVERDUE_MORETHAN_15DAYS_COUNT',
'COMB_MOBILEUSEDTIMES_RECENT30NOCONNECTRATIO_MALE'
);

-- 修改阈值

update sysAutoReviewRule s set s.ruleValue = '5',s.updateTime = now() where ruleDetailType = 'COMB_3_CHILDRENAMOUNT' and disabled =0;

update sysAutoReviewRule s set s.ruleValue = '0#10',s.updateTime = now() where ruleDetailType = 'COMB_MOBILEUSAGETIME_MALE_MARRIAGE' and disabled =0;

update sysAutoReviewRule s set s.ruleValue = '0.86',s.updateTime = now() where ruleDetailType = 'RECENT_30_CALL_RATE' and disabled =0;

update sysAutoReviewRule s set s.ruleValue = '0.65',s.updateTime = now() where ruleDetailType = 'COMB_SEX_RECENT30_MISSEDCALLRATE' and disabled =0;

update sysAutoReviewRule s set s.ruleValue = '0.75',s.updateTime = now() where ruleDetailType = 'RECENT_30_CALL_NO_CONNECT_RATE' and disabled =0;

update sysAutoReviewRule s set s.ruleValue = '0.65',s.updateTime = now() where ruleDetailType = 'COMB_SEX_RECENT30_MISSEDCALLINDATE' and disabled=0;


