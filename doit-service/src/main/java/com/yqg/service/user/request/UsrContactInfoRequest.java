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
public class UsrContactInfoRequest extends BaseRequest {
    @ApiModelProperty(value = "??????")
    @JsonProperty
    private String contactStr;
    @ApiModelProperty(value = "???")
    @JsonProperty
    private String orderNo;
    @ApiModelProperty(value = "?????1")
    @JsonProperty
    private String contactsName1;
    @ApiModelProperty(value = "??1")
    @JsonProperty
    private String relation1;
    @ApiModelProperty(value = "?????1")
    @JsonProperty
    private String contactsMobile1;
    @ApiModelProperty(value = "?????2")
    @JsonProperty
    private String contactsName2;
    @ApiModelProperty(value = "??2")
    @JsonProperty
    private String relation2;
    @ApiModelProperty(value = "?????2")
    @JsonProperty
    private String contactsMobile2;

    @ApiModelProperty(value = "???????")
    @JsonProperty
    private String alternatePhoneNo;
}