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
public class BankInfoRequest {

    @ApiModelProperty(value = "", required = true)
    @JsonProperty("account_no")
    @NotNull
    private String accountNo;
    @ApiModelProperty(value = "", required = true)
    @JsonProperty("bank_code")
    @NotNull
    private String bankCode;
    @ApiModelProperty(value = "", required = true)
    @JsonProperty("card_name")
    @NotNull
    private String cardName;
    @JsonProperty("priority")
    @NotNull
    private Integer priority;
}