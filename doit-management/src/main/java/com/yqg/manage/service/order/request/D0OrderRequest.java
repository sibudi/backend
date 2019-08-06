package com.yqg.manage.service.order.request;

import io.swagger.annotations.Api;
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
public class D0OrderRequest extends OrderSearchRequest {

    @ApiModelProperty("是否复借0：否 1：是")
    private Integer isRepeatBorrowing;//是否复借0：否 1：是

    @ApiModelProperty("订单标签(1:完全失联,2:暂时失联,3:可联跳票,4:可联承诺)")
    private Integer orderTag;//订单标签(1:完全失联,2:暂时失联,3:可联跳票,4:可联承诺)

    @ApiModelProperty("催收人员id")
    private Integer outsourceId;//催收人员id
}
