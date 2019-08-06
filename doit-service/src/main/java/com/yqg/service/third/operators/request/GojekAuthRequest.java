package com.yqg.service.third.operators.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.models.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/12/20.
 */
@Data
public class GojekAuthRequest  extends BaseRequest {

    @ApiModelProperty(value = "??token")
    @JsonProperty
    private String report_task_token;
    @ApiModelProperty(value = "???")
    @JsonProperty
    private String captcha;
    @ApiModelProperty(value = "??token")
    @JsonProperty
    private String auth_token;
    @ApiModelProperty(value = "???")
    @JsonProperty
    private String phoneNo;
    @ApiModelProperty(value = "???")
    @JsonProperty
    private String orderNo;
    @ApiModelProperty(value = "?????????gojek")
    @JsonProperty
    private String website;

    private String name;
}
