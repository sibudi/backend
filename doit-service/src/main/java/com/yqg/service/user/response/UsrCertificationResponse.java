package com.yqg.service.user.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/11/25.
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsrCertificationResponse {

    @ApiModelProperty(value = "")
    @JsonProperty
    private Integer certificationResult;

    @ApiModelProperty(value = "")
    @JsonProperty
    private Integer certificationResult2;

}
