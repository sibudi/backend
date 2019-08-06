package com.yqg.manage.controller.third;

import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.third.PayOurService;
import com.yqg.manage.service.third.request.BalaceActionRequest;
import com.yqg.manage.service.third.request.OfflineRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * Author: tonggen
 * Date: 2019/4/17
 * time: 下午5:06
 * 转发淼科那边接口
 */
@RestController
@RequestMapping("/manage")
public class PayController {

    @Value("${manager.ourPayPath}")
    private String url;

    @Autowired
    private PayOurService service;

    @RequestMapping(value = "/all/balance", method = RequestMethod.GET)
    public ResponseEntitySpec<Object> balance(String token) {
        String managerUrl = url;
        managerUrl += "all/balance";
        return ResponseEntitySpecBuilder.success(service.balance(managerUrl, token));
    }

    @RequestMapping(value = "/fund/offline/all", method = RequestMethod.GET)
    public ResponseEntitySpec<Object> offline(OfflineRequest request) {
        String managerUrl = url;
        managerUrl += "fund/offline/all";
        return ResponseEntitySpecBuilder.success(service.offline(managerUrl, request));
    }

    @RequestMapping(value = "/fund/offline/balance/action", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> action(@RequestBody BalaceActionRequest request) throws Exception{
        String managerUrl = url;
        managerUrl += "fund/offline/balance/action";
        return ResponseEntitySpecBuilder.success(service.action(managerUrl, request));
    }

}
