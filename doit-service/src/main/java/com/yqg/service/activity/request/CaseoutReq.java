package com.yqg.service.activity.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.models.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Features:
 * Created by huwei on 18.8.16.
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseoutReq extends BaseRequest{
    @ApiModelProperty(value = "提现金额")
    @JsonProperty
    private String amount;
    @ApiModelProperty(value = "提现账户类型")
    @JsonProperty
    private String channel;
}
