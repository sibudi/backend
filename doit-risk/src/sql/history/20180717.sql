-- 增加facebook公司名称一直规则【免核】
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'免初审和复审',22,
  'FACEBOOK_COMPANY_CONTAIN','facebook公司名称一致',
  '',1,3,15,1375,"V1");
-- 原来facebook公司名称不一致记录为打标签规则

update sysAutoReviewRule u set u.ruleDesc = 'facebook公司名称不一致',u.remark='免初审和复审-非免核规则，和FACEBOOK_COMPANY_CONTAIN区分',
u.ruleType = '10',u.updateTime = now() where u.ruleDetailType = 'FACEBOOK_COMPANY_NOT_CONTAIN' and disabled=0 ;


-- 原来FACEBOOK_COMPANY_NOT_CONTAIN规则的数据统一增加到FACEBOOK_COMPANY_CONTAIN规则中
insert into ordRiskRecord(uuid,createTime,updateTime,remark,userUuid,orderNo,ruleType,ruleDetailType,ruleDesc,ruleRealValue)

select replace(uuid(), '-', ''),now(),now(),'新规则上线数据补充',userUuid,orderNo,22,'FACEBOOK_COMPANY_CONTAIN','facebook公司名称一致','false'
from ordRiskRecord where ruleDetailType ='FACEBOOK_COMPANY_NOT_CONTAIN'
and ruleRealValue='true'
and disabled=0

union all
select replace(uuid(), '-', ''),now(),now(),'新规则上线数据补充',userUuid,orderNo,22,'FACEBOOK_COMPANY_CONTAIN','facebook公司名称一致','true'
from ordRiskRecord where ruleDetailType ='FACEBOOK_COMPANY_NOT_CONTAIN'
and ruleRealValue='false'
and disabled=0

union all
select replace(uuid(), '-', ''),now(),now(),'新规则上线数据补充',userUuid,orderNo,22,'FACEBOOK_COMPANY_CONTAIN','facebook公司名称一致','null'
from ordRiskRecord where ruleDetailType ='FACEBOOK_COMPANY_NOT_CONTAIN'
and ruleRealValue='null'
and disabled=0

;


commit;


