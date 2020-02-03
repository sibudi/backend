use doit;
select * from sysParam order by id desc;
DELETE sysParam where sysKey like 'rdn:%';
INSERT INTO sysParam (uuid, createTime, updateTime, sysKey, sysValue) VALUES
(uuid(), now(), now(), 'rdn:disburse:channel', 'cimb,'),
(uuid(), now(), now(), 'rdn:repayment:channel', 'cimb,'),
(uuid(), now(), now(), 'rdn:notification:email:to', 'arief.halim@do-it.id;'),
(uuid(), now(), now(), 'rdn:notification:email:cc', 'arief.halim@do-it.id;'),
(uuid(), now(), now(), 'rdn:disburse:type', 'bulk'),
(uuid(), now(), now(), 'rdn:repayment:type', 'bulk');
select * from sysParam order by id desc;