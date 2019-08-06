package com.yqg.manage.service.collection.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/*****
 * @Author Jacob
 * created at ${date}
 * ?????????
 ****/

@Getter
@Setter
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOrderResponse extends CollectionBaseResponse{

    @ApiModelProperty(value = "申请时间")
    private Date applyTime;
    @ApiModelProperty(value = "应还时间")
    private Date refundTime;
    @ApiModelProperty(value = "逾期天数")
    private Long overdueDays;
    @ApiModelProperty(value = "实还时间")
    private Date actualRefundTime;
    @ApiModelProperty(value = "姓名")
    private String realName;
    @ApiModelProperty(value = "借款人身份")
    private Integer userRole;
    @ApiModelProperty(value = "手机号码")
    private String mobile;
    @ApiModelProperty(value = "用户ID（用于查询手机号码)")
    private String uuid;

    private Integer extendType; //是否展期

    private Integer calType; //是否结清

    @ApiModelProperty(value = "当前催收人员姓名")
    private String collectiorName;

}
