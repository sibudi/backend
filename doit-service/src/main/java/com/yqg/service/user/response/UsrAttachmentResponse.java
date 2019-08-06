package com.yqg.service.user.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/11/25.
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsrAttachmentResponse {

    @ApiModelProperty(value = "")
    @JsonProperty
    private int attachmentType;
    @ApiModelProperty(value = "")
    @JsonProperty
    private String attachmentUrl;
    @ApiModelProperty(value = "")
    @JsonProperty
    private String attachmentName;
}
