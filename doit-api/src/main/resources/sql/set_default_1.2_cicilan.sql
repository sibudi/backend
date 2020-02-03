-- check before insert
SELECT * FROM sysParam WHERE sysKey = 'system:default:isinstallment'

-- if not exist then insert
INSERT INTO sysParam (disabled, uuid, createUser, updateUser, createTime, updateTime, remark, sysKey, sysValue, description)
VALUES (0, (SELECT UUID()), 0, 0, CURRENT_DATE, CURRENT_DATE, '', 'system:default:isinstallment', 
	'9', '9: all user, 1: new user') ;

-- check before insert
SELECT * FROM sysParam WHERE sysKey = 'system:default:installment:product'

-- if not exist then insert
INSERT INTO sysParam (disabled, uuid, createUser, updateUser, createTime, updateTime, remark, sysKey, sysValue, description)
VALUES (0, (SELECT UUID()), 0, 0, CURRENT_DATE, CURRENT_DATE, '', 'system:default:installment:product', 
	'1006', 'default product. need system:default:isinstallment')


#add sales in product channel, so we can create report later using usersource field
INSERT INTO doit.sysProductChannel
(uuid, disabled, createTime, updateTime, createUser, updateUser, remark, productUuid, borrowingAmount, borrowingTerm, productType, dayRate, channel)
VALUES((SELECT UUID()), 0, CURRENT_DATE, CURRENT_DATE, 0, 0, 'SALES-Hertati', '1006', 1200000, 3, 1, 0.0017, 9002);

INSERT INTO doit.sysProductChannel
(uuid, disabled, createTime, updateTime, createUser, updateUser, remark, productUuid, borrowingAmount, borrowingTerm, productType, dayRate, channel)
VALUES((SELECT UUID()), 0, CURRENT_DATE, CURRENT_DATE, 0, 0, 'SALES-Anji', '1006', 1200000, 3, 1, 0.0017, 9003);

INSERT INTO doit.sysProductChannel
(uuid, disabled, createTime, updateTime, createUser, updateUser, remark, productUuid, borrowingAmount, borrowingTerm, productType, dayRate, channel)
VALUES((SELECT UUID()), 0, CURRENT_DATE, CURRENT_DATE, 0, 0, 'SALES-Gita', '1006', 1200000, 3, 1, 0.0017, 9004);

INSERT INTO doit.sysProductChannel
(uuid, disabled, createTime, updateTime, createUser, updateUser, remark, productUuid, borrowingAmount, borrowingTerm, productType, dayRate, channel)
VALUES((SELECT UUID()), 0, CURRENT_DATE, CURRENT_DATE, 0, 0, 'SALES-Fajri', '1006', 1200000, 3, 1, 0.0017, 9005);
