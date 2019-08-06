package com.yqg.manage.service.third;

import com.alibaba.druid.support.json.JSONUtils;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.utils.StringUtils;
import com.yqg.manage.service.third.request.BalaceActionRequest;
import com.yqg.manage.service.third.request.OfflineRequest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Author: tonggen
 * Date: 2018/10/19
 * time: 上午10:52
 */
@Component
@Slf4j
public class PayOurService {


    public Object balance(String managerUrl, String token) {

        String result = sendGet(managerUrl, "", token);
        if (StringUtils.isEmpty(result)) {
            return null;
        }
        return JSONUtils.parse(result);
    }

    public Object offline(String managerUrl, OfflineRequest request) {

        String result = sendGet(managerUrl, request.toString(), request.getToken());
        if (StringUtils.isEmpty(result)) {
            return null;
        }
        return JSONUtils.parse(result);
    }

    @Autowired
    private OkHttpClient httpClient;

    public Object action (String managerUrl, BalaceActionRequest request) throws ServiceExceptionSpec{

        RequestBody requestBody = new FormBody.Builder()
                .add("funder", request.getFunder())
                .add("channel", request.getChannel())
                .add("offlineType",request.getOfflineType())
                .add("amount",String.valueOf(request.getAmount()))
                .add("operator", request.getOperator())
                .add("comment", request.getComment())
                .build();

        Request postRequest = new Request.Builder()
                .url(managerUrl)
                .post(requestBody)
                .header("X-AUTH-TOKEN", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwYXltZW50IiwiZXhwIjoxODQ4ODkwMTM2fQ" +
                        ".LfH_9hZmU5XgBcvZOfB9Jl2Z2_FsAkg_y2Hzt2dePLBOlZCvCmFrtqzYl7xidshQp3uhR62AR0Td_KrImCEEjA")
//                .addHeader("Content-Type","application/json")
//                .addHeader("Accept","application/json")
                .build();

        try {
             Response respone = httpClient.newCall(postRequest).execute();
             if(!respone.isSuccessful()){
                 log.info("Top up or withdraw failed!!!!, {}",respone.body().toString());
             }
        } catch (Exception e) {
            log.error("send action error! ", e);
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_ADD_ITEM_ERROR);
        }
        return null;
    }

    /**
     110      * 向指定URL发送GET方法的请求
     111      *
     112      * @param url
     113      *            发送请求的URL
     114      * @param param
     115      *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     116      * @return URL 所代表远程资源的响应结果
     117      */
     public static String sendGet(String url, String param, String token) {
         String result = "";
         BufferedReader in = null;
         try {
                 String urlNameString = url + "?" + param;
                 URL realUrl = new URL(urlNameString);
                 // 打开和URL之间的连接
                 URLConnection connection = realUrl.openConnection();
                 // 设置通用的请求属性
                 connection.setRequestProperty("accept", "*/*");
                 connection.setRequestProperty("connection", "Keep-Alive");
                 connection.setRequestProperty("user-agent",
                                 "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                 if (StringUtils.isNotEmpty(token)) {
                     connection.setRequestProperty("X-AUTH-TOKEN", token);
                 }
                 // 建立实际的连接
                 connection.connect();
                 // 获取所有响应头字段
                 Map<String, List<String>> map = connection.getHeaderFields();
                 // 遍历所有的响应头字段
//                 for (String key : map.keySet()) {
//                         System.out.println(key + "--->" + map.get(key));
//                     }
                 // 定义 BufferedReader输入流来读取URL的响应
                 in = new BufferedReader(new InputStreamReader(
                                 connection.getInputStream()));
                 String line;
                 while ((line = in.readLine()) != null) {
                         result += line;
                     }
             } catch (Exception e) {
                 System.out.println("发送GET请求出现异常！" + e);
                 e.printStackTrace();
             }
         // 使用finally块来关闭输入流
         finally {
                 try {
                         if (in != null) {
                                 in.close();
                             }
                     } catch (Exception e2) {
                         e2.printStackTrace();
                     }
             }
         return result;
     }
}
