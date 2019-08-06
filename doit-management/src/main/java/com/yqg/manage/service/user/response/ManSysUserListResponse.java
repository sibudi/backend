package com.yqg.manage.service.user.response;


import lombok.Data;

import java.util.Date;

/**
 * @author Jacob
 */
@Data
public class ManSysUserListResponse {
    private Integer id;

    private String uuid;

    private String username;

    private String realname;

    private String mobile;

    private String remark;

    private Integer status;

    private Date createTime;

    private String roles;

    private String collectionPhone;

    private String collectionWa;

    private String employeeNumber;

    private String voicePhone;

    /**
     * 用户是否已登录
     */
    private Boolean onlineOrNot;

}
