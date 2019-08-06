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
public class InviteListReq extends BaseRequest{
    @ApiModelProperty(value = "页数")
    @JsonProperty
    private Integer pageNo = 1;
    @ApiModelProperty(value = "分页大小")
    @JsonProperty
    private Integer pageSize = 30;
}
