package com.yqg.manage.scheduling.twilioCall;

import com.yqg.common.utils.ServiceJudgeUtils;
import com.yqg.service.third.twilio.TwilioService;
import com.yqg.service.third.twilio.config.TwilioConfig;
import com.yqg.service.third.twilio.request.TwilioWhatsAppRecordRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Author: tonggen
 * Date: 2018/10/16
 * time: 下午12:27
 */
@Component
public class TwilioWhatsAppScheduling {

    private Logger logger = LoggerFactory.getLogger(TwilioWhatsAppScheduling.class);

    @Autowired
    private TwilioService twilioService;

    @Autowired
    private TwilioConfig config;

//    @Scheduled(cron = "0 0 9 * * ?")
    public void whatsAppD_1() throws Exception{
        logger.info("<=============>    send whatsApp D_1 at 9:00 start     <=================>");
        if (ServiceJudgeUtils.judgeRunOrNot(config.getServerAddress())) {
            TwilioWhatsAppRecordRequest request = new TwilioWhatsAppRecordRequest();
            request.setBatchNo(String.valueOf(System.currentTimeMillis() + "D-1"));
            request.setDays(-1);
            request.setReplyContent("这是一个测试");
            twilioService.startWhatsAppTwilio(request);
        }
        logger.info("<=============>    send whatsApp D_1 at 9:00 end<=================> ");
    }


//    @Scheduled(cron = "0 */1 * * * ?")
    public void startUpdateWhatsApp() throws Exception{
        logger.info("<=============>    update whatsapp start     <=================>");
        //判断服务器是否正确
//        if (judgeRunOrNot(config.getServerAddress())) {
            twilioService.startUpdateWhatsApp();
//        }
        logger.info("<=============>    update whatsapp end     <=================>");
    }


}
