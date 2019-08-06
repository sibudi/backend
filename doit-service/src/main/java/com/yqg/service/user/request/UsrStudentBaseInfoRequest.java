package com.yqg.service.user.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.models.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UsrStudentBaseInfoRequest extends BaseRequest {

    @ApiModelProperty(value = "订单号")
    @JsonProperty
    private String orderNo;
    @ApiModelProperty(value = "邮箱")
    @JsonProperty
    private String email;
    @ApiModelProperty(value = "学历")
    @JsonProperty
    private String academic;
    @ApiModelProperty(value = "生日")
    @JsonProperty
    private String birthday;
    @ApiModelProperty(value = "家庭成员数量")
    @JsonProperty
    private Integer familyMemberAmount;
    @ApiModelProperty(value = "家庭年收入")
    @JsonProperty
    private String familyAnnualIncome;
    @ApiModelProperty(value = "父亲姓名")
    @JsonProperty
    private String fatherName;
    @ApiModelProperty(value = "父亲手机号")
    @JsonProperty
    private String fatherMobile;
    @ApiModelProperty(value = "父亲职位")
    @JsonProperty
    private String fatherPosition;
    @ApiModelProperty(value = "母亲的姓名")
    @JsonProperty
    private String motherName;
    @ApiModelProperty(value = "母亲手机号")
    @JsonProperty
    private String motherMobile;
    @ApiModelProperty(value = "母亲职位")
    @JsonProperty
    private String motherPosition;
    @ApiModelProperty(value = "居住状况")
    @JsonProperty
    private Integer dwellingCondition;
    @ApiModelProperty(value = "省")
    @JsonProperty
    private String province;
    @ApiModelProperty(value = "市")
    @JsonProperty
    private String city;
    @ApiModelProperty(value = "大区")
    @JsonProperty
    private String bigDirect;
    @ApiModelProperty(value = "小区")
    @JsonProperty
    private String smallDirect;
    @ApiModelProperty(value = "详细地址")
    @JsonProperty
    private String detailed;
    @ApiModelProperty(value = "纬度")
    @JsonProperty
    private String lbsY;
    @ApiModelProperty(value = "经度")
    @JsonProperty
    private String lbsX;
    @ApiModelProperty(value = "地址类型")
    @JsonProperty
    private Integer addressType;

    @ApiModelProperty(value = "借款用途")
    @JsonProperty
    private String borrowUse;

    @ApiModelProperty(value = "出生地:省")
    @JsonProperty
    private String birthProvince;
    @ApiModelProperty(value = "出生地:市")
    @JsonProperty
    private String birthCity;
    @ApiModelProperty(value = "出生地:大区")
    @JsonProperty
    private String birthBigDirect;
    @ApiModelProperty(value = "出生地:小区")
    @JsonProperty
    private String birthSmallDirect;


    @ApiModelProperty(value = "whatsapp账号")
    @JsonProperty
    private String whatsappAccount;


    // 家庭主妇需求 新增
    @ApiModelProperty(value = "月生活费")
    @JsonProperty
    private String mouthCost;
    @ApiModelProperty(value = "月生活费来源")
    @JsonProperty
    private String mouthCostSource;
    @ApiModelProperty(value = "是否在做兼职")  // 1是 2 不是
    @JsonProperty
    private String isPartTime;

    @ApiModelProperty(value = "兼职名称")
    @JsonProperty
    private String partTimeName;

    // 如果有兼职
    @ApiModelProperty(value = "兼职类型")
    @JsonProperty
    private String partTimeType;
    @ApiModelProperty(value = "兼职收入")
    @JsonProperty
    private String partTimeIncome;
    @ApiModelProperty(value = "兼职证明人姓名")
    @JsonProperty
    private String partTimeProveName;
    @ApiModelProperty(value = "兼职证明人手机号")
    @JsonProperty
    private String partTimeProveTele;

    @ApiModelProperty(value = "同班同学姓名")
    @JsonProperty
    private String classmateName;
    @ApiModelProperty(value = "同班同学手机号")
    @JsonProperty
    private String classmateTele;

    @ApiModelProperty(value = "家庭卡url")
    @JsonProperty
    private String kkCardPhoto;
}