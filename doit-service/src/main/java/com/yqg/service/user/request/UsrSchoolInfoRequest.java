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
public class UsrSchoolInfoRequest extends BaseRequest {

    @ApiModelProperty(value = "???")
    @JsonProperty
    private String orderNo;
    @ApiModelProperty(value = "????")
    @JsonProperty
    private String schoolName;
    @ApiModelProperty(value = "??")
    @JsonProperty
    private String major;
    @ApiModelProperty(value = "????")
    @JsonProperty
    private String startSchoolDate;
    @ApiModelProperty(value = "??")
    @JsonProperty
    private String studentNo;
    @ApiModelProperty(value = "???URL")
    @JsonProperty
    private String studentCardUrl;
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
    @ApiModelProperty(value = "?????URL")
    @JsonProperty
    private String scholarshipUrl;
    @ApiModelProperty(value = "????URL")
    @JsonProperty
    private String englishUrl;
    @ApiModelProperty(value = "?????URL")
    @JsonProperty
    private String computerUrl;
    @ApiModelProperty(value = "???URL")
    @JsonProperty
    private String schoolCardUrl;
    @ApiModelProperty(value = "??????")
    @JsonProperty
    private String otherCertificateUrl;

}