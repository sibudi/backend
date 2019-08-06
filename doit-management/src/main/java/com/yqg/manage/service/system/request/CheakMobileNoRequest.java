package com.yqg.manage.service.system.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2018/1/7.
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheakMobileNoRequest {

    @ApiModelProperty(value = "加密的手机号")
    @JsonProperty
    private String mobileNumber;

    @ApiModelProperty(value = "没有加密的手机号")
    @JsonProperty
    private String encryptMobile;
}
