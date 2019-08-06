package com.yqg.manage.service.check.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: microservice
 * @description: 质检语音返回前端的实体
 * @author: 许金泉
 * @create: 2019-04-03 10:25
 **/
@Data
@NoArgsConstructor
public class QualityCheckingVoiceResponse {


    private String uuid;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 姓名
     */
    private String realName;

    /**
     * 申请金额
     */
    private BigDecimal applyAmount;

    /**
     * 申请期限
     */
    private Integer applyDeadline;

    /**
     * 催收人员
     */
    private String userName;


    /**
     * 拨打开始时间
     */
    private Date answerStartTime;

    /**
     * 录音开始时间
     */
    private Date recordBeginTime;

    /**
     * 录音结束时间
     */
    private Date recordEndTime;

    /**
     * 录音时长（秒）
     */
    private Integer recordLength;

    private String attachmentPathUrl;

    private String phone;

}
