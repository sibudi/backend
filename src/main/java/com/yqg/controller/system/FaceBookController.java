package com.yqg.controller.system;

import com.yqg.common.models.BaseRequest;
import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.common.utils.UserSessionUtil;
import com.yqg.service.system.request.FacebookRequest;
import com.yqg.service.system.response.FacebookResponse;
import com.yqg.service.system.service.FacebookService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by wanghuaizhou on 2018/1/23.
 */
@RestController
@Slf4j
@RequestMapping("/facebook")
public class FaceBookController {

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private FacebookService facebookService;

    @Value("${jxl.faceBookUrl}")
    private String faceBookUrl;

    /**
     *   获取facebook url
     * */
    @RequestMapping("/getFacebookUrl")
    @ResponseBody
    public ResponseEntity<FacebookResponse> getFacebookUrl(HttpServletRequest request, @RequestBody FacebookRequest facebookRequest)throws Exception {

//        UserSessionUtil.filter(request, this.redisClient, facebookRequest);
//        log.info("request body{}", JsonUtils.serialize(facebookRequest));
        log.info(" 获取facebook url");
        return ResponseEntityBuilder.success(this.facebookService.getFacebookUrl(facebookRequest,faceBookUrl));
    }

    /**
     *   facebook授权后回调
     * */
    @RequestMapping(value = "/getFacebookData", method = RequestMethod.GET)
    @ResponseBody
    public void getFacebookData(@RequestParam Map<String, String> map)
            throws Exception {

        log.info("收到facebook 回调");
        log.info(map.toString());

        if (!StringUtils.isEmpty(map.get("code"))){
            if (map.get("code").equals("SUCCESS")){
                this.facebookService.getFacebookCallback(map);
//            return ResponseEntityBuilder.success();
            }
        }

//        else {
//            return new ResponseEntityBuilder<>().code(100).message("collect_exception").build();
//        }

    }
}
