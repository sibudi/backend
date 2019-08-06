package com.yqg.service.p2p.response;

import lombok.Data;

/**
 * Created by Didit Dwianto on 2018/3/1.
 */
@Data
public class P2PResponse {

    private Integer code;  // 0 是正常，其他为错误
    private String message;
    private Object data; // 结果

    public P2PResponse withCode(int code) {
        this.code = code;
        return this;
    }

    public P2PResponse withMessage(String message) {
        this.message = message;
        return this;
    }

    public P2PResponse withData(Object data) {
        this.data = data;
        return this;
    }

    public static boolean isSuccessResponse(P2PResponse response) {
        return response != null && response.getCode() != null && response.getCode() == 0;
    }


}
