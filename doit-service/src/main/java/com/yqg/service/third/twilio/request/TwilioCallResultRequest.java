package com.yqg.service.third.twilio.request;

import lombok.Data;

/**
 * Author: tonggen
 * Date: 2018/10/16
 * time: 下午2:09
 */
@Data
public class TwilioCallResultRequest {

    /**
     * D0，D-1等
     */
    private String callPhase;

    /**
     * 全部或者未接通用户
     */
    private Integer callPhaseType;

    /**
     * 逾期几天
     */
    private Integer days;

    /**
     * 外呼需要的url
     */
    private String callUrl;

    /**
     * 批次号
     */
    private String batchNo;

}
