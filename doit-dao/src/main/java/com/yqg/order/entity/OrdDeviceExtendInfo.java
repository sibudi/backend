package com.yqg.order.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Table("ordDeviceExtendInfo")
@Getter
@Setter
public class OrdDeviceExtendInfo extends BaseEntity implements Serializable {
    private String orderNo;
    private String userUuid;
    private String device1;
    private String device2;
    private String device3;
    private String device4;
}
