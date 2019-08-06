package com.yqg.service.user.request;

import lombok.Data;

/**
 * Author: tonggen
 * Date: 2019/6/20
 * time: 10:29 AM
 */
@Data
public class UsrQuestionnaireListRequest {

    private Integer type ;//1 is bussiness, 2 not

    private Integer state;

    private String createTimeBegin;

    private String createTimeEnd;

    private Integer pageNo = 1;

    private Integer pageSize = 10;

    private String userUuid;
}
