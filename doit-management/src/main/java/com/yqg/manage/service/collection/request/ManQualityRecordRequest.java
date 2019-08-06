package com.yqg.manage.service.collection.request;

import lombok.Data;

/**
 * Author: tonggen
 * Date: 2018/11/22
 * time: 4:46 PM
 */
@Data
public class ManQualityRecordRequest {

    private String orderNo;

    private Integer checkTag;

    private String remark;

    private Integer outsourceId;//催收人员
    private Integer checkerId;//质检人员

    private String startTime;//开始时间
    private String endTime;//结束时间
    private String userUuid;

    private Integer type = 0; //0 备注质检 1语音质检

    private Integer postId;//用户催收阶段
}
