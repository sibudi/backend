package com.yqg.manage.service.mongo.response;

import lombok.Data;

/**
 * @Author Jacob
 */
@Data
public class OrderEmergencyContactResponse {

    private String mobile;

    private String realName;

    public OrderEmergencyContactResponse() {

    }

    public OrderEmergencyContactResponse(String mobile, String realName) {
        this.mobile = mobile;
        this.realName = realName;
    }
}
