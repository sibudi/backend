package com.yqg.manage.service.collection.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 *
 ****/

@Getter
@Setter
@ApiModel
public class OverdueOrderStatistics extends OverdueStatisticsItem{

    @ApiModelProperty(value = "已分配催收订单合计")
    private Integer totalAssigned = 0;

    @ApiModelProperty(value = "未分配催收订单合计")
    private Integer totalUnAssigned = 0;


}
