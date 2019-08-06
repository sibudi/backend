package com.yqg.manage.service.order.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Author: tonggen
 * Date: 2019/2/26
 * time: 4:17 PM
 */
@Data
public class CouponSendRequest {


    private List<CouponRequest> couponRequests;

    @Override
    public String toString() {
        return "CouponSendRequest{" +
                "couponRequests=" + couponRequests +
                '}';
    }
}
