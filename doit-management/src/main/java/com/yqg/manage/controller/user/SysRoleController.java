package com.yqg.manage.controller.user;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.entity.user.ManSysRole;
import com.yqg.manage.service.user.SysRoleService;
import com.yqg.manage.service.user.request.ManSysPermissionRequest;
import com.yqg.manage.service.user.request.ManSysRoleRequest;
import com.yqg.manage.service.user.response.ManSysPermissionListResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author alan
 */
@RestController
@H5Request
@Api(tags = "????")
public class SysRoleController {
    private static Logger logger = LoggerFactory.getLogger(SysRoleController.class);

    @Autowired
    private SysRoleService sysRoleService;

    @ApiOperation("添加系统角色")
    @RequestMapping(value = "/manage/sysRoleAdd", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> sysRoleAdd(@RequestBody ManSysRoleRequest roleRequest)
            throws Exception {
        this.sysRoleService.addSysRole(roleRequest);
        return ResponseEntitySpecBuilder.success();
    }


    @ApiOperation("查询系统角色列表")
    @RequestMapping(value = "/manage/sysRoleList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<List<ManSysRole>> sysRoleList() throws Exception {
        return ResponseEntitySpecBuilder.success(this.sysRoleService.sysRolesList());
    }

    @ApiOperation("通过Id查询premissionTree")
    @RequestMapping(value = "/manage/rolePermissionCheckList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<List<ManSysPermissionListResponse>> rolePermissionCheckList(@RequestBody ManSysPermissionRequest request) throws Exception {
        return ResponseEntitySpecBuilder.success(this.sysRoleService.rolePermissionCheckList(request));
    }

    @ApiOperation("修改系统角色")
    @RequestMapping(value = "/manage/sysRoleEdit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> sysRoleEdit(@RequestBody ManSysRoleRequest roleRequest)
            throws Exception {
        this.sysRoleService.sysRoleEdit(roleRequest);
        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("判断角色名称是否重复；Author: tonggen")
    @RequestMapping(value = "/manage/usrRole", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> judgeUsrRoleName(@RequestBody ManSysRoleRequest roleRequest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.sysRoleService.judgeUsrRoleName(roleRequest.getRoleName()));
    }
}
