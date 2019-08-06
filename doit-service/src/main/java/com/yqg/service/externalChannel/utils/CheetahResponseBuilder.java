package com.yqg.service.externalChannel.utils;

import com.yqg.service.externalChannel.response.CheetahResponse;

/**
 * Created by wanghuaizhou on 2018/12/26.
 */
public class CheetahResponseBuilder {

    public static CheetahResponse buildResponse(CheetahResponseCode responseCode) {
        CheetahResponse response = new CheetahResponse();
        response.setCode(responseCode.getCode());
        response.setMessage(responseCode.getMessage());
        return response;
    }

}
