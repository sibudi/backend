package com.yqg.controller.user;

import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.models.BaseRequest;
import com.yqg.common.models.LoginSession;
import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.GetIpAddressUtil;
import com.yqg.service.user.request.*;
import com.yqg.service.user.response.UsrAttachmentResponse;
import com.yqg.service.user.response.UsrCertificationResponse;
import com.yqg.service.user.service.UsrService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by wanghuaizhou on 2017/11/24.
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class UsrController {

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private UsrService usrService;


    @ApiOperation("用户意见反馈")
    @RequestMapping(value = "/feedback", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<Object> userFeedback(HttpServletRequest request, @RequestBody UsrFeedBackRequest usrFeedBackRequest) throws Exception {

//        UserSessionUtil.filter(request,this.redisClient, usrFeedBackRequest);
//        log.info("request body {}", JsonUtils.serialize(usrFeedBackRequest));
        log.info("用户意见反馈");
        this.usrService.userFeedBack(usrFeedBackRequest);
        return ResponseEntityBuilder.success();
    }

//
//    @ApiOperation("用户登录或注册")
//    @RequestMapping(value = "/signup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
//    @ResponseBody
//    public ResponseEntity<LoginSession> signup(HttpServletRequest httpRequest, @RequestBody UsrRequst usrRequst)
//            throws Exception {
//        //log.info("request body {}", JsonUtils.serialize(usrRequst));
//        log.info("用户登录或注册");
//        String ipAddr = GetIpAddressUtil.getIpAddr(httpRequest);
//        log.info("request ip address {}", ipAddr);
//        usrRequst.setIPAdress(ipAddr);
//        return ResponseEntityBuilder.success(this.usrService.signup(usrRequst));
//    }

//
//    @ApiOperation("短信验证码自动登录")
//    @RequestMapping(value = "/smsAutoLogin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
//    @ResponseBody
//    public ResponseEntity<LoginSession> smsAutoLogin(HttpServletRequest httpRequest, @RequestBody UsrRequst usrRequst)
//            throws Exception {
//        //log.info("request body {}", JsonUtils.serialize(usrRequst));
//        log.info("短信验证码自动登录");
//        return ResponseEntityBuilder.success(this.usrService.smsAutoLogin(usrRequst));
//    }

//    @ApiOperation("退出登录")
//    @RequestMapping(value = "/logout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
//    @ResponseBody
//    public ResponseEntity<Object> logout(HttpServletRequest request, @RequestBody BaseRequest baseRequest) throws ServiceException {
////        UserSessionUtil.filter(request,redisClient,baseRequest);
////        log.info("request body {}", JsonUtils.serialize(baseRequest));
//        log.info("退出登录");
//        this.usrService.logout(baseRequest);
//        return ResponseEntityBuilder.success();
//    }


    @ApiOperation("验证信息页面初始化")
    @RequestMapping(value = "/initCertificationView", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<UsrCertificationResponse> initCertificationView(HttpServletRequest request, @RequestBody BaseRequest baseRequest) throws Exception {

//        UserSessionUtil.filter(request,this.redisClient,baseRequest);
//        log.info("request body {}", JsonUtils.serialize(baseRequest));
        log.info("验证信息页面初始化");
        return ResponseEntityBuilder.success(this.usrService.initCertificationView(baseRequest));
    }

    @ApiOperation("提交验证信息")
    @RequestMapping(value = "/submitCertificationInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<Object> submitCertificationInfo(HttpServletRequest request, @RequestBody UsrSubmitCerInfoRequest infoRequest) throws Exception {

//        UserSessionUtil.filter(request,this.redisClient,infoRequest);
//        log.info("request body {}", JsonUtils.serialize(infoRequest));
        log.info("提交验证信息");
        this.usrService.submitCertificationInfo(infoRequest);
        return ResponseEntityBuilder.success();
    }


    @ApiOperation("提交补充信息")
    @RequestMapping(value = "/submitSupplementInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<Object> submitSupplementInfo(HttpServletRequest request, @RequestBody UsrSubmitSupplementInfoRequest infoRequest) throws Exception {

//        UserSessionUtil.filter(request,this.redisClient,infoRequest);
//        log.info("request body {}", JsonUtils.serialize(infoRequest));
        log.info("提交补充信息");
        this.usrService.submitSupplementInfo(infoRequest);
        return ResponseEntityBuilder.success();
    }

    @ApiOperation("反显补充信息")
    @RequestMapping(value = "/initSupplementInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<List<UsrAttachmentResponse>> initSupplementInfo(HttpServletRequest request, @RequestBody BaseRequest baseRequest) throws Exception {

//        UserSessionUtil.filter(request,this.redisClient,baseRequest);
//        log.info("request body {}", JsonUtils.serialize(baseRequest));
        log.info("反显补充信息");
        return ResponseEntityBuilder.success(this.usrService.initSupplementInfo(baseRequest));
    }

    @ApiOperation("查询用户身份")
    @RequestMapping(value = "/cheakUserRole", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<Object> cheakUserRole(HttpServletRequest request, @RequestBody BaseRequest baseRequest) throws Exception {

//        UserSessionUtil.filter(request,this.redisClient,baseRequest);
//        log.info("request body {}", JsonUtils.serialize(baseRequest));
        log.info("查询用户身份");
        return ResponseEntityBuilder.success(this.usrService.cheakUserRole(baseRequest));
    }

    @ApiOperation("用户对催收人员打分")
    @RequestMapping(value = "/insertCollectionScore", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<Object> insertCollectionScore(@RequestBody UsrEvaluateScoreRequest request) throws Exception {

        log.info("用户对催收人员打分");
        this.usrService.insertCollectionScore(request);
        return ResponseEntityBuilder.success();
    }
}
