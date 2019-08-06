package com.yqg.manage.service.collection.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
public class OverdueCollectionStatisticsResponse {

    @ApiModelProperty(value = "所有订单汇总信息")
    private OverdueOrderStatistics orderStatistics;

    @ApiModelProperty(value = "催收人员分配订单汇总信息")
    private List<OverdueCollectorOrderStatistics> collectorOrderStatistics;
}
