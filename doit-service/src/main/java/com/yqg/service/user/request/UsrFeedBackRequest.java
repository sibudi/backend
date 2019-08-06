package com.yqg.service.user.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.models.BaseRequest;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsrFeedBackRequest extends BaseRequest {


    @JsonProperty
    private String feedBackContent;

    @JsonProperty
    private String feedBackImages;

    @JsonProperty
    private Integer sourceType;

    @JsonProperty
    private String collectionName;
}
