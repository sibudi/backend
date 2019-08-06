package com.yqg.service.system.request;

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
public class DictionaryRequest extends BaseRequest {
    @ApiModelProperty(value = "??id")
    @JsonProperty
    private Integer id;
    @ApiModelProperty(value = "???id")
    @JsonProperty
    private String parentId;
    @ApiModelProperty(value = "????")
    @JsonProperty
    private String dicName;
    @ApiModelProperty(value = "????")
    @JsonProperty
    private String dicCode;
    @ApiModelProperty(value = "??")
    @JsonProperty
    private Integer pageNo = 1;
    @ApiModelProperty(value = "????")
    @JsonProperty
    private Integer pageSize = 10;
    @ApiModelProperty(value = "??")
    @JsonProperty
    private String language;

}

