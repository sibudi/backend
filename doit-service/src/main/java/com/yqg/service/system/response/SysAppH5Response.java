package com.yqg.service.system.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
public class SysAppH5Response {

    @ApiModelProperty(value = "url key")
    @JsonProperty
    private String urlKey;
    @ApiModelProperty(value = "url value")
    @JsonProperty
    private String urlValue;
    @ApiModelProperty(value = "url ")
    @JsonProperty
    private String urlDesc;
}
