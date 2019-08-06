UPDATE ordOrder o
SET
    o.status = 12
WHERE
    o.uuid in ('011905070019376040','011905061345598740','011905061622085340','011905061630513230','011812111501258140')

    ;  -- blacklist

 INSERT INTO `doit`.`ordBlack`
(
`uuid`,`createTime`,`updateTime`,`remark`,`userUuid`,`orderNo`,`ruleValue`,`ruleHitNo`,`ruleRealValue`,`responseMessage`,`ruleRejectDay`)
select
replace(uuid(), '-', ''),now(),now(),'',userUuid,uuid,'','24-ADVANCE_BLACKLIST','true','命中advance黑名单','360'
from ordOrder o where o.uuid = '011905070019376040';

INSERT INTO `doit`.`ordBlack`
(
`uuid`,`createTime`,`updateTime`,`remark`,`userUuid`,`orderNo`,`ruleValue`,`ruleHitNo`,`ruleRealValue`,`responseMessage`,`ruleRejectDay`)
select
replace(uuid(), '-', ''),now(),now(),'手动异常处理190507',userUuid,uuid,'','24-ADVANCE_BLACKLIST','true','命中advance黑名单','360'
from ordOrder o where o.uuid = '011905061345598740';

 INSERT INTO `doit`.`ordBlack`
(
`uuid`,`createTime`,`updateTime`,`remark`,`userUuid`,`orderNo`,`ruleValue`,`ruleHitNo`,`ruleRealValue`,`responseMessage`,`ruleRejectDay`)
select
replace(uuid(), '-', ''),now(),now(),'手动异常处理190507',userUuid,uuid,'','24-ADVANCE_BLACKLIST','true','命中advance黑名单','360'
from ordOrder o where o.uuid = '011905070019376040';

INSERT INTO `doit`.`ordBlack`
(
`uuid`,`createTime`,`updateTime`,`remark`,`userUuid`,`orderNo`,`ruleValue`,`ruleHitNo`,`ruleRealValue`,`responseMessage`,`ruleRejectDay`)
select
replace(uuid(), '-', ''),now(),now(),'手动异常处理190507',userUuid,uuid,'','24-ADVANCE_BLACKLIST','true','命中advance黑名单','360'
from ordOrder o where o.uuid = '011905061622085340';

INSERT INTO `doit`.`ordBlack`
(
`uuid`,`createTime`,`updateTime`,`remark`,`userUuid`,`orderNo`,`ruleValue`,`ruleHitNo`,`ruleRealValue`,`responseMessage`,`ruleRejectDay`)
select
replace(uuid(), '-', ''),now(),now(),'手动异常处理190507',userUuid,uuid,'','24-ADVANCE_BLACKLIST','true','命中advance黑名单','360'
from ordOrder o where o.uuid = '011905061630513230';


INSERT INTO `doit`.`ordBlack`
(
`uuid`,`createTime`,`updateTime`,`remark`,`userUuid`,`orderNo`,`ruleValue`,`ruleHitNo`,`ruleRealValue`,`responseMessage`,`ruleRejectDay`)
select
replace(uuid(), '-', ''),now(),now(),'手动异常处理190507',userUuid,uuid,'','24-ADVANCE_BLACKLIST','true','命中advance黑名单','360'
from ordOrder o where o.uuid = '011812111501258140';



UPDATE ordOrder o
SET
    o.status = 12
WHERE
    o.uuid in ('011904061051251970')

    ;  -- multi-platform


INSERT INTO `doit`.`ordBlack`
(
`uuid`,`createTime`,`updateTime`,`remark`,`userUuid`,`orderNo`,`ruleValue`,`ruleHitNo`,`ruleRealValue`,`responseMessage`,`ruleRejectDay`)
select
replace(uuid(), '-', ''),now(),now(),'手动异常处理190507',userUuid,uuid,'','24-ADVANCE_MULTI_PLATFORM','true','advance近7天内多头借贷异常','180'
from ordOrder o where o.uuid = '011904061051251970';

