package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/6/8.
 */
@Data
public class ApplyAndSubmitRate {

    private String createDay;
    private String userSource;
    private String registerNum;
    private String applyNum;
    private String submitNum;
    private String lengdingNum;
    private String applyRate;
    private String submitRate;
    private String lendingRate;
}
