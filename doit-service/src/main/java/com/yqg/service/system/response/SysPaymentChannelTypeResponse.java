package com.yqg.service.system.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 */
@Data
public class SysPaymentChannelTypeResponse {

    @ApiModelProperty(value = "????  1 bluePay 2 xendit")
    @JsonProperty
    private String paymentType;
    @ApiModelProperty(value = "????  1 bluePay 2 xendit")
    @JsonProperty
    private Boolean isOnline;
    @JsonProperty
    private String message;
    @JsonProperty
    private String description;
    @JsonProperty
    private String name;


}






