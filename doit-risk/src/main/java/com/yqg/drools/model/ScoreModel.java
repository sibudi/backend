package com.yqg.drools.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ScoreModel {

    //600评分
    private Boolean gojekVerified;  //gojek验证
    private Long ecommerceTicketBankCredit; //ecommerce+ticket+bank+credit
    private String mobileLanguage; //手机语言
    private int gender; //性别 1:男 2:女
    private String phoneBrand; //手机品牌
    private Long appCount; //app数量
    private Boolean hasDrivingLicense; //是否有驾驶证
    private String positionName; //职位
    private String academic; //学历
    private String borrowingPurpose; //借款用途
    private String orderProvince; //省份
    private Boolean bigDirectSameForOrderAndHome; //订单大区小区和家庭住址相同
    private String bankName;  //bankcode缩写
    private Long periodOfCreateAndCommitTime; //单位分钟

    //100评分
    private Long creditCardAppCount;//  信用卡app个数
    private Long ecommerceAppCount;// 电商app个数
    private String homeCity; //居住城市
    private String iziOnlineAge;
    private Boolean whatsappAndOwnerTelSame; //whatsapp和电话是否相同
    private Integer age;  //年龄
    private String homeProvinceMarriageAcademic; //家庭省份婚姻学历组合
    private String childrenCountHasSalaryPicEnterpriseAppCount;

    //600评分V2
    private String iziAgeWhatsappSameAsMobile;//izi网龄&whatsapp账号和用户手机号是否一致(组合变量：1)izi网龄：0表示缺失,3,4,5,6为 从usrIziVerifyResult表查出的在网时长；2)whatsapp账号和用户手机号是否一致:1一致,0不一致,2缺)
    private String iziVerifyResponseWhatsappSameAsMobile;//组合变量：1)izi验证结果：从usrIziVerifyResult表查出的izi手机号码实名认证结果；2)whatsapp账号和用户手机号是否一致:1一致,0不一致,2缺失
    private int emergeInIQorGood600;//联系人有首借600未逾期或已提额用户
    private String dependentBusiness;//行业
    private String liveProvice;
    private String timeForFillingIdentifyInfo;//ordStepHistory表中step1到step2的时间，单位：分钟；如果该订单没有step1，请按照-1打分
    private String monthlyIncome;//月收入


    //50评分
    private Integer lnOfdiffTimeForRegisterAndOrderCreate; //时间差取自然对数
    private String bigDirect;//大区
    private String linkmanRelation1;

    public enum VariableEnum {
        V1_01,
        V1_02,
        V2_01,
        V2_02,
        V2_03,
        V2_04,
        V2_05,
        V2_DEFAULT,
        V3_01,
        V3_02_DEFAULT,
        V4_01,
        V4_02,
        V5_01,
        V5_02,
        V5_03,
        V5_04,
        V5_DEFAULT,
        V6_01,
        V6_02,
        V6_03,
        V6_04,
        V6_05,
        V6_DEFAULT,
        V7_01,
        V7_02,
        V8_01,
        V8_DEFAULT,
        V8_03,
        V8_04,
        V9_DEFAULT,
        V9_02,
        V10_DEFAULT,
        V10_02,
        V10_03,
        V11_DEFAULT,
        V12_01,
        V12_02,
        V11_02,
        V11_03,
        V13_01,
        V13_DEFAULT,
        V13_03,
        V14_01,
        V14_02,
        V14_03,
        V14_04,


        //product100
        PRD100_V1_01,
        PRD100_V1_02,
        PRD100_V1_DEFAULT,
        PRD100_V2_01,
        PRD100_V2_02,
        PRD100_V2_03,
        PRD100_V2_DEFAULT,
        PRD100_V3_01,
        PRD100_V3_02,
        PRD100_V3_03,
        PRD100_V3_DEFAULT,
        PRD100_V3_05,
        PRD100_V4_01,
        PRD100_V4_02,
        PRD100_V5_DEFAULT,
        PRD100_V5_02,
        PRD100_V5_03,
        PRD100_V6_01,
        PRD100_V6_DEFAULT,
        PRD100_V6_03,
        PRD100_V7_01,
        PRD100_V7_02,
        PRD100_V8_01,
        PRD100_V8_02,
        PRD100_V8_03,
        PRD100_V8_04,
        PRD100_V9_01,
        PRD100_V9_02,
        PRD100_V10_01,
        PRD100_V10_02,
        PRD100_V11_01,
        PRD100_V11_02,
        PRD100_V11_03,
        PRD100_V12_01,
        PRD100_V12_02,
        PRD100_V12_03,
        PRD100_V12_04,

        //600评分V2
        PRODUCT600_V2_V1_01,
        PRODUCT600_V2_V1_02,
        PRODUCT600_V2_V2_01,
        PRODUCT600_V2_V2_02,
        PRODUCT600_V2_V2_03,
        PRODUCT600_V2_V2_04 ,
        PRODUCT600_V2_V3_01,
        PRODUCT600_V2_V3_02,
        PRODUCT600_V2_V3_03,
        PRODUCT600_V2_V4_01,
        PRODUCT600_V2_V4_DEFAULT,
        PRODUCT600_V2_V5_01,
        PRODUCT600_V2_V5_02,
        PRODUCT600_V2_V6_01,
        PRODUCT600_V2_V6_02,
        PRODUCT600_V2_V7_01,
        PRODUCT600_V2_V7_02,
        PRODUCT600_V2_V7_03,
        PRODUCT600_V2_V7_DEFAULT,
        PRODUCT600_V2_V8_01,
        PRODUCT600_V2_V8_02,
        PRODUCT600_V2_V8_DEFAULT,
        PRODUCT600_V2_V9_01,
        PRODUCT600_V2_V9_02,
        PRODUCT600_V2_V9_03,
        PRODUCT600_V2_V9_DEFAULT,
        PRODUCT600_V2_V10_01,
        PRODUCT600_V2_V10_02,
        PRODUCT600_V2_V10_03,
        PRODUCT600_V2_V10_DEFAULT,
        PRODUCT600_V2_V11_01,
        PRODUCT600_V2_V11_02,
        PRODUCT600_V2_V11_03,
        PRODUCT600_V2_V11_04,
        PRODUCT600_V2_V12_01,
        PRODUCT600_V2_V12_02,
        PRODUCT600_V2_V12_03,
        PRODUCT600_V2_V12_04,

        PRD50_V1_01,
        PRD50_V1_02,
        PRD50_V1_DEFAULT,
        PRD50_V1_04,
        PRD50_V1_05,
        PRD50_V2_01,
        PRD50_V2_02,
        PRD50_V2_DEFAULT,
        PRD50_V3_01,
        PRD50_V3_02,
        PRD50_V4_DEFAULT,
        PRD50_V4_02,
        PRD50_V4_03,
        PRD50_V5_DEFAULT,
        PRD50_V5_02,
        PRD50_V5_03,
        PRD50_V6_01,
        PRD50_V6_DEFAULT,
        PRD50_V6_03,
        PRD50_V7_01,
        PRD50_V7_02,
        PRD50_V7_03,
        PRD50_V8_01,
        PRD50_V8_02,
        PRD50_V8_03,
        PRD50_V9_01,
        PRD50_V9_02,
        PRD50_V10_DEFAULT,
        PRD50_V10_02,
        PRD50_V11_01,
        PRD50_V11_02,
        PRD50_V12_01,
        PRD50_V12_02,


        ;

    }

}
