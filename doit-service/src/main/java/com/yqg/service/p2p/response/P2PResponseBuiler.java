package com.yqg.service.p2p.response;

/**
 * Created by wanghuaizhou on 2018/9/7.
 */
public class P2PResponseBuiler {

    public static P2PResponse buildResponse(P2PResponseCode responseCode) {
        P2PResponse response = new P2PResponse();
        response.setCode(responseCode.getCode());
        response.setMessage(responseCode.getMessage());
        return response;
    }
}
