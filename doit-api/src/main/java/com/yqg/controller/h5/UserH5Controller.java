package com.yqg.controller.h5;

import com.alibaba.fastjson.JSONObject;
import com.yqg.common.annotations.H5Request;
import com.yqg.common.constants.MessageConstants;
import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.GetIpAddressUtil;
import com.yqg.common.utils.JsonUtils;
import com.yqg.service.h5.UsrH5Service;
import com.yqg.service.h5.request.SmsH5Request;
import com.yqg.service.h5.request.UserRegisterH5Request;
import com.yqg.service.h5.response.ImageCodeModelSpec;
import com.yqg.service.h5.response.SmsH5Response;
import com.yqg.service.order.OrdService;
import com.yqg.service.order.request.OrdRequest;
import com.yqg.service.partner.request.DeviceInfoRequest;
import com.yqg.service.partner.request.RegistrationRequest;
import com.yqg.service.system.service.RepayRateService;
import com.yqg.service.third.mobox.MoboxService;
import com.yqg.service.user.request.LinkManRequest;
import com.yqg.service.user.request.SaveUserPhotoRequest;
import com.yqg.service.user.request.UsrBankRequest;
import com.yqg.service.user.request.UsrRequst;
import com.yqg.service.user.request.UsrWorkBaseInfoRequest;
import com.yqg.service.user.service.UserLinkManService;
import com.yqg.service.user.service.UsrBankService;
import com.yqg.service.user.service.UsrBaseInfoService;
import com.yqg.service.user.service.UsrService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

/**
 * Created by wanghuaizhou on 2018/1/9.
 */
@H5Request
@RestController
@Slf4j
@RequestMapping("/web/users")
public class UserH5Controller {

    @Autowired
    private UsrH5Service usrH5Service;
    @Autowired
    private UsrService usrService;

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private OrdService ordService;
    @Autowired
    private UsrBaseInfoService usrBaseInfoService;
    @Autowired
    private UserLinkManService userLinkManService;
    @Autowired
    private UsrBankService usrBankService;

    @Autowired
    private RepayRateService repayRateService;

    @ApiOperation("获取图片验证码")
    @RequestMapping(value = "/randomImage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<ImageCodeModelSpec> randomImage() throws Exception {
        log.info("H5注册 获取图片验证码");
        return ResponseEntitySpecBuilder.success(this.usrH5Service.randomImage());
    }

    @ApiOperation("H5注册 校验图形验证码后发送短信")
    @RequestMapping(value = "/sendH5SmsCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<SmsH5Response> sendH5SmsCode(@RequestBody SmsH5Request smsH5Request) throws Exception {
        log.info("验图形验证码后发送短信");
        return ResponseEntitySpecBuilder.success(this.usrH5Service.sendH5SmsCode(smsH5Request));
    }

    @ApiOperation("H5 registration")
    @RequestMapping(value = "/register/partner", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> register(HttpServletRequest request, @RequestBody UserRegisterH5Request h5Request) throws Exception {

        //log.info("request body {}", JsonUtils.serialize(h5Request));
        log.info("H5注册 接口");
        String ipAddr = GetIpAddressUtil.getIpAddr(request);
        log.info("request ip address {}", ipAddr);
        h5Request.setIPAdress(ipAddr);
        return ResponseEntitySpecBuilder.success(this.usrH5Service.register(h5Request));
    }

    @ApiOperation("用户邀请注册")
    @RequestMapping(value = "/inviteSignup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<JSONObject> inviteSignup(HttpServletRequest httpRequest, @RequestBody UsrRequst usrRequst)
            throws Exception {
        //log.info("request body {}", JsonUtils.serialize(usrRequst));
        log.info("用户邀请注册");
        String ipAddr = GetIpAddressUtil.getIpAddr(httpRequest);
        log.info("request ip address {}", ipAddr);
        usrRequst.setIPAdress(ipAddr);
        return ResponseEntitySpecBuilder.success(this.usrService.inviteSignup(usrRequst));
    }


    @ApiOperation("获取金额回收率")
    @RequestMapping(value = "/getRepayRate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public String getRepayRate()
            throws Exception {
        log.info("获取金额回收率");
        return this.repayRateService.getRepayRate();
    }

}
