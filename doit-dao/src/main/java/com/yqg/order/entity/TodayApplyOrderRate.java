package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/6/8.
 */
@Data
public class TodayApplyOrderRate {
    private String date;//??
    private String applyingNum;//?????
    private String commitNum;//?????
    private String autoCheckPassNum;//?????
    private String lendNum;//?????
    private String commitRate;//?????
    private String applyAllRate;//?????
    private String applyPassRate;//?????
    private String lendSuccessRate;//?????
}
