package com.yqg.service.p2p.request;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2019/7/8.
 */
@Data
public class CheckOrderBillStatusRequest {

    private String creditorNo;
    private String periodNo;
    private String sign;
}
