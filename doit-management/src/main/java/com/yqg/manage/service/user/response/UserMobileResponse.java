package com.yqg.manage.service.user.response;

import lombok.Data;

/**
 * Author: tonggen
 * Date: 2018/9/12
 * time: 下午4:11
 */
@Data
public class UserMobileResponse extends BaseUserMobileResponse{

    private String relation;

    private String waOrLine;

    private Integer sequence;
}
