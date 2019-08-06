package com.yqg.service.externalChannel.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/3/12.
 */
@Data
public class Cash2GetOrdStatusRequest {

    @JsonProperty(value = "order_no")
    private String orderNo;

}
