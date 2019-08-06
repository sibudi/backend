package com.yqg.service.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/11/25.
 */
@Data
public class UsrStudentBaseInfoModel {
    private String email = "";
    private String academic = "";
    private String birthday = "";
    private int familyMemberAmount;
    private String familyAnnualIncome = "";
    private String fatherName = "";
    private String fatherMobile = "";
    private String fatherPosition = "";
    private String motherName = "";
    private String motherMobile = "";
    private String motherPosition = "";
    private int dwellingCondition;
    private String province = "";
    private String city = "";
    private String bigDirect = "";
    private String smallDirect = "";
    private String detailed = "";
    private String borrowUse = "";
    private String birthProvince= "";//
    private String birthCity= "";//
    private String birthBigDirect= "";//
    private String birthSmallDirect= "";//

    // whatsapp账号
    private String whatsappAccount;

    // 家庭主妇需求 新增
    // 月生活费
    private String mouthCost;
    // 月生活费来源
    private String mouthCostSource;
    // 是否在做兼职  0不是 1 是
    private String isPartTime;
    // 是否是兼职
    private String partTimeDetail;

    // 兼职名称
    private String partTimeName;

    // 如果有兼职
    // 兼职类型
    private String partTimeType;
    // 兼职收入
    private String partTimeIncome;
    // 兼职证明人姓名
    private String partTimeProveName;
    // 兼职证明人手机号
    private String partTimeProveTele;

    // 同班同学姓名
    private String classmateName;
    // 同班同学手机号
    private String classmateTele;

    //家庭卡url
    private String kkCardPhoto;
}