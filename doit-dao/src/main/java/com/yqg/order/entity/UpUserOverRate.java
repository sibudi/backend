package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/9/27.
 */
@Data
public class UpUserOverRate {

    private String batchId;
    private String batchNum;
    private String applyingNum;
    private String commitNum;
    private String checkPassNum;
    private String lendSuccessNum;
    private String batchApplyingRate;
    private String commitRate;

}
