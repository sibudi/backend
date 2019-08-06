create table if not exists advanceMultiPlatform
(
	id int auto_increment
		primary key,
	userUuid varchar(45) null,
	orderNo varchar(45) null,
	tp1To7D int null,
	tp1To14D int null,
	tp1To21D int null,
	tp1To30D int null,
	tp1To60D int null,
	tp1To90D int null,
	mqc1H int null,
	mqc3H int null,
	mqc6H int null,
	mqc12H int null,
	mqc24H int null,
	createTime datetime null,
	updateTime datetime null,
	createUser int default 0 null,
	updateUser int default 0 null,
	remark varchar(200) default '' null,
	disabled tinyint default '0' null,
	uuid varchar(45) default '' null
)
comment 'advance多头数据'
;

create index advanceMultiPlatform_idxOrderNo
	on advanceMultiPlatform (orderNo)
;

create index advanceMultiPlatform_idxUserUuid
	on advanceMultiPlatform (userUuid)
;


create table if not exists advanceBlacklist
(
	id int auto_increment
		primary key,
	userUuid varchar(45) null,
	orderNo varchar(45) null,
	recommendation varchar(40) null,
	createUser int default 0 null,
	updateUser int default 0 null,
	createTime datetime null,
	updateTime datetime null,
	remark varchar(200) default '' null,
	disabled tinyint default '0' null,
	uuid varchar(45) default '' null
)
comment 'advance黑名单'
;

create index advanceBlacklist_idxOrderNo
	on advanceBlacklist (orderNo)
;

create index advanceBlacklist_idxUserUuid
	on advanceBlacklist (userUuid)
;

create table if not exists advanceBlacklistDetail
(
	id int auto_increment
		primary key,
	userUuid varchar(45) null,
	orderNo varchar(45) null,
	eventTime varchar(40) null,
	hitReason varchar(100) null,
	productType varchar(100) null,
	reasonCode varchar(200) null,
	createUser int default 0 null,
	updateUser int default 0 null,
	createTime datetime null,
	updateTime datetime null,
	remark varchar(200) default '' null,
	disabled tinyint default '0' null,
	uuid varchar(45) default '' null
)
	comment 'advance黑名单明细'
;

create index advanceBlacklistDetail_idxOrderNo
	on advanceBlacklistDetail (orderNo)
;

create index advanceBlacklistDetail_idxUserUuid
	on advanceBlacklistDetail (userUuid)
;




