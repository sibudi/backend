package com.yqg.service.system.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.models.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class SysAppVersionRequest extends BaseRequest {

    @ApiModelProperty(value = "app?? ?1.iOS  2.Android?")
    @JsonProperty
    private Integer appType;
    @ApiModelProperty(value = "???")
    @JsonProperty
    private String client_type;
    @ApiModelProperty(value = "???")
    @JsonProperty
    private String client_version;
}
