package com.yqg.manage.service.collection.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.manage.service.collection.response.D0CollectorOrderStatistics;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Getter
@Setter
@ApiModel
public class D0AutoAssignmentRequest {

    @ApiModelProperty(value = "催收员请求分配的订单数量列表")
    private List<D0CollectorOrderStatistics> collectorAssignmentRequest;

    @ApiModelProperty(value = "催收区间")
    @JsonProperty
    private String section;

    @ApiModelProperty(value = "来源 0 催收 1.质检")
    @JsonProperty
    private Integer sourceType = 0;

    @ApiModelProperty(value = "申请金额")
    private BigDecimal[] amountApply;

    @ApiModelProperty(value = "金额是否为其它 0 未选 1 已选")
    @JsonProperty
    private Integer otherAmount;
}
