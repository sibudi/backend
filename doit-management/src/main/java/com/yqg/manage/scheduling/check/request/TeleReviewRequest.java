package com.yqg.manage.scheduling.check.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Jacob
 * ??????
 */
@Data
public class TeleReviewRequest {

    @ApiModelProperty(value = "用户uuid")
    private String uuid;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    @ApiModelProperty(value = "类型 1学生，2已工作")
    private Integer type;

    @ApiModelProperty(value = "语言 1中文，2印尼文")
    private Integer langue;

    @ApiModelProperty(value = "1个人审核 3公司审核")
    private Integer teleReviewType;

    private Integer createUser;

    @ApiModelProperty(value = "标记是否真的电核拒绝")
    private Boolean teleReviewEndFlag;

}
