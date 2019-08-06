package com.yqg.service.externalChannel.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomHttpResponse {
    private Integer status;
    private String content;
    private String errorMsg;

    public boolean isResponseOk() {
        boolean statusOk = String.valueOf(status).startsWith("2");
        return statusOk;
    }

    public CustomHttpResponse withErrorMsg(String msg){
        this.errorMsg = msg;
        return this;
    }
}
