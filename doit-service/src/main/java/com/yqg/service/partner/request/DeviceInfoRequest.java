/*
 * Copyright (c) 2017-2018 , Inc. All Rights Reserved.
 */
package com.yqg.service.partner.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.models.BaseRequest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

import javax.validation.constraints.NotNull;

@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class DeviceInfoRequest {

    @ApiModelProperty(value = "")
    @JsonProperty("longitude")
    
    private String longitude;
    @ApiModelProperty(value = "")
    @JsonProperty("latitude")
    
    private String latitude;
    @ApiModelProperty(value = "")
    @JsonProperty("device_type")
    
    private String deviceType;
    @JsonProperty("device_name")
    
    private String deviceName;
    @JsonProperty("device_brand")
    
    private String deviceBrand;
    @JsonProperty("os_version")
    
    private String osVersion;
    @JsonProperty("total_memory")
    
    private String totalMemory;
    @JsonProperty("remain_memory")
    
    private String remainMemory;
    @JsonProperty("total_space")
    
    private String totalSpace;
    @JsonProperty("remain_space")
    
    private String remainSpace;
    @JsonProperty("cpu_type")
    
    private String cpuType;
    @JsonProperty("device_id")
    
    private String deviceId;
    @JsonProperty("last_power_on")
    
    private String lastPowerOn;
    @JsonProperty("DNS")
    
    private String DNS;
    @JsonProperty("isRooted")
    
    private Integer isRooted;
    @JsonProperty("netType")
    
    private String netType;
    @JsonProperty("memory_card_capacity")
    
    private String memoryCardCapacity;
    @JsonProperty("mac_address")
    
    private String macAddress;
    @JsonProperty("language")
    
    private String language;
    @JsonProperty("ip_address")
    
    private String ipAddress;
    @JsonProperty("is_simulator")
    
    private Integer isSimulator;
    @JsonProperty("battery")
    
    private String battery;
    @JsonProperty("androidId")
    
    private String androidId;
    @JsonProperty("fcmToken")
    
    private String fcmToken;
}