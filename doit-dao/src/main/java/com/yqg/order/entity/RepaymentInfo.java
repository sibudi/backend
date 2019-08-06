package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by Didit Dwianto on 2018/2/1.
 */
@Data
public class RepaymentInfo {
    private String step;
    private String resolvingNotOverdue;
    private String resolvedNotOverdue;
    private String resolvingOverdue;
    private String tesolvedOverdue;
    private String total;
}
