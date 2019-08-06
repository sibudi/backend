package com.yqg.service.user.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.models.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/8/17.
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UsrHousewifeRequest extends BaseRequest {

    @ApiModelProperty(value = "订单号")
    @JsonProperty
    private String orderNo;
    @ApiModelProperty(value = "家庭月收入")
    @JsonProperty
    private String homeMouthIncome;
    @ApiModelProperty(value = "主要家庭收入方式")
    @JsonProperty
    private String incomeType;
    @ApiModelProperty(value = "主要家庭收入来源者")
    @JsonProperty
    private String incomtSource;

    // 主要家庭收入来源者相关信息
    @ApiModelProperty(value = "姓名")
    @JsonProperty
    private String sourceName;
    @ApiModelProperty(value = "手机号")
    @JsonProperty
    private String sourceTel;
    @ApiModelProperty(value = "月收入")
    @JsonProperty
    private String mouthIncome;
    @ApiModelProperty(value = "工作类型 （公司员工 非公司员工)）")
    @JsonProperty
    private String workType;

    // 是否是公司员工  0 是 1 不是
    private Integer isCompanyUser;
    // 如果是公司员工
    @ApiModelProperty(value = "公司名称")
    @JsonProperty
    private String companyName;
    @ApiModelProperty(value = "????")
    @JsonProperty
    private String companyPhone;
    @ApiModelProperty(value = "?")
    @JsonProperty
    private String province;
    @ApiModelProperty(value = "?")
    @JsonProperty
    private String city;
    @ApiModelProperty(value = "??")
    @JsonProperty
    private String bigDirect;
    @ApiModelProperty(value = "??")
    @JsonProperty
    private String smallDirect;
    @ApiModelProperty(value = "????")
    @JsonProperty
    private String detailed;
    @ApiModelProperty(value = "??")
    @JsonProperty
    private String lbsY;
    @ApiModelProperty(value = "??")
    @JsonProperty
    private String lbsX;

    // 如果不是公司员工
    @ApiModelProperty(value = "收入的主要方式")
    @JsonProperty
    private String incomeWithNoCom;
}
