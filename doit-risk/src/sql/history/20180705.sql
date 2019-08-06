
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',6,
  'INSTALL_APP_COUNT','首借累计app个数','',
  '',1,3,15,1340,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
  'MOBILE_IS_EMERGENCYTEL_FOR_OVERDUE30USERS','借款人手机号码是逾期30天以上用户的紧急联系人','',
  '',1,3,15,1345,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
  'MOBILE_AS_EMERGENCYTEL_COUNT','借款人手机号码已作为紧急联系人出现次数','',
  '',1,3,15,1350,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
  'EMERGENCYTEL_IS_OVERDUE30USERS','借款人手机号码已作为紧急联系人出现次数','',
  '',1,3,15,1355,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',4,
  'EMERGENCYTEL_NOT_IN_CALLRECORD','两个紧急联系人均不在通话记录中','',
  '',1,3,15,1355,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
  'DEVICEID_IS_OVERDUE30USERS','设备号命中逾期30天以上用户的设备号','',
  '',1,3,15,1360,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',3,
  'CONTACT_HIT_OVERDUE30USERS_COUNT','通讯录命中逾期30天黑名单次数','',
  '',1,3,15,1365,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',4,
  'CALL_RECORD_HIT_OVERDUE30USERS_COUNT','通话记录对象命中逾期30天及以上黑名单','',
  '',1,3,15,1370,"V1");

CREATE TABLE `usrBlackList` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `disabled` int(11) NOT NULL DEFAULT '0' COMMENT '删除标志',
  `remark` varchar(80)  DEFAULT '' COMMENT '备注',
  `mobileDes` varchar(50) NOT NULL DEFAULT '' COMMENT '手机号',
  `userUuid` varchar(80) NOT NULL DEFAULT '' COMMENT '用户uuid',
  `deviceId` varchar(80) DEFAULT '' COMMENT '设备号',
  `idCardNo` varchar(30) DEFAULT '' COMMENT '身份证号',
  `type` int(10) NOT NULL DEFAULT '0' COMMENT '0:逾期大于等于30天用户',
  `uuid` varchar(45) DEFAULT NULL,
  `createUser` int(11) DEFAULT '0',
  `createTime` datetime DEFAULT NULL,
  `updateUser` int(11) DEFAULT '0',
  `updateTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `mobile` (`mobileDes`),
  KEY `userUuid` (`userUuid`),
  KEY `deviceId` (`deviceId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 初始化逾期30天及以上的用户
insert into usrBlackList(uuid,userUuid,deviceId,idCardNo,mobileDes,createTime,updateTime)
SELECT
    REPLACE(UUID(), '-', ''),
    u.uuid,
    d.deviceId,
    u.idCardNo,
    u.mobileNumberDES,
    now(),
    now()
FROM
    usrUser u
        LEFT JOIN
   (select distinct dd.userUuid,dd.deviceId from ordDeviceInfo dd where dd.disabled=0 and dd.deviceId<>'' and dd.deviceId is not null
	and dd.orderNo in (
         SELECT
            o.uuid
        FROM
            ordOrder o
        WHERE
            o.disabled = 0
                AND o.refundTime IS NOT NULL
                AND DATEDIFF((CASE
                        WHEN o.actualRefundTime IS NULL THEN NOW()
                        ELSE o.actualRefundTime
                    END),
                    o.refundTime) >= 30)
   )  d on u.uuid = d.userUuid
WHERE
    u.disabled=0
    and u.uuid IN (SELECT
            userUuid
        FROM
            ordOrder o
        WHERE
            o.disabled = 0
                AND o.refundTime IS NOT NULL
                AND DATEDIFF((CASE
                        WHEN o.actualRefundTime IS NULL THEN NOW()
                        ELSE o.actualRefundTime
                    END),
                    o.refundTime) >= 30);







commit;