package com.yqg.risk.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseCondition;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Didit Dwianto on 2017/12/17.
 */
@Data
@Table("riskDeviceIdBlackList")
public class RiskDeviceIdBlackList extends BaseCondition implements Serializable{

    private static final long serialVersionUID = -2227552491240190511L;

    private Integer id;

    private Integer disabled;

    private String status;

    private String type;   // 0 deviceId  1 imei

    private String deviceId;
}
