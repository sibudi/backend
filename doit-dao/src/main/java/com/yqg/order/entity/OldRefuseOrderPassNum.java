package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by Didit Dwianto on 2018/2/2.
 */
@Data
public class OldRefuseOrderPassNum {
    private String date;
    private String commitRate;
    private String autoCheckPassRate;
    private String firstCheckPassRate;
    private String secondCheckPassRate;
    private String applyPassRate;
    private String lendSuccessRate;
    private String applyAllRate;
}
