package com.yqg.common.enums.user;

import lombok.Getter;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/7
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Getter
public enum UserSourceEnum {

    CashCash("28"),
    Cheetah("68");
    UserSourceEnum(String code){
        this.code=code;
    }
    private String code;
}
