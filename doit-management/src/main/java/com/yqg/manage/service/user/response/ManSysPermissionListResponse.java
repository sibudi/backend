package com.yqg.manage.service.user.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;

/**
 * @author alan
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManSysPermissionListResponse {
    private Integer id;

    private Boolean isCheck;

    private String permissionName;

    private String permissionCode;

    private String permissionUrl;

    private Object children;

    public Integer getId() {
        return id;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public String getPermissionUrl() {
        return permissionUrl;
    }

    public Object getChildren() {
        return children;
    }

    public Boolean getIsCheck() {
        return isCheck;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public void setPermissionUrl(String permissionUrl) {
        this.permissionUrl = permissionUrl;
    }

    public void setChildren(Object children) {
        this.children = children;
    }

    public void setIsCheck(Boolean isCheck) {
        this.isCheck = isCheck;
    }
}
