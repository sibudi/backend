package com.yqg.manage.controller.loan;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.loan.ManagementLoanService;
import com.yqg.manage.service.loan.request.LoanIssuedParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 ****/

@RestController
@RequestMapping("/manage")
@Slf4j
@H5Request
@Api(tags = "贷款管理类")
public class LoanController {

    @Autowired
    private ManagementLoanService managementLoanService;

    @ApiOperation("后台管理人员出发放款, Author: zengxiangcai")
    @RequestMapping(value = "/loan/issuing", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> issueLoan(@RequestBody LoanIssuedParam param) {
        log.info("send issue loan request, orderNo= " + param.getOrderUUID());
        managementLoanService.issueLoan(param.getOrderUUID());
        return ResponseEntitySpecBuilder.success();
    }
}
