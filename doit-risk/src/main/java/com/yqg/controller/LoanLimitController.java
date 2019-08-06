package com.yqg.controller;

import com.yqg.drools.service.ApplicationService;
import com.yqg.drools.service.response.ApiBaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loan-limit")
public class LoanLimitController {
    @Autowired
    private ApplicationService applicationService;

    @RequestMapping(value = "/firstReBorrowing", method = RequestMethod.GET)
    public ApiBaseResponse checkLoanLimit(@RequestParam("userUuid") String userUuid) {
        return applicationService.checkUserLoanLimit(userUuid);
    }
}
