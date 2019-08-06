package com.yqg.manage.service.user.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.models.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Jacob
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ManUsrFeedBackRemarkRequest extends BaseRequest {
    @ApiModelProperty(value = "订单编号")
    @JsonProperty
    private String uuid;
    @ApiModelProperty(value = "订单备注")
    @JsonProperty
    private String remark;
    @ApiModelProperty(value = "订单标签")
    @JsonProperty
    private Integer orderTag;
    @ApiModelProperty(value = "提醒时间")
    @JsonProperty
    private Date alertTime;
    @ApiModelProperty(value = "问题类型")
    @JsonProperty
    private Integer questionType;
    @ApiModelProperty(value = "解决情况")
    @JsonProperty
    private Integer stageType;
    @ApiModelProperty(value = "客服姓名")
    @JsonProperty
    private String operatorName;
    @ApiModelProperty(value = "用户id")
    @JsonProperty
    private Integer userId;
}
