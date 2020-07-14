package com.yqg.service.externalChannel.utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import com.yqg.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import com.yqg.common.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/6
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Slf4j
public class HttpUtil {

    private final static int socketTimeOut = 600000;//60s
    private final static int connectTimeOut = 200000;//20s

    public static String postJson(String jsonParam, String url, Header... headers) {
        Long startTime = System.currentTimeMillis();
        CloseableHttpResponse response = null;
        String responseContent = null;
        log.info("url: {} ,request: {}", url, jsonParam);
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(socketTimeOut)
                    .setConnectTimeout(connectTimeOut)
                    .build();

            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);

            //设置header
            for (Header h : headers) {
                httpPost.addHeader(h);
            }
            StringEntity entity = new StringEntity(jsonParam, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            response = httpclient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                responseContent = EntityUtils.toString(responseEntity, "UTF-8");
            }
            String responseCode = String.valueOf(response.getStatusLine().getStatusCode());
            if (responseCode.startsWith("2")) {
                //正常返回
                return responseContent;
            } else {
                log.warn("url：{} ，response is：{}, responseCode:{}", url, responseContent,
                        response.getStatusLine().getStatusCode());
                throw new Exception("invalid response status");
            }
        } catch (Exception e) {
            log.info("send data to url: {}, error with param: {}", url, jsonParam);
            log.error("error", e);
        } finally {
            log.info("url: {} , response: {}, cost: {} ms", url, responseContent, (System.currentTimeMillis() - startTime));
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("close response error", e);
                }
            }
        }

        return null;
    }

    public static void main(String[] args) {
        //Header header = new BasicHeader("")
        String result = postJson("{\"JSONFile\":{\"userid\":\"ita@do-it.id\",\"email\":\"tonggen@yishufu.com\"}}","https://apiuat.tandatanganku" +
                ".com/CheckUserMitra" +
                ".html");
        System.err.println(result);
    }

    public static CustomHttpResponse sendMultiPartRequest(String url, Map<String, String> dataParam, Map<String, byte[]> fileParam,
                                                          Map<String,String> headerMap) {
        return sendMultiPartRequest(url, dataParam, fileParam, headerMap, false, null);
    }
    public static CustomHttpResponse sendMultiPartRequest(String url, Map<String, String> dataParam, Map<String, byte[]> fileParam,
                                                          Map<String,String> headerMap, Boolean hideLog) {
        return sendMultiPartRequest(url, dataParam, fileParam, headerMap, hideLog, null);
    }
    public static CustomHttpResponse sendMultiPartRequest(String url, Map<String, String> dataParam, Map<String, byte[]> fileParam,
                                                          Map<String,String> headerMap, String orderNo) {
        return sendMultiPartRequest(url, dataParam, fileParam, headerMap, false, orderNo);
    }
    public static CustomHttpResponse sendMultiPartRequest(String url, Map<String, String> dataParam, Map<String, byte[]> fileParam,
                                                          Map<String,String> headerMap, Boolean hideLog, String orderNo) {
        Long startTime = System.currentTimeMillis();
        log.info("start request url: {} with param: {}", url, JsonUtils.serialize(dataParam));
        CloseableHttpResponse response = null;
        CustomHttpResponse customHttpResponse = new CustomHttpResponse();
        try (CloseableHttpClient httpClient = HttpClients.createDefault();) {
            HttpPost httpPost = new HttpPost(url);
            // 设置连接超时时间
            RequestConfig requestConfig =
                    RequestConfig.custom().setConnectTimeout(20000).setConnectionRequestTimeout(connectTimeOut).setSocketTimeout(socketTimeOut).build();
            httpPost.setConfig(requestConfig);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(Consts.UTF_8);
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            String filename = "";
            if (fileParam != null) {
                for (Map.Entry<String, byte[]> fileEntry : fileParam.entrySet()) {
                    filename = StringUtils.isEmpty(orderNo) ? fileEntry.getKey() : orderNo;
                    builder.addBinaryBody(fileEntry.getKey(), fileEntry.getValue(), ContentType.DEFAULT_BINARY, filename);
//                    builder.addBinaryBody(fileEntry.getKey(),fileEntry.getValue());
//                    FileBody fileBody = new FileBody(fileEntry.getValue(), ContentType.DEFAULT_BINARY);
//                    builder.addPart(fileEntry.getKey(), fileBody);
                }

            }

            if (dataParam != null) {
                for (Map.Entry<String, String> textEntry : dataParam.entrySet()) {
                    builder.addTextBody(textEntry.getKey(), textEntry.getValue(), ContentType.TEXT_PLAIN);
                }
            }

            if (headerMap != null) {
                for (Map.Entry<String, String> header : headerMap.entrySet()) {
                    httpPost.addHeader(header.getKey(), header.getValue());
                }
            }
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            // 执行提交
            response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            customHttpResponse.setStatus(response.getStatusLine().getStatusCode());
            if (responseEntity != null) {
                String responseContent = EntityUtils.toString(responseEntity, "UTF-8");
                customHttpResponse.setContent(responseContent);
            }
            if (Boolean.TRUE.equals(hideLog)) {
                log.info("the response of url: {} is: <hidden>", url);
            }
            else {
                log.info("the response of url: {} is: {}", url, JsonUtils.serialize(customHttpResponse));
            }
            return customHttpResponse;
        } catch (Exception e) {
            log.error("send data to url: " + url + ", error with param: " + JsonUtils.serialize(dataParam), e);
            return customHttpResponse.withErrorMsg(e.getMessage());
        } finally {
            log.info("url: {}, cost: {} ms", url, (System.currentTimeMillis() - startTime));
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {

                }
            }
        }
    }


    /**
     * 获取POST请求中Body参数
     *
     * @param request
     * @return 字符串
     */
    public static String getParm(HttpServletRequest request) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String line = null;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sb.toString();

    }
}
