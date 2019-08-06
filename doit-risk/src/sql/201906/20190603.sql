create table if not exists ordDeviceExtendInfo
(
	id int auto_increment
		primary key,
	orderNo varchar(45) null,
	userUuid varchar(45) null,
	device1 varchar(45) null comment 'md5(设备信息和第一联系人手机号)',
	device2 varchar(45) null comment 'md5(设备信息和第二联系人手机号)',
	device3 varchar(45) null comment 'md5(设备信息和第三联系人手机号)',
	device4 varchar(45) null comment 'md5(设备信息和第四联系人手机号)',
	createTime datetime null,
	updateTime datetime null,
	createUser int default '0' null,
	updateUser int default '0' null,
	remark varchar(200) null,
	disabled tinyint default '0' null,
	uuid varchar(45) default '' null
)
comment '设备扩展信息'
;

create index ordDeviceExtendInfo_idxDevice1
	on ordDeviceExtendInfo (device1)
;

create index ordDeviceExtendInfo_idxDevice2
	on ordDeviceExtendInfo (device2)
;

create index ordDeviceExtendInfo_idxDevice3
	on ordDeviceExtendInfo (device3)
;

create index ordDeviceExtendInfo_idxDevice4
	on ordDeviceExtendInfo (device4)
;

create index ordDeviceExtendInfo_idxOrderNo
	on ordDeviceExtendInfo (orderNo)
;
