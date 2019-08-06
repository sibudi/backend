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
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManSysUserRequest {
    @ApiModelProperty(value = "用户名")
    @JsonProperty
    private String username;

    @ApiModelProperty(value = "真实姓名")
    @JsonProperty
    private String realname;

    @ApiModelProperty(value = "状态")
    @JsonProperty
    private Integer status;

    @ApiModelProperty(value = "uuid")
    @JsonProperty
    private String uuid;

    @ApiModelProperty(value = "roleIds")
    @JsonProperty
    private String roleIds;

    @ApiModelProperty(value = "老密码")
    @JsonProperty
    private String oldPassword;

    @ApiModelProperty(value = "新密码")
    @JsonProperty
    private String newPassword;

    @ApiModelProperty(value = "id")
    @JsonProperty
    private Integer id;

    @ApiModelProperty(value = "备注")
    @JsonProperty
    private String remark;

    @ApiModelProperty(value = "是否为第三方人员")
    @JsonProperty
    private Integer third;

    @ApiModelProperty(value = "手机号码")
    @JsonProperty
    private String mobile;

    @ApiModelProperty(value = "????")
    @JsonProperty
    private Boolean thirdPlatform;

    @ApiModelProperty(value = "删除还是新增组长, 1新增；2删除")
    @JsonProperty
    private Integer addOrDelete;

    @ApiModelProperty(value = "催收电话号码")
    @JsonProperty
    private String collectionPhone;

    @ApiModelProperty(value = "催收WA")
    @JsonProperty
    private String collectionWa;

    @ApiModelProperty(value = "员工工号")
    @JsonProperty
    private String employeeNumber;

    @ApiModelProperty(value = "语音电话")
    @JsonProperty
    private String voicePhone;
}
