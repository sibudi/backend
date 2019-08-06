package com.yqg.service.third.infinity.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class InfinityRequest {
    @ApiModelProperty("请求token")
    @JsonProperty
    private String token;
    @ApiModelProperty("授权appid")
    @JsonProperty
    private String appid;
    @ApiModelProperty(value = "")
    @JsonProperty
    private String accesskey;
    @ApiModelProperty("请求服务")
    @JsonProperty
    private String service;
    @ApiModelProperty("请求返回状态")
    @JsonProperty
    private String status;
    @ApiModelProperty("同步标志1、2、3")
    @JsonProperty
    private String syncflag;
    @ApiModelProperty("分机号")
    @JsonProperty
    private String extnumber;
    @ApiModelProperty("拨打目标号码")
    @JsonProperty
    private String destnumber;
    @ApiModelProperty("电话开始时间")
    @JsonProperty
    private Date starttime;
    @ApiModelProperty("电话结束时间")
    @JsonProperty
    private Date endtime;
    @ApiModelProperty("录音文件名称")
    @JsonProperty
    private String filename;

    @ApiModelProperty("登录用户UUID")
    @JsonProperty
    private String uuid;
    @ApiModelProperty("订单号")
    @JsonProperty
    private String orderNo;
    @ApiModelProperty("催收人员")
    @JsonProperty
    private String realName;
    @ApiModelProperty("姓名")
    @JsonProperty
    private String userName;
    @ApiModelProperty("申请金额")
    @JsonProperty
    private BigDecimal applyAmount;
    @ApiModelProperty("申请期限")
    @JsonProperty
    private int applyDeadline;
    @ApiModelProperty("外呼节点 1 催收  2 电核")
    @JsonProperty
    private Integer callNode;
    @ApiModelProperty("外呼手机号类型（// 1本人电话 2公司电话 3 紧急联系人 4 备选联系人)")
    @JsonProperty
    private Integer callType;
}
