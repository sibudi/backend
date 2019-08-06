package com.yqg.manage.service.third.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: tonggen
 * Date: 2019/4/17
 * time: 3:41 PM
 */
@NoArgsConstructor
@Data
public class BalaceActionRequest {


    private String funder;

    private String channel;

    private String offlineType;

    private double amount;

    private String operator;

    private String comment;


}
