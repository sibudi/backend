package com.yqg.service.externalChannel.utils;

import com.yqg.service.externalChannel.response.Cash2Response;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/7
 * @Email zengxiangcai@yishufu.com
 *
 ****/

public class Cash2ResponseBuiler {

    public static Cash2Response buildResponse(Cash2ResponseCode responseCode) {
        Cash2Response response = new Cash2Response();
        response.setCode(responseCode.getCode());
        response.setMessage(responseCode.getMessage());
        return response;
    }

}
