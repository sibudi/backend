package com.yqg.manage.service.third.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: tonggen
 * Date: 2018/10/19
 * time: 上午11:15
 */
@NoArgsConstructor
@Data
public class AllBalaceResponse {


    private String funder;
    private int bcabalance;
    private int cimbbalance;
    private int bnibalance;
    private int bribalance;
    private String bcablockedBalance;
    private String cimbblockedBalance;
    private String bniblockedBalance;
    private String briblockedBalance;
}
