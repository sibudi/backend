
/****
发布：task/api/risk
*/

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',26,
  'SPECIAL_RULE_100_RMB_PRODUCT','改变贷款金额为100RMB规则','',1,3,15,1870,"V1",2);


-- 上线拒绝规则
update sysAutoReviewRule s set s.ruleResult = 2, ruleStatus = 1 where s.ruleDetailType
in ('FIRST_LINKMAN_EXISTS','EMAIL_EXISTS','SAME_WORK_ADDRESS_AND_ORDER_ADDRESS_EXISTS'
,'SAME_HOME_ADDRESS_AND_ORDER_ADDRESS_EXISTS','SAME_WORK_ADDRESS_AND_HOME_ADDRESS_EXISTS');

-- 更改表

ALTER TABLE sysAutoReviewRule ADD specifiedProduct tinyint DEFAULT 0;
-- 新增表
CREATE TABLE `orderChangeHistory` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(45) NOT NULL DEFAULT '' COMMENT '订单编号',
  `disabled` int(11) NOT NULL DEFAULT '0' COMMENT '删除标志',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime NOT NULL COMMENT '更新时间',
  `createUser` int(11) NOT NULL DEFAULT '0' COMMENT '创建用户',
  `updateUser` int(11) NOT NULL DEFAULT '0' COMMENT '更新用户',
  `remark` varchar(200) NOT NULL DEFAULT '' COMMENT '备注',
  `productUuid` varchar(45) NOT NULL DEFAULT '' COMMENT '产品的code编号',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '订单状态',
  `orderStep` int(11) NOT NULL DEFAULT '0' COMMENT '订单步骤',
  `amountApply` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单金额',
  `borrowingTerm` int(11) NOT NULL DEFAULT '0' COMMENT '订单期限',
  `serviceFee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '服务费',
  `interest` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '利息',
  `borrowingCount` int(11) NOT NULL DEFAULT '0' COMMENT '借款次数',
  `channel` int(11) NOT NULL DEFAULT '0' COMMENT '订单渠道',
  `payChannel` int(2) NOT NULL DEFAULT '0' COMMENT '资金渠道，0：自有资金',
  `orderPositionId` varchar(45) NOT NULL DEFAULT '' COMMENT '下单位置id',
  `userUuid` varchar(45) NOT NULL DEFAULT '' COMMENT '用户UUID',
  `userBankUuid` varchar(45) NOT NULL DEFAULT '' COMMENT '用户打款银行UUID',
  `applyTime` datetime DEFAULT NULL COMMENT '申请时间',
  `lendingTime` datetime DEFAULT NULL COMMENT '放款日期',
  `refundTime` datetime DEFAULT NULL COMMENT '应还款日期',
  `actualRefundTime` datetime DEFAULT NULL COMMENT '实际还款日期',
  `firstChecker` int(4) DEFAULT '0',
  `secondChecker` int(4) DEFAULT '0',
  `orderType` int(4) DEFAULT '0' COMMENT '借款评分',
  `lendingRating` decimal(10,2) DEFAULT '0.00' COMMENT '借款评分',
  `overOneApply` int(4) DEFAULT '0' COMMENT '是否多头借贷（0否;1是）',
  `requestGuarantee` varchar(100) DEFAULT '' COMMENT '抵押物',
  `paymentFrequencyType` int(4) DEFAULT '0' COMMENT '还款频率类型（1 = daily, 2 = weekly, 3 = monthly, 4 = quarter, 5 = semester, 6 = yearly）',
  `paymentType` int(4) DEFAULT '0' COMMENT '还款方式（1 = amortization, 2= bullet payment, 3 = discount, 4 = grace period, 5 = other）',
  `otherLoaninformation` varchar(45) DEFAULT '' COMMENT '剩余借款信息',
  `loanStatus` int(4) DEFAULT '0' COMMENT '放款状态（1、未逾期 2、已逾期 3、坏账）',
  `approvedAmount` varchar(45) DEFAULT '' COMMENT '审批通过金额',
  `markStatus` varchar(45) DEFAULT '0' COMMENT '推标状态 （0 未发标 1 发标成功 2 发标失败）',
  `score` varchar(45) DEFAULT '' COMMENT '订单打分',
  `thirdType` int(4) DEFAULT '0' COMMENT '第三方订单标示  1 CashCash\r\n',
  PRIMARY KEY (`id`),
  KEY `idx_userUuid` (`userUuid`),
  KEY `idx_orderNo` (`uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='订单产品信息修改记录表';


-- 设置100rmb产品可放开规则
update sysAutoReviewRule s set specifiedProduct = 1
where s.ruleDetailType in (
    'CONTACT_SENSITIVI_COUNT','RECENT_30_CALL_RATE','COMB_SEX_RECENT30_MISSEDCALLRATE',
                           'SHORT_MESSAGE_KEY_INfO_EMPTY','COMB_3_CHILDRENAMOUNT'
                           ,'COMB_MOBILE_CAP_MARITAL_EDUCATION',
                           'PHONE_BRAND','AUTO_CALL_REJECT_BACKUP_LINKMAN_VALID_COUNT',
    'FACEBOOK_AVERAGE_MONTH_COMMENT_FEMALE','SEX_MARRIAGE_EDUCATION'

    ) and s.disabled=0;


-- 增加开关

INSERT INTO doit.sysParam (disabled, uuid, createUser, updateUser, createTime, updateTime, remark, sysKey, sysValue, description) VALUES ( 0, '',
0, 0, '2018-11-14 03:26:27', '2018-11-14 03:26:29', '', 'risk:100rmb_product:switch:iOS', 'false', '100RMB产品ios开关');
INSERT INTO doit.sysParam (disabled, uuid, createUser, updateUser, createTime, updateTime, remark, sysKey, sysValue, description) VALUES ( 0, '',
0, 0, '2018-11-14 03:27:12', '2018-11-14 03:27:14', '', 'risk:100rmb_product:switch:android', 'false', '100RMB产品android开关');


-- 增加产品配置

INSERT INTO doit.sysProduct (id, uuid, disabled, createTime, updateTime, createUser, updateUser, remark, productCode, borrowingAmount, borrowingTerm, dueFeeRate, dueFee, interestRate, interest, overdueFee, overdueRate1, overdueRate2, productLevel, productOrder) VALUES (29, '29', 1, '2018-11-13 16:06:32', '2018-11-13 16:06:32', 0, 0, '降额产品', '', 200000.00, 7, 0.20, 40000.00, 0.36, 1400.00, 40000.00, 0.01, 0.02, 1, 8);
INSERT INTO doit.sysProduct (id, uuid, disabled, createTime, updateTime, createUser, updateUser, remark, productCode, borrowingAmount, borrowingTerm, dueFeeRate, dueFee, interestRate, interest, overdueFee, overdueRate1, overdueRate2, productLevel, productOrder) VALUES (30, '30', 1, '2018-11-13 16:06:32', '2018-11-13 16:06:32', 0, 0, '降额产品', '', 200000.00, 8, 0.20, 40000.00, 0.36, 1600.00, 40000.00, 0.01, 0.02, 1, 8);
INSERT INTO doit.sysProduct (id, uuid, disabled, createTime, updateTime, createUser, updateUser, remark, productCode, borrowingAmount, borrowingTerm, dueFeeRate, dueFee, interestRate, interest, overdueFee, overdueRate1, overdueRate2, productLevel, productOrder) VALUES (31, '31', 1, '2018-11-13 16:06:32', '2018-11-13 16:06:32', 0, 0, '降额产品', '', 200000.00, 9, 0.20, 40000.00, 0.36, 1800.00, 40000.00, 0.01, 0.02, 1, 8);
INSERT INTO doit.sysProduct (id, uuid, disabled, createTime, updateTime, createUser, updateUser, remark, productCode, borrowingAmount, borrowingTerm, dueFeeRate, dueFee, interestRate, interest, overdueFee, overdueRate1, overdueRate2, productLevel, productOrder) VALUES (32, '32', 1, '2018-11-13 16:06:32', '2018-11-13 16:06:32', 0, 0, '降额产品', '', 200000.00, 10, 0.20, 40000.00, 0.36, 2000.00, 40000.00, 0.01, 0.02, 1, 8);
INSERT INTO doit.sysProduct (id, uuid, disabled, createTime, updateTime, createUser, updateUser, remark, productCode, borrowingAmount, borrowingTerm, dueFeeRate, dueFee, interestRate, interest, overdueFee, overdueRate1, overdueRate2, productLevel, productOrder) VALUES (33, '33', 1, '2018-11-13 16:06:32', '2018-11-13 16:06:32', 0, 0, '降额产品', '', 200000.00, 11, 0.20, 40000.00, 0.36, 2200.00, 40000.00, 0.01, 0.02, 1, 8);
INSERT INTO doit.sysProduct (id, uuid, disabled, createTime, updateTime, createUser, updateUser, remark, productCode, borrowingAmount, borrowingTerm, dueFeeRate, dueFee, interestRate, interest, overdueFee, overdueRate1, overdueRate2, productLevel, productOrder) VALUES (34, '34', 1, '2018-11-13 16:06:32', '2018-11-13 16:06:32', 0, 0, '降额产品', '', 200000.00, 12, 0.20, 40000.00, 0.36, 2400.00, 40000.00, 0.01, 0.02, 1, 8);
INSERT INTO doit.sysProduct (id, uuid, disabled, createTime, updateTime, createUser, updateUser, remark, productCode, borrowingAmount, borrowingTerm, dueFeeRate, dueFee, interestRate, interest, overdueFee, overdueRate1, overdueRate2, productLevel, productOrder) VALUES (35, '35', 1, '2018-11-13 16:06:32', '2018-11-13 16:06:32', 0, 0, '降额产品', '', 200000.00, 13, 0.20, 40000.00, 0.36, 2600.00, 40000.00, 0.01, 0.02, 1, 8);
INSERT INTO doit.sysProduct (id, uuid, disabled, createTime, updateTime, createUser, updateUser, remark, productCode, borrowingAmount, borrowingTerm, dueFeeRate, dueFee, interestRate, interest, overdueFee, overdueRate1, overdueRate2, productLevel, productOrder) VALUES (36, '36', 1, '2018-11-13 16:06:32', '2018-11-13 16:06:32', 0, 0, '降额产品', '', 200000.00, 14, 0.20, 40000.00, 0.36, 2800.00, 40000.00, 0.01, 0.02, 1, 8);