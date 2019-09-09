表相关
====

select count(1) from sysAutoReviewRule where disabled=0 and ruleResult ='2';


select * from flowRuleSet; -- 规则流程(FlowEnum)

select * from ruleParam; -- 规则参数



select * from scoreTemplate; -- 评分模板

select * from orderScore; -- 评分结果

select * from orderScoreDetail; -- 评分明细

select * from asyncTaskInfo; -- 异步任务（待签章订单）

select*from fraudUserOrderInfo;  -- 欺诈用户订单（后台管理excel导入）

select* from iziWhatsAppDetail;--- whatsapp明细版本

select *from kettle_job_msg; [kettle mongo数据导入到风控库用];

select * from loanLimitRuleResult; -- 100 50 降额规则(首借还款后调用判断是否降额，命中的规则放入该表)

select * from ordBlack; -- 拒绝规则（每个订单只记录一条）

select * from ordDeviceExtendInfo;  -- 自定义设备指纹(联系人+设备信息)

select * from orderContract; -- 电子签章下载签约后文档

select * from orderReviewStep; -- 紧急联系人，公司+本人


select * from riskErrorLog;  -- 审核异常单记录

select * from usrBlackList; -- 逾期，敏感用户黑名单

select * from usrFaceVerifyResult; -- asli 身份证和自拍照比对分

select * from usrIziVerifyResult;  -- izi 的数据IziService

select * from usrSignContractStep; -- 电子签章用

select * from usrVerifyResult; -- 身份验证结果










asli --》

digisign--》









---> 打标签--》通用--》600--》 pass
                    --> 100 -- pass
                       --  50 -- pass
                            -- reject



