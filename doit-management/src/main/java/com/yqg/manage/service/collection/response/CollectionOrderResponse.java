package com.yqg.manage.service.collection.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Getter
@Setter
@ApiModel
public class CollectionOrderResponse extends CollectionBaseResponse{


    @ApiModelProperty(value = "订单用户姓名")
    private String realName;

    @ApiModelProperty(value = "是否复借1:是0：否")
    private Integer isRepeatBorrowing;

    @ApiModelProperty(value = "借款次数")
    private Integer borrowingCount;

    @ApiModelProperty(value = "复借次数")
    private Integer reBorrowingCount;

    @ApiModelProperty(value = "申请金额")
    private BigDecimal amountApply;

    @ApiModelProperty(value = "订单标签")
    private Integer orderTag;

    @ApiModelProperty(value = "催收人员姓名")
    private String collectorName;

    @ApiModelProperty(value = "质检列表中催收人员姓名")
    private String qualityCollectorName;

    @JsonIgnore
    private Integer outsourceId;

    @JsonIgnore
    private Integer subOutSourceId;

    @ApiModelProperty(value = "应还时间")
    private Date refundTime;

    @ApiModelProperty(value = "逾期天数")
    private Long overdueDays;

    @ApiModelProperty(value = "用户id")
    private String userUuid;

    @ApiModelProperty(value = "承诺还款时间")
    private Date promiseRepaymentTime;


    private Integer extendType; //是否展期

    private Integer calType; //是否结清

    @ApiModelProperty(value = "质检结果")
    private String checkResult;

    @ApiModelProperty(value = "质检结果印尼文")
    private String checkResultInn;

    @ApiModelProperty(value = "语音质检结果")
    private String voiceCheckResult;

    @ApiModelProperty(value = "语音质检结果印尼文")
    private String voiceCheckResultInn;

    @ApiModelProperty(value = "最新质检时间")
    private Date updateTime;

    @ApiModelProperty(value = "是否已分配1:是0:否")
    private Integer isAssigned;

    @ApiModelProperty(value = "催收还是质检标识")
    private Integer sourceType;
}
