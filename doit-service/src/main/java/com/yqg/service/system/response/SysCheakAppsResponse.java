package com.yqg.service.system.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/11/26.
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class SysCheakAppsResponse {

    @ApiModelProperty(value = "????app")
    @JsonProperty
    private String isCheak;
    @ApiModelProperty(value = "applicationWorkspace??")
    @JsonProperty
    private String method1;
    @ApiModelProperty(value = "workspace??")
    @JsonProperty
    private String method2;
    @ApiModelProperty(value = "applications??")
    @JsonProperty
    private String method3;
    @ApiModelProperty(value = "bundleIdentifier??")
    @JsonProperty
    private String method4;
    @ApiModelProperty(value = "bundleVersion??")
    @JsonProperty
    private String method5;
    @ApiModelProperty(value = "localizedName??")
    @JsonProperty
    private String method6;
}
