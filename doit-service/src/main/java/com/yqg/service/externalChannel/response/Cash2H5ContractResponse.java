package com.yqg.service.externalChannel.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by luhong on 2018/3/13.
 */
@Data
public class Cash2H5ContractResponse {
    @JsonProperty(value = "url_key")
    private String urlKey;
    @JsonProperty(value = "url_value")
    private String urlValue;
    @JsonProperty(value = "url_desc")
    private String urlDesc;

}
