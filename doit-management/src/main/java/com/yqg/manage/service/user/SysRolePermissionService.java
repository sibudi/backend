package com.yqg.manage.service.user;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.manage.dal.user.ManSysRolePermissionDao;
import com.yqg.manage.entity.user.ManSysRolePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author alan
 */
@Component
public class SysRolePermissionService {
    @Autowired
    private ManSysRolePermissionDao manSysRolePermissionDao;

    /**
     * 添加角色权限关联*/
    public void addSysRolePermissItem(ManSysRolePermission manSysRolePermission)
            throws ServiceExceptionSpec{
        this.manSysRolePermissionDao.insert(manSysRolePermission);
    }

    /**
     * 修改角色权限关联*/
    public void editSysRolePermissItem(ManSysRolePermission manSysRolePermission)
            throws ServiceExceptionSpec {
        this.manSysRolePermissionDao.update(manSysRolePermission);
    }

    /**
     * 通过roleId查询权限url列表*/
    public List<ManSysRolePermission> SysRolePermissItemByRoleId(Integer roleId)
            throws ServiceExceptionSpec {
        ManSysRolePermission search = new ManSysRolePermission();
        search.setRoleId(roleId);
        search.setDisabled(0);
        List<ManSysRolePermission> result = this.manSysRolePermissionDao.scan(search);
        return result;
    }

    /**
     * 根据roleId将role和permission关系置为无效*/
    public void delSysRolePermissItemByRoleId(Integer roleId)
            throws ServiceExceptionSpec{
        if(roleId == null){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_ITEM_ERROR);
        }
        this.manSysRolePermissionDao.delByRoleId(roleId);
    }

}
