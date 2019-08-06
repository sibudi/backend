package com.yqg.system.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Didit Dwianto on 2017/11/26.
 */
@Data
@Table("sysPaymentChannel")
public class SysPaymentChannel extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -7006462299609605355L;

    private String paymentChannelName;

    private String paymentChannelCode;

    private Integer type;

}
