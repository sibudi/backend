package com.yqg.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.MDC;

@Slf4j
public class LogUtils {
    public static void addMDCRequestId(String requestId) {
        MDC.put("X-Request-Id", requestId);
    }

    public static void removeMDCRequestId() {
        MDC.remove("X-Request-Id");
    }

    public static void main(String[] args) {

        RequestBody requestBody = new FormBody.Builder()
                .add("from","abc")   //
                .add("to","123")
//                .add("text","this is a test")
                .add("language","id-ID")
                .add("audioFileUrl","http://h5.do-it.id/Test.mp3")
                .build();
        log.info("request body: {}",requestBody.toString());

        Request request = new Request.Builder()
                .url("https://api.infobip.com")
                .post(requestBody)
                .header("Authorization", "Basic "+ "afs")
                .addHeader("Content-Type","application/json")
                .addHeader("Accept","application/json")
                .build();

        System.err.println(request);
    }
}
