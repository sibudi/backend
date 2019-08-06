package com.yqg.drools.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * 通讯录信息
 */

@Getter
@Setter
public class ContactInfo {

//    private List<String> phoneNumbers;//通讯录手机号列表
//
//    private List<String> names;//通讯录姓名列表


    //private long overdueDays30Times;//用户手机号逾期30天次数
    //private int overdueDays15Times;

    //private int contactOverdueDays15Times;

    private Long phoneCount;//通讯录手机号码数
    private Long sensitiveWordCount;//敏感词个数
    private Long interrelatedWordCount;//同业词个数
    private Long relativeWordCount;//亲属词个数
    private boolean relativeWordRejectedByProbability;


    private Boolean firstLinkManNotIn; //通讯录无第一联系人
    private Boolean secondLinkManNotIn;//通讯录无第二联系人

    private String firstLinkManNumber;//第一联系人号码

    private String secondLinkManNumber;//第二联系人号码




}
