package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by Didit Dwianto on 2018/2/2.
 */
@Data
public class NewAndOldersTurnRate {
    private String applyDay;
    private String newApplyDay;
    private String newTurnRate;
    private String oldApplyNum;
    private String oldTurnRate;
    private String applySum;
    private String passSum;
    private String allTurnRate;
}
