package com.yqg.service.third.operators.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/12/20.
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class GojekSendSmsResponse {

    @ApiModelProperty(value = "")
    @JsonProperty
    private String report_task_token;
    @ApiModelProperty(value = "")
    @JsonProperty
    private String message;
    @ApiModelProperty(value = "")
    @JsonProperty
    private String auth_token;
}
