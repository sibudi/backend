package com.yqg.manage.service.order.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author Jacob
 * ????
 */
@Data
@ApiModel
public class ManOrderRemarkResponse {


    @ApiModelProperty("添加时间")
    private Date createTime;

    @ApiModelProperty("操作人姓名")
    private String operator;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("标签")
    private Integer orderTag;

    @ApiModelProperty("提醒时间")
    private Date alertTime;

    @ApiModelProperty(value = "承诺还款时间")
    private Date promiseRepaymentTime;

    @ApiModelProperty(value = "联系人电话")
    @JsonProperty
    private String contactMobile;
    @ApiModelProperty(value = "联系人方式")
    @JsonProperty
    private Integer contactMode;
    @ApiModelProperty(value = "联系人结果")
    @JsonProperty
    private Integer contactResult;



}
