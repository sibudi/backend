package com.yqg.manage.service.collection.request;

import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.manage.service.order.request.OrderSearchRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Getter
@Setter
@ApiModel
public class AssignableCollectionOrderReq extends OrderSearchRequest{

    @ApiModelProperty(value = "订单标签(1:完全失联,2:暂时失联,3:可联跳票,4:可联承诺)")
    private Integer orderTag;

    @ApiModelProperty(value = "催收人员id")
    private Integer outsourceId;

    @ApiModelProperty(value = "是否已分配1:是0:否")
    private Integer isAssigned;

    @ApiModelProperty(value = "是否复借订单1：是0：否")
    private Integer isRepeatBorrowing;

    @ApiModelProperty(value = "最小逾期天数")
    private Integer overdueDayMin;

    @ApiModelProperty(value = "最大逾期天数")
    private Integer overdueDayMax;

    @ApiModelProperty(value = "页码")
    private Integer pageNo = 1;

    @ApiModelProperty(value = "页大小")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "子账号催收人员id")
    private Integer subOutSourceId;

    private OrdStateEnum status;

    @ApiModelProperty(value = "是否展期")
    private Integer extendType;

    @ApiModelProperty(value = "是否结清")
    private Integer calType;

    @ApiModelProperty(value = "订单扩展标记")
    private Integer orderType;

    @ApiModelProperty(value = "人员来源 0:催收人员分配; 1,质检人员分配(默认为0）")
    private Integer sourceType = 0;

    @ApiModelProperty(value = "最新质检时间开始")
    private String startUpdateTime;

    @ApiModelProperty(value = "最新质检时间结束")
    private String endUpdateTime;

    @ApiModelProperty(value = "质检人员id")
    private Integer checkerId;

    @ApiModelProperty(value = "申请金额")
    private BigDecimal amountApply;

    private String orderNo;

}
