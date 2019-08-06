package com.yqg.manage.service.user.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author alan
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManSysRoleRequest {
    @ApiModelProperty(value = "id")
    @JsonProperty
    private Integer id;
    @ApiModelProperty(value = "角色名称")
    @JsonProperty
    private String roleName;
    @ApiModelProperty(value = "备注")
    @JsonProperty
    private String remark;
    @ApiModelProperty(value = "权限id集合")
    @JsonProperty
    private String permissionIds;
    @ApiModelProperty(value = "status")
    @JsonProperty
    private Integer status;

    public Integer getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getRemark() {
        return remark;
    }

    public String getPermissionIds() {
        return permissionIds;
    }

    public Integer getStatus() {
        return status;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setPermissionIds(String permissionIds) {
        this.permissionIds = permissionIds;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
