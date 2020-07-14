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

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RegistrationRequest  {

    @ApiModelProperty(value = "")
    @JsonProperty("partner_id")
    private String partnerId;
    @ApiModelProperty(value = "")
    @JsonProperty("request_id")
    private String requestId;
    @ApiModelProperty(value = "")
    @JsonProperty("product_type")
    private String productType;
    @JsonProperty("customer_info")
    private CustomerInfoRequest customerInfo;
}