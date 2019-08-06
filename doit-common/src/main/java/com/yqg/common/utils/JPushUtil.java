package com.yqg.common.utils;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author  Jacob
 */

public class JPushUtil {

    private static Logger logger= LoggerFactory.getLogger(JPushUtil.class);

    public final static String APP_KEY="3be89951aaeb560eaf55100f";//ios,android????

    public final static String MASTER_SECRET="aeafa18cd0f9b60319dad138";

    public static void main(String[] args) {
        for( int i=0;i<1;i++) {
            jpushAllAlias("4B90D1D2383F4AF6B93AEACB61AEBC8A", "???????????????");
        }
    }

    /**
     * ??????
     * @return
     */
    public PushPayload push(PushPayload payload){
        JPushClient jpushClient = new JPushClient(this.MASTER_SECRET, this.APP_KEY, null, ClientConfig.getInstance());
       // PushPayload payload =  buildPushObject_all_alias_alert(alias,content);
        try {
            PushResult result = jpushClient.sendPush(payload);
            logger.info("Got result - " + result);
        } catch (APIConnectionException e) {
            logger.error("Connection error, should retry later", e);
        } catch (APIRequestException e) {
            logger.error("Should review the error, and fix the request", e);
            logger.info("HTTP Status: " + e.getStatus());
            logger.info("Error Code: " + e.getErrorCode());
            logger.info("Error Message: " + e.getErrorMessage());
            logger.debug("=====================>??????:"+e);
        }
        logger.info("========>??????:"+ JsonUtils.serialize(payload));
        return payload;
    }



    /**
     * ????,????
     * @return
     */
    public static PushPayload buildPushObject_all_all_alert(String content) {
        return PushPayload.alertAll(content);
    }



    /**
     * ????,?????alias
     * @return
     */
    public static PushPayload buildPushObject_all_alias_alert_ios(String alias,String content) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all()).setOptions(Options.newBuilder().setApnsProduction(true).build())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.alert(content))
                .build();

    }


    /**
     * ??uuid????
     * @param alias
     * @param content
     */
    public static void jpushAllAlias(String alias,String content){
        new JPushUtil().push(buildPushObject_all_alias_alert_ios(alias, content));
    }

}
