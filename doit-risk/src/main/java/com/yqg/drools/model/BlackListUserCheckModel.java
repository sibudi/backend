package com.yqg.drools.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlackListUserCheckModel {

    private Boolean idCardNoInOverdue15BlackList;//申请人身份证命中逾期15天以上用户身份证号黑名单
    private Boolean mobileInOverdue15BlackList;//申请手机号命中逾期15天以上用户手机号黑名单
    private Boolean emergencyTelInOverdue15BlackListEmergencyTel;//申请人的紧急联系人手机号码是逾期15天以上用户的紧急联系人
    private Boolean bankcardNoInOverdue15BlackList;//申请人银行卡命中逾期15天以上用户银行卡号黑名单
    private Boolean imeiInOverdue15BlackList;//申请手机IMEI命中逾期15天以上用户IMEI黑名单

    //private Integer emergencyTelInOverdue15BlackListCount;//申请人的紧急联系人手机号码命中逾期15天以上用户的次数
    private Boolean mobileIsOverdue15BlackListEmergencyTel;//申请人手机号码是逾期15天以上用户的紧急联系人
    private Boolean emergencyTelInOverdue15BlackList;//紧急联系人手机号码是逾期15天以上用户

    private Boolean mobileInOverdue15BlackListContacts;//申请手机号命中逾期15天以上用户通讯录
    private Boolean mobileInOverdue15BlackListCallRecords;//申请手机号命中逾期15天用户的通话记录
    private Boolean mobileInOverdue15BlackListShortMsg;//申请手机号命中逾期15天用户的短信对象

    private Boolean contactInOverdue15BlackList;//通讯录命中逾期15天黑名单
    private Boolean callRecordInOverdue15BlackList; //通话记录对象命中逾期15天及以上黑名单


    private Integer contactInOverdue15Count; //通讯录号码命中逾期15天用户次数
    private Integer callRecordInOverdue15Count;//通话记录号码命中逾期15天用户次数

    private Boolean mobileIsFraudUserEmergencyTel;//申请人手机号命中欺诈用户的紧急联系人
    private Long mobileInFraudUserCallRecordsCount;//申请人手机号命中欺诈用户的通话记录次数


    private Boolean imeiInFraudUser;//申请人imei命中欺诈用户黑名单

    private Boolean mobileInFraudUser;//申请人手机号命中欺诈用户黑名单
    private Boolean idCardNoInFraudUser;//申请人身份证号命中欺诈用户黑名单
//    private Boolean bankcardNoInFraudUser;//申请人银行号命中欺诈用户黑名单
//    private Boolean deviceIdInFraudUser;//申请设备号命中欺诈用户黑名单


    private Boolean hitFraudUserInfo; //命中欺诈用户信息

    private boolean deviceIdInOverdue30DaysUser; //设备号命中逾期30天以上用户的设备号

    private Integer smsContactOverdue15DaysCount;//短信通讯对象逾期15天的次数



    //逾期7天
    private Boolean callRecordInOverdue7BlackList;//通话记录对象命中逾期7天及以上黑名单
    //private boolean mobileInOverdue7BlackListCallRecord;//申请手机号命中逾期7天用户的通话记录==>查所有通话记录，未处理
    private Boolean imeiInOverdue7BlackList;//申请手机IMEI命中逾期7天以上用户IMEI黑名单
    private Boolean idCardNoInOverdue7BlackList;//申请人身份证命中逾期7天以上用户身份证号黑名单
    private Boolean mobileInOverdue7BlackList;//申请手机号命中逾期7天以上用户手机号黑名单
    private Boolean bankcardInOverdue7BlackList;//申请人银行卡命中逾期7天以上用户银行卡号黑名单
    private Boolean emergencyTelInOverdue7BlackListEmergencyTel;//申请人的紧急联系人手机号码是逾期7天以上用户的紧急联系人
    private Boolean mobileInOverdue7BlackListEmergencyTel;//申请人手机号码是逾期7天以上用户的紧急联系人
    private Boolean emergencyTelInOverdue7BlackList;//申请人的紧急联系人手机号码是逾期7天以上用户

    //敏感用户规则
    private Boolean hitSensitiveUserInfo;//命中敏感人员身份证号或手机号

    private Boolean hitCollectorBlackUserInfo;//命中催收黑名单用户的手机号

    private Boolean hitComplaintUserInfo;//向ojk投诉用户身份证or手机号

    //20100114
    private Boolean whatsappInOverdue7BlackList;  //申请人的va号码命中逾期7天用户的号码
    private Boolean whatsappInOverdue7BlackListEmergencyTel; //申请人的va号码命中逾期7天用户紧急联系人
    private Boolean whatsappInOverdue7BlackListCallRecord;//申请人的va号码命中逾期7天用户通话记录
    private Boolean whatsappInOverdue7BlackListContact; //申请人的va号码命中逾期7天用户通讯录
    private Boolean whatsappInOverdue7BlackListSms; //申请人的va号码命中逾期7天用户的短信
    private Boolean emergencyTelInFraudUserEmergencyTel;//申请人的紧急联系人号码命中欺诈用户的紧急联系人
    private Boolean emergencyTelInFraudUserCallRecord;//申请人的紧急联系人号码命中欺诈用户的通话记录
    private Boolean emergencyTelInFraudUserContact; //申请人的紧急联系人号码命中欺诈用户的通讯录
    private Boolean emergencyTelInFraudUserSms;//申请人的紧急联系人号码命中欺诈用户的短信
    private Boolean emergencyTelInFraudUserWhatsapp;//申请人的紧急联系人号码命中欺诈用户的va
    private Boolean companyTelInOverdue7BlackList;//申请人公司电话命中逾期7天用户的公司电话
    private Boolean companyAddressInOverdue7BlackList;//申请人公司地址命中逾期7天用户的公司地址
    private Boolean homeAddressInOverdue7BlackList;//申请人居住地址命中逾期7天用户的居住地址
    private Boolean companyTelInFraudBlackList;//申请人公司电话命中欺诈用户的公司电话
    private Boolean companyAddressInFraudBlackList;//申请人公司地址命中欺诈用户的公司地址
    private Boolean homeAddressInFraudBlackList;//申请人居住地址命中欺诈用户的居住地址


//    // 逾期7天用户相关
//    private Boolean mobileIsEmergencyTelForOverdue7UserWith1th; //申请人号码是首借逾期7天用户的紧急联系人
//    private Boolean emergencyTelIsEmergencyTelForOverdue7UserWith1th; //申请人联系人是首借逾期7天用户的紧急联系人
//    private Boolean emergencyTelIsOverdue7UserWith1th; //申请人联系人是首借逾期7天用户
//    private Boolean mobileIsEmergencyTelForOverdue7UserWith2th; //申请人号码是第二次借款逾期7天用户的紧急联系人
//    private Boolean emergencyTelIsEmergencyTelForOverdue7UserWith2th; //申请人联系人是第二次借款逾期7天用户的紧急联系人
//    private Boolean emergencyTelIsOverdue7UserWith2th; //申请人联系人是第二次借款逾期7天用户
//
//    private Boolean emergencyTelIsEmergencyTelForOverdue7UserWith3Or4th; //申请人联系人是第三、四次借款逾期7天用户的紧急联系人


    private Boolean mobileIsEmergencyTelForUnSettledOverdue7UserWith1th; //申请人手机号是（首借逾期7天及以上并且当前逾期未还）用户的联系人号码
    private Boolean emergencyTelIsUnSettledOverdue7UserWith1th;//申请人联系人号码是（首借逾期7天及以上并且当前逾期未还用户）的本人号码
    private Boolean emergencyTelIsEmergencyTelForUnSettledOverdue7UserWith1th;//申请人联系人号码是（首借逾期7天及以上并且当前逾期未还用户）的联系人号码
    private Boolean emergencyTelIsUnSettledOverdue7UserWith2th;//申请人联系人号码是(第2笔逾期7天及以上并且当前逾期未还用户）的本人号码
    private Boolean emergencyTelIsEmergencyTelForUnSettledOverdue7UserWith2th;//申请人联系人号码是（第2笔逾期7天及以上并且当前逾期未还用户）的联系人号码
    private Boolean mobileIsEmergencyTelForUnSettledOverdue7UserWith2th;//申请人手机号是（第2笔逾期7天及以上用户并且当前逾期未还用户）的联系人号码
    private Boolean emergencyTelIsEmergencyTelForUnSettledOverdue7UserWith3Or4th;//申请人联系人号码是（第3/4笔逾期7天及以上并且当前逾期未还用户）的联系人号码

    private Boolean mobileIsEmergencyTelForSettledOverdue7UserWith1th; //申请人手机号是（首借逾期7天及以上并且当前逾期已还）用户的联系人号码
    private Boolean emergencyTelIsSettledOverdue7UserWith1th;//申请人联系人号码是（首借逾期7天及以上并且当前逾期已还用户）的本人号码
    private Boolean emergencyTelIsEmergencyTelForSettledOverdue7UserWith1th;//申请人联系人号码是（首借逾期7天及以上并且当前逾期已还用户）的联系人号码
    private Boolean emergencyTelIsSettledOverdue7UserWith2th;//申请人联系人号码是(第2笔逾期7天及以上并且当前逾期已还用户）的本人号码
    private Boolean emergencyTelIsEmergencyTelForSettledOverdue7UserWith2th;//申请人联系人号码是（第2笔逾期7天及以上并且当前逾期已还用户）的联系人号码
    private Boolean mobileIsEmergencyTelForSettledOverdue7UserWith2th;//申请人手机号是（第2笔逾期7天及以上用户并且当前逾期已还用户）的联系人号码
    private Boolean emergencyTelIsEmergencyTelForSettledOverdue7UserWith3Or4th;//申请人联系人号码是（第3/4笔逾期7天及以上并且当前逾期已还用户）的联系人号码



}
