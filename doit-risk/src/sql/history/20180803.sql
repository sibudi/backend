-- 新增和名单用户相关的规则

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
  'IMEI_IN_OVERDUE30_BLACK_LIST_USER','申请手机IMEI命中逾期30天以上用户IMEI黑名单','命中','',2,1,15,1380,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
  'ID_CARD_IN_OVERDUE30_BLACK_LIST_USER','申请人身份证命中逾期30天以上用户身份证号黑名单','命中','',2,1,15,1385,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
  'MOBILE_IN_OVERDUE30_BLACK_LIST','申请手机号命中逾期30天以上用户手机号黑名单','命中','',2,1,15,1390,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
  'EMERGENCY_TEL_IS_OVERDUE30_BLACK_LIST_USER_EMERGENCY_TEL','申请人的紧急联系人手机号码是逾期30天以上用户的紧急联系人','命中','',2,1,15,1395,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
  'BANKCARD_NUMBER_IN_OVERDUE30_BLACK_LIST_USER_BANKCARD','申请人银行卡命中逾期30天以上用户银行卡号黑名单','命中','',2,1,15,1400,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
  'EMERGENCY_TEL_IN_OVERDUE15_BLACK_LIST_USER_COUNT','申请人的紧急联系人手机号码命中逾期15天以上用户的次数','','',1,3,15,1405,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
  'MOBILE_IN_OVERDUE15_BLACK_LIST_USER_EMERGENCY_TEL','申请人手机号码是逾期15天以上用户的紧急联系人','命中','',2,1,15,1410,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
  'SMS_PHONES_IN_OVERDUE15_BLACK_LIST_USER','申请人短信通话对象命中逾期15天以上黑名单','命中','',2,1,15,1415,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
  'CONTACT_PHONES_IN_OVERDUE15_BLACK_LIST_USER_COUNT','申请人通讯录号码命中逾期15天黑名单个数','','',1,3,15,1420,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
  'MOBILE_IS_FRAUD_USER_EMERGENCY_TEL','申请人手机号命中欺诈用户的紧急联系人','命中','',2,1,15,1425,"V1");

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleCondition,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',7,
  'MOBILE_IS_FRAUD_USER_CALL_RECORD_COUNT','申请人手机号命中欺诈用户的通话记录次数','大于','0',2,1,15,1430,"V1");


-- 初始化30天黑名单

-- 逾期30天用户
insert into usrBlackList(uuid,userUuid,deviceId,imei,idCardNo,mobileDes,linkManContactNumber1,linkManContactNumber2,bankCardNumber,createTime,
                         updateTime,type)
SELECT
  REPLACE(UUID(), '-', ''),
  u.uuid userUuid,
  d.deviceId,
  d.IMEI,
  u.idCardNo,
  u.mobileNumberDES,
  replace(replace(link.linkman1,'-',''),' ',''),
  replace(replace(link.linkman2,'-',''),' ',''),
  ub.bankNumberNo,
  now() createTime,
  now() upateTime,
  3
FROM
  usrUser u
  LEFT JOIN
  -- 设备信息
  (SELECT DISTINCT
     dd.userUuid,
     dd.deviceId,
     dd.IMEI,
     count(1)
   FROM ordDeviceInfo dd
   WHERE disabled = 0
   GROUP BY dd.userUuid, dd.deviceId, dd.IMEI
  ) d ON u.uuid = d.userUuid
  LEFT JOIN
  -- 联系人信息
  (
    SELECT
      u1.userUuid,
      u1.contactsMobile linkman1,
      u2.contactsMobile linkman2
    FROM usrLinkManInfo u1
      JOIN usrLinkManInfo u2 ON u1.userUuid = u2.userUuid
    WHERE u1.sequence = 1 AND u1.disabled = 0
          AND u2.sequence = 2 AND u2.disabled = 0
  ) link ON u.uuid = link.userUuid
  -- 银行卡信息
  LEFT JOIN
  usrBank ub ON u.uuid = ub.userUuid AND ub.isRecent = 0 AND ub.disabled = 1
WHERE
  u.disabled = 0
  AND u.uuid IN (
    --  逾期30天及以上用户
    SELECT userUuid
    FROM
      ordOrder o
    WHERE
      o.disabled = 0
      AND o.refundTime IS NOT NULL
      AND DATEDIFF((CASE
                    WHEN o.actualRefundTime IS NULL
                      THEN NOW()
                    ELSE o.actualRefundTime
                    END),
                   o.refundTime) >= 30);

-- [15,30)
insert into usrBlackList(uuid,userUuid,deviceId,imei,idCardNo,mobileDes,linkManContactNumber1,linkManContactNumber2,bankCardNumber,createTime,
                         updateTime,type)
  SELECT
    REPLACE(UUID(), '-', ''),
    u.uuid userUuid,
    d.deviceId,
    d.IMEI,
    u.idCardNo,
    u.mobileNumberDES,
    replace(replace(link.linkman1,'-',''),' ',''),
    replace(replace(link.linkman2,'-',''),' ',''),
    ub.bankNumberNo,
    now() createTime,
    now() upateTime,
    4
  FROM
    usrUser u
    LEFT JOIN
    -- 设备信息
    (SELECT DISTINCT
       dd.userUuid,
       dd.deviceId,
       dd.IMEI,
       count(1)
     FROM ordDeviceInfo dd
     WHERE disabled = 0
     GROUP BY dd.userUuid, dd.deviceId, dd.IMEI
    ) d ON u.uuid = d.userUuid
    LEFT JOIN
    -- 联系人信息
    (
      SELECT
        u1.userUuid,
        u1.contactsMobile linkman1,
        u2.contactsMobile linkman2
      FROM usrLinkManInfo u1
        JOIN usrLinkManInfo u2 ON u1.userUuid = u2.userUuid
      WHERE u1.sequence = 1 AND u1.disabled = 0
            AND u2.sequence = 2 AND u2.disabled = 0
    ) link ON u.uuid = link.userUuid
    -- 银行卡信息
    LEFT JOIN
    usrBank ub ON u.uuid = ub.userUuid AND ub.isRecent = 0 AND ub.disabled = 1
  WHERE
    u.disabled = 0
    AND u.uuid IN (
      --  逾期30天及以上用户
      SELECT userUuid
      FROM
        ordOrder o
      WHERE
        o.disabled = 0
        AND o.refundTime IS NOT NULL
        AND DATEDIFF((CASE
                      WHEN o.actualRefundTime IS NULL
                        THEN NOW()
                      ELSE o.actualRefundTime
                      END),
                     o.refundTime) >= 15
       and DATEDIFF((CASE
                     WHEN o.actualRefundTime IS NULL
                       THEN NOW()
                     ELSE o.actualRefundTime
                     END),
                    o.refundTime) <30);



-- 逾期30天用户
insert into usrBlackList(uuid,userUuid,deviceId,imei,idCardNo,mobileDes,linkManContactNumber1,linkManContactNumber2,bankCardNumber,createTime,
                         updateTime,type)
SELECT
  REPLACE(UUID(), '-', ''),
  u.uuid userUuid,
  d.deviceId,
  d.IMEI,
  u.idCardNo,
  u.mobileNumberDES,
  replace(replace(link.linkman1,'-',''),' ',''),
  replace(replace(link.linkman2,'-',''),' ',''),
  ub.bankNumberNo,
  now() createTime,
  now() upateTime,
  1
FROM
  usrUser u
  LEFT JOIN
  -- 设备信息
  (SELECT DISTINCT
     dd.userUuid,
     dd.deviceId,
     dd.IMEI,
     count(1)
   FROM ordDeviceInfo dd
   WHERE disabled = 0
   GROUP BY dd.userUuid, dd.deviceId, dd.IMEI
  ) d ON u.uuid = d.userUuid
  LEFT JOIN
  -- 联系人信息
  (
    SELECT
      u1.userUuid,
      u1.contactsMobile linkman1,
      u2.contactsMobile linkman2
    FROM usrLinkManInfo u1
      JOIN usrLinkManInfo u2 ON u1.userUuid = u2.userUuid
    WHERE u1.sequence = 1 AND u1.disabled = 0
          AND u2.sequence = 2 AND u2.disabled = 0
  ) link ON u.uuid = link.userUuid
  -- 银行卡信息
  LEFT JOIN
  usrBank ub ON u.uuid = ub.userUuid AND ub.isRecent = 0 AND ub.disabled = 1
WHERE
  u.disabled = 0
  AND u.uuid IN (
    --  逾期30天及以上用户
    SELECT userUuid
    FROM
      ordOrder o
    WHERE
      o.disabled = 0
      AND o.refundTime IS NOT NULL
      AND DATEDIFF((CASE
                    WHEN o.actualRefundTime IS NULL
                      THEN NOW()
                    ELSE o.actualRefundTime
                    END),
                   o.refundTime) >= 30);

-- [15,30)
insert into usrBlackList(uuid,userUuid,deviceId,imei,idCardNo,mobileDes,linkManContactNumber1,linkManContactNumber2,bankCardNumber,createTime,
                         updateTime,type)
  SELECT
    REPLACE(UUID(), '-', ''),
    u.uuid userUuid,
    d.deviceId,
    d.IMEI,
    u.idCardNo,
    u.mobileNumberDES,
    replace(replace(link.linkman1,'-',''),' ',''),
    replace(replace(link.linkman2,'-',''),' ',''),
    ub.bankNumberNo,
    now() createTime,
    now() upateTime,
    2
  FROM
    usrUser u
    LEFT JOIN
    -- 设备信息
    (SELECT DISTINCT
       dd.userUuid,
       dd.deviceId,
       dd.IMEI,
       count(1)
     FROM ordDeviceInfo dd
     WHERE disabled = 0
     GROUP BY dd.userUuid, dd.deviceId, dd.IMEI
    ) d ON u.uuid = d.userUuid
    LEFT JOIN
    -- 联系人信息
    (
      SELECT
        u1.userUuid,
        u1.contactsMobile linkman1,
        u2.contactsMobile linkman2
      FROM usrLinkManInfo u1
        JOIN usrLinkManInfo u2 ON u1.userUuid = u2.userUuid
      WHERE u1.sequence = 1 AND u1.disabled = 0
            AND u2.sequence = 2 AND u2.disabled = 0
    ) link ON u.uuid = link.userUuid
    -- 银行卡信息
    LEFT JOIN
    usrBank ub ON u.uuid = ub.userUuid AND ub.isRecent = 0 AND ub.disabled = 1
  WHERE
    u.disabled = 0
    AND u.uuid IN (
      --  逾期30天及以上用户
      SELECT userUuid
      FROM
        ordOrder o
      WHERE
        o.disabled = 0
        AND o.refundTime IS NOT NULL
        AND DATEDIFF((CASE
                      WHEN o.actualRefundTime IS NULL
                        THEN NOW()
                      ELSE o.actualRefundTime
                      END),
                     o.refundTime) >= 15
       and DATEDIFF((CASE
                     WHEN o.actualRefundTime IS NULL
                       THEN NOW()
                     ELSE o.actualRefundTime
                     END),
                    o.refundTime) <30);
