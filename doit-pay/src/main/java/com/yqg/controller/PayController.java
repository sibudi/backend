package com.yqg.controller;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.common.utils.JsonUtils;
import com.yqg.request.ManualRepayOrderRequest;
import com.yqg.service.LoanService;
import com.yqg.service.PayService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

/**
 * Created by Didit Dwianto on 2017/12/28.
 */
@H5Request
@RestController
@Slf4j
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private LoanService loanService;
    @Autowired
    private PayService payService;

//    @ApiOperation("????")
//    @RequestMapping(value = "/loanTest", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
//    @ResponseBody
//    public ResponseEntity<OrderOrderResponse> toOrder(HttpServletRequest request, @RequestBody BaseRequest baseRequest)
//            throws ServiceException {
//
//        payService.cheakRepayOrder();
//        return ResponseEntityBuilder.success();
//    }


    @ApiOperation("????????")
    @RequestMapping(value = "/manualRepayOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> register(HttpServletResponse response, HttpServletRequest request,
                                               @RequestBody ManualRepayOrderRequest repayOrderRequest) throws Exception {
        log.info("request body {}", JsonUtils.serialize(repayOrderRequest));
        this.payService.manualOperationRepayOrder(repayOrderRequest);
        return ResponseEntitySpecBuilder.success();
    }

//    @ApiOperation("??---????????")
//    @RequestMapping(value = "/loanOrderByFinancial", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
//    @ResponseBody
//    public ResponseEntitySpec<Object> loanOrderByFinancial(@RequestBody ManualRepayOrderRequest repayOrderRequest) throws Exception {
//
//        log.info("request body {}", JsonUtils.serialize(repayOrderRequest));
//        this.loanService.LoanOrderByFinancial(repayOrderRequest.getOrderNo());
//        return ResponseEntitySpecBuilder.success();
//    }

}
