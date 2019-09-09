package com.yqg.service.user.response;

import lombok.Data;

import java.util.Date;

/**
 * Author: tonggen
 * Date: 2019/6/20
 * time: 10:32 AM
 */
@Data
public class UsrQuestionnaireBaseResponse {

    private String uuid;

    private String userUuid;

    private Integer type;

    private Date createTime;

    private Integer state; //1.wait check, 2.pass 3, not pass

    private Integer checker;

    private String checkerName;

    private String userName;

}
