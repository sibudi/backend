package com.yqg.order.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import com.yqg.common.utils.StringUtils;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: tonggen
 * Date: 2019/2/26
 * time: 2:15 PM
 */
@Data
@Table("couponRecord")
public class CouponRecord extends BaseEntity implements Serializable {

    private String userUuid;
    private String orderNo;// VARCHAR ( 32 ) NOT NULL DEFAULT '' COMMENT '订单编号',
    private String userName;// VARCHAR ( 200 ) NOT NULL DEFAULT '' COMMENT '用户姓名',

    private Integer couponConfigId;//	int ( 11 ) NOT NULL DEFAULT 0 COMMENT '对应couponConfig ID',
    private Integer status;//	int ( 2 ) NOT NULL DEFAULT 0 COMMENT '券状态（1 已使用，2 已过期，3 未使用）',
    private BigDecimal money;// NUMERIC not null default 0 comment '优惠券金额',
    private Date validityStartTime;//	datetime  COMMENT '有效期开始时间',
    private Date validityEndTime;//	datetime  COMMENT '有效期结束时间',
    private Date usedDate;//	datetime  COMMENT '使用日期',
    private Integer sendPersion;// int(11) not null DEFAULT 0 comment '发放人',


    @Getter
    public enum StatusEnum {
        NOT_USE(3),
        OUT_DATE(2),
        BE_USED(1);
        StatusEnum(int code) {
            this.code = code;
        }
        private int code;

    }

    public void setValidityStartTime(String startTime) {

        if (StringUtils.isEmpty(startTime)) {
            return ;
        }
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            this.validityStartTime = sf.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setValidityEndTime(String endTime) {

        if (StringUtils.isEmpty(endTime)) {
            return ;
        }
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            this.validityEndTime = sf.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
