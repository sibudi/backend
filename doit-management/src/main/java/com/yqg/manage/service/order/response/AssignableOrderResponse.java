package com.yqg.manage.service.order.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Getter
@Setter
@ApiModel
public class AssignableOrderResponse extends OrderBaseResponse{

    @ApiModelProperty("审核人员id")
    private Integer reviewerId;

    @ApiModelProperty("审核人员姓名")
    private String reviewerName;

    @ApiModelProperty("分配时间")
    private Date reviewTime;
}
