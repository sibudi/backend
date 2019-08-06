package com.yqg.service.externalChannel.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/6
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Component
@Getter
@Setter
public class Cash2Config {

    @Value("${third.cashcash.aesKey}")
    private String aesKey = "27oovmz7gueaud8l";//aes加密秘钥

    @Value("${third.cashcash.initVector}")
    private String initVector = "abc123rty456nji7"; //aes加密初始向量(cbc模式)

    @Value("${third.cashcash.md5Token}")
    private String token = "KY8GZ-42A0A-Q8OXB-HOJJY-4U8UM-KLTGM"; //md5签名token

    @Value("${third.cashcash.appId}")
    private String appId = "10000";

    @Value("${third.cashcash.version}")
    private String version = "1";

    @Value("${third.cashcash.partnerName}")
    private String partnerName="UangUang";

    @Value("${third.cashcash.partnerKey}")
    private String partnerKey="8uv4g8z33rcg6mh5ejziq5b8h";

    @Value("${third.cashcash.ipWhiteList}")
    private String ipWhiteList;


}
