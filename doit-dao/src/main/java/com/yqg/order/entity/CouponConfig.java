package com.yqg.order.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Author: tonggen
 * Date: 2019/2/26
 * time: 2:15 PM
 */
@Data
@Table("couponConfig")
public class CouponConfig extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -4822676242262582997L;

    private String couponCode;// VARCHAR ( 32 ) NOT NULL DEFAULT '' COMMENT '优惠券编号',
    private String alias;// VARCHAR ( 500 ) NOT NULL DEFAULT '' COMMENT '中文名称',

    private String indonisaName;//	VARCHAR ( 32 ) NOT NULL DEFAULT '' COMMENT '印尼名称',
    private Integer status;//	int ( 4 ) NOT NULL DEFAULT 0 COMMENT '优惠券状态 0 有效 1 无效'
}
