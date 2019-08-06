package com.yqg.manage.service.order.response;

import lombok.Data;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: tonggen
 * Date: 2019/2/26
 * time: 6:07 PM
 */
@Data
public class CouponResponse {

    private String alias;

    private String indonisaName;

    private BigDecimal money;

    private Integer status;

    private String sendTime;

    private String validateTime;

    private String usedTime;

    private String orderNo;

    private Date createTime;

    private Date validityStartTime;//	datetime  COMMENT '有效期开始时间',
    private Date validityEndTime;//	datetime  COMMENT '有效期结束时间',
    private Date usedDate;//	datetime  COMMENT '使用日期',




}
