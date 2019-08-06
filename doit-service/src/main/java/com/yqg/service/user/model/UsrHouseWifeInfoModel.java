package com.yqg.service.user.model;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/8/17.
 */
@Data
public class UsrHouseWifeInfoModel {

    private String homeMouthIncome;
    private String incomeType;
    private String incomtSource;

    // 主要家庭收入来源者相关信息
    private String sourceName;
    private String sourceTel;
    private String mouthIncome;
    private String workType;


    private String province;
    private String city;
    private String bigDirect;
    private String smallDirect;
    private String detailed;

    // 是否是公司员工  0 是 1 不是
    private Integer isCompanyUser;
    // 如果是公司员工
    private String companyName;
    private String companyPhone;

    // 如果不是公司员工
    private String incomeWithNoCom;
}
