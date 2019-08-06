package com.yqg.manage.service.collection.response;

import lombok.Data;

/**
 * Author: tonggen
 * Date: 2018/3/26
 * time: 下午2:08
 */
@Data
public class CollectSmsTemplateResponse {

    /**
     * 发送操作人
     */
    private String sender;

    /**
     * 发送时间
     */
    private String sendTime;

    /**
     * 借款人
     */
    private String loaner;

    /**
     * 接受人
     */
    private String receiver;

    /**
     * 短信标题
     */
    private String smsTitle;

    /**
     * 短信内容
     */
    private String smsContent;

    /**
     * 是否到达
     */
    private Integer isArrived;

}
