package com.yqg.manage.service.order.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 *
 * ???????
 *
 ****/

@Getter
@Setter
@ApiModel(value = "?????")
public class AssignableOrderRequest extends OrderSearchRequest{

    @ApiModelProperty(value = "审核人员id")
    Integer reviewerId;//审核人员id

    @ApiModelProperty(value = "是否已经分配 0 未分配 1已分配")
    Integer isAssignment;
}