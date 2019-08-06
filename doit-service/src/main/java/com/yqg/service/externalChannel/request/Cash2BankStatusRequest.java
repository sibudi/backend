package com.yqg.service.externalChannel.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cash2BankStatusRequest {
    @JsonProperty(value = "order_no")
    private String orderNo;
}
