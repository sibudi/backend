package com.yqg.manage.entity.user;


import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;

import java.io.Serializable;

/**
 * @author alan
 */
@Table("manSysPermission")
public class ManSysPermission extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -3156409293344364123L;

    private Integer parentId;

    private String permissionName;

    private String permissionCode;

    private String permissionUrl;


    public Integer getParentId() {
        return parentId;
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

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
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
}
