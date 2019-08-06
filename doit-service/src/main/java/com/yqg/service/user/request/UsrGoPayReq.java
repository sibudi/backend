package com.yqg.service.user.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.models.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Features:
 * Created by huwei on 18.8.16.
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UsrGoPayReq extends BaseRequest{
    @ApiModelProperty(value = "", required = true)
    @JsonProperty
    @NotNull
    private String mobileNumber;
    @ApiModelProperty(value = "", required = true)
    @JsonProperty
    @NotNull
    private String userName;

}
