/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Login session 
 * @author Jacob
 *
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class LoginSession extends CompatibleResponse{
    
    @JsonProperty("appName")
    private String appName;
    
    @JsonProperty("userName")
    private String userName;

    @JsonProperty("userUuid")
    private String userUuid;

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("loginTime")
    private String loginTime;

    @JsonProperty("lastVerifyTime")
    private String lastVerifyTime;

    @JsonProperty("clientVersion")
    private String clientVersion;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("ip")
    private String ip;

    @JsonProperty("expireIn")
    private String expireIn;

    @JsonProperty("status")
    private String status;
    @ApiModelProperty(value = "?????key")
    @JsonProperty
    private String smsCodeKey;

    @ApiModelProperty(value = "????")
    @JsonProperty
    private String idCard;
    @ApiModelProperty(value = "????")
    @JsonProperty
    private String realName;
    @ApiModelProperty(value = "????")
    @JsonProperty
    private Integer userRole;

    @ApiModelProperty(value = "登录还是注册 默认0不是（新用户 注册） 1是（老用户 登录）")
    @JsonProperty
    private Integer isLogin;
}
