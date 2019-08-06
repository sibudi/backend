package com.yqg.manage.service.user.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author alan
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ManSysLoginRequest {
    @ApiModelProperty(value = "用户名")
    @JsonProperty
    private String username;
    @ApiModelProperty(value = "密码")
    @JsonProperty
    private String password;
    @ApiModelProperty(value = "是否为第三方人员")
    @JsonProperty
    private Integer third;
}
