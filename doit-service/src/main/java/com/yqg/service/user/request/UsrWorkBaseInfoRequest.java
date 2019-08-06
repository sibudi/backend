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
public class UsrWorkBaseInfoRequest extends BaseRequest {

    @ApiModelProperty(value = "???")
    @JsonProperty
    private String orderNo;
    @ApiModelProperty(value = "??")
    @JsonProperty
    private String email;
    @ApiModelProperty(value = "??")
    @JsonProperty
    private String academic;
    @ApiModelProperty(value = "??")
    @JsonProperty
    private String birthday;
    @ApiModelProperty(value = "??")
    @JsonProperty
    private String religion;
    @ApiModelProperty(value = "?????")
    @JsonProperty
    private String motherName;
    @ApiModelProperty(value = "????")
    @JsonProperty
    private Integer childrenAmount;
    @ApiModelProperty(value = "????")
    @JsonProperty
    private Integer maritalStatus;
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
    private String borrowUse;

    @ApiModelProperty(value = "???:?")
    @JsonProperty
    private String birthProvince;
    @ApiModelProperty(value = "???:?")
    @JsonProperty
    private String birthCity;
    @ApiModelProperty(value = "???:??")
    @JsonProperty
    private String birthBigDirect;
    @ApiModelProperty(value = "???:??")
    @JsonProperty
    private String birthSmallDirect;

    //cashcash添加
    @JsonProperty
    private String monthlyIncome;

    //税卡
    @JsonProperty
    private String npwp;


    @ApiModelProperty(value = "保险卡url")
    @JsonProperty
    private String insuranceCardPhoto;

    @ApiModelProperty(value = "whatsapp账号")
    @JsonProperty
    private String whatsappAccount;

    @ApiModelProperty(value = "家庭卡url")
    @JsonProperty
    private String kkCardPhoto;
}