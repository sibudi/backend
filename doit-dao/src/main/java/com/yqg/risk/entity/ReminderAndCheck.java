package com.yqg.risk.entity;

import lombok.Data;

/**
 * Created by Didit Dwianto on 2018/2/8.
 */
@Data
public class ReminderAndCheck {

    private String createDay;
    private String realName;
    private String juniorTask;
    private String juniorFinish;
    private String juniorPass;
    private String juniorFinishRate;
    private String juniorPassRate;
    private String seniorTask;
    private String seniorFinish;
    private String seniorPass;
    private String seniorFinishRate;
    private String seniorPassRate;

//    1.2 1.3
    private String date;
    private String type;
    private String noFinishNum;
    private String noTaskNum;

//    2.1
    private String ruleType;
    private String description;
    private String hitOrders;

//    2.2
    private String PhoneCheckDate;
    private String CompanyCheckNumber;
    private String CompanyPhoneSuccessRate;
    private String FIRSTPhoneCheckNumber;
    private String FIRSTPhoneSuccessRate;
    private String SECONDPhoneCheckNumber;
    private String SECONDPhoneSuccessRate;
    private String CumulativeNumberOfPhoneCheck;

//    2.3 接通电核的通过率表
    private String PhoneCheckResult;
    private String CompanyPhoneCheckNumber;
    private String p1;
//    private String FIRSTPhoneCheckNumber;
    private String f1;
//    private String SecondPhoneCheckNumber;
    private String s1;

    // 2.4 接通电核的拒绝原因表
    private String rejectDate;
    private String rejectReason;
    private String rejectOrders;
    private String proportion;

//    2.5  审核员电核接通率表
    private String Reviewers;
    private String FIRSTPhoneSuccessNumber;
    private String SECONDPhoneSuccessNumber;

    // 1.8 公司电话的平均拨打次数表

    private String callDate;
    private String reviewers;
    private String companyOrdersNumber;
    private String numberOfCallsMadeByCompanyPhone;
    private String firstOrdersNumber;
    private String firstCallNumber;
    private String secondOrdersNumber;
    private String secondCallNumber;
}
