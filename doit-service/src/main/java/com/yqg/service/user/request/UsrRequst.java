/*
 * Copyright (c) 2017-2018 , Inc. All Rights Reserved.
 */
package com.yqg.service.user.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.models.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UsrRequst extends BaseRequest {

    @ApiModelProperty(value = "", required = true)
    @JsonProperty
    @NotNull
    private String mobileNumber;
    @ApiModelProperty(value = "", required = true)
    @JsonProperty
    @NotNull
    private String smsCode;
    @ApiModelProperty(value = "")
    @JsonProperty
    @NotNull
    private String userUuid;
    @ApiModelProperty(value = "")
    @JsonProperty
    @NotNull
    private Integer role;
    @ApiModelProperty(value = "")
    @JsonProperty
    private String smsKey;
    @ApiModelProperty(value = "")
    @JsonProperty
    private String realName;
    @ApiModelProperty(value = "")
    @JsonProperty
    private String userSource;//渠道来源
    @ApiModelProperty(value = "")
    @JsonProperty
    private String invite;//邀请人
    @ApiModelProperty(value = "")
    @JsonProperty
    private String email;//邀请人
    @ApiModelProperty(value = "")
    @JsonProperty
    private String currentPIN;
    @ApiModelProperty(value = "")
    @JsonProperty
    private String newPIN;
    @ApiModelProperty(value = "")
    @JsonProperty("fcmtoken")
    private String fcmToken;
    
}