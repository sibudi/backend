package com.yqg.manage.service.collection.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@ApiModel
@Getter
@Setter
public class OverdueAssignmentStatisticsRequest {

    @ApiModelProperty(value = "催收人员岗位id")
    private Integer postId;

    @ApiModelProperty(value = "逾期区间")
    private String section;

    @ApiModelProperty(value = "催收人员id")
    private Integer outSourceId;

    @ApiModelProperty(value = "来源 0 催收 1.质检")
    @JsonProperty
    private Integer sourceType = 0;

    @ApiModelProperty(value = "申请金额")
    private BigDecimal[] amountApply;

    @ApiModelProperty(value = "金额是否为其它 0 未选 1 已选")
    @JsonProperty
    private Integer otherAmount;
}
