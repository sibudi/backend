package com.yqg.controller;

import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.service.pay.RepayService;
import com.yqg.service.pay.response.RepayResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

/**
 * Created by arief.halim on 2019/12/10.
 */
@RestController
@Slf4j
@RequestMapping("/rdn")
public class RdnRepaymentController {

    @Autowired
    private RepayService repayService;

    @RequestMapping(value = "/bulk-repayment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<RepayResponse> BulkRdnRepayment(HttpServletResponse response, HttpServletRequest request) throws Exception {
        RepayResponse repayResponse = this.repayService.BulkRdnRepayment();

        return ResponseEntitySpecBuilder.success(repayResponse);
    }
}
