package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/8/20.
 */
@Data
public class SouceInDateNum {

    private String expireWeek;
    private String source;
    private String expireNum;
    private String D1overDueRate;
    private String D7overDueRate;
    private String D15overDueRate;
}
