package com.yqg.service.pay.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/12/29.
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class RepayResponse {

    @ApiModelProperty(value = "??? 0 ???")
    @JsonProperty
    private String code;
    @ApiModelProperty(value = "??????? xendit ??paymentCode")
    @JsonProperty
    private String paymentCode;
    @ApiModelProperty(value = "String error message if any")
    @JsonProperty
    private String errorMessage;
    @ApiModelProperty(value = "String error message if any")
    @JsonProperty
    private String depositStatus;
}
