package com.yqg.service.third.infinity.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class InfinityResponse {
    @ApiModelProperty("token")
    @JsonProperty
    private String token;
    @ApiModelProperty("请求状态")
    @JsonProperty
    private String ret;
    @ApiModelProperty(value = "请求结果")
    @JsonProperty
    private String desc;
    @ApiModelProperty("公司代码")
    @JsonProperty
    private String companycode;
    @ApiModelProperty("公司名称")
    @JsonProperty
    private String companyname;
    @ApiModelProperty("授权时间")
    @JsonProperty
    private String authtime;
    @ApiModelProperty("授权model")
    @JsonProperty
    private String authmodel;
    @ApiModelProperty("请求时间")
    @JsonProperty
    private String reqtime;
    @ApiModelProperty("响应时间")
    @JsonProperty
    private String rsptime;



}
