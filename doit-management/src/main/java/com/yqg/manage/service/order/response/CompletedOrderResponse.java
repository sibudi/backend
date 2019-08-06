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
 *
 ****/

@Getter
@Setter
@ApiModel(value = "?????")
public class CompletedOrderResponse extends OrderBaseResponse {

    @ApiModelProperty(value = "初审人员id")
    private Integer juniorReviewerId;//初审id

    @ApiModelProperty(value = "初审人员姓名")
    private String juniorReviewerName; //初审姓名

    @ApiModelProperty(value = "复审人员id")
    private Integer seniorReviewerId;//初审id

    @ApiModelProperty(value = "复审人员姓名")
    private String seniorReviewerName;//复审姓名

    @ApiModelProperty(value = "初审完成时间")
    private Date juniorReviewTime;//初审时间

    @ApiModelProperty(value = "复审完成时间")
    private Date seniorReviewTime;//复审时间

    @ApiModelProperty(value = "应还时间")
    private Date refundTime;

    @ApiModelProperty(value = "实还时间")
    private Date actualRefundTime;

    @ApiModelProperty(value = "逾期天数")
    private Long overdueDays;//逾期天数

    @ApiModelProperty(value = "初审分配时间")
    private Date juniorAssignTime;

    @ApiModelProperty(value = "复审分配时间")
    private Date seniorAssignTime;
}
