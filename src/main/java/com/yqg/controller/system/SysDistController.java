package com.yqg.controller.system;

import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.UserSessionUtil;
import com.yqg.service.system.service.SysDistService;
import com.yqg.service.system.request.SysDistRequest;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * Created by ??? on 2017/11/24.
 */
@RestController
@Slf4j
@RequestMapping("/sysDist")
public class SysDistController {


    @Autowired
    private SysDistService sysDistService;
    @Autowired
    private RedisClient redisClient;

    @ApiOperation("????????")
    @RequestMapping(value = "/getSysDist", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> getSysDist(HttpServletRequest request, @RequestBody SysDistRequest sysDistRequest) throws Exception {

//        UserSessionUtil.filter(request,this.redisClient,sysDistRequest);
//        log.info("request body {}", JsonUtils.serialize(sysDistRequest));
        return ResponseEntityBuilder.success(this.sysDistService.getDistList(sysDistRequest));

    }

}
