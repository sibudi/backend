package com.yqg.service.user.response;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2019/6/28.
 */
@Data
public class SmsSendInforbipResponse {

    private String pinId;

    private String to;

    private String ncStatus;

    private String smsStatus;
}
