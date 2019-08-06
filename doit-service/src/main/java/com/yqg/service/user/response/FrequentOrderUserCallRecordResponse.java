package com.yqg.service.user.response;

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
