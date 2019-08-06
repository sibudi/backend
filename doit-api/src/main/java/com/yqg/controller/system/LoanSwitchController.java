package com.yqg.controller.system;

import com.yqg.common.models.BaseRequest;
import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.service.system.request.LoanSwitchRequest;
import com.yqg.service.system.response.InitLoanSwitchResponse;
import com.yqg.service.system.response.LoanSwitchResponse;
import com.yqg.service.system.service.LoanSwitchService;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;

@RestController
@Slf4j
@RequestMapping("/system")
public class LoanSwitchController {

    @Autowired
    private LoanSwitchService loanSwitchService;


    @ApiOperation("更新放款开关状态")
    @RequestMapping(value = "/UpdateLoanSwitch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<LoanSwitchResponse> loanSwitch(@RequestBody LoanSwitchRequest loanSwitchRequest){

        return ResponseEntityBuilder.success(this.loanSwitchService.loanSwitch(loanSwitchRequest));
    }

    @ApiOperation("放款开关状态初始化")
    @RequestMapping(value = "/InitLoanSwitch", method = RequestMethod.POST)
    public ResponseEntity<InitLoanSwitchResponse> initLoanSwitch(){

        return ResponseEntityBuilder.success(this.loanSwitchService.initLoanSwitch());
    }

}
