package com.yqg.manage.service.system.request;

import lombok.Data;

/**
 * Author: tonggen
 * Date: 2019/5/14
 * time: 10:58 AM
 */
@Data
public class ManCtrlcRecordRequest {

    private String orderNo;

    private String userUuid;

    private Integer operator;

    private String content;

}
