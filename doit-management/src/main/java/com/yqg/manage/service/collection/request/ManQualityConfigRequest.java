package com.yqg.manage.service.collection.request;

import lombok.Data;

/**
 * Author: tonggen
 * Date: 2018/11/22
 * time: 4:46 PM
 */
@Data
public class ManQualityConfigRequest {

    private Integer id;

    private String title;

    private String titleInn;

    private String fineMoney;

    private Integer pageNo = 1;

    private Integer pageSize = 10;

    private Integer type = 0; //0 备注质检 1 语音质检
}
