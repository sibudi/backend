package com.yqg.service.externalChannel.response;

import com.yqg.common.annotations.CheetahRequest;
import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/12/26.
 */
@Data
@CheetahRequest
public class CheetahResponse {
    private int code;
    private String message;
    private Object data;

    public CheetahResponse withCode(int code) {
        this.code = code;
        return this;
    }

    public CheetahResponse withMessage(String message) {
        this.message = message;
        return this;
    }

    public CheetahResponse withData(Object data) {
        this.data = data;
        return this;
    }
}
