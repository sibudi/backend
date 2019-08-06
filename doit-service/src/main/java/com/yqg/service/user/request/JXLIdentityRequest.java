package com.yqg.service.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.models.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/3/15.
 */
@Data
public class JXLIdentityRequest extends BaseRequest {

    @ApiModelProperty(value = "订单号")
    @JsonProperty
    private String orderNo;

    private String name; //姓名
    private String nik; //身份证号
}
