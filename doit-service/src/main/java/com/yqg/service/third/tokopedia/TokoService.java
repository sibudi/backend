package com.yqg.service.third.tokopedia;

import com.alibaba.fastjson.JSONObject;
import com.yqg.common.utils.HttpTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Created by Didit Dwianto on 2017/11/27.
 */
@Service
@Slf4j
public class TokoService {
//http://47.74.156.133:8080/spider/toko/getDetail
//    @Value("${toko.url}")
//    private  String url;
    int connectTimeout=30000;
    int readTimeout=10000;
//    public String getInfo(String email, String pwd)
//            throws Exception{
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("email",email);
//        jsonObject.put("pwd",pwd);
//        String result =HttpTools.post(url,null,jsonObject.toString(),connectTimeout,readTimeout);
//        return result;
//    }
    public String getInfo(String url,String email, String pwd,String batchId)
            throws Exception{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email",email);
        jsonObject.put("pwd",pwd);
        if (null!=batchId&&!StringUtils.isEmpty(batchId)) {
            jsonObject.put("batchId", batchId);
        }
        String result =HttpTools.post(url,null,jsonObject.toString(),connectTimeout,readTimeout);
        return result;
    }

}
