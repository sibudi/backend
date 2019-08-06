
insert into usrBlackList(uuid,remark,deviceId,imei,idCardNo,mobileDes,linkManContactNumber1,linkManContactNumber2,bankCardNumber,createTime,
                         updateTime,type)
values
(REPLACE(UUID(), '-', ''),'催收黑名单-Emmy Pristiwati Gerhananingsih','','','3515185206830002','mhuyIR2I08TIv4A+LAa0zw==','','','',now(),now(),8);
