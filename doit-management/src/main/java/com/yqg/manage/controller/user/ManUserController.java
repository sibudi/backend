package com.yqg.manage.controller.user;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.entity.user.ManUser;
import com.yqg.manage.service.review.response.AutoReviewRuleResponse;
import com.yqg.manage.service.user.ManUserService;
import com.yqg.manage.service.user.ReviewerSchedulerService;
import com.yqg.manage.service.user.request.*;
import com.yqg.manage.service.user.response.ManSysLoginResponse;
import com.yqg.manage.service.user.response.ManSysUserResponse;
import com.yqg.manage.service.user.response.ReviewerCollectionResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author alan
 */
@Api(tags="系统用户管理")
@RestController
@H5Request
@RequestMapping("/manage")
public class ManUserController {

    @Autowired
    private ManUserService manUserService;

    @Autowired
    private ReviewerSchedulerService reviewerSchedulerService;

    @ApiOperation("后台用户登陆")
    @RequestMapping(value = "/sysLogin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<ManSysLoginResponse> sysLogin(HttpServletRequest request,
                                                            @RequestBody ManSysLoginRequest sysLoginRequest)
            throws Exception {

        ManSysLoginResponse response = this.manUserService.sysLogin(request, sysLoginRequest);
        if (response == null) {
            ResponseEntitySpec<ManSysLoginResponse> result = new ResponseEntitySpec<>();
            result.setCode(10211);
            return result;
        }
        return ResponseEntitySpecBuilder
                .success(response);
    }

    @ApiOperation("后台用户退出登录")
    @RequestMapping(value = "/sysLoginOut", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<ManSysLoginResponse> sysLoginOut(HttpServletRequest request)
            throws Exception {

        this.manUserService.sysLoginOut(request);
        return ResponseEntitySpecBuilder
                .success();
    }

    @ApiOperation("后台添加新用户")
    @RequestMapping(value = "/sysUserAdd", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<ManUser> sysUserAdd(@RequestBody ManSysUserRequest sysUserRequest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.manUserService.addSysUser(sysUserRequest));
    }

    @ApiOperation("修改后台用户")
    @RequestMapping(value = "/sysUserEdit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> sysUserEdit(@RequestBody ManSysUserRequest sysUserRequest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.manUserService.editSysUser(sysUserRequest));
    }

    @ApiOperation("修改后台用户密码")
    @RequestMapping(value = "/sysUserPass", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> sysUserPass(@RequestBody ManSysUserRequest sysUserRequest)
            throws Exception {
        this.manUserService.updateSysUserPasswd(sysUserRequest);
        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("重置后台用户密码 author:tonggen")
    @RequestMapping(value = "/manusr/passwordReset", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> resetPassword(@RequestBody AutoReviewRuleResponse request)
            throws Exception {
        this.manUserService.resetPassword(request);
        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("查询用户列表")
    @RequestMapping(value = "/sysUserList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> sysUserList(
            @RequestBody ManSysUserListRequest sysUserRequest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.manUserService.sysUserList(sysUserRequest));
    }

    @ApiOperation("通过姓名查询当前登录的sessionId(暂时使用）")
    @RequestMapping(value = "/getSessionIdByName", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> getSessionIdByName(
            @RequestBody ManSysUserListRequest sysUserRequest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.manUserService.getSessionIdByName(sysUserRequest.getUsername()));
    }

    @ApiOperation("通过备注查询用户列表")
    @RequestMapping(value = "/manUserListByRemark", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> manUserListByRemark(
            @RequestBody ManSysUserRequest sysUserRequest)
            throws Exception {
        return ResponseEntitySpecBuilder
                .success(this.manUserService.manUserListByRemark(sysUserRequest));
    }

    @ApiOperation("所有后台用户列表")
    @RequestMapping(value = "/allManUserList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> manUserList(@RequestBody ManSysUserRequest sysUserRequest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.manUserService.manUserList(sysUserRequest));
    }

    @ApiOperation("查询所有岗位已经分配的审核人员列表; Author: zengxiangcai")
    @RequestMapping(value = "/reviewers", method = RequestMethod.GET)
    public ResponseEntitySpec<List<ReviewerCollectionResponse>> getReviewers() {
        return ResponseEntitySpecBuilder.success(manUserService.getReviewerList());
    }


    @ApiOperation("保存每日审核人员调度信息; Author: zengxiangcai")
    @RequestMapping(value = "/reviewers/scheduler", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> addReviewerScheduler(
            @RequestParam("sessionId") String sessionId,
            @RequestBody ReviewerSchedulerRequest request) {
        reviewerSchedulerService
                .addReviewerScheduler(manUserService.getUserIdBySession(sessionId), request);
        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("查询某一个岗位所有审核人员; Author: zengxiangcai")
    @RequestMapping(value = "/all-post-reviewers", method = RequestMethod.POST)
    public ResponseEntitySpec<List<ManSysUserResponse>> getReviewersByPostName(
            @RequestBody ReviewerRequestParam param) {
        return ResponseEntitySpecBuilder
                .success(reviewerSchedulerService.getAllReviewersByPostName(param.getPostName()));
    }

    @ApiOperation("查询某一个岗位所有审核人员; Author: zengxiangcai")
    @RequestMapping(value = "/current-reviewers", method = RequestMethod.POST)
    public ResponseEntitySpec<List<ManSysUserResponse>> getCurrentReviewersByPostName(
            @RequestBody ReviewerRequestParam param) {
        return ResponseEntitySpecBuilder
                .success(manUserService.getCurrentReviewersByPostName(param.getPostName()));
    }

    @ApiOperation("判断用户名称是否重复; Author: tonggen")
    @RequestMapping(value = "/manuser", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> judgeUserName(@RequestBody ManSysUserRequest sysUserRequest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.manUserService.judgeUserName(sysUserRequest.getUsername()));
    }

    @ApiOperation("强制修改密码")
    @RequestMapping(value = "/forceChangePassword.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> forceChangePassword(@RequestBody ManSysUserRequest manSysUserRequest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.manUserService.forceChagePassword(manSysUserRequest));
    }

    @ApiOperation("增加组员组长")
    @RequestMapping(value = "/addOrDeleteParentId", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> addParentId(@RequestBody ManSysUserRequest manSysUserRequest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.manUserService.addOrDeleteParentId(manSysUserRequest));
    }

    @ApiOperation("查询组员和组长")
    @RequestMapping(value = "/listParentId", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> listParentId()
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.manUserService.listParentId());
    }
}
