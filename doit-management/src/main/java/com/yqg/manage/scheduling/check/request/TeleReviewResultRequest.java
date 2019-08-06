package com.yqg.manage.scheduling.check.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Jacob
 * ??????
 */
@Data
public class TeleReviewResultRequest {


    @ApiModelProperty(value = "问题")
    private String question;

    @ApiModelProperty(value = "答案")
    private String answer;

    @ApiModelProperty(value = "是否回答正确 0 错误 1 正确")
    private Integer result;

}
