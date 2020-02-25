package com.yqg.scheduling;

import com.yqg.service.system.service.FcmRemindService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Setiya Budi on 2020/02/14.
 */
@Component
@Slf4j
public class FcmRemindScheduling {

    @Autowired
    private FcmRemindService fcmRemindService;

//    // TO ????? ????8?
    @Scheduled(cron = "0 01 08 * * ?") //
    public void refundBbeforeRemindT0() {
        log.info("refundBbeforeRemindT0 begin");
        this.fcmRemindService.refundBeforeRemindT0();
        log.info("refundBbeforeRemindT0 end");
    }

    // TO T-3 T-1 ????? ????4?
    @Scheduled(cron = "0 01 16 * * ?") // "0 01 16 * * ?"
    public void refundBbeforeRemind() {
        log.info("refundBbeforeRemind begin");
        this.fcmRemindService.refundBeforeRemind();
        log.info("refundBbeforeRemind end");
    }

    //  T+1 T+3 T+5 ???? ????4?
    @Scheduled(cron = "0 01 09 * * ?") //"0 01 08 * * ?"
    public void refundAfterRemind() {
        log.info("refundAfterRemind begin");
        this.fcmRemindService.refundAfterRemind();
        log.info("refundAfterRemind end");
    }
}
