package com.yqg.service.user.model;

import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/12/20.
 */
@Data
public class UserCertificationInfoInRedis {

    private String orderNo;
    private String userUuid;
    private String phoneNo;
    private String pwd;
    private String email;
    private String batchId;
    private String certType;
    private String report_task_token;
    private String mobile;
}
