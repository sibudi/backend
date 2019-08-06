insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'HIT_FRAUD_USER_INFO','命中欺诈用户组合属性','',2,1,15,1520,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',24,
  'MULTI_HIT_FRAUD_USER_INFO','命中欺诈用户组合属性-复借','',2,1,15,1525,"V1");

CREATE TABLE fraudUserOrderInfo
(
  id INT,
  uuid VARCHAR(50),
  realName VARCHAR(200) DEFAULT '' COMMENT '用户姓名',
  motherName VARCHAR(200) DEFAULT '' COMMENT '母亲姓名',
  orderAddressDetail VARCHAR(500) DEFAULT '' COMMENT '下单地址',
  emergencyTel VARCHAR(50) DEFAULT '' COMMENT '第一紧急联系人电话',
  emergencyTel2 VARCHAR(50) DEFAULT '' COMMENT '第二紧急联系人电话',
  emergencyName VARCHAR(200) COMMENT '第一紧急联系人姓名',
  emergencyName2 VARCHAR(200) DEFAULT '' COMMENT '第二紧急联系人姓名',
  age TINYINT COMMENT '年龄',
  companyName VARCHAR(200) DEFAULT '' COMMENT '公司名称',
  companyAddress VARCHAR(500) DEFAULT '' COMMENT '公司地址：省市+大区+小区',
  companyTel VARCHAR(50) DEFAULT '' COMMENT '公司电话',
  firstCommonContactTel VARCHAR(50) DEFAULT '' COMMENT '第一常用联系人',
  secondCommonContactTel VARCHAR(50) DEFAULT '' COMMENT '第一常用联系人',
  firstCommonContactName VARCHAR(50) DEFAULT '' COMMENT '第一常用联系人姓名',
  secondCommonContactName VARCHAR(50) DEFAULT '' COMMENT '第二常用联系人姓名',
  ipAddress VARCHAR(50) DEFAULT '' COMMENT 'ip地址',
  pictureCount INT  COMMENT '图片数量',
  liveAddress VARCHAR(50) DEFAULT '' COMMENT '居住地址：省市+大区+小区',
  userUuid VARCHAR(50) COMMENT '用户userUuid',
  orderNo VARCHAR(50) COMMENT '订单编号',
  disabled tinyint default 0,
  remark VARCHAR(200) DEFAULT '',
  createUser int DEFAULT 0,
  updateUser int DEFAULT 0,
  createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null COMMENT '创建时间',
  updateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null COMMENT '更新时间'
);
ALTER TABLE fraudUserOrderInfo COMMENT = '欺诈用户订单信息表';

