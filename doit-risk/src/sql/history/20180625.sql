update sysAutoReviewRule u set u.disabled=1,u.remark='20180625下线' ,
u.updateTime = now()
where u.ruleDetailType in ('COMB_SEX_AGE_MISSEDCALLINRATE','COMB_CALLANDCONTACT_MALE_MARRIAGE_EDUCATION');

update sysAutoReviewRule u set u.ruleValue='300',u.remark='近30天通话时长<1改为(近30天通话时长&男，阈值<300秒)' ,
u.updateTime = now(),
u.ruleDesc='近30天通话时长&男'
where u.ruleDetailType = 'RECENT_30_CALL_TIME';

update sysAutoReviewRule u set u.ruleResult = 1,u.ruleStatus=3 ,
u.remark='拒绝改为打标签',updateTime =now()
where u.ruleDetailType in ('RE_COUNT_NUM','SMS_OVERDUE_COUNT','COMB_2_RECORD_30SMSCOUNT');



commit;