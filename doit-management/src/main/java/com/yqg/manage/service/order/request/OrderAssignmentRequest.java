package com.yqg.manage.service.order.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 * ????????
 ****/

@ApiModel
@Getter
@Setter
public class OrderAssignmentRequest {
   @ApiModelProperty("订单uuid列表")
   private List<String> orderUUIDs;

   @ApiModelProperty("审批人id")
   private Integer reviewerId;

}
