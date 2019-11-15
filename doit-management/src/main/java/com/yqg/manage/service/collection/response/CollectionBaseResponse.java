package com.yqg.manage.service.collection.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Author: tonggen
 * Date: 2019/4/23
 * time: 3:54 PM
 */
@Data
@ApiModel(description = "collection base class")
public class CollectionBaseResponse {

    @ApiModelProperty(value = "订单编号")
    private String orderNo;
    @ApiModelProperty(value = "申请金额")
    private BigDecimal amountApply;
    @ApiModelProperty(value = "申请期限")
    private String borrowingTerm;

    private Integer orderType;

    private Integer isTerm; //1 是分期 2 不是分期

    private String needPayTerm;//当前需要还款的期限

    @ApiModelProperty(value = "订单号")
    private String uuid;

    private Integer id;
}
