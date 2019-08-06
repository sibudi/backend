package com.yqg.service.system.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/12/3.
 */
@Data
public class SysSchoolListResponse {

    @ApiModelProperty(value = "")
    @JsonProperty
    private String dicItemName;
}
