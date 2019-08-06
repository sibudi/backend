package com.yqg.service.externalChannel.request;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/12/27.
 */
@Data
public class CheetahBaseRequest {

    private String accessKey;

    private long timestamp;

    private String sign;
}
