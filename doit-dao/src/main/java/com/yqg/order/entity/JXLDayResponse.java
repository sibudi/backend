package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/8/20.
 */
@Data
public class JXLDayResponse {

    private String date;
    private String amount;
    private String response;
    private String total;
    private String proportion;
}
