package com.yqg.manage.service.collection.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/*****
 * @Author Jacob
 * created at ${date}
 * ????????
 ****/

@Getter
@Setter
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class OutCollectionResponse extends CollectionBaseResponse{


    @ApiModelProperty(value = "应还时间")
    private Date refundTime;
    @ApiModelProperty(value = "逾期天数")
    private Long overdueDays;
    @ApiModelProperty(value = "复借次数")
    private Integer borrowingCount;
    @ApiModelProperty(value = "最高逾期天数")
    private Long maxOverdueDays;
    @ApiModelProperty(value = "订单标签")
    private Integer orderTag;
    @ApiModelProperty(value = "跟进时间")
    private Date followTime;
    @ApiModelProperty(value = "姓名")
    private String realName;
    @ApiModelProperty(value = "借款人身份")
    private Integer userRole;
    @ApiModelProperty(value = "借款人性别")
    private Integer userSex;
    @ApiModelProperty(value = "手机号码")
    private String mobile;
    @ApiModelProperty(value = "银行卡")
    private String userBank;
    @ApiModelProperty(value = "用户ID（用于查询手机号码)")
    private String uuid;
    @ApiModelProperty(value = "承诺还款时间")
    private Date promiseRepaymentTime;


    @ApiModelProperty(value = "发薪日")
    private Integer payDay;
    @ApiModelProperty(value = "催收联系情况")
    private Integer[] collectionContactResult;
    @ApiModelProperty(value = "当前催收人员姓名")
    private String collectiorName;

    @ApiModelProperty(value = "当前催收人员ID")
    private Integer collectiorId;

    @ApiModelProperty(value = "最新质检时间")
    private Date updateTime;
    @ApiModelProperty(value = "质检结果")
    private String checkResult;

    @ApiModelProperty(value = "质检结果印尼文")
    private String checkResultInn;

    @ApiModelProperty(value = "语音质检结果")
    private String voiceCheckResult;

    @ApiModelProperty(value = "语音质检结果印尼文")
    private String voiceCheckResultInn;
}
