package com.yqg.controller.system;

import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.models.BaseRequest;
import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.UserSessionUtil;
import com.yqg.service.system.service.IndexService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import javax.ws.rs.core.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 首页
 * Created by luhong on 2017/11/24.
 */
@RestController
@RequestMapping("/index")
@Slf4j
public class IndexController {

    @Autowired
    private IndexService indexService;

    @ApiOperation("初始化首页")
    @RequestMapping(value = "/initHomeView", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<Object> initHomeView(HttpServletRequest request, @RequestBody BaseRequest baseRequest)
            throws ServiceException {
        log.info("初始化首页");
        // TODO 加解密
        return ResponseEntityBuilder.success(indexService.initHomeView(baseRequest));
    }

}
