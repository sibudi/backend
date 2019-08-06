package com.yqg.service.system.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/11/26.
 */
@Data
public class SysPaymentChannelResponse {

    @ApiModelProperty(value = "????  1 bluePay 2 xendit")
    @JsonProperty
    private String paymentType;
    @ApiModelProperty(value = "??????")
    @JsonProperty
    private String paymentChannelName;
    @ApiModelProperty(value = "??????")
    @JsonProperty
    private String paymentChannel;
}
