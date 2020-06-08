package com.yqg.manage.service.user;


import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.manage.dal.user.ManSysRoleDao;
import com.yqg.manage.entity.user.ManSysRole;
import com.yqg.manage.entity.user.ManSysRolePermission;
import com.yqg.manage.service.user.request.ManSysPermissionRequest;
import com.yqg.manage.service.user.request.ManSysRoleRequest;
import com.yqg.manage.service.user.response.ManSysPermissionListResponse;
import com.yqg.manage.service.user.response.ManSysRoleNameResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author alan
 */
@Component
public class SysRoleService {
    @Autowired
    private ManSysRoleDao manSysRoleDao;

    @Autowired
    private SysRolePermissionService sysRolePermissionService;

    @Autowired
    private SysPermissionService sysPermissionService;

    /**
     * 添加系统角色*/
    @Transactional
    public void addSysRole(ManSysRoleRequest sysRoleRequest) throws ServiceExceptionSpec {

        ManSysRole addInfo = this.getSysRoleCommonInfo(sysRoleRequest);
        addInfo.setUuid(UUIDGenerateUtil.uuid());
        Integer success = this.manSysRoleDao.insert(addInfo);
        //rizky addInfo is updated immediately after insert is successful,get id afterward
        if(success.equals(1)){
            Integer roleId = addInfo.getId();
            String[] permissionString = sysRoleRequest.getPermissionIds().split(",");
            this.addRolePermissionLink(permissionString,roleId);
        }
        else {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_ADD_ITEM_ERROR);
        }
    }

    /**
     * 修改系统角色*/
    @Transactional
    public void sysRoleEdit(ManSysRoleRequest sysRoleRequest) throws ServiceExceptionSpec{
        if(sysRoleRequest.getId() == null){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_SYS_ROLE_ERROR);
        }
        ManSysRole editInfo = this.getSysRoleCommonInfo(sysRoleRequest);
        editInfo.setId(sysRoleRequest.getId());
        this.manSysRoleDao.update(editInfo);

        this.sysRolePermissionService.delSysRolePermissItemByRoleId(sysRoleRequest.getId());

        String[] permissionString = sysRoleRequest.getPermissionIds().split(",");
        this.addRolePermissionLink(permissionString,sysRoleRequest.getId());
    }

    /**
     * 查询系统角色列表*/
    public List<ManSysRole> sysRolesList() throws ServiceExceptionSpec {
        ManSysRole search = new ManSysRole();
        search.setDisabled(0);
        List<ManSysRole> result = this.manSysRoleDao.scan(search);
        return result;
    }

    /**
     * 查询与角色关联的权限url*/
    public List<ManSysPermissionListResponse> rolePermissionCheckList(ManSysPermissionRequest request)
            throws ServiceExceptionSpec{
        if(request.getId() == null){

        }
        List<Integer> permissionsIds = new ArrayList<>();

        List<ManSysRolePermission> permissionsList = this.sysRolePermissionService.SysRolePermissItemByRoleId(request.getId());
        for(ManSysRolePermission item:permissionsList){
            permissionsIds.add(item.getPermissionId());
        }
        List<ManSysPermissionListResponse> permissionsResult = this.sysPermissionService.permissionList(permissionsIds);

        return permissionsResult;
    }
    /**
     * 系统角色信息公共方法*/
    private ManSysRole getSysRoleCommonInfo(ManSysRoleRequest sysRoleRequest)
            throws ServiceExceptionSpec {
        if(StringUtils.isEmpty(sysRoleRequest.getRoleName()) ||
                StringUtils.isEmpty(sysRoleRequest.getPermissionIds())){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_SYS_ROLE_ERROR);
        }
        ManSysRole info = new ManSysRole();
        info.setRoleName(sysRoleRequest.getRoleName());
        info.setRemark(sysRoleRequest.getRemark());
        info.setStatus(sysRoleRequest.getStatus());
        return info;
    }
    /**
     * 添加系统角色权限关联关系*/
    private void addRolePermissionLink(String[] permissionString,Integer roleId)
            throws ServiceExceptionSpec {
        for(String item:permissionString){
            ManSysRolePermission temp = new ManSysRolePermission();
            temp.setPermissionId(Integer.valueOf(item));
            temp.setRoleId(roleId);
            this.sysRolePermissionService.addSysRolePermissItem(temp);
        }
    }

    /**
     * 通过roleId查询角色列表*/
    public List<ManSysRole> sysRoleListById(Integer id){
        ManSysRole search = new ManSysRole();
        search.setId(id);
        search.setDisabled(0);
        return this.manSysRoleDao.scan(search);
    }

    /**
     * 判断角色名称是否重复
     * @param roleName
     */
    public ManSysRoleNameResponse judgeUsrRoleName(String roleName) throws ServiceExceptionSpec {

        if (StringUtils.isEmpty(roleName)) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_SYS_ROLE_ERROR);
        }
        ManSysRole search = new ManSysRole();
        search.setRoleName(roleName);
        search.setDisabled(0);
        List<ManSysRole> rList = manSysRoleDao.scan(search);

        ManSysRoleNameResponse response = new ManSysRoleNameResponse();
        if (CollectionUtils.isEmpty(rList)) {
            response.setRepeat(false);
        } else {
            response.setRepeat(true);
        }
        return response;

    }
}
