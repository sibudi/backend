package com.yqg.manage.enums;


import lombok.Getter;

/**
 * Author: tonggen
 * Date: 2019/5/8
 * time: 5:59 PM
 */
@Getter
public enum CouponTypeEnum {

    COUPON_D_15("5天利息减免券",2,5, -15),
    COUPON_D_10("3天利息减免券",2,3, -10),
    COUPON_D_5("1天利息减免券",2,1, -5);

    private String alias;//couponConfig的中文名称 （通过名称查到配置表的信息）

    private Integer validityDays; //有效天数

    private Integer interestDownDays; //利息减少的天数

    private Integer overDueDays; //逾期天数(负数是因为 用当前时间减去应还款时间）

    CouponTypeEnum(String alias, Integer validityDays, Integer interestDownDays, Integer overDueDays) {
        this.alias = alias;
        this.validityDays = validityDays;
        this.interestDownDays = interestDownDays;
        this.overDueDays = overDueDays;
    }
}
