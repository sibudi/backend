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
public class WorkInfoRequest {

    @ApiModelProperty(value = "")
    @JsonProperty("company_name")
    private String companyName;
    @ApiModelProperty(value = "")
    @JsonProperty("company_phone")
    private String company_phone;
    @ApiModelProperty(value = "")
    @JsonProperty("company_ext_phone")
    private String companyExtPhone;
    @JsonProperty("company_province")
    private String companyProvince;
    @JsonProperty("company_region")
    private String companyRegion;
    @JsonProperty("company_subdistrict")
    private String companySubdistrict;
    @JsonProperty("company_village")
    private String companyVillage;
    @JsonProperty("company_specific_address")
    private String companySpecificAddress;
    @JsonProperty("job_position")
    private String jobPosition;
    @JsonProperty("monthly_income")
    private String monthlyIncome;
    @JsonProperty("industry")
    private String industry;
}