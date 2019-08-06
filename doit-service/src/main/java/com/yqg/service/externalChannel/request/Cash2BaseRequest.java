package com.yqg.service.externalChannel.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/6
 * @Email zengxiangcai@yishufu.com
 * cashcash 参数基类
 ****/

@Getter
@Setter
public class Cash2BaseRequest {

    private int version;

    @JsonProperty(value = "partner_key")
    private String partnerKey;

    @JsonProperty(value = "partner_name")
    private String partnerName;

    private String appid;

    private int timestamp;

    private String sign;

}
