package com.yqg.service.user.response;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/7/5.
 */
@Data
public class SmsVeriResponse {
    private String code;

    private String message;

    private String transactionId;

}
