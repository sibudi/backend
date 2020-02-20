package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Table("usrPIN")
public class UsrPIN extends BaseEntity implements Serializable {
	private String mobileNumberDES;
	private String emailAddressDES;
	private Integer isPhoneNumberVerified;
	private Integer isTemporaryPIN;
	private String pinDES;
	private Date expiration;
}