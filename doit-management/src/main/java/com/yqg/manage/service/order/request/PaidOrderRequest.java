package com.yqg.manage.service.order.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 * ???????
 *
 ****/

@Getter
@Setter
@ApiModel
public class PaidOrderRequest extends OverdueOrderRequest {

    @ApiModelProperty(value = "实际还款日期-起始")
    private String actualPaymentStartDate;//实际还款-查询开始时间

    @ApiModelProperty(value = "实际还款日期-截止")
    private String actualPaymentEndDate;//实际还款-查询结束时间
}
