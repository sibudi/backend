package com.yqg.manage.service.order.response;

import com.yqg.manage.service.order.request.OrderSearchRequest.OrderChannelEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 *  @author alan
 */
@Data
@ApiModel
public class ManOrderListResponse {
    private Integer id;

    @ApiModelProperty("订单号")
    private String uuid;

    private Date createTime;

    private Date updateTime;

    private Integer createUser;

    private Integer updateUser;

    private Integer status;             //订单状态

    private Integer orderStep;          //订单步骤

    private BigDecimal amountApply;     //订单金额

    private Integer borrowingTerm;      //订单期限

    private BigDecimal serviceFee;      //服务费

    private BigDecimal interest;        //利息

    private Integer borrowingCount;     //借款次数

    private Integer channel;            //订单渠道

    private String userUuid;            //用户UUID

    private Date applyTime;             //申请时间

    private Date lendingTime;           //放款日期

    private Date refundTime;            //应还款日期

    private Date actualRefundTime;      //实际还款日期

    private String firstChecker;        //初审人员

    private String secondChecker;       //复审人员

    private String realName;            //姓名

    private String mobileNumber;        //手机号

    private String overDueDay;          //逾期天数

    private Integer orderTag;           //催收标签

    private Integer outsourceId;        //催收人员

    private Integer thirdDistribute;    //是否被分配给催收人员

    private Date firstCheckTime;        //初审提交时间

    private Date secondCheckTime;       //复审提交时间

    private OrderChannelEnum orderChannelEnum;

    private Integer secondCheckStatus; //复审状态 0：未处理 1 处理中

    private Integer operatorType; //订单复审标签，1表示稍后再审
}
