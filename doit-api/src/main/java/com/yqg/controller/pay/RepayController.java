package com.yqg.controller.pay;

import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.common.redis.RedisClient;
import com.yqg.service.pay.RepayService;
import com.yqg.service.pay.request.RepayRequest;
import com.yqg.service.pay.response.RepayResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wanghuaizhou on 2017/12/29.
 */
@RestController
@Slf4j
@RequestMapping("/repay")
public class RepayController{

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private RepayService repayService;

    @RequestMapping("/repayment")
    @ResponseBody
    public ResponseEntity<RepayResponse> repayment(HttpServletRequest request, @RequestBody RepayRequest repayRequest)throws Exception {
        log.info("用户提交还款");
        RepayResponse repayResponse =  this.repayService.repayment(repayRequest);
        return ResponseEntityBuilder.success(repayResponse);
    }
}
