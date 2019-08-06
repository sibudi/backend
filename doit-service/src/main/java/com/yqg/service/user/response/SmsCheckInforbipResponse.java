package com.yqg.service.user.response;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2019/6/28.
 */
@Data
public class SmsCheckInforbipResponse {

    private String pinId;

    private String msisdn;

    private String verified;

    private String attemptsRemaining;

}
