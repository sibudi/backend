package com.yqg.manage.service.user;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.manage.dal.user.ManSysUserRoleDao;
import com.yqg.manage.entity.user.ManSysUserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author alan
 */
@Component
public class ManUserRoleService {
    @Autowired
    private ManSysUserRoleDao manSysUserRoleDao;

    /**
     * 新增用户与角色关联关系*/
    public void addUserRoleLink(String roleIds,Integer userId) throws ServiceExceptionSpec {
        if(StringUtils.isEmpty(roleIds) || userId == null){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_ADD_USER_ERROR);
        }
        String[] roleIdString = roleIds.split(",");
        for(String roleItem:roleIdString){
            ManSysUserRole info = new ManSysUserRole();
            info.setDisabled(0);
            info.setRoleId(Integer.valueOf(roleItem));
            info.setUserId(userId);
            info.setStatus(0);
            this.manSysUserRoleDao.insert(info);
        }

    }

    /**
     * 删除用户与角色关联关系*/
    public void delUserRoleLink(Integer userId) throws ServiceExceptionSpec {
        this.manSysUserRoleDao.delUserRoleLink(userId);
    }

    /**
     * 通过用户的id查询关联的角色*/
    public List<ManSysUserRole> userRoleListByUserId(Integer userId) {
        ManSysUserRole sysUserRole = new ManSysUserRole();
        sysUserRole.setUserId(userId);
        sysUserRole.setDisabled(0);
        return this.manSysUserRoleDao.scan(sysUserRole);
    }
}
