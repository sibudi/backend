package com.yqg.manage.entity.user;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @author alan
 */
@Data
@Table("manSysRole")
public class ManSysRole extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -1093354618519125616L;

    private String roleName;

    private Integer status;

    public String getRoleName() {
        return roleName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
