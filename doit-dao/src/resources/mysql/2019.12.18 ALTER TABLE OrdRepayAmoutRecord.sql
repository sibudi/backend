Use doit;
select count(*) from ordRepayAmoutRecord;
ALTER TABLE ordRepayAmoutRecord 
ADD COLUMN `serviceFee` decimal(10,2) AFTER actualRepayAmout,
ADD COLUMN `actualDisbursedAmount` decimal(10,2) AFTER actualRepayAmout,
ADD COLUMN `status` varchar(45);
select * from ordRepayAmoutRecord LIMIT 10;

