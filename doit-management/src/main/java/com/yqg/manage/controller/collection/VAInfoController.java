package com.yqg.manage.controller.collection;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.manage.service.vainfo.VAInfoService;
import com.yqg.service.pay.RepayService;
import com.yqg.service.pay.request.RepayRequest;
import com.yqg.service.pay.response.RepayResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Created by Janhsen on 2020/03/05.
 */
@RestController
@RequestMapping("/manage")
@H5Request
@Api(tags = "vainfo")
public class VAInfoController{

    @Autowired
    private VAInfoService vaInfoService;

    @ApiOperation(value = "VA information")
    @RequestMapping(value = "/vainfo", method = RequestMethod.POST)
    public ResponseEntity<RepayResponse> repayment(HttpServletRequest request, @RequestBody RepayRequest repayRequest)throws Exception {
        RepayResponse repayResponse =  this.vaInfoService.vaInfo(repayRequest);
        return ResponseEntityBuilder.success(repayResponse);
    }
}
