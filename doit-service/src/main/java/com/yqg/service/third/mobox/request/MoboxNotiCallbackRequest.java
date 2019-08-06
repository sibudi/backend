package com.yqg.service.third.mobox.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by wanghuaizhou on 2019/2/27.
 */
@Data
public class MoboxNotiCallbackRequest {

    /**
     *  成功 SUCCESS ，失败 FAILURE ，超时 TIMEOUT ， 任务提交成功 CREATE
     * */
    @JsonProperty(value = "notify_event")
    private String notifyEvent;   // 通知事件

    @JsonProperty(value = "notify_type")
    private String notifyType;   // 通知类型  授权采集是 ACQUIRE

    @JsonProperty(value = "notify_time")
    private String notifyTime;   // 通知时间 格式:YYYY-MM-DD HH:MM:SS

    private String sign;   // 暂不不⽀支持 验证签名

    @JsonProperty(value = "passback_params")
    private String passbackParams;   // 透传参数，创建任务时传⼊入



    @JsonProperty(value = "notify_data")
    private NotifyData notifyData; // 返回创建的任务信息

    @Data
    public static class NotifyData {

        /**
         *  0     事件: SUCCESS  采集任务成功
            137   事件: CREATE   任务提交成功
            211   事件: FAILURE  创建任务阶段:渠道维护中，任务失败;
            1     事件: FAILURE  用户授权/采集阶段:未知错误，反馈错误给技术⽀支持
            132   事件: FAILURE  用户授权阶段:⽤用户超时等，授权失败
            170   事件: FAILURE  用户授权阶段:加载登录链接中⽤用户取消认证(如加载时⻓长太⻓长)
            171   事件: FAILURE  用户授权阶段:账号密码输⼊入阶段，⽤用户取消认证(如未输⼊入账密)
            172   事件: FAILURE  用户授权阶段:官⽹网认证中⽤用户取消认证(如⽤用户点击完login后，未能顺利利退出)
            173   事件: FAILURE  用户授权阶段:加载登录成功⻚页中⽤用户取消认证(如加载时⻓长太⻓长)
            200~207 事件: FAILURE  ⽤用户授权阶段:⽬目标服务不不可⽤用
         * */
        private String code; // 返回码

        private String message; // 返回码

        @JsonProperty(value = "task_id")
        private String taskId;  // 任务编码

        private DetailData data; // 返回任务信息

        @Data
        public static class DetailData {

            @JsonProperty(value = "channel_type")
            private String channelType; // 渠道类型

            @JsonProperty(value = "channel_code")
            private String channelCode; // 渠道类型

            @JsonProperty(value = "channel_src")
            private String channelSrc; // 暂返回为NULL 渠道数据源。如: Telkomsel

            @JsonProperty(value = "channel_attr")
            private String channelAttr; // 暂返回为NULL 渠道属性。如:印尼

            @JsonProperty(value = "real_name")
            private String realName; // 真实姓名。如:张三

            @JsonProperty(value = "identity_code")
            private String identityCode; // 身份证号码

            @JsonProperty(value = "user_mobile")
            private String userMobile; // 手机号码

            @JsonProperty(value = "created_time")
            private String createdTime; // 任务创建时间

            @JsonProperty(value = "user_name")
            private String userName; // ⽤用户名，即⼿手机号码
        }

    }



}
