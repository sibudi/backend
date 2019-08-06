package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Didit Dwianto on 2017/11/25.
 */
@Data
@Table("usrLoginHistory")
public class UsrLoginHistory extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -8576900933451257658L;

    private String userUuid;
    private String deviceNomber;
    private String deviceType;
    private String macAddress;
    private String ipAddress;
    private String networkType;
    private String mobileSysVersionNo;
    private String marketChannelNo;
    private String applicationVersionNo;
    private String lbsX;
    private String lbsY;
}
