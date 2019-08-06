package com.yqg.manage.service.system.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author alan
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManAppVersionListRequest {
    @ApiModelProperty(value = "app状态")
    @JsonProperty
    private Integer status;
    @ApiModelProperty(value = "app类型 （1.Android  2.iOS）")
    @JsonProperty
    private Integer appType;
    @ApiModelProperty(value = "页数")
    @JsonProperty
    private Integer pageNo;
    @ApiModelProperty(value = "分页大小")
    @JsonProperty
    private Integer pageSize = 10;
    @ApiModelProperty(value = "升序降序")
    @JsonProperty
    private String orderType;
}
