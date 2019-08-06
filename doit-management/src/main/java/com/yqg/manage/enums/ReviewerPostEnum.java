package com.yqg.manage.enums;

import lombok.Getter;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 * ??????
 ****/

@Getter
public enum  ReviewerPostEnum {

    JUNIOR_REVIEWER("初审岗"),
    SENIOR_REVIEWER("复审");

    ReviewerPostEnum (String desc){
        this.desc = desc;
    }
    private String  desc;
}
