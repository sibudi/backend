package com.yqg.manage.service.collection.response;

import lombok.Data;

import java.util.Date;

/**
 * Author: tonggen
 * Date: 2018/11/23
 * time: 3:44 PM
 */
@Data
public class ManQualityCountResponse {

    /**
     * 催收人员
     */
    private String collector;


    /**
     * 类型：0.备注质检 1.语音质检
     */
    private Integer type;

    /**
     * 统计时间
     */
    private String countTime;
    /**
     * 存在问题中文
     */
    private String title;
    /***
     * 存在问题印尼文
     */
    private String titleInn;
    /**
     * 问题统计次数
     */
    private Integer questionCount;
    /**
     * 罚款金额
     */
    private String fineMoneys;

    /**
     * 订单号
     */
    private String orderNo;

    private String userName;

    private String overDudDay;

    private String collectionName;

    private String days;

    private String fineMoney;

    private String operator;

    private String remark;

    private Date createTime;

    private Integer collectorId;

    private Integer checkTag;

    /**
     * sheet3 结果
     */
    private Integer outsourceId = 0;

    private Integer totalOrderCount = 0;

    private Integer qulityOrderCount = 0;

    private Integer remarkOrderCount = 0;

    private String qualityCheckRate = "";

    private String avgQualityCheckRate = "";

    private Integer voiceQulityOrderCount = 0;

    private Integer voiceRemarkOrderCount = 0;

    private String voiceQualityCheckRate = "";

    private String voiceAvgQualityCheckRate = "";
}
