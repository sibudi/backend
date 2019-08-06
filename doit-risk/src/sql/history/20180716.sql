
update sysAutoReviewRule set ruleValue = 60,ruleResult=1,ruleStatus=3,updateTime = now() where ruleDetailType = 'YITU_SCORE_LIMIT';

update sysAutoReviewRule set ruleResult=1,ruleStatus=3,updateTime = now() where ruleDetailType = 'CASH2_FACE_PLUS_PLUS_SCORE';

commit;