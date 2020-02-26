use doit;

#create temp table, dont forget to delete after success monitoring
CREATE TABLE usrPINTemp LIKE usrPIN; 
INSERT usrPINTemp SELECT * FROM usrPIN;

#update mobile to email
UPDATE usrPIN p
INNER JOIN usrPINTemp t on t.uuid = p.uuid
SET p.mobileNumberDES = t.emailAddressDES

#update email to mobile
UPDATE usrPIN p
INNER JOIN usrPINTemp t on t.uuid = p.uuid
SET p.emailAddressDES = t.mobileNumberDES