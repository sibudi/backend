package com.yqg.common.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.enums.SmsTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author  Didit Dwianto
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SmsRequest extends BaseRequest{
    @ApiModelProperty(value = "???",required = true)
    @JsonProperty
    @NotNull
    private String mobileNumber;
    @ApiModelProperty(value = "????,(REGISTER=??, LOGIN=??;)",required = true)
    @JsonProperty
    @NotNull
    private SmsTypeEnum smsType;
    @ApiModelProperty(value = "????",required = false)
    @JsonProperty
    private String iDCardNo;

    @ApiModelProperty(value = "验证码类型  1 短信  2 语音",required = false)
    @JsonProperty
    private String verifyType = "1";

}
