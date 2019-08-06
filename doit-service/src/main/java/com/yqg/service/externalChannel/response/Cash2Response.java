package com.yqg.service.externalChannel.response;

import com.yqg.common.annotations.CashCashRequest;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/6
 * @Email zengxiangcai@yishufu.com
 * cashcash主动推送数据时返回的参数
 ****/

@Getter
@Setter
@CashCashRequest
public class Cash2Response {
    private int code;
    private String message;
    private Object data;

    public Cash2Response withCode(int code) {
        this.code = code;
        return this;
    }

    public Cash2Response withMessage(String message) {
        this.message = message;
        return this;
    }

    public Cash2Response withData(Object data) {
        this.data = data;
        return this;
    }
}
