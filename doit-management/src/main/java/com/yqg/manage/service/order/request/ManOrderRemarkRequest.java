package com.yqg.manage.service.order.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/*****
 * @Author Jacob
 *
 ****/

@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManOrderRemarkRequest {

    @ApiModelProperty(value = "备注表id（第二次调用电审备注添加需要传）")
    @JsonProperty
    private Integer id;
    @ApiModelProperty(value = "创建用户Id （需要每次都传）")
    @JsonProperty
    private Integer createUser;
    @ApiModelProperty(value = "更新用户Id （需要每次都传）")
    @JsonProperty
    private Integer updateUser;
    @ApiModelProperty(value = "订单编号（需要每次都传）")
    @JsonProperty
    private String orderNo;
    @ApiModelProperty(value = "备注（填写备注时传）")
    @JsonProperty
    private String remark;
    @ApiModelProperty(value = "订单标签（添加催收打标签时传）")
    @JsonProperty
    private Integer orderTag;
    @ApiModelProperty(value = "提醒时间（需要提醒时传）")
    @JsonProperty
    private String alertTime;
    @ApiModelProperty(value = "开始时间（电核结束时刻需要传）")
    @JsonProperty
    private String startTime;
    @ApiModelProperty(value = "结束时间（电核结束时刻需要传）")
    @JsonProperty
    private String endTime;
    @ApiModelProperty(value = "时长（电核结束时刻传）")
    @JsonProperty
    private String burningTime;
    @ApiModelProperty(value = "备注类型 1 个人电话审核， 2 催收日志 （每次都需要传）3 公司电话审核")
    @JsonProperty
    private Integer type;
    @ApiModelProperty(value = "用户id，用于提醒显示 （每次都需要传）")
    @JsonProperty
    private String userUuid;
    @ApiModelProperty(value = "语言 1中文 2印尼文")
    @JsonProperty
    private Integer langue;
    @ApiModelProperty(value = "电话审核结果")
    @JsonProperty
    private Integer teleReviewResult;
    @ApiModelProperty(value = "工作年限 ManWorkYearEnum")
    @JsonProperty
    private Integer workYear;
    @ApiModelProperty(value = "操作类型 (1 稍后再拨， 2 通过，3 无法审核，4 拒绝)")
    @JsonProperty
    private Integer operationType;
    @ApiModelProperty(value = "电核操作类型（1 公司未接通 2 公司接通 3 第一联系人未接通 4 第一联系人接通 5 第二联系人未接通 6 第二联系人接通）")
    private Integer teleReviewOperationType;

    @ApiModelProperty(value = "问题及答案")
    private List<CompanyTeleQARequest> companyTeleQARequest;

    @ApiModelProperty(value = "常用联系人姓名")
    private String realName;
    @ApiModelProperty(value = "常用联系人电话")
    private String mobile;
    @ApiModelProperty(value = "承诺还款时间")
    private String promiseRepaymentTime;

    @ApiModelProperty(value = "拒绝原因（点击手动拒绝是，填写的原因）")
    private String description;

    @ApiModelProperty(value = "发薪日")
    private Integer payDay;

}
