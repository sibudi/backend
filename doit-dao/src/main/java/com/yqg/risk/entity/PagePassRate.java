package com.yqg.risk.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2019/4/1.
 */
@Data
public class PagePassRate {

    private String createDay;
    private String userSource;
    private String applyRate;
    private String roleRate;
    private String identityRate;
    private String informationRate;
    private String workRate;
    private String contactsRate;
    private String verificateRate;
    private String bankRate;

}
