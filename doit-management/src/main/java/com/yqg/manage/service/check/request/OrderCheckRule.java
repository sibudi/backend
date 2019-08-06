package com.yqg.manage.service.check.request;

import lombok.Data;

@Data
public class OrderCheckRule {
    //private Integer infoType;

    private Integer ruleCount;

    /**
     * 1 A 2 B, 3 C, 4D, 5 E
     */
    private Integer ruleLevel;

    private Boolean ruleResult;

    private String description;

    private String descriptionInn;

    private Integer type;
}
