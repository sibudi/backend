package com.yqg.manage.service.order.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author alan
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManAlertMessageRequest {
    @ApiModelProperty(value = "订单编号")
    @JsonProperty
    private String orderNo;
    @ApiModelProperty(value = "用户uuid")
    @JsonProperty
    private String userUuid;
    @ApiModelProperty(value = "订单标签")
    @JsonProperty
    private Integer orderTag;
    @ApiModelProperty(value="uuid")
    @JsonProperty
    private String uuid;
    @ApiModelProperty(value = "订单备注")
    @JsonProperty
    private String remark;
    @ApiModelProperty(value = "提醒时间")
    @JsonProperty
    private Date alertTime;
    @ApiModelProperty(value = "操作人id")
    @JsonProperty
    private Integer operateId;
    @ApiModelProperty(value = "手机号")
    @JsonProperty
    private String mobileNumber;
    @ApiModelProperty(value = "真实姓名")
    @JsonProperty
    private String realName;
    @ApiModelProperty(value = "是否是测试订单")
    @JsonProperty
    private Integer isTestOrder;
    @ApiModelProperty(value = "1中文， 2印尼文")
    @JsonProperty
    private Integer langue;


    public String getOrderNo() {
        return orderNo;
    }

    public Integer getOrderTag() {
        return orderTag;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public String getRemark() {
        return remark;
    }

    public Date getAlertTime() {
        return alertTime;
    }

    public Integer getOperateId() {
        return operateId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getRealName() {
        return realName;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setOrderTag(Integer orderTag) {
        this.orderTag = orderTag;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setAlertTime(Date alertTime) {
        this.alertTime = alertTime;
    }

    public void setOperateId(Integer operateId) {
        this.operateId = operateId;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getIsTestOrder() {
        return isTestOrder;
    }

    public void setIsTestOrder(Integer isTestOrder) {
        this.isTestOrder = isTestOrder;
    }

    public Integer getLangue() {
        return langue;
    }

    public void setLangue(Integer langue) {
        this.langue = langue;
    }
}
