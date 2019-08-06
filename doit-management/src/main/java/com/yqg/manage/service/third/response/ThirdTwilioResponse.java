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
public class ThirdTwilioResponse {
    @ApiModelProperty(value = "批次号")
    private String batchNo;

    @ApiModelProperty(value = "发送人群")
    private String callPhase;

    @ApiModelProperty(value = "发送时间")
    private Date startTime;

    @ApiModelProperty(value = "使用渠道")
    private String channel;

    @ApiModelProperty(value = "发送条数")
    private Integer sendCount;

    @ApiModelProperty(value = "接通率")
    private String passRate;

    @ApiModelProperty(value = "手机号码")
    private String phoneNumber;

    @ApiModelProperty(value = "状态")
    private Integer passOrNot;


}
