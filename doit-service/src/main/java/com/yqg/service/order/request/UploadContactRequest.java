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
public class UploadContactRequest extends BaseRequest {

    @ApiModelProperty(value = "")
    @JsonProperty
    private String contactStr;
    @ApiModelProperty(value = "")
    @JsonProperty
    private String orderNo;

}
