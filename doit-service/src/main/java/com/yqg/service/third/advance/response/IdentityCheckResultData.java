package com.yqg.service.third.advance.response;

import lombok.Data;

/**
 * Created by Jacob on 2017/11/26.
 */
@Data
public class IdentityCheckResultData {
    private String name;
    private String idNumber;
    private String province;
    private String city;
    private String district;
    private String village;
}
