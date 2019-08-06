package com.yqg.manage.scheduling.check.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author Jacob
 * ??????
 */
@Data
public class SaveTeleReviewRequest {

    @ApiModelProperty(value = "用户uuid")
    private String userUuid;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;


    @ApiModelProperty(value = "语言 1中文，2印尼文")
    private Integer langue;

    @ApiModelProperty(value = "用户uuid")
    private List<TeleReviewResultRequest> resultRequests;

}
