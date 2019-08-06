package com.yqg.service.p2p.request;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/9/7.
 */
@Data
public class CheckOrderInfoRequest extends P2PInvokeBaseParam {

    private String creditorNo;
    private String status;
}
