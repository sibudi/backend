
-- create table

create table if not exists orderReviewStep
(
	id int auto_increment
		primary key,
	orderNo varchar(45) null,
	userUuid varchar(45) null,
	step tinyint null comment '1:紧急联系人公司外呼审核
2:本人外呼审核',
	remark varchar(200) null,
	disabled tinyint default '0' null,
	uuid varchar(45) null,
	createTime datetime null,
	updateTime datetime null,
	createUser int default '0' null,
	updateUser int default '0' null
)
;

create index orderReviewStep_idxOrderNo
	on orderReviewStep (orderNo)
;
