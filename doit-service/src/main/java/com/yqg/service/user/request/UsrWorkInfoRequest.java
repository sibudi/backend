package com.yqg.service.user.request;

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
public class UsrWorkInfoRequest extends BaseRequest {

    @ApiModelProperty(value = "???")
    @JsonProperty
    private String orderNo;
    @ApiModelProperty(value = "????")
    @JsonProperty
    private String companyName;
    @ApiModelProperty(value = "????")
    @JsonProperty
    private String positionName;
    @ApiModelProperty(value = "???")
    @JsonProperty
    private String monthlyIncome;
    @ApiModelProperty(value = "????")
    @JsonProperty
    private String companyPhone;
    @ApiModelProperty(value = "?")
    @JsonProperty
    private String province;
    @ApiModelProperty(value = "?")
    @JsonProperty
    private String city;
    @ApiModelProperty(value = "??")
    @JsonProperty
    private String bigDirect;
    @ApiModelProperty(value = "??")
    @JsonProperty
    private String smallDirect;
    @ApiModelProperty(value = "????")
    @JsonProperty
    private String detailed;
    @ApiModelProperty(value = "??")
    @JsonProperty
    private String lbsY;
    @ApiModelProperty(value = "??")
    @JsonProperty
    private String lbsX;
    @ApiModelProperty(value = "????")
    @JsonProperty
    private Integer addressType;
    @ApiModelProperty(value = "????")
    @JsonProperty
    private String dependentBusiness;

    @ApiModelProperty(value = "员工身份号码")
    @JsonProperty
    private String employeeNumber;
    @ApiModelProperty(value = "分机号码")
    @JsonProperty
    private String extensionNumber;
}