create table if not exists iziWhatsAppDetail
(
	id int auto_increment
		primary key,
	userUuid varchar(45) null,
	orderNo varchar(45) null,
	type tinyint null comment '0:本人1,2,4,5分别对对第一二三四联系人',
	status varchar(100) null,
	disabled tinyint default '0' null,
	mobileNumber varchar(20) null comment '检查的手机号',
	statusUpdate varchar(32) null,
	whatsapp varchar(10) null comment '检查结果：yes no checking',
	signature varchar(512) null,
	businessUser varchar(10) null comment 'yes or no',
	avatar varchar(10) null comment '是否有头像 yes no',
	createTime datetime null,
	updateTime datetime null,
	createUser int default '0' null,
	updateUser int default '0' null,
	uuid varchar(45) default '' null,
	remark int null,
	message varchar(200) null
)
;

create index iziWhatsAppDetail_idxOrderNo
	on iziWhatsAppDetail (orderNo)
;

create index iziWhatsAppDetail_idxUserUuid
	on iziWhatsAppDetail (userUuid)
;

