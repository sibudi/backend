package com.yqg.activity.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

/**
 * Features:
 * Created by huwei on 18.8.16.
 */
@Data
@Table("usrGoPay")
public class UsrGoPay extends BaseEntity{
    private String userUuid;
    private String mobileNumber;
    private String userName;
}
