package com.yqg.service.p2p.request;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/9/6.
 */
@Data
public class QueryUserInfoReqeust  extends P2PInvokeBaseParam {
    private String userUuid;
}
