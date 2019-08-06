package com.yqg.manage.service.order.response;

import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.manage.service.order.request.OrderSearchRequest.OrderChannelEnum;
import com.yqg.manage.service.order.request.OrderSearchRequest.UserRoleEnum;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
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
@ApiModel
public class OrderBaseResponse {

    @ApiModelProperty(value = "订单号")
    private String uuid;//订单号

    @ApiModelProperty(value = "用户姓名")
    private String realName;//订单用户名称

    @ApiModelProperty(value = "用户uuid")
    private String userUuid;//

    @ApiModelProperty(value = "用户身份")
    private Integer userRole;

    @ApiModelProperty(value = "申请金额")
    private BigDecimal amountApply;

    @ApiModelProperty(value = "申请期限")
    private String borrowingTerm;

    @ApiModelProperty(value = "申请时间")
    private Date applyTime;//申请时间

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;//更新时间

    @ApiModelProperty(value = "订单渠道")
    private Integer channel;

    @ApiModelProperty(value = "订单状态")
    private Integer status;

    @ApiModelProperty(value = "申请次数")
    private Integer borrowingCount;

    private Integer extendType; //是否展期

    private Integer calType; //是否结清

    private Integer orderType;

    private Integer isTerm; //1 是分期 2 不是分期

    private String needPayTerm;//当前需要还款的期限
}
