package com.yqg.user.entity;

import com.yqg.base.data.condition.BaseEntity;
import com.yqg.base.data.annotations.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@Table("usrRegisterDevice")
public class RegisterDeviceInfo extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -688555633189811271L;

    private String userUuid;
    private String deviceNumber;
    private String deviceType;
    private String macAddress;
    private String ipAddress;
    private String fcmToken;

}
