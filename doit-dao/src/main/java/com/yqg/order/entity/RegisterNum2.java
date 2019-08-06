package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/7/6.
 */
@Data
public class RegisterNum2 {

    private String refuseDate;// 拒绝日期
    private String refuseReason;// 拒绝原因
    private String refuseNum;// 拒绝订单数
    private String commitNum;// 提交数
    private String commitRefuseRate;// 在提交订单中的命中率

}
