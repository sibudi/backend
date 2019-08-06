package com.yqg.management.entity;

import com.yqg.base.data.annotations.Column;
import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/*****
 * @Author super
 * created at ${date}
 *
 ****/

@Getter
@Setter
@Table("qualityCheckingVoice")
public class InfinityBillEntity extends BaseEntity implements Serializable {

    @Column("extNumber")
    private String extnumber;
    @Column("destNumber")
    private String destnumber;
    @Column("displayNumber")
    private String displaynumber;

    @Column("recordBeginTime")
    private String starttime;
    @Column("answerStartTime")
    private String answertime;
    @Column("recordEndTime")
    private String endtime;
    private String duration;
    private String billsec;
    private String direction;
    private String callmethod;
    @Column("userId")
    private String userid;
    private String memberid;
    private String chengshudu;
    private String recordfilename;
    private String downloadip;
    private String hangupdirection;
    private String hangupcause;
    private String customuuid;

    private String orderNo;
    private String realName;
    private String userName;
    private BigDecimal applyAmount;
    private int applyDeadline;

    private Integer callNode;
    private Integer callType;

}
