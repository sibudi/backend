package com.yqg.service.user.request;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/9/19.
 */
@Data
public class InsertOrderPaymentCodeRequest {

    private String paymentCode;

    private String orderNo;

    private String paymentAmout;
}
