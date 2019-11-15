package com.yqg.controller.system;


import com.yqg.common.annotations.CompatibleRSA;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.models.*;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.common.utils.GetIpAddressUtil;
import com.yqg.common.utils.JsonUtils;
import com.yqg.service.system.request.SysAppVersionRequest;
import com.yqg.service.system.response.SysAppVersionModel;
import com.yqg.service.system.service.SystemService;
import com.yqg.service.user.request.UsrRequst;
import com.yqg.service.user.service.SmsService;
import com.yqg.service.user.service.UsrService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

@CompatibleRSA
@RestController
@Slf4j
public class SystemCompatibleRSAController {

    @Autowired
    private SystemService systemService;

    @Autowired
    private UsrService usrService;
    @Autowired
    private SmsService smsService;

    @ApiOperation("app更新接口")
    @RequestMapping(value = "/system/isUpdate", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<SysAppVersionModel> isUpdate(HttpServletRequest request, @RequestBody SysAppVersionRequest sysAppVersionRequest) throws Exception {

        log.info("request body {}", JsonUtils.serialize(sysAppVersionRequest));
        //log.info("app更新接口");
        SysAppVersionModel result =  this.systemService.checkUpdate(sysAppVersionRequest);
        result.setAppVersion(sysAppVersionRequest.getClient_version());
        return ResponseEntityBuilder.success(result);
    }


    @ApiOperation("用户登录或注册")
    @RequestMapping(value = "/users/signup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<LoginSession> signup(HttpServletRequest httpRequest, @RequestBody UsrRequst usrRequst)
            throws Exception {
        log.info("request body {}", JsonUtils.serialize(usrRequst));
        //log.info("用户登录或注册");
        String ipAddr = GetIpAddressUtil.getIpAddr(httpRequest);
        log.info("request ip address {}", ipAddr);
        usrRequst.setIPAdress(ipAddr);
        LoginSession result = this.usrService.signup(usrRequst);
        result.setAppVersion(usrRequst.getClient_version());
        return ResponseEntityBuilder.success(result);
    }

    @ApiOperation("/v2/users/signup")
    @RequestMapping(value = "/v2/users/signup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<LoginSession> signupV2(HttpServletRequest httpRequest, @RequestBody UsrRequst usrRequst)
            throws Exception {
        log.info("v2/users/signup - request body {}", JsonUtils.serialize(usrRequst));
        String ipAddr = GetIpAddressUtil.getIpAddr(httpRequest);
        log.info("request ip address {}", ipAddr);
        usrRequst.setIPAdress(ipAddr);
        LoginSession result = this.usrService.signupV2(usrRequst);
        result.setAppVersion(usrRequst.getClient_version());
        return ResponseEntityBuilder.success(result);
    }

    @ApiOperation("???????")
    @RequestMapping(value = "/system/smsCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<Object> smsCode(HttpServletRequest httpServletRequest, @RequestBody SmsRequest smsRequest) throws Exception {
        log.info("/system/smsCode - request body {}", JsonUtils.serialize(smsRequest));
        smsService.sendSmsCode(smsRequest);
        CompatibleResponse result = new CompatibleResponse();
        result.setAppVersion(smsRequest.getClient_version());
        return ResponseEntityBuilder.success(result );
    }

    @ApiOperation("/v2/system/smsCode")
    @RequestMapping(value = "/v2/system/smsCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<Object> smsCodeV2(HttpServletRequest httpServletRequest, @RequestBody SmsRequest smsRequest) throws Exception {
        log.info("/v2/system/smsCode - request body {}", JsonUtils.serialize(smsRequest));
        smsService.sendSmsCodeV2(smsRequest);
        CompatibleResponse result = new CompatibleResponse();
        result.setAppVersion(smsRequest.getClient_version());
        return ResponseEntityBuilder.success(result );
    }

    @ApiOperation("退出登录")
    @RequestMapping(value = "/users/logout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<Object> logout(HttpServletRequest request, @RequestBody BaseRequest baseRequest) throws ServiceException {
//        UserSessionUtil.filter(request,redisClient,baseRequest);
        log.info("request body {}", JsonUtils.serialize(baseRequest));
        log.info("退出登录");
        this.usrService.logout(baseRequest);
        CompatibleResponse result = new CompatibleResponse();
        result.setAppVersion(baseRequest.getClient_version());
        return ResponseEntityBuilder.success(result);
    }


    @ApiOperation("短信验证码自动登录")
    @RequestMapping(value = "/users/smsAutoLogin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<LoginSession> smsAutoLogin(HttpServletRequest httpRequest, @RequestBody UsrRequst usrRequst)
            throws Exception {
        log.info("request body {}", JsonUtils.serialize(usrRequst));
        //log.info("短信验证码自动登录");
        LoginSession result = this.usrService.smsAutoLogin(usrRequst);
        result.setAppVersion(usrRequst.getClient_version());
        return ResponseEntityBuilder.success(result);
    }
}
