package com.yqg.service.system.response;

import lombok.Data;


/**
 * Created by wanghuaizhou on 2019/5/15.
 */
@Data
public class CouponResponse {

    private String couponNum; // 优惠券金额
    private String couponStartDate;//	优惠券 有效期开始时间
    private String couponEndDate;//	优惠券 有效期结束时间
    private String couponName;//	优惠券名称
    private String status;//	优惠券类型   1 已使用，2 已过期，3 未使用
}
