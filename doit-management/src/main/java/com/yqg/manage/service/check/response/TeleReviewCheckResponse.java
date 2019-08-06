package com.yqg.manage.service.check.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Jacob
 */
@Getter
@Setter
@ApiModel
public class TeleReviewCheckResponse {

    @ApiModelProperty(value = "问题及答案")
    private List<TeleReviewQuestionResponse> questions;

    @ApiModelProperty(value = "电话审核备注")
    private List<ManOrderRemarkResponse> remarks;

    @ApiModelProperty(value = "判断复审是否被拒绝 （当备注中有拒绝类型）1 被拒绝 0 默认 2 电核第一常用联系人 3 电核第二常用联系人 4 稍后再拨 5 通过")
    private Integer pass;
}
