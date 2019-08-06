package com.yqg.manage.service.order.request;

import com.yqg.common.enums.order.OrdStateEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 * ???????
 ****/

@Getter
@Setter
@ApiModel
public class ReviewerOrderApplicationParam {

    @ApiModelProperty("订单状态:FIRST_CHECK：初审申请审核单 SECOND_CHECK: 复审申请审核单")
    private OrdStateEnum status;
}
