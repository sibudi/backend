package com.yqg.manage.service.order.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
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
public class PaidOrderResponse extends OrderBaseResponse {

    @ApiModelProperty(value = "是否复借0：否 1：是")
    private Integer isRepeatBorrowing;//是否复借0：否 1：是

    @ApiModelProperty(value = "是否测试单0：否 1：是")
    private Integer isTest;

    @ApiModelProperty(value = "订单标签1:完全失联,2:暂时失联,3:可联跳票,4:可联承诺")
    private Integer orderTag;

    @ApiModelProperty(value = "应还款日期")
    private Date refundTime;

    @ApiModelProperty(value = "逾期天数")
    private Long overdueDays;

    private Integer outsourceId;

    @ApiModelProperty(value = "催收人员姓名")
    private String outsourceUserName;

    @ApiModelProperty(value = "实际还款日期")
    private Date actualRefundTime;
}
