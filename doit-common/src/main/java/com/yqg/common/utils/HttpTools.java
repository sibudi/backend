package com.yqg.common.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Didit Dwianto on 2017/10/25.
 */
@Slf4j
public class HttpTools {

    /*****???? *****/
    public static final int TIMEOUT = 10000;


    public static String post(String url, Map<String, String> headers, Map<String, String> params, int connectTimeout, int readTimeout) {
        CloseableHttpClient httpclient = null;
        HttpPost httppost = new HttpPost(url);
        try {
            httpclient = HttpClients.createDefault();
            if (params == null || params.size() == 0) {
                return "";
            }

            for (String key : headers.keySet()) {
                httppost.setHeader(key, headers.get(key));
            }

            // ??????
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            for (String key : params.keySet()) {
                formparams.add(new BasicNameValuePair(key, params.get(key)));
            }
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httppost.setEntity(uefEntity);


            // ???????????
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(readTimeout)
                    .setConnectTimeout(connectTimeout)
                    .setStaleConnectionCheckEnabled(true)
                    .build();
            httppost.setConfig(requestConfig);
            HttpResponse response = null;
            try {
                response = httpclient.execute(httppost);
            } catch (Exception e) {
                for (int i = 0; i < 2; i++) {
                    try {
                        response = httpclient.execute(httppost);
                        break;
                    } catch (Exception e2) {
                        if (i == 1) {
                            throw e2;
                        } else {
                            continue;
                        }
                    }
                }
            }

            try {
                if (response == null) {
                    return "";
                } else {
                    if (response.getStatusLine().getStatusCode() >= 400) {
                    }
                }
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toString(entity, "UTF-8");
                }
            } finally {

            }
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httppost != null) {
                httppost.releaseConnection();
            }
        }
        return "";
    }

    public static String post(String url, Map<String, String> headers, String jsonParam, int connectTimeout, int readTimeout) {
        CloseableHttpClient httpclient = null;
        HttpPost httppost = new HttpPost(url);
        try {
//            httpclient = HttpClients.createDefault();
            httpclient = new DefaultHttpClient();
            HttpClientParams.setCookiePolicy(httpclient.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);

            StringEntity postentity = new StringEntity(JSONObject.parse(jsonParam).toString(), "utf-8");
            postentity.setContentType("application/json");
            httppost.setEntity(postentity);

            for (Map.Entry<String, String>  header : headers.entrySet()) {
                httppost.setHeader(header.getKey(), header.getValue());
            }
            if (null != headers && headers.get("Accept") == null){
                httppost.setHeader("Accept", "application/json");
            }
            // ???????????
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(readTimeout)
                    .setConnectTimeout(connectTimeout)
                    .setStaleConnectionCheckEnabled(true)
                    .build();
            httppost.setConfig(requestConfig);
            HttpResponse response = null;
            try {
                response = httpclient.execute(httppost);
            } catch (Exception e) {
                log.error("invoke remote server exception url: " + url, e);
            }

            try {
                if (response == null) {
                    return "";
                } else {
                    if (response.getStatusLine().getStatusCode() >= 400) {
                    }
                }
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toString(entity, "UTF-8");
                }
            } finally {

            }
        } catch (Exception e) {
            log.error("invoke exception, url: " + url, e);
        } finally {
            if (httppost != null) {
                httppost.releaseConnection();
            }
        }
        return "";
    }

}
