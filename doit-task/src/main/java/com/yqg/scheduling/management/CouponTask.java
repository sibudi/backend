package com.yqg.scheduling.management;

import com.alibaba.druid.support.json.JSONUtils;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.redis.RedisClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 优惠券发放以及更新等任务
 * Author: tonggen
 * Date: 2019/5/10
 * time: 1:58 PM
 */
@Service
@Slf4j
public class CouponTask {

    @Value("${manager.path}")
    private String url;

    @Autowired
    private OkHttpClient httpClient;

    @Autowired
    private RedisClient redisClient;

    // 每天定时发送不同类型的优惠券
    //@Scheduled(cron = "0 0 3 * * ?")
    public void sendCouponsAuto() {
        String managerUrl = url;
        managerUrl += "sendCouponsAuto/managerTask?request=";
        if (judgeSwitchOpen(RedisContants.COUPON_D_15_SWITCH)) {
            sendGetRequest(managerUrl + "COUPON_D_15" );
        }
        if (judgeSwitchOpen(RedisContants.COUPON_D_10_SWITCH)) {
            sendGetRequest(managerUrl + "COUPON_D_10" );
        }
        if (judgeSwitchOpen(RedisContants.COUPON_D_5_SWITCH)) {
            sendGetRequest(managerUrl + "COUPON_D_5" );
        }
    }

    // 印尼时间上午10点，下午2点（未还款）。
    //@Scheduled(cron = "0 0 10,14 * * ?")
    public void sendTwilioCallCouponAuto() {
        String managerUrl = url;
        managerUrl += "sendTwilioCallCouponAuto/managerTask?request=";
        if (judgeSwitchOpen(RedisContants.COUPON_D_15_SWITCH)) {
            sendGetRequest(managerUrl + "COUPON_D_15" );
        }
        if (judgeSwitchOpen(RedisContants.COUPON_D_10_SWITCH)) {
            sendGetRequest(managerUrl + "COUPON_D_10" );
        }
        if (judgeSwitchOpen(RedisContants.COUPON_D_5_SWITCH)) {
            sendGetRequest(managerUrl + "COUPON_D_5" );
        }
    }

    // 每天更新优惠券状态
    @Scheduled(cron = "0 50 0 * * ?")
    public void updateCouponStatus() {
        String managerUrl = url;
        managerUrl += "updateCouponStatus/managerTask";
        sendGetRequest(managerUrl);
    }

    private boolean judgeSwitchOpen(String key) {

        return "true".equals(redisClient.get(key));
    }
    /**
     * send request.
     * @param manageUrl
     * @return
     */
    private void sendGetRequest(String manageUrl){

        log.info("start couponTask , url is  {}", manageUrl);
        Request request = new Request.Builder()
                .url(manageUrl)
                .get()
                .build();
        try {
            //发送不同的优惠券，每次都调用
            Response response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                log.error("sendCouponsAuto  not success, url is {}", manageUrl);
            } else {
                log.info("result data is {}", JSONUtils.parse(response.body().string()));
            }
        } catch (IOException e) {
            log.error("sendCouponsAuto is error", e);
        }
    }
}
