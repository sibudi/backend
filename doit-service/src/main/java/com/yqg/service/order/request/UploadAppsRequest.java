package com.yqg.service.order.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.models.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UploadAppsRequest extends BaseRequest {

    @ApiModelProperty(value = "")
    @JsonProperty
    private String orderNo;

    @ApiModelProperty(value = "")
    @JsonProperty
    private String appsListStr;
}
