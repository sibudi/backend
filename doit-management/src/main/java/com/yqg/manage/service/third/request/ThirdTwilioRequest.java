package com.yqg.manage.service.third.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Author: tonggen
 * Date: 2018/10/19
 * time: 上午10:59
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThirdTwilioRequest {

    @ApiModelProperty(value = "批次号")
    private String batchNo;

    @ApiModelProperty(value = "发送人群")
    private String callPhase;

    @ApiModelProperty(value = "使用通道")
    private String channel;

    @ApiModelProperty(value = "发送开始时间")
    private String sendStartTime;

    @ApiModelProperty(value = "发送结束时间")
    private String sendEndTime;

    @ApiModelProperty(value = "开始页")
    private Integer pageNo=1;

    @ApiModelProperty(value = "每页大小")
    private Integer pageSize=10;

}
