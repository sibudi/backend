package com.yqg.system.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by wanghuaizhou on 2018/7/19.
 */
@Data
@Table("sysDeviceIdWhiteList")
public class SysDeviceIdWhiteList extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 7797840814771221550L;

    private String deviceId;   //设备id
    private String thirdType;  //设备标示 0 Do-It 1 CashCash
}

