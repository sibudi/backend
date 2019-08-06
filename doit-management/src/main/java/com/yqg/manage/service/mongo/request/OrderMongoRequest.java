package com.yqg.manage.service.mongo.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class OrderMongoRequest {

    @ApiModelProperty(value = "订单编号")
    private String orderNo;
    @ApiModelProperty(value = "用户uuid")
    private String userUuid;
}
