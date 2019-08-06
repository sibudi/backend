package com.yqg.drools.service.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiBaseResponse {
    private String code = "0" ;
    private String message;
    private Object data;

    public static ApiBaseResponse createResponse(){
        ApiBaseResponse response = new ApiBaseResponse();
        return response;
    }

    public ApiBaseResponse withCode(String code){
        this.code = code;
        return this;
    }

    public ApiBaseResponse withMessage(String msg){
        this.message = msg;
        return this;
    }

    public ApiBaseResponse withData(Object data){
        this.data = data;
        return this;
    }
}
