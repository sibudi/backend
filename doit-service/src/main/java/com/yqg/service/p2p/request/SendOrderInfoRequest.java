package com.yqg.service.p2p.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.jaxrs.json.annotation.JSONP;
import com.yqg.common.models.BaseRequest;
import io.swagger.models.auth.In;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Didit Dwianto on 2018/3/1.
 */
@Data
public class SendOrderInfoRequest extends P2PInvokeBaseParam {

    private String borrowingPurposes;  // 借款用途
    private Integer riskLevel;   //风险等级
    private String creditorNo;   // 债权编号
    private String lenderId;    // 借款人用户uuid
    private BigDecimal amountApply;  // 申请金额
    private String term;  // 期限
    private BigDecimal borrowerYearRate;  // 借款年化利率
    private BigDecimal serviceFee;  // 前置服务费
    private String biddingTime;  // 发标时间
    private Integer channel;  // 来源
    private String bankCode;  // 银行code
    private String bankName;  // 银行名称
    private String bankNumber;  // 银行卡号
    private String bankCardholder;  // 银行卡持卡人

    private String name; //姓名
    private String idCardNo;//身份证号
    private String sex;//性别：1男2女
    private String age;
    private String isMarried;
    private String identity; //角色
    private String mobile;
    private String email;
    @JsonProperty(value = "acdemic")
    private String academic;
    @JsonProperty(value = "birty")
    private String birthday;
    private String religion; //宗教
    private String address;
    private String inhabit;
    private String isIdentidyAuth;
    private String isBankCardAuth;
    private String isLindManAuth;
    private String isInsuranceCardAuth;
    private String isFamilyCardAuth;
    private String creditScore;
    private String sign;

    private Integer creditorType;  // 1-普通 2-分期 3-展期
    private String detail;  //  分期债权传分期数，展期债权传展期前债权编号
}
