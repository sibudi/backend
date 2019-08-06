package com.yqg.system.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: tonggen
 * Date: 2018/10/16
 * time: 上午9:53
 */
@Data
@Table("twilioWhatsAppRecord")
public class TwilioWhatsAppRecord extends BaseEntity implements Serializable {

    private String userUuid; //varchar(32) not null default '' comment '用户uuid',
    private String orderNo; // varchar(32) not null default '' comment '订单号',
    private String replyContent;//varchar(1600) not null default '' comment '回复内容',
    private int solveType; //int(4) not null default 0 comment '解决情况',
    private Date promiseTime; //dateTime comment '承诺还款时间',
    private String twilioPhone; //varchar(32) not null default '' comment '外显电话号码',
    private String userPhone; // varchar(32) not null default '' comment '用户电话号码(用于发送给twilio的号码)',
    private String phoneNum; // varchar(32) not null default '' comment '用户电话号码',
    private String status; // varchar(32) not null default '' comment '发送状态',
    private String price; // varchar(32) NOT null default '' comment '价格',
    private String sid;// varchar(32) not null default '' comment '本次的唯一标识',
    private String batchNo;// varchar(32) not null default '' comment '我们生成的批次号'

    private int direction; //int(4) not null default 0 comment '发送的方向 1.twilio发送过来， 2.用户发送给twilio'

    private Date dataCreateTime; //wa 数据创建时间
}
