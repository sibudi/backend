use doit;
select * from sysParam order by id desc;
DELETE sysParam where sysKey like 'rdn:%';
INSERT INTO sysParam (uuid, createTime, updateTime, sysKey, sysValue) VALUES
(uuid(), now(), now(), 'rdn:disburse:channel', 'cimb,'),
(uuid(), now(), now(), 'rdn:repayment:channel', 'cimb,'),
(uuid(), now(), now(), 'rdn:notification:email:to', 'arief.halim@do-it.id;'),
(uuid(), now(), now(), 'rdn:notification:email:cc', 'arief.halim@do-it.id;'),
(uuid(), now(), now(), 'rdn:disburse:type', 'bulk'),
(uuid(), now(), now(), 'rdn:repayment:type', 'bulk'),
(uuid(), now(), now(), 'rdn:bulk:disburse:ratio:tcc', '0.7'),
(uuid(), now(), now(), 'rdn:bulk:disburse:ratio:gsi', '0.3'),
(uuid(), now(), now(), 'rdn:bulk:interest:ratio:lender', '0.75'),
(uuid(), now(), now(), 'rdn:bulk:interest:ratio:glotech', '0.25');
select * from sysParam order by id desc;