package com.yqg.manage.service.check.request;

import lombok.Data;

/**
 * @author alan
 */
@Data
public class CheckRequest {
    private String remark;

    private String orderNo;

    private String sessionId;

    private Integer pass;

    /**
     * 初审提交类型（ManOrderCheckRemarkEnum)
     */
    private Integer type;

    /**
     * 保存审核时长
     */
    private String burningTime;

    /**
     * true 初审直接拒绝
     */
    private Boolean unPass;
}
