create table if not exists asyncTaskInfo
(
	id int auto_increment
		primary key,
	userUuid varchar(45) null,
	orderNo varchar(45) null,
	taskType tinyint null comment '1:签约任务',
	taskStatus tinyint null comment '0:待处理 1:处理中 2:处理完成(成功完成or失败完成)',
	createTime datetime null,
	updateTime datetime null,
	createUser int default '0' null,
	updateUser int default '0' null,
	disabled tinyint default '0' null,
	remark varchar(200) null,
	uuid varchar(32) default '' null
)
comment '异步任务表'
;

create index idx_orderNo
	on asyncTaskInfo (orderNo)
;

