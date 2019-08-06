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
@ApiModel
public class CompletedOrderRequest extends OrderSearchRequest {

    @ApiModelProperty(value = "初审人员id")
    private Integer juniorReviewerId; //初审人员id

    @ApiModelProperty(value = "复审人员id")
    private Integer seniorReviewerId;//复审人员id

    private Boolean isAdmin;

    private Integer operatorId;//操作人员


    private String juniorAssignBeginTime;//初审分配时间-最小值

    private String juniorAssignEndTime;//初审分配时间-最大值

    private String juniorFinishBeginTime;//初审完成时间-最小值

    private String juniorFinishEndTime;//初审完成时间-最大值


    private String seniorAssignBeginTime;//复审分配时间-最小值

    private String seniorAssignEndTime;//复审分配时间-最大值

    private String seniorFinishBeginTime;//复审完成时间-最小值

    private String seniorFinishEndTime;//复审完成时间-最大值

    private Integer reviewerId;//审核人员

}
