package com.yqg.service.third.infinity.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class InfinityExtNumberResponse {

    @ApiModelProperty("分机号码")
    @JsonProperty
    private String extnumber;
    @ApiModelProperty("分机密码")
    @JsonProperty
    private String password;
    @ApiModelProperty(value = "分机状态")
    @JsonProperty
    private String status;

}
