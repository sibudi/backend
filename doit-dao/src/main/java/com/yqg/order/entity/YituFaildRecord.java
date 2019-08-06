package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/6/22.
 */
@Data
public class YituFaildRecord {
    private String orderNo;
    private String userUuid;
    private String responseMessage;
}
