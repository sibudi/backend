
update flowRuleSet set flowName='AUTO_CALL_OWNER' where ruleDetailType in ('AUTO_CALL_REJECT_OWNER_CALL_INVALID','AUTO_CALL_REJECT_OWNER_EXCEED_LIMIT');


update ruleParam set flowName='AUTO_CALL_OWNER' where ruleDetailType in ('AUTO_CALL_REJECT_OWNER_CALL_INVALID','AUTO_CALL_REJECT_OWNER_EXCEED_LIMIT');



