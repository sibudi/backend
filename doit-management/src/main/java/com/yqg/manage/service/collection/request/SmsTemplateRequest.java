package com.yqg.manage.service.collection.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by tonggen on 2018/3/26.
 */
@Data
@ApiModel
public class SmsTemplateRequest {

    @ApiModelProperty("Id")
    private Integer id;

    @ApiModelProperty("模板Id")
    private String smsTemplateId;

    @ApiModelProperty("短信标题")
    private String smsTitle;

    @ApiModelProperty("短信内容")
    private String smsContent;

    @ApiModelProperty("创建人")
    private Integer createUser;

    @ApiModelProperty("更新人")
    private Integer updateUser;

    @ApiModelProperty("发送时间最小值")
    private String minSendTime;

    @ApiModelProperty("发送时间最大值")
    private String maxSendTime;

    @ApiModelProperty("发送操作人")
    private String sendUser;

    @ApiModelProperty("是否到达")
    private Integer isArrived;

    @ApiModelProperty("开始页")
    private Integer pageNo = 1;

    @ApiModelProperty("每页大小")
    private Integer pageSize = 10;

    @ApiModelProperty("sysKey")
    private String sysKey;
}
