package com.yqg.service.h5.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.models.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2018/1/9.
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmsH5Request extends BaseRequest {

    @ApiModelProperty(value = "")
    @JsonProperty
    private String mobile;
    @ApiModelProperty(value = "")
    @JsonProperty
    private String imgCode;
    @ApiModelProperty(value = "")
    @JsonProperty
    private String imgKey;

}