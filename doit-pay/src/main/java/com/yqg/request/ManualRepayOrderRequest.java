package com.yqg.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.models.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2018/2/14.
 */
@Data
public class ManualRepayOrderRequest extends BaseRequest{

    private String orderNo;
    private String userUuid;
}
