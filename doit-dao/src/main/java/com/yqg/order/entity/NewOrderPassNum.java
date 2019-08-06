package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by Didit Dwianto on 2018/2/2.
 */
@Data
public class NewOrderPassNum {
    private String date;
    private String applyingNum;
    private String commitNum;
    private String autoCheckPassNum;
    private String firstCheckPassNum;
    private String secondCheckPassNum;
    private String lendNum;
    private String lendSuccessNum;
    private String autoCheckNoPassNum;
    private String firstCheckNoPassNum;
    private String firstCheckhandNoPassNum;
    private String secondCheckNoPassNum;
}
