package com.yqg.management.entity;

import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: microservice
 * @description: 质检语音实体
 * @author: 许金泉
 * @create: 2019-04-02 19:36
 **/
@Data
@com.yqg.base.data.annotations.Table("qualityCheckingVoice")
public class QualityCheckingVoice extends BaseEntity implements Serializable {


    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 分机号码
     */
    private String extNumber;
    /**
     * 拨打的电话号码
     */
    private String destNumber;
    /**
     * 下载的全url路径
     */
    private String downUrl;
    /**
     * 文件服务器的路径
     */
    private String attachmentSavePath;

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

    /**
     * 呼叫结果状态（0 默认呼叫已请求 1. 呼叫处理成功 2. 呼叫失败）
     */
    private Integer callState;

    /**
     * 呼叫具体结果 1 呼叫成功 2 呼叫失败
     */
    private Integer callResult;

    /**
     * 结果返回的错误码（详情查看VoiceCallEnum)
     */
    private Integer errorId;

    /**
     * 外呼节点 1 催收  2 电核
     */
    private Integer callNode;
    /**
     * 外呼手机号类型（// 1本人电话 2公司电话 3 紧急联系人 4 备选联系人)
     */
    private Integer callType;

    private String userId;

    private Integer sourceType;

    private String phone;

}
