start transaction;


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
       'MULTI_LAST_LOAN_AMOUNT_OVERDUE_CURRENT_AMOUNT_BORROWING_COUNT','last_loan_amount&current_loan_amount&last_loan_overdue_borrowing_count',
       '160000#160000#0#3',
       2,1,15,2635,
       "V1", 3,0);


commit;