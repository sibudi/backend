package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2019/1/21.
 */
@Data
public class OrderCountWithOldUser {

    private String date;
    private String applyNum;
    private String commitNum;
    private String lendNum;
    private String lendSuccessNum;

}
