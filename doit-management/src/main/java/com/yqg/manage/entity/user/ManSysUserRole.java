package com.yqg.manage.entity.user;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author alan
 */
@Data
@Table("manSysUserRole")
public class ManSysUserRole extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -3176344003099461340L;

    private Integer userId;

    private Integer roleId;

    private Integer status;

    public Integer getUserId() {
        return userId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
