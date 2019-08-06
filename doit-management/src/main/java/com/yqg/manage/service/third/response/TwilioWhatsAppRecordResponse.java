package com.yqg.manage.service.third.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Author: tonggen
 * Date: 2018/10/19
 * time: 上午11:15
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwilioWhatsAppRecordResponse {

    @ApiModelProperty(value = "记录表ID")
    private Integer id;

    @ApiModelProperty(value = "记录表UUID")
    private String uuid;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "申请金额")
    private String amountApply;

    @ApiModelProperty(value = "申请期限")
    private Integer borrowingTerm;

    @ApiModelProperty(value = "逾期天数")
    private Integer overDueDay;

    @ApiModelProperty(value = "回复内容")
    private String replyContent;

    @ApiModelProperty(value = "操作员")
    private String operator;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "解决情况")
    private Integer solveType;

    @ApiModelProperty(value = "承诺还款时间")
    private Date promiseTime;

    @ApiModelProperty(value = "用户手机号")
    private String phoneNum;

    @ApiModelProperty(value = "userUuid")
    private String userUuid;


}
