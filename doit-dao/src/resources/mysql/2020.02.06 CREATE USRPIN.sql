#create user pin table

CREATE TABLE usrPIN (
	id INT NOT NULL AUTO_INCREMENT,
	uuid varchar(45) NULL,
	disabled TINYINT DEFAULT 0 NOT NULL,
	mobileNumberDES varchar(50) NOT NULL,
	emailAddressDES varchar(100) NOT NULL,
	isPhoneNumberVerified TINYINT DEFAULT 0 NOT NULL,
	isTemporaryPIN varchar(100) DEFAULT 1 NOT NULL,
	pinDES varchar(100) NOT NULL,
	expiration DATETIME NOT NULL,
	remark varchar(300),
	createUser INT DEFAULT 0 NOT NULL,
	createTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updateUser INT DEFAULT 0 NOT NULL,
	updateTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
	CONSTRAINT usrPIN_PK PRIMARY KEY (id),
	CONSTRAINT usrPIN_UN UNIQUE KEY (mobileNumberDES,emailAddressDES,expiration)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;
CREATE INDEX usrPIN_mobileNumberDES_IDX USING BTREE ON doit.usrPIN (mobileNumberDES,emailAddressDES,expiration);


#add new column: isMobileValidated: validate whether this user mobile phone already do the otp, emailaddress: user email address
ALTER TABLE doit.usrUser ADD isMobileValidated TINYINT DEFAULT 0 NULL;
ALTER TABLE doit.usrUser ADD emailAddress varchar(100) NULL;

#add new column for fcm token for push notification
ALTER TABLE doit.usrRegisterDevice ADD fcmToken varchar(255) NULL;