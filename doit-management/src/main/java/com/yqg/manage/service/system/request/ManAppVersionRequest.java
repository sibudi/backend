package com.yqg.manage.service.system.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManAppVersionRequest {
    @ApiModelProperty(value = "app类型 （1.摇钱罐客户端 ）")
    @JsonProperty
    private Integer appType;
    @ApiModelProperty(value = "app更新时间")
    @JsonProperty
    private String appUpdateDate;
    @ApiModelProperty(value = "下载地址")
    @JsonProperty
    private String downloadAddress;
    @ApiModelProperty(value = "是否强更 (1.不强制；2.强制）")
    @JsonProperty
    private Integer isForce;
    @ApiModelProperty(value = "左按钮")
    @JsonProperty
    private String leftButton;
    @ApiModelProperty(value = "版本升级说明")
    @JsonProperty
    private String memo;
    @ApiModelProperty(value = "右按钮")
    @JsonProperty
    private String rightButton;
    @ApiModelProperty(value = "是否可用（1.启用；2.禁用）")
    @JsonProperty
    private Integer status;
    @ApiModelProperty(value = "标题")
    @JsonProperty
    private String title;
    @ApiModelProperty(value = "版本号")
    @JsonProperty
    private String versionNo;
    @ApiModelProperty(value = "uuid")
    @JsonProperty
    private String uuid;
    @ApiModelProperty(value = "sysType（1.Android  2.iOS）")
    @JsonProperty
    private Integer sysType;
    @ApiModelProperty(value = "id")
    @JsonProperty
    private Integer id;
}
