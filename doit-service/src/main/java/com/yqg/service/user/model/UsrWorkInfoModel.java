package com.yqg.service.user.model;

import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/11/25.
 */
@Data
public class UsrWorkInfoModel {
    private String companyName = "";
    private String positionName = "";
    private String monthlyIncome = "";
    private String companyPhone = "";
    private String province = "";
    private String city = "";
    private String bigDirect = "";
    private String smallDirect = "";
    private String detailed = "";
    private String dependentBusiness = "";//

    // 员工身份号码
    private String employeeNumber;
    // 分机号码
    private String extensionNumber;
}