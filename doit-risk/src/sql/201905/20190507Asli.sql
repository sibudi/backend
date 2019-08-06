create table if not exists asliIdentityAuthResult
(
	id int auto_increment
		primary key,
	disabled int default '0' not null comment '删除标志',
	uuid varchar(45) default '' not null comment 'uuid',
	createTime datetime not null comment '创建时间',
	updateTime datetime not null comment '更新时间',
	createUser int default '0' not null comment '创建用户',
	updateUser int default '0' not null comment '更新用户',
	remark varchar(100) default '' not null comment '备注',
	orderNo varchar(45) default '' not null comment '订单号',
	userUuid varchar(45) default '' not null comment 'userUuid',
	message varchar(45) default '' not null comment 'message',
	status varchar(45) default '' not null comment 'status',
	error varchar(45) default '' not null comment 'error',
	name varchar(45) default '' not null comment 'name',
	birthplace varchar(45) default '' not null comment 'birthplace',
	birthdate varchar(45) default '' not null comment 'birthdate',
	address varchar(500) default '' not null comment 'address',
	constraint uuid_uq
		unique (uuid)
)
comment 'asli实名结果表'
;

create index idx_userUuid
	on asliIdentityAuthResult (userUuid)
;
create index idx_orderNo
	on asliIdentityAuthResult (orderNo)
;