package com.yqg.controller.system;

import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.SmsRequest;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.JsonUtils;
import com.yqg.service.user.service.SmsService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

/**
 * ?????
 * Created by lxj on 2017/11/24.
 */
@RestController
@Slf4j
public class SmsController {

    @Autowired
    private SmsService smsService;
    @Autowired
    private RedisClient redisClient;

//    @ApiOperation("???????")
//    @RequestMapping(value = "/system/smsCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
//    @ResponseBody
//    public ResponseEntity<Object> smsCode(HttpServletRequest httpServletRequest, @RequestBody SmsRequest smsRequest) throws Exception {
//        //log.info("request body {}", JsonUtils.serialize(smsRequest));
//        log.info("???????");
//        smsService.sendSmsCode(smsRequest);
//        return ResponseEntityBuilder.success();
//    }


}
