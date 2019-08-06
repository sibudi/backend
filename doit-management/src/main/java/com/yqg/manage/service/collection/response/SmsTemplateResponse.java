package com.yqg.manage.service.collection.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created with IntelliJ IDEA.
 * User: caomiaoke
 * Date: 26/03/2018
 * Time: 11:20 AM
 */

@Getter
@Setter
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class SmsTemplateResponse {

    @ApiModelProperty(value = "短信模版编号")
    private String smsTemplateId;
    @ApiModelProperty(value = "短信标题")
    private String smsTitle;
    @ApiModelProperty(value = "短信内容")
    private String smsContent;
}
