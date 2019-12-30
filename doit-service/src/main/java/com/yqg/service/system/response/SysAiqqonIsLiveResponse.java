package com.yqg.service.system.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Gilang on 11/25/2019.
 */
@Data
public class SysAiqqonIsLiveResponse {

    @JsonProperty
    private String isOn;
  

}
