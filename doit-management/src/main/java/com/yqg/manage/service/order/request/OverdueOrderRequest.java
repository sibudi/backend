package com.yqg.manage.service.order.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.jetty.websocket.jsr356.InitException;

import java.math.BigDecimal;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 * ??????
 ****/

@Setter
@Getter
@ApiModel
public class OverdueOrderRequest extends OrderSearchRequest {

    @ApiModelProperty(value = "是否测试单(0:否,1:是)",name = "isTest")
    private Integer isTest; //0否，1是

    @ApiModelProperty(value = "是否复借0:否，1:是",name = "isRepeatBorrowing")
    private Integer isRepeatBorrowing;//是否付借

    @ApiModelProperty(value = "订单标签1:完全失联,2:暂时失联,3:可联跳票,4:可联承诺")
    private Integer orderTag;

    @ApiModelProperty(value = "应还款日期起始日期")
    private String dueDayStartTime; //应还款日期--查询起始时间

    @ApiModelProperty(value = "应还款日期截止日期")
    private String dueDayEndTime;//应还款日期--查询结束时间


    @ApiModelProperty(value = "最小逾期天数")
    private Integer minOverdueDays;//最小逾期天数

    @ApiModelProperty(value = "最大逾期天数")
    private Integer maxOverdueDays;//最大逾期天数

    @ApiModelProperty(value = "催收人员id")
    private Integer outsourceId;//催收人员id

    @ApiModelProperty(value = "申请金额")
    private BigDecimal amountApply;

    @ApiModelProperty(value = "是否是委外人员")
    private Integer isThird;

    @ApiModelProperty(value = "发薪日")
    private Integer payDay;

    @ApiModelProperty(value = "组员的名字，模糊查询")
    private String collectiorId;

    @ApiModelProperty(value = "质检列表中的催收人员")
    private Integer collectionInQualityCheckId;//
}
