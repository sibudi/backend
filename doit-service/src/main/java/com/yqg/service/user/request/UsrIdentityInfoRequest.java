package com.yqg.service.user.request;

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
public class UsrIdentityInfoRequest extends BaseRequest{
    @ApiModelProperty(value = "???")
    @JsonProperty
    private String orderNo;
    @ApiModelProperty(value = "??")
    @JsonProperty
    private String name;
    @ApiModelProperty(value = "????")
    @JsonProperty
    private String idCardNo;
    @ApiModelProperty(value = "??")
    @JsonProperty
    private Integer sex;
    @ApiModelProperty(value = "??????url")
    @JsonProperty
    private String idCardPhoto;
    @ApiModelProperty(value = "??????url")
    @JsonProperty
    private String handIdCardPhoto;
}