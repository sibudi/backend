package com.yqg.manage.service.check.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


/**
 * @author alan
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderCheckBase {
    @ApiModelProperty(value = "订单编号")
    @JsonProperty
    private String orderNo;
    @ApiModelProperty(value = "审核规则数组")
    @JsonProperty
    private List<OrderCheckRule> checkRuleList;
    @ApiModelProperty(value = "审核规则备注")
    @JsonProperty
    private OrderCheckRemark checkRuleRemark;
    @ApiModelProperty(value = "审核信息模块类型")
    @JsonProperty
    private Integer infoType;
    @ApiModelProperty(value = "session")
    @JsonProperty
    private String sessionId;
}
