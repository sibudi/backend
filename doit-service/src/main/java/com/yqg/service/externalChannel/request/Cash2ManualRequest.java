package com.yqg.service.externalChannel.request;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2019/1/16.
 */
@Data
public class Cash2ManualRequest {

    private String orderNo;//订单号

    private Integer orderStatus;// 订单状态
}
