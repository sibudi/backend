package com.yqg.drools.model;

import lombok.Getter;
import lombok.Setter;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 *
 * 内部黑名单
 *
 ****/

@Getter
@Setter
public class InnerBlackList {

    private int isInYQGBlackListSize;//命中摇钱罐和名单次數
    private Boolean isIMEIInYQGBlackList = false;
}
