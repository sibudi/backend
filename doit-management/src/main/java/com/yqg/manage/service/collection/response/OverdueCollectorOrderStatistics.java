package com.yqg.manage.service.collection.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
public class OverdueCollectorOrderStatistics extends OverdueStatisticsItem {
    @ApiModelProperty(value = "催收员id")
    private Integer userId;

    @ApiModelProperty("催收员姓名")
    private String realName;
}
