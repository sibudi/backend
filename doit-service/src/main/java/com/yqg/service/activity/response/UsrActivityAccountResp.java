package com.yqg.service.activity.response;

import lombok.Data;

/**
 * Features:
 * Created by huwei on 18.8.17.
 */
@Data
public class UsrActivityAccountResp {
    private Integer type;//1-银行卡  2-gopay
    private String channel;
    private String number;
    private String cardName;
    private Integer status;//银行卡绑卡状态
}
