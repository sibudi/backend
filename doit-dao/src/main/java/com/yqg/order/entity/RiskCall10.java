package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/12/24.
 */
@Data
public class RiskCall10 {

    private String calldate;
    private String twilioEmerCallNum;
    private String TotalEffectRate;
    private String maybeEffect_busyRate;
    private String maybeEffect_NoAnswerRate;
    private String effectRate;
    private String InvalidRate;

}
