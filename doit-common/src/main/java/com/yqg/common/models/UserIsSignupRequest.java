package com.yqg.common.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Didit Dwianto on 2017/11/25.
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserIsSignupRequest extends BaseRequest{
    @ApiModelProperty(value = "???",required = true)
    @JsonProperty
    private String mobileNumber;

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
