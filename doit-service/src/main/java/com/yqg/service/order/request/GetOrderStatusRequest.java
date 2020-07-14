package com.yqg.service.order.request;

import java.io.Serializable;

import lombok.Data;

@Data
public class GetOrderStatusRequest implements Serializable {
    
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String orderNo;
}