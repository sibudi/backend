package com.yqg.manage.service.mongo.response;

import lombok.Data;

/**
 * @Author Jacob
 */
@Data
public class HouseWifiInfoResponse {

    private String companyName;
    private String companyPhone;
    private String salaryName;
    private String incomeType;

    public HouseWifiInfoResponse(){}

    public HouseWifiInfoResponse(String companyName, String companyPhone, String salaryName, String incomeType) {
        this.companyName = companyName;
        this.companyPhone = companyPhone;
        this.salaryName = salaryName;
        this.incomeType = incomeType;
    }
}
