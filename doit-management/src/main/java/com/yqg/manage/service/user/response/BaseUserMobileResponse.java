package com.yqg.manage.service.user.response;

import lombok.Data;

/**
 * Author: tonggen
 * Date: 2018/9/12
 * time: 下午2:04
 */
@Data
public class BaseUserMobileResponse {


    private String mobile;

    private String callResult;

    private String realName;

    /**
     * 催收结果
     */
    private Integer contactResultPhone;

    private Integer contactResultWA;

    private Integer contactResultSms;

}
