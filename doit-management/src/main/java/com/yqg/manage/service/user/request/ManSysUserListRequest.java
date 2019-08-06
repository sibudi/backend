package com.yqg.manage.service.user.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Jacob
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManSysUserListRequest {
    @ApiModelProperty(value = "用户名")
    @JsonProperty
    private String username;

    @ApiModelProperty(value = "状态")
    @JsonProperty
    private Integer status;

    @ApiModelProperty(value = "手机号码")
    @JsonProperty
    private String mobile;

    @ApiModelProperty(value = "页数")
    @JsonProperty
    private Integer pageNo = 1;
    @ApiModelProperty(value = "分页大小")
    @JsonProperty
    private Integer pageSize = 10;

    private Integer lanuge;

    @ApiModelProperty(value = "是否在线, 1在线 2 不在")
    @JsonProperty
    private Integer onlineOrNot;
}
