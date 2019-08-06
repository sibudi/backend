package com.yqg.manage.entity.user;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author alan
 */
@Table("manSysRolePermission")
public class ManSysRolePermission extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -6654717083838930991L;

    private Integer roleId;

    private Integer permissionId;

    public Integer getRoleId() {
        return roleId;
    }

    public Integer getPermissionId() {
        return permissionId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }
}
