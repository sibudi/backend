package com.yqg.scheduling.management;


import com.alibaba.fastjson.JSONObject;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.HttpTools;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class TwilioCallTask {

    @Value("${manager.path}")
    private String url;

    @Autowired
    private OkHttpClient client;
    @Autowired
    private RedisClient redisClient;

    // @Scheduled(cron = "0 0 8 * * ?")
    public void call8D_1() {
        if (judgeSwitchOpen(RedisContants.TWILIO_CALL_TOTAL_SWITCH)) {
            String managerUrl = url;
            managerUrl += "call8D_1/managerTask";
            JSONObject jsonObject = new JSONObject();
            HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
        }
    }

    // @Scheduled(cron = "0 0 8 * * ?")
    public void call8D_2() {
        if (judgeSwitchOpen(RedisContants.TWILIO_CALL_TOTAL_SWITCH)) {

            String managerUrl = url;
            managerUrl += "call8D_2/managerTask";
            JSONObject jsonObject = new JSONObject();
            HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
        }
    }

    // @Scheduled(cron = "0 0 11 * * ?")
    public void call11D_1() {
        if (judgeSwitchOpen(RedisContants.TWILIO_CALL_TOTAL_SWITCH)) {

            String managerUrl = url;
            managerUrl += "call11D_1/managerTask";
            JSONObject jsonObject = new JSONObject();
            HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
        }
    }

    // @Scheduled(cron = "0 0 11 * * ?")
    public void call11D_2() {
        if (judgeSwitchOpen(RedisContants.TWILIO_CALL_TOTAL_SWITCH)) {

            String managerUrl = url;
            managerUrl += "call11D_2/managerTask";
            JSONObject jsonObject = new JSONObject();
            HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
        }
    }

    // @Scheduled(cron = "0 0 11 * * ?")
    public void call11D_3() {
        if (judgeSwitchOpen(RedisContants.TWILIO_CALL_TOTAL_SWITCH)) {

            String managerUrl = url;
            managerUrl += "call11D_3/managerTask";
            JSONObject jsonObject = new JSONObject();
            HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
        }
    }

    // @Scheduled(cron = "0 0 15 * * ?")
    public void call15D_1() {
        if (judgeSwitchOpen(RedisContants.TWILIO_CALL_TOTAL_SWITCH)) {

            String managerUrl = url;
            managerUrl += "call15D_1/managerTask";
            JSONObject jsonObject = new JSONObject();
            HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
        }
    }

    // @Scheduled(cron = "0 0 15 * * ?")
    public void call15D_2() {
        if (judgeSwitchOpen(RedisContants.TWILIO_CALL_TOTAL_SWITCH)) {

            String managerUrl = url;
            managerUrl += "call15D_2/managerTask";
            JSONObject jsonObject = new JSONObject();
            HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
        }
    }


    /**
     *
     update twilio voice message.
     */
    // @Scheduled(cron = "0 */1 * * * ?")
    public void startUpdateCallResult0() {
        String managerUrl = url;
        managerUrl += "startUpdateCallResult/managerTask";
        sendGetRequest(managerUrl + "?num=0");
    }

    // @Scheduled(cron = "0 */1 * * * ?")
    public void startUpdateCallResult1() {
        String managerUrl = url;
        managerUrl += "startUpdateCallResult/managerTask";
        sendGetRequest(managerUrl + "?num=1");
    }
    // @Scheduled(cron = "0 */1 * * * ?")
    public void startUpdateCallResult2() {
        String managerUrl = url;
        managerUrl += "startUpdateCallResult/managerTask";
        sendGetRequest(managerUrl + "?num=2");
    }

    /**
     *
     update infobip voice message.
     */
    // @Scheduled(cron = "0 0/2 8-18 * * ?")
    public void getInfobipCollectionReport0() {
        if (judgeSwitchOpen(RedisContants.TWILIO_CALL_TOTAL_SWITCH)) {
            String managerUrl = url;
            managerUrl += "getInfobipCollectionReport/managerTask";
            sendGetRequest(managerUrl + "?num=0");
        }
    }

    // @Scheduled(cron = "0 0/2 8-18 * * ?")
    public void getInfobipCollectionReport1() {
        if (judgeSwitchOpen(RedisContants.TWILIO_CALL_TOTAL_SWITCH)) {
            String managerUrl = url;
            managerUrl += "getInfobipCollectionReport/managerTask";
            sendGetRequest(managerUrl + "?num=1");
        }
    }
    // @Scheduled(cron = "0 0/2 8-18 * * ?")
    public void sgetInfobipCollectionReport2() {
        if (judgeSwitchOpen(RedisContants.TWILIO_CALL_TOTAL_SWITCH)) {
            String managerUrl = url;
            managerUrl += "getInfobipCollectionReport/managerTask";
            sendGetRequest(managerUrl + "?num=2");
        }
    }

    /**
     * 沉默用户
     */
    // @Scheduled(cron = "0 0 12 * * ?")
    public void infobipSilient() {
        if (judgeSwitchOpen(RedisContants.INFOBIP_CALL_TOTAL_SWITCH + ":silientVoiceCall")) {
            String managerUrl = url;
            managerUrl += "infobipSilient/managerTask";
            JSONObject jsonObject = new JSONObject();
            HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
        }
    }

    /**
     * 申请为提交
     */
    // @Scheduled(cron = "0 0 12 * * ?")
    public void infobipNotSumbit() {
        if (judgeSwitchOpen(RedisContants.INFOBIP_CALL_TOTAL_SWITCH + ":notSubmitVoiceCall")) {
            String managerUrl = url;
            managerUrl += "infobipNotSumbit/managerTask";
            JSONObject jsonObject = new JSONObject();
            HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
        }
    }

    /**
     * 降额未确认
     */
    @Scheduled(cron = "0 0 12 * * ?")
    public void infobipReduce() {
        if (judgeSwitchOpen(RedisContants.INFOBIP_CALL_TOTAL_SWITCH + ":reduceVoiceCall")) {
            String managerUrl = url;
            managerUrl += "infobipReduce/managerTask";
            JSONObject jsonObject = new JSONObject();
            HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
        }
    }

    /**
     * send request.
     * @param manageUrl
     * @return
     */
    private void sendGetRequest(String manageUrl){

        log.info("start twilioTask , url is  {}", manageUrl);
        Request request = new Request.Builder()
                .url(manageUrl)
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                log.error("twilioTask  not success, url is {}", manageUrl);
            }
        } catch (IOException e) {
            log.error("twilioTask is error", e);
        }
    }
    private boolean judgeSwitchOpen(String key) {

        return "true".equals(redisClient.get(key));
    }
}
