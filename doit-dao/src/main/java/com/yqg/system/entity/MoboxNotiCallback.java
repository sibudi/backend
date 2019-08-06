package com.yqg.system.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by wanghuaizhou on 2019/2/27.
 */
@Data
@Table("moboxNotiCallback")
public class MoboxNotiCallback extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 7997242168638983205L;

    private String orderNo; // 订单号
    private String userUuid; // 用户id
//    private String type; // 1 同盾回调  2 task查询结果
    private String notifyEvent; // 通知事件。成功 SUCCESS ，失败 FAILURE ，超时 TIMEOUT ， 任务提交成功 CREATE
    private String notifyType; // 通知类型。授权采集是 ACQUIRE
    private String notifyTime; // 通知时间。YYYY-MM-DD HH:MM:SS
    private String code; // 返回码
    private String message; // 提示消息
    private String taskId; // 任务编码
    private String channelType; // 渠道类型
    private String channelCode; // 渠道编码
    private String realName; // 真实姓名
    private String identityCode; // 身份证号码
    private String userMobile; // 手机号码
    private String createdTime; // 任务创建时间
    private String userName;  // 用户名，即⼿手机号码

}
