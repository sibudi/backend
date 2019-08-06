package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by Didit Dwianto on 2018/1/6.
 */
@Data
public class AdvanceVerifyRejectRate {
    private String p1;//日期
    private String p2;//聚信立实名认证未通过人数
    private String p3;//(聚信立)实名认证总人数
    private String p4;//聚信立实名认证未通过率
    private String p5;//(Advance)实名认证未通过人数
    private String p6;//Advance实名认证总人数
    private String p7;//Advance实名认证未通过率
    private String p8;//实名认证拒绝率
    private String p9;
    private String p10;
    private String p11;
    private String p12;
}
