Use doit;


#insert new product channel: 84
INSERT INTO doit.sysProductChannel
(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,productUuid,borrowingAmount,borrowingTerm,productType,dayRate,channel) VALUES
(4,0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0,0,'',1006,1200000,3,1,0.0017,84)

#insert new product installment
INSERT INTO doit.sysProduct
(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,productCode,borrowingAmount,borrowingTerm,dueFeeRate,dueFee,interestRate,interest,overdueFee,overdueRate1,overdueRate2,productLevel,productOrder,termAmount,productType,dayRate,rate1,rate2,rate3,rate4,rate5,rate6) VALUES
(1006,0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0,0,'1.2 Mio installment every 2 week 400k',1006,1200000,3,0.196,235200,0,0,40000,0.01,0.01,1006,1006,400000,202,0.196,0.0007,0.0007,0.0007,0.0007,0.0007,0.0003);

