package com.yqg.manage.service.order.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 *
 ****/

@Setter
@Getter
@ApiModel
public class D0OrderResponse extends OrderBaseResponse{

    @ApiModelProperty("催收人员名称")
    private String outsourceUserName;//催收人员名称

    @ApiModelProperty("催收人员id")
    private Integer outsourceId;//催收人员id

    @ApiModelProperty("是否复借0：否 1：是")
    private Integer isRepeatBorrowing;//是否复借0：否 1：是

    @ApiModelProperty("催收联系情况")
    private Integer[] collectionContactResult;

}
