package com.yqg.service.system.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.models.CompatibleResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class SysAppVersionModel extends CompatibleResponse {

    @ApiModelProperty(value = "app类型 （1.Android  2.iOS）")
    @JsonProperty
    private String appType;
    @ApiModelProperty(value = "app更新时间")
    @JsonProperty
    private String appUpdateTime;
    @ApiModelProperty(value = "下载地址")
    @JsonProperty
    private String downloadAddress;
    @ApiModelProperty(value = "是否强更 (1.不强制；2.强制）")
    @JsonProperty
    private String isForce;
    @ApiModelProperty(value = "是否更新")
    @JsonProperty
    private String isUpdate;
    @ApiModelProperty(value = "左按钮文案")
    @JsonProperty
    private String leftBtnTitle;
    @ApiModelProperty(value = "版本升级说明")
    @JsonProperty
    private String updateContent;
    @ApiModelProperty(value = "右按钮文案")
    @JsonProperty
    private String rightBtnTitle;
    @ApiModelProperty(value = "是否可用（1.启用；2.禁用）")
    @JsonProperty
    private String status;
    @ApiModelProperty(value = "标题")
    @JsonProperty
    private String updateTitle;


}
