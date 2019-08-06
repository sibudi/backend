
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',25,
  'AUTO_CALL_REJECT_LINKMAN_VALID_COUNT','紧急联系人外呼有效个数小于2','',2,1,15,1580,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',25,
  'AUTO_CALL_REJECT_BACKUP_LINKMAN_VALID_COUNT','备选联系人外呼有效个数小于6','',2,1,15,1585,"V1");

create table if not exists backupLinkmanItem
(
	id int auto_increment
		primary key,
	uuid varchar(50) null,
	orderNo varchar(50) null comment '订单号',
	userUuid varchar(50) null comment '用户id',
	linkmanName varchar(200) default '' null comment '联系人名称',
	linkmanNumber varchar(50) default '' null comment '联系人号码',
	fromCallRecord tinyint default '0' null comment '是否从童话记录提取 1:是0：否',
	isRelative tinyint default '0' null comment '是否帅选的亲属词联系人1：是0：否',
	isConfirmed tinyint default '0' null comment '用户是否确认1：是0：否',
	updateUser int default '0' null,
	createUser int default '0' null,
	createTime timestamp null,
	remark varchar(200) default '' null,
	updateTime timestamp null,
	disabled tinyint default '0' null,
	orderSequence tinyint default '0' null comment '备选联系人排序'
)
;

create index backupLinkmanItem_orderNo
	on backupLinkmanItem (orderNo)
;

create index backupLinkmanItem_userUuid
	on backupLinkmanItem (userUuid)
;