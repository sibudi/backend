package com.yqg.manage.controller.user;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.user.ManUserCommonInfoService;
import com.yqg.manage.service.user.request.ManUserUserRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;

/**
 * @author alan
 */
@RestController
@H5Request
public class ManUserCommonInfoController {
    @Autowired
    private ManUserCommonInfoService manUserCommonInfoService;

    @ApiOperation("通过用户手机号查询")
    @RequestMapping(value = "/manage/smsCodeByMobile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> smsCodeByMobile( @RequestBody ManUserUserRequest sysLoginRequest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.manUserCommonInfoService.getSmsCodeByMobile(sysLoginRequest));
    }

    @ApiOperation("通过用户手机号,从mysql中查询短信验证码")
    @RequestMapping(value = "/manage/smsCodeByMobileFromMysql", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> smsCodeByMobileFromMysql( @RequestBody ManUserUserRequest sysLoginRequest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.manUserCommonInfoService.smsCodeByMobileFromMysql(sysLoginRequest));
    }

    @ApiOperation("")
    @RequestMapping(value = "/manage/user/email", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> getUserEmail( @RequestBody ManUserUserRequest sysLoginRequest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.manUserCommonInfoService.getUserEmail(sysLoginRequest));
    }

    @ApiOperation("")
    @RequestMapping(value = "/manage/user/updateemail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> setUserEmail(@RequestBody ManUserUserRequest sysLoginRequest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.manUserCommonInfoService.setUserEmail(sysLoginRequest));
    }

    @ApiOperation("")
    @RequestMapping(value = "/manage/user/resetpin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> resetPIN( @RequestBody ManUserUserRequest sysLoginRequest)
            throws Exception {
        this.manUserCommonInfoService.resetPIN(sysLoginRequest);
        return ResponseEntitySpecBuilder.success(true);
    }
}
