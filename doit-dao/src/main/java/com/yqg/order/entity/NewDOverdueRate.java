package com.yqg.order.entity;

import lombok.Data;

/**
 * Created by luhong on 2018/3/8.
 */
@Data
public class NewDOverdueRate {
    private String arrivalDay;
    private String newArrivalNum;
    private String newD1OverdueRate;
    private String newD7OverdueRate;
    private String newD15OverdueRate;
    private String newD30OverdueRate;
    private String oldArrivalNum;
    private String oldD1OverdueRate;
    private String oldD7OverdueRate;
    private String oldD15OverdueRate;
    private String oldD30OverdueRate;
    private String allArrivalNum;
    private String allD1OverdueRate;
    private String allD7OverdueRate;
    private String allD15OverdueRate;
    private String allD30OverdueRate;


}
