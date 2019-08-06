package com.yqg.service.system.response;

import lombok.Data;

/**
 * Author: tonggen
 * Date: 2018/11/1
 * time: 6:04 PM
 */
@Data
public class CollectorInfoResponse {

    /**
     * 是否包含催收人员
     */
    private Boolean hasCollectorOrNot;
    /**
     * 催收人员phone
     */
    private String collectionPhone;
    /**
     * 催收人员WA
     */
    private String collectionWa;
    /**
     * 催收人员名称
     */
    private String collectionName;
}
