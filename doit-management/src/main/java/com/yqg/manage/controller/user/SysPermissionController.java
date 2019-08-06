package com.yqg.manage.controller.user;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.user.SysPermissionService;
import com.yqg.manage.service.user.request.ManButtonPermissionRequest;
import com.yqg.manage.service.user.request.ManSysPermissionRequest;
import com.yqg.manage.service.user.request.ManSysUserRequest;
import com.yqg.manage.service.user.response.ManSysPermissionListResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author alan
 */
@RestController
@H5Request
@Api(tags = "权限管理")
public class SysPermissionController {

    @Autowired
    private SysPermissionService sysPermissionService;

    @ApiOperation("添加系统权限url")
    @RequestMapping(value = "/manage/permissionItemAdd", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> permissionItemAdd(HttpServletRequest request, @RequestBody ManSysPermissionRequest permissionRequest)
            throws Exception{
        this.sysPermissionService.addPermissionItem(permissionRequest);
        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("修改系统权限url")
    @RequestMapping(value = "/manage/permissionItemEdit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> permissionItemEdit(HttpServletRequest request, @RequestBody ManSysPermissionRequest permissionRequest)
            throws Exception{
        this.sysPermissionService.editPermissionItem(permissionRequest);
        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("查询权限列表")
    @RequestMapping(value = "/manage/permissionList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> permissionList()
            throws Exception{
        return ResponseEntitySpecBuilder.success(this.sysPermissionService.permissionList(new ArrayList<>()));
    }

    @ApiOperation("通过用户id查询用户权限树")
    @RequestMapping(value = "/manage/permissionTreeByUserId", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<List<ManSysPermissionListResponse>> permissionTreeByUserId (@RequestBody ManSysUserRequest sysUserRequest)
            throws Exception{
        return ResponseEntitySpecBuilder.success(this.sysPermissionService.permissionTreeByUserId(sysUserRequest.getId()));
    }

    @ApiOperation("通过用户id查询用户权限树（扩展支持印尼文）")
    @RequestMapping(value = "/manage/permissionTreeInnByUserId", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<List<ManSysPermissionListResponse>> permissionTreeInnByUserId (@RequestBody ManSysUserRequest sysUserRequest)
            throws Exception{
        return ResponseEntitySpecBuilder.success(this.sysPermissionService.permissionTreeInnByUserId(sysUserRequest.getId()));
    }

    @ApiOperation("根据字典code，查询用户是否具有按钮权限")
    @RequestMapping(value = "/manage/hasButtonPermission", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Boolean> hasButtonPermission (@RequestBody ManButtonPermissionRequest request)
            throws Exception{
        return ResponseEntitySpecBuilder.success(this.sysPermissionService.hasButtonPermission(request));
    }
}
