package com.yqg.manage.service.user.response;

import lombok.Data;

/**
 * @author alan
 */
@Data
public class ManSysLoginResponse {
    private String sessionId;

    private String userName;

    private String realName;

    private String uuid;

    private Integer id;

    private Integer country;

    private String employeeNumber;

    private String voicePhone;

}
