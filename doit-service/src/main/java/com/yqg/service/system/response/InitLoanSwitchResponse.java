package com.yqg.service.system.response;

import lombok.Data;

import java.util.*;

@Data
public class InitLoanSwitchResponse {

    private String code;

    private String message;

    private Map<String,String> loanSwitchStatus;



}
