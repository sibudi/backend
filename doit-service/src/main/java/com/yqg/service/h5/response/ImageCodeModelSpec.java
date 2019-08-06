package com.yqg.service.h5.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2018/1/9.
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageCodeModelSpec {

    @ApiModelProperty(value = "")
    @JsonProperty
    private String imgBase64;
    @ApiModelProperty(value = "")
    @JsonProperty
    private String imgSessionId;

}
