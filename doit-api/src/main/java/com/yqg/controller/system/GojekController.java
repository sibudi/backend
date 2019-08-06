package com.yqg.controller.system;

import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.UserSessionUtil;
import com.yqg.service.third.gojek.GojekService;
import com.yqg.service.third.gojek.request.GojekRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Didit Dwianto on 2017/12/20.
 */
@RestController
@Slf4j
@RequestMapping("/gojek")
public class GojekController {

    @Value("${gojek.beginurl}")
    private  String beginurl;
    @Value("${gojek.authurl}")
    private  String authurl;
    @Value("${gojek.resendCaptchaurl}")
    private  String resendCaptchaurl;
    @Value("${gojek.getGoJekReportDataurl}")
    private  String getGoJekReportDataurl;

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private GojekService gojekService;

    @RequestMapping("/begin")
    @ResponseBody
    public ResponseEntity<Object> begin(HttpServletRequest request, @RequestBody GojekRequest gojekRequest)throws ServiceException {
//        UserSessionUtil.filter(request, this.redisClient, gojekRequest);
//        log.info("request body{}", JsonUtils.serialize(gojekRequest));
        gojekRequest.setId_num(gojekRequest.getMobile());
        String result = gojekService.begin(beginurl, gojekRequest).toString();
        log.info(result);
        return ResponseEntityBuilder.success();
    }

}
