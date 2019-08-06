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
public class ManSysPermissionRequest {
    @ApiModelProperty(value = "父权限id")
    @JsonProperty
    private Integer parentId;
    @ApiModelProperty(value = "权限编号")
    @JsonProperty
    private String permissionCode;
    @ApiModelProperty(value = "权限名称")
    @JsonProperty
    private String permissionName;
    @ApiModelProperty(value = "权限url")
    @JsonProperty
    private String permissionUrl;
    @ApiModelProperty(value = "权限id")
    @JsonProperty
    private Integer id;

    public Integer getParentId() {
        return parentId;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public String getPermissionUrl() {
        return permissionUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public void setPermissionUrl(String permissionUrl) {
        this.permissionUrl = permissionUrl;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
