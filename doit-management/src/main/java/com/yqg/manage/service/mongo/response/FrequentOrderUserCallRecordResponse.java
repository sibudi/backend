package com.yqg.manage.service.mongo.response;

import lombok.Data;

/**
 * @Author Jacob
 */
@Data
public class FrequentOrderUserCallRecordResponse {

    private String mobile;

    private String realName;

    public FrequentOrderUserCallRecordResponse() {

    }

    public FrequentOrderUserCallRecordResponse(String mobile, String realName) {
        this.mobile = mobile;
        this.realName = realName;
    }
}
