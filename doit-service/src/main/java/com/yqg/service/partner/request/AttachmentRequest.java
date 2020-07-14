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

import javax.validation.constraints.NotNull;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AttachmentRequest {

    @ApiModelProperty(value = "")
    @JsonProperty("attachment_type")
    @NotNull
    private String attachmentType;
    @ApiModelProperty(value = "")
    @JsonProperty("attachment_base64")
    @NotNull
    private String attachmentBase64;
    @ApiModelProperty(value = "")
    @JsonProperty("attachment_url")
    @NotNull
    private String attachmentUrl;
}