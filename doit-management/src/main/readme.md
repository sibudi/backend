###上线记录

#####语音质检 2019-01-10
> 原型见：https://gpyup4.axshare.com  质检模块加入语音质检文件夹

> 语音质检上线 并且将初审cashcash待提交情况发送短信让其下载我们的app
    
    alter table doit.manQualityCheckConfig add type int(2) not null default 0 comment '类型：0.备注质检 1.语音质检';
    alter table doit.manQualityCheckRecord add type int(2) not null default 0 comment '类型：0.备注质检 1.语音质检';
    alter table doit.collectionOrderDetail add voiceCheckResult int(2) not null default 0 comment '语音质检的记录配置表外键';

##### 一个月内，降额后超过12小时没有点击确认借款的用户 外呼 ， 催收人员评分(2019-1-11)

    create table doit.usrEvaluateScore (
    id int(11) not null primary key auto_increment,
    uuid varchar(32) not null default 0 ,
    disabled int(2) not null default 0,
    createTime datetime ,
    createUser int(11) not null default 0,
    updateTime datetime,
    updateUser int(11) not null default 0,
    remark varchar(1000) not null default '',
    orderNo varchar(32) not null default '' comment '订单号',
    userUuid varchar(32) not null default '' comment '用户或者催收的userUuid',
    serviceMentality int(4) not null default 0 comment '服务意识',
    communicationBility int(4) not null default 0 comment '沟通能力',
    repayDesire int(4) not null default 0 comment '还款意愿',
    repayBility int(4) not null default 0 comment '还款能力',
    userDiathesis int(4) not null default 0 comment '用户素质',
    postId int(11) not null default 0 comment '催收阶段',
    type int(2) not null default 0 comment '1.用户对催收的评价 2.催收对用户的评价'
    ) comment '用户以及催收评分表';
    
##### 评分上线 （2019-01-17 14：00）；

##### 增加催收分配额度 ， 质检催收人员栓选 （2019-01-22 12：00）；

##### 修复一个bug  usrEvaluateScore type=2 记录 2019-01-23 18：00）；


##### 初审重构 （2019-01-27 00：00：18）；

##### 2019-02-19
1. 优化批量发送短信；
2. 全部订单页面和全部审核页面加上默认申请时间；
3. 删除多余日志；
4. 更改读写数据源。

##### 2019-02-25
1. 优化批量发送短信大文件；

    CREATE TABLE couponConfig (
      id INT ( 11 ) NOT NULL PRIMARY KEY auto_increment,
      uuid VARCHAR ( 32 ) NOT NULL DEFAULT 0,
      disabled INT ( 2 ) NOT NULL DEFAULT 0,
      createTime datetime,
      createUser INT ( 11 ) NOT NULL DEFAULT 0,
      updateTime datetime,
      updateUser INT ( 11 ) NOT NULL DEFAULT 0,
      remark VARCHAR ( 1000 ) NOT NULL DEFAULT '',
      couponCode VARCHAR ( 32 ) NOT NULL DEFAULT '' COMMENT '优惠券编号',
      alias VARCHAR ( 500 ) NOT NULL DEFAULT '' COMMENT '中文名称',
    
      indonisaName	VARCHAR ( 32 ) NOT NULL DEFAULT '' COMMENT '印尼名称',
    	`status`	int ( 4 ) NOT NULL DEFAULT 0 COMMENT '优惠券状态 0 有效 1 无效'
    ) comment '优惠券配置表';
    
    CREATE TABLE couponRecord (
      id INT ( 11 ) NOT NULL PRIMARY KEY auto_increment,
      uuid VARCHAR ( 32 ) NOT NULL DEFAULT 0,
      disabled INT ( 2 ) NOT NULL DEFAULT 0,
      createTime datetime,
      createUser INT ( 11 ) NOT NULL DEFAULT 0,
      updateTime datetime,
      updateUser INT ( 11 ) NOT NULL DEFAULT 0,
      remark VARCHAR ( 1000 ) NOT NULL DEFAULT '',
      orderNo VARCHAR ( 32 ) NOT NULL DEFAULT '' COMMENT '订单编号',
      userName VARCHAR ( 200 ) NOT NULL DEFAULT '' COMMENT '用户姓名',
    
      couponConfigId	int ( 11 ) NOT NULL DEFAULT 0 COMMENT '对应couponConfig ID',
      `status`	int ( 2 ) NOT NULL DEFAULT 0 COMMENT '券状态（1 已使用，2 已过期，3 未使用）',
    	money NUMERIC not null default 0 comment '优惠券金额',
      validityStartTime	datetime  COMMENT '有效期开始时间',
    	validityEndTime	datetime  COMMENT '有效期结束时间',
    	usedDate	datetime  COMMENT '使用日期',
    	sendPersion int(11) not null DEFAULT 0 comment '发放人',
      INDEX index1 ( orderNo ),
      INDEX index2 ( usedDate ),
    	 INDEX index3 ( validityStartTime ),
      INDEX index4 ( validityEndTime )
    );

##### 2019-04-15 语音电话
CREATE TABLE `qualityCheckingVoice` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `uuid` varchar(45) NOT NULL DEFAULT '' COMMENT 'uuid',
  `disabled` int(11) NOT NULL DEFAULT '0' COMMENT '删除标志',
  `remark` varchar(200) NOT NULL DEFAULT '' COMMENT '备注',
  `orderNo` varchar(50) NOT NULL DEFAULT '' COMMENT '订单编号',
  `extNumber` varchar(50) NOT NULL DEFAULT '' COMMENT '分机号码',
  `destNumber` varchar(50) NOT NULL DEFAULT '' COMMENT '拨打的电话号码',
  `downUrl` varchar(500) NOT NULL DEFAULT '' COMMENT '下载的全url路径',
  `attachmentSavePath` varchar(500) NOT NULL DEFAULT '' COMMENT '文件服务器的路径',
  `realName` varchar(100) NOT NULL DEFAULT '' COMMENT '姓名',
  `applyAmount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '申请金额',
  `applyDeadline` int(11) NOT NULL DEFAULT '0' COMMENT '申请期限',
  `userName` varchar(100) NOT NULL DEFAULT '' COMMENT '催收人员',
  `createUser` int(11) NOT NULL DEFAULT '0' COMMENT '创建用户',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateUser` int(11) NOT NULL DEFAULT '0' COMMENT '更新用户',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `answerStartTime` datetime DEFAULT NULL COMMENT '拨打开始时间',
  `recordBeginTime` datetime DEFAULT NULL COMMENT '录音开始时间',
  `recordEndTime` datetime DEFAULT NULL COMMENT '录音结束时间',
  `recordLength` int(11) NOT NULL DEFAULT '0' COMMENT '录音时长（秒）',
  `callState` int(11) NOT NULL DEFAULT '0' COMMENT '呼叫结果状态（0 默认呼叫已请求 1. 呼叫处理成功 2. 呼叫失败）',
  `callResult` int(11) NOT NULL DEFAULT '0' COMMENT '呼叫具体结果',
  `errorId` int(11) NOT NULL DEFAULT '0' COMMENT '结果返回的错误码',
  `callNode` int(11) NOT NULL DEFAULT '0' COMMENT '外呼节点 1 催收  2 电核',
  `callType` int(11) NOT NULL DEFAULT '0' COMMENT ' 1本人电话 2公司电话 3 紧急联系人 4 备选联系人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uuid_uq` (`uuid`),
  KEY `orderNo_index` (`orderNo`) USING BTREE,
  KEY `realName_index` (`realName`) USING BTREE,
  KEY `userName_index` (`userName`) USING BTREE,
  KEY `recordBeginTime_index` (`recordBeginTime`) USING BTREE,
  KEY `recordEndTime_index` (`recordEndTime`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8 COMMENT='质检语音表';
SET FOREIGN_KEY_CHECKS=1;


alter table manUser
add `voicePhone` varchar(50) NOT NULL DEFAULT '' COMMENT '语音电话';
