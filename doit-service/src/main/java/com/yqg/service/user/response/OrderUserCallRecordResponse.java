package com.yqg.service.user.response;

import lombok.Data;

/**
 * @Author Jacob
 */
@Data
public class OrderUserCallRecordResponse {

    private String mobile;

    private String realName;

    private Integer duration;

    private String callTime;
    /**
     * 呼叫类型 ： 1
     */
    private Integer type ;

    private Integer callCount;
}
