package com.yqg.manage.service.check.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author Jacob
 */
@Getter
@Setter
@ApiModel
public class TeleReviewQuestionResponse {

    @ApiModelProperty(value = "问题")
    private String question;

    @ApiModelProperty(value = "答案")
    private String answer;

    @ApiModelProperty(value = "结果 0错误 1正确")
    private String result;

    @ApiModelProperty(value = "对应电核表Id")
    private Integer manOrderRemarkId;

    public TeleReviewQuestionResponse() {}

    public TeleReviewQuestionResponse(String question, String answer, Integer manOrderRemarkId) {
        this.question = question;
        this.answer = answer;
        this.manOrderRemarkId = manOrderRemarkId;
    }
}
