package com.yqg.manage.service.order.request;

import lombok.Data;

/**
 * Author: tonggen
 * Date: 2019/2/26
 * time: 2:37 PM
 */
@Data
public class CouponBaseRequest {

    private Integer id;
    /**
     * 优惠券编号
     */
    private String couponCode;

    /**
     * 优惠券中文名
     */
    private String alias;

    /**
     * 印尼名称
     */
    private String indonisaName;

    /**
     * 0 有效 1 无效
     */
    private Integer status;

    private Integer pageNo;

    private Integer pageSize;

}
