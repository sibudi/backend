package com.yqg.manage.service.user;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.manage.dal.user.ManSysPermissionDao;
import com.yqg.manage.dal.user.ManSysUserRoleDao;
import com.yqg.manage.entity.user.ManSysPermission;
import com.yqg.manage.entity.user.ManSysRolePermission;
import com.yqg.manage.entity.user.ManSysUserRole;
import com.yqg.manage.service.user.request.ManButtonPermissionRequest;
import com.yqg.manage.service.user.request.ManSysPermissionRequest;
import com.yqg.manage.service.user.response.ManSysPermissionListResponse;
import com.yqg.service.system.request.DictionaryRequest;
import com.yqg.service.system.response.SysDicItemModel;
import com.yqg.service.system.service.SysDicService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author alan
 */
@Component
public class SysPermissionService {
    @Autowired
    private ManSysPermissionDao manSysPermissionDao;

    @Autowired
    private ManUserRoleService sysUserRoleService;

    @Autowired
    private SysRolePermissionService sysRolePermissionService;

    @Autowired
    private ManSysUserRoleDao manSysUserRoleDao;

    @Autowired
    private SysDicService sysDicService;

    private static Logger logger = LoggerFactory.getLogger(SysPermissionService.class);
    /**
     * 添加系统权限子项*/
    public void addPermissionItem(ManSysPermissionRequest request)
            throws ServiceExceptionSpec{
        if(request.getParentId() == null){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_SYS_PERMISSION_ERROR);
        }
        ManSysPermission addInfo = this.getRequestData(request);
        addInfo.setUuid(UUIDGenerateUtil.uuid());
        addInfo.setParentId(request.getParentId());
        this.manSysPermissionDao.insert(addInfo);
    }


    /**
     * 修改系统子权限*/
    public void editPermissionItem(ManSysPermissionRequest request) throws ServiceExceptionSpec{
        if(request.getId() == null){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_SYS_PERMISSION_ERROR);
        }
        ManSysPermission editInfo = this.getRequestData(request);
        editInfo.setUpdateTime(new Date());
        editInfo.setId(request.getId());
        this.manSysPermissionDao.update(editInfo);
    }

    /**
     * 通过id查询权限列表*/
    public List<ManSysPermission> getPermissionListById(String ids) throws ServiceExceptionSpec {
        return this.manSysPermissionDao.permissionListById(ids);
    }

    /**
     * 查询系统子权限列表*/
    public List<ManSysPermissionListResponse> permissionList(List<Integer> permissionsIds) throws ServiceExceptionSpec{
        List<ManSysPermissionListResponse> response = new ArrayList<>();
        ManSysPermission search = new ManSysPermission();
        search.setParentId(0);
        search.setDisabled(0);
        List<ManSysPermission> parent = this.manSysPermissionDao.scan(search);    //查出父节点
        if(CollectionUtils.isEmpty(parent)){
            return null;
        }
        /*遍历父节点*/
        for (ManSysPermission item:parent){
            ManSysPermissionListResponse temp = new ManSysPermissionListResponse();
            temp.setPermissionCode(item.getPermissionCode());
            temp.setPermissionName(item.getPermissionName());
            temp.setPermissionUrl(item.getPermissionUrl());
            temp.setId(item.getId());
            if(permissionsIds.contains(item.getId())){
                temp.setIsCheck(true);
            }else {
                temp.setIsCheck(false);
            }

            ManSysPermission parentInfo = new ManSysPermission();
            parentInfo.setParentId(item.getId());
            parentInfo.setDisabled(0);

            /*根据父节点查询子节点*/
            List<ManSysPermission> childrenResult = this.manSysPermissionDao.scan(parentInfo);
            List<ManSysPermissionListResponse> childrenList = new ArrayList<>();
            /*遍历子节点*/
            for (ManSysPermission obj:childrenResult){
                ManSysPermissionListResponse tempObj = new ManSysPermissionListResponse();
                tempObj.setPermissionCode(obj.getPermissionCode());
                tempObj.setPermissionName(obj.getPermissionName());
                tempObj.setPermissionUrl(obj.getPermissionUrl());
                tempObj.setId(obj.getId());
                if(permissionsIds.contains(obj.getId())){
                    tempObj.setIsCheck(true);
                }else {
                    tempObj.setIsCheck(false);
                }
                childrenList.add(tempObj);
            }
            temp.setChildren(childrenList);
            response.add(temp);
        }

        return response;

    }

    /**
     * 通过用户id查询用有的权限*/
    public List<ManSysPermissionListResponse> permissionTreeByUserId(Integer userId) throws ServiceExceptionSpec {
        if(userId == null){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_PERMISSIONTREE_ERROR);
        }
        List<ManSysPermissionListResponse> responses = new ArrayList<>();
        //根据用户查询所拥有的角色
        List<ManSysUserRole> userRoles = this.sysUserRoleService.userRoleListByUserId(userId);

        if(userRoles != null && userRoles.size() > 0){
            List<ManSysRolePermission> allPermissionList = new ArrayList<>();
            //根据角色ID可以获取角色ID下的权限列表
            for(ManSysUserRole roleItem:userRoles){
                List<ManSysRolePermission> tempPermissionList = this.sysRolePermissionService.SysRolePermissItemByRoleId(roleItem.getRoleId());
                allPermissionList.addAll(tempPermissionList);
            }
            if(allPermissionList != null && allPermissionList.size() > 0){
                /*所有权限id*/
                Set<Integer> permissionIdList = new HashSet<>(allPermissionList.size());
                permissionIdList.addAll(allPermissionList.stream().map(ManSysRolePermission::getPermissionId).collect(Collectors.toList()));

                List<ManSysPermission> permissionList = this.getPermissionListById(StringUtils.join(permissionIdList.toArray(),","));
                logger.info(""+permissionList);
                /*所有顶级菜单*/
                List<ManSysPermission> topPermissions = new ArrayList<>();
                topPermissions.addAll(permissionList.stream().filter(sysPermission -> sysPermission.getParentId() == 0).distinct().collect(Collectors.toList()));
                permissionList.removeAll(topPermissions);   /*去除顶级菜单,*/
                for(ManSysPermission permission : topPermissions){
                    ManSysPermissionListResponse top = this.getPermissionRes(permission);
                    List<ManSysPermissionListResponse> childList = new ArrayList<>();
                    for(ManSysPermission childPermission : permissionList){
                        if(childPermission.getParentId() == permission.getId()){
                            ManSysPermissionListResponse child = this.getPermissionRes(childPermission);

                            childList.add(child);
                        }
                        top.setChildren(childList);
                    }
                    responses.add(top);
                }
            }
        }
        return responses;
    }

    /**
     * 返回权限子项初始化数据*/
    private ManSysPermissionListResponse getPermissionRes(ManSysPermission permission)
            throws ServiceExceptionSpec {
        ManSysPermissionListResponse top = new ManSysPermissionListResponse();
        top.setId(permission.getId());
        top.setPermissionUrl(permission.getPermissionUrl());
        top.setPermissionName(permission.getPermissionName());
        top.setPermissionCode(permission.getPermissionCode());
        return top;
    }

    /**
     * 查询*/

    private ManSysPermission getRequestData(ManSysPermissionRequest request)
            throws ServiceExceptionSpec    {
        if(StringUtils.isEmpty(request.getPermissionCode()) ||
                StringUtils.isEmpty(request.getPermissionName()) ||
                StringUtils.isEmpty(request.getPermissionUrl())){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_SYS_PERMISSION_ERROR);
        }
        ManSysPermission info = new ManSysPermission();
        info.setPermissionCode(request.getPermissionCode());
        info.setPermissionName(request.getPermissionName());
        info.setPermissionUrl(request.getPermissionUrl());
        return info;
    }

    /**
     * 支持包括印尼文的权限
     * @param id
     * @return
     */
    public List<ManSysPermissionListResponse> permissionTreeInnByUserId(Integer id) throws ServiceExceptionSpec {

        List<ManSysPermissionListResponse> responses = this.permissionTreeByUserId(id);
        if (CollectionUtils.isEmpty(responses)) {
            return new ArrayList<>();
        }
        //印尼文的加上"Inn"后缀
        for (ManSysPermissionListResponse parent : responses) {
            //最终返回的子孩子
            List<ManSysPermissionListResponse> newChildren = new ArrayList<>();
            //孩子进行翻倍扩展
            for (ManSysPermissionListResponse children : (List<ManSysPermissionListResponse>)parent.getChildren()) {
                ManSysPermissionListResponse child = new ManSysPermissionListResponse();
                BeanUtils.copyProperties(children, child);
                child.setPermissionUrl(children.getPermissionUrl() + "Inn");
                newChildren.add(child);
            }
            parent.setChildren(newChildren);
        }
        return responses;

    }

    /**
     * 根据字典code，查询用户是否具有按钮权限
     * @param request
     * @return
     */
    public Boolean hasButtonPermission(ManButtonPermissionRequest request) throws Exception {

        if (StringUtils.isEmpty(request.getPermissionCode()) ||
                request.getUserId() == null) {
            return false;
        }
        //查询用户角色
        ManSysUserRole manSysUserRole = new ManSysUserRole();
        manSysUserRole.setUserId(request.getUserId());
        manSysUserRole.setStatus(0);
        manSysUserRole.setDisabled(0);
        List<ManSysUserRole> roleList = manSysUserRoleDao.scan(manSysUserRole);
        if (CollectionUtils.isEmpty(roleList)) {
            return false;
        }
        List<Integer> roles = roleList.stream().map(elem->elem.getRoleId()).collect(Collectors.toList());

        //查询字典权限
        DictionaryRequest dictionaryRequest = new DictionaryRequest();
        dictionaryRequest.setDicCode(request.getPermissionCode());
        List<SysDicItemModel> sysDicItemModelList = this.sysDicService.dicItemListByDicCode(dictionaryRequest);
        if (CollectionUtils.isEmpty(sysDicItemModelList)) {
            return false;
        }
        for (SysDicItemModel sysDicItemModel : sysDicItemModelList) {
            if (roles.contains(Integer.valueOf(sysDicItemModel.getDicItemValue()))) {
                return true;
            }
        }
        return false;
    }
}
