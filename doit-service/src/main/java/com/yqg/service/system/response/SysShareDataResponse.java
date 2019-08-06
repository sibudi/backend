package com.yqg.service.system.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/12/2.
 */
@Data
public class SysShareDataResponse {

    @ApiModelProperty(value = "???title")
    @JsonProperty
    private String shareTitle;
    @ApiModelProperty(value = "???content")
    @JsonProperty
    private String shareContent;
    @ApiModelProperty(value = "?????url")
    @JsonProperty
    private String shareImageUrl;
    @ApiModelProperty(value = "???url")
    @JsonProperty
    private String shareUrl;

}
