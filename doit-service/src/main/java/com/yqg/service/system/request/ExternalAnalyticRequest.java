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
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ExternalAnalyticRequest extends BaseRequest {
    // @ApiModelProperty(value = "??")
    // @JsonProperty
    // private String uuid;
    @ApiModelProperty(value = "??????")
    @JsonProperty
    private String eventName;
    @ApiModelProperty(value = "????")
    @JsonProperty
    private String eventAttribute;
    @ApiModelProperty(value = "????")
    @JsonProperty
    private String eventValue;
    @ApiModelProperty(value = "????")
    @JsonProperty
    private String source;
    @ApiModelProperty(value = "??")
    @JsonProperty
    private Integer pageNo = 1;
    @ApiModelProperty(value = "????")
    @JsonProperty
    private Integer pageSize = 10;
    @ApiModelProperty(value = "??")
    @JsonProperty
    private String remark;
    @ApiModelProperty(value = "??")
    @JsonProperty
    private String partnerName;

}

