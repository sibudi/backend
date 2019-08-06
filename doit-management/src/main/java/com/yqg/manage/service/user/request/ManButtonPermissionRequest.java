package com.yqg.manage.service.user.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Jacob
 */
@ApiModel
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManButtonPermissionRequest {
    @ApiModelProperty(value = "用户id")
    @JsonProperty
    private Integer userId;

    @ApiModelProperty(value = "用户uuid")
    @JsonProperty
    private String userUuid;

    @ApiModelProperty(value = "权限code")
    @JsonProperty
    private String permissionCode;

    @ApiModelProperty(value = "订单号")
    @JsonProperty
    private String orderNo;
}
