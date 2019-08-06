package com.yqg.service.p2p.response;

import lombok.Getter;

/**
 * Created by wanghuaizhou on 2018/9/7.
 */
@Getter
public enum P2PResponseCode {

    CODE_OK_1(0,"success"),
    CODE_ERROR_1(1,"error");


    P2PResponseCode(int code,String message){
        this.code=code;
        this.message=message;
    }
    private String message;
    private int code;

}
