start transaction;


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
       'MULTI_FIRST_AMOUNT_LAST_OVERDUE_DAYS_CUR_AMOUNT_BORROWING_COUNT','first_loan_150&last_loan_overdue&borrowCount','300000#0#160000#2',2,1,15,2570,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
       'MULTI_FIRST_AMOUNT_LAST_OVERDUE_DAYS_CUR_AMOUNT_BORROWING_COUNT_N','first_loan_150&last_loan_overdue&borrowCountN','300000#1#160000#3',2,1,15,2575,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
       'MULTI_FIRST_AMOUNT_CUR_AMOUNT_BORROWING_COUNT','first_amount&current_amoun&borrowCount','160000#160000#2',2,1,15,2580,
       "V1", 3,0);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
                              ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',20,
       'MULTI_FIRST_AMOUNT_LAST_OVERDUE_DAYS_BORROWING_COUNT','first_amount&last_loan_overdue&borrowCount','160000#0#2',2,1,15,2580,
       "V1", 3,0);


commit;
