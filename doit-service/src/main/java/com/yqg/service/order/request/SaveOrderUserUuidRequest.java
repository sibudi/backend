package com.yqg.service.order.request;

import lombok.Data;

@Data
public class SaveOrderUserUuidRequest {
    private String orderNo;
    private String userUuid;
}