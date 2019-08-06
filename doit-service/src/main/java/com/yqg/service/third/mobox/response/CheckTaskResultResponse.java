package com.yqg.service.third.mobox.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by wanghuaizhou on 2019/2/26.
 */
@Data
public class CheckTaskResultResponse {

    /**
     *  0  任务创建成功
        212  appName缺失或⽆无效     检查appname 是否正确;
        2404 渠道信息缺失或错误       检查 channel_type和channel_code
        2405 身份证号码缺失或格式错误  检查身份证号码 identity_code ⾮非身份证号码核验
        2406 姓名缺失或格式错误       检查姓名 real_name
        2407 ⼿手机号码缺失或格式错误  检查⼿手机号码 user_mobile
        211  渠道维护中              任务失败、等待渠道恢复通知，请与技术⽀支持沟通;
     **/
    private String code;   // 返回码
    private String message; // 提示信息

    private ResultData data; // 返回创建的任务信息

    @Data
    public static class ResultData {

        @JsonProperty(value = "channel_type")
        private String channelType; // 渠道类型
        @JsonProperty(value = "channel_code")
        private String channelCode; // 渠道编码
        @JsonProperty(value = "real_name")
        private String realName; // 真实姓名
        @JsonProperty(value = "identity_code")
        private String identityCode; // 身份证号码或其他有效证件
        @JsonProperty(value = "user_mobile")
        private String userMobile; // 手机号码
        @JsonProperty(value = "created_time")
        private String createdTime; // 任务创建时间

        @JsonProperty(value = "task_data")
        private  Object taskData; // 数据

        }

    @JsonProperty(value = "task_id")
    private String taskId;  // 任务编码
}