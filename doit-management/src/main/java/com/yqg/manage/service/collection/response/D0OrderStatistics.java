package com.yqg.manage.service.collection.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 * ?????????
 ****/

@ApiModel
@Getter
@Setter
public class D0OrderStatistics extends D0StatisticsItem {

    //已分配
    @ApiModelProperty(value = "总分配订单数")
    private Integer totalAssigned = 0;

    //未分配
    @ApiModelProperty("未分配订单总数")
    private Integer totalUnAssigned = 0;


}
