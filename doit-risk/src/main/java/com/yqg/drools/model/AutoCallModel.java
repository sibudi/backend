package com.yqg.drools.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AutoCallModel {
    //本人相关
    private Boolean hasValidCall;
    private Boolean needReject;
    private Boolean exceedLimit;

    private Boolean needOwnerAutoCall;//是否需要本人外呼

    //联系人相关
    private Long emergencyPassCount;//紧急联系人外呼通过数量
    private Long backupPassCount;//备选联系人外呼通过数量
    private Integer borrowingCount;//借款次数
    private Boolean needLinkmanCall;//需要进行联系人外呼

    private Boolean isCashCahOrder; //是cash2的订单

    private Boolean is100RmbProduct;//100rmb产品

   // private Boolean companyTelResultInvalid;//公司电话外呼结果无效

    private Integer companyTelAutoCallResult;//公司电话外呼结果

  //  private Boolean companyTelResultValid;//公司电话外呼有效

    private Integer firstOrderCompanyTelCallResult; //复借对应的首借公司电话外呼结果


    private BigDecimal totalMemory;

    private String deviceType;

    private String bankCode;

    private int sex;

    private BigDecimal monthlyIncome;

    private RUserInfo.IziPhoneAgeResult iziPhoneAgeResult; //izi在网时长结果

    private RUserInfo.IziPhoneVerifyResult iziPhoneVerifyResult; //izi手机实名结果

    private String mobileLanguage;

    private Integer childrenCount;

    private String academic;//学历

    private Long appCountForEcommerce;

    private Long validLinkmanCallCountWithInforbip;

    private Long appCountForCreditCard;//信用卡类app
}
