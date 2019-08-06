package com.yqg.service.externalChannel.utils;

import com.yqg.common.utils.JsonUtils;
import com.yqg.service.externalChannel.config.Cash2Config;
import com.yqg.service.externalChannel.config.CheetahConfig;
import com.yqg.service.externalChannel.request.Cash2ApiParam;
import com.yqg.service.externalChannel.request.Cash2ApiParam.EncryptData;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import com.yqg.service.externalChannel.request.CheetahBaseRequest;
import com.yqg.service.externalChannel.request.CheetahOrdFeedRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/6
 * @Email zengxiangcai@yishufu.com
 * 构建调用cashcash 的参数
 ****/

@Slf4j
public class SendDataBuiler {

    public Cash2ApiParam buildParam(Map<String, Object> param,Cash2Config config) {

        log.info("the cashcash send param is: {}", JsonUtils.serialize(param));

        TreeMap<String, Object> sortedData = new TreeMap<>(Comparator.naturalOrder());
        sortedData.putAll(param);
        //通用参数
        sortedData.put("appid", config.getAppId());
        sortedData.put("partner_key", config.getPartnerKey());
        sortedData.put("partner_name", config.getPartnerName());
        sortedData.put("version", config.getVersion());
        long timestamp = System.currentTimeMillis() / 1000L;//当前时间：秒
        sortedData.remove("timestamp");
        sortedData.remove("sign");
        String sign = SecurityUtil.cash2DataSignature(sortedData, timestamp,config);

        sortedData.put("sign", sign);
        sortedData.put("timestamp", timestamp);

        EncryptData encryptData = new EncryptData();

        encryptData.setPartnerKey(config.getPartnerKey());
        encryptData.setEncryptedData(AesUtil
            .encryptData(JsonUtils.serialize(sortedData), config.getAesKey(),
                config.getInitVector()));

        Cash2ApiParam sendToCash2Data = new Cash2ApiParam();

        sendToCash2Data.setData(encryptData);

        return sendToCash2Data;

    }



    public CheetahBaseRequest buildParam(CheetahOrdFeedRequest param, CheetahConfig config, HttpMethod method) {

        log.info("the cheetah send param is: {}", JsonUtils.serialize(param));

        long timestamp = System.currentTimeMillis() / 1000L;//当前时间：秒
        String postBodyJsonString = JsonUtils.serialize(param);
        String sign = null;
        switch (method) {
            case GET:
                sign = SecurityUtil.cheetahEncryptionSign(String.valueOf(timestamp), config);
                break;
            default:
                sign = SecurityUtil.cheetahEncryptionSign(timestamp + "$" + postBodyJsonString, config);
                break;
        }

        CheetahBaseRequest request = new CheetahBaseRequest();
        request.setAccessKey(config.getAccessKey());
        request.setTimestamp(timestamp);
        request.setSign(sign);

        return request;

    }


}
