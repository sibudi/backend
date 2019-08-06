package com.yqg.controller.user;

import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.models.BaseRequest;
import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.common.redis.RedisClient;
import com.yqg.service.user.request.UsrBankRequest;
import com.yqg.service.user.request.UsrGoPayReq;
import com.yqg.service.user.response.UsrGoPayResp;
import com.yqg.service.user.service.UsrGoPayService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

/**
 * Features:
 * Created by huwei on 18.8.16.
 */
@RestController
@Slf4j
@RequestMapping("/gopay")
public class UsrGoPayController {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private UsrGoPayService usrGoPayService;

    @ApiOperation("添加用户gopay信息")
    @RequestMapping(value = "/saveUserGoPay", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<Object> saveUserGoPay(HttpServletRequest request, @RequestBody UsrGoPayReq usrGoPayReq)
            throws ServiceException {
//        UserSessionUtil.filter(request, this.redisClient, userBankRequest);
//        log.info("request body{}", JsonUtils.serialize(userBankRequest));
        log.info("添加用户gopay信息");
        usrGoPayService.saveUserGoPay(usrGoPayReq);
        return ResponseEntityBuilder.success();
    }

    @ApiOperation("查询用户gopay信息")
    @RequestMapping(value = "/selectUserGoPay", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<UsrGoPayResp> selectUserGoPay(HttpServletRequest request, @RequestBody BaseRequest baseRequest)
            throws ServiceException {
//        UserSessionUtil.filter(request, this.redisClient, baseRequest);
//        log.info("request body{}", JsonUtils.serialize(baseRequest));
        log.info("添加用户gopay信息");
        return ResponseEntityBuilder.success(usrGoPayService.selectUserGoPay(baseRequest));
    }
}
