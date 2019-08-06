package com.yqg.service.user.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.models.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/11/25.
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UsrSubmitCerInfoRequest extends BaseRequest{

    @ApiModelProperty(value = "?????")
    @JsonProperty
    private String orderNo;

    @ApiModelProperty(value = "?????2?????3 ????...")
    @JsonProperty
    private Integer certificationType;

    @ApiModelProperty(value = "????")
    @JsonProperty
    private String certificationData;

    @ApiModelProperty(value = "?????0????1?????2????")
    @JsonProperty
    private Integer certificationResult;

    @ApiModelProperty(value = "??url????????? ??????????")
    @JsonProperty
    private String attachmentUrl;

}
