package com.yqg.manage.service.check.response;

import lombok.Data;

import java.util.Date;

/**
 * Author: tonggen
 * Date: 2018/6/13
 * time: 下午3:12
 */
@Data
public class ManSecondRecordResponse {

    private Date createTime;

    private String userName;

    private Integer operatorType;
}
