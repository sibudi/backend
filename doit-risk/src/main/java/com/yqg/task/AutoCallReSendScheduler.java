package com.yqg.task;

import com.yqg.service.AutoCallErrorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AutoCallReSendScheduler {

    @Autowired
    private AutoCallErrorService autoCallErrorService;

    //ahalim: speedup for demo
    @Scheduled(cron = "0/30 * * * * ?")
    //@Scheduled(cron = "0 7 9-18 * * ?")
    public void reSend() {
        log.info("AutoCallReSendScheduler reSend - Start");
        Long startTime = System.currentTimeMillis();
        try {
            autoCallErrorService.reSend();
        } catch (Exception e) {
            log.info("AutoCallReSendScheduler reSend - error", e);
        } finally {
            log.info("AutoCallReSendScheduler resend - End: {} ms", (System.currentTimeMillis() - startTime));
        }
    }
    
    // Order status is 17, but teleCallResult is disabled
    @Scheduled(cron = "0 57 9,12,15,18 * * ?")
    public void reSendWithException() {
        log.info("AutoCallReSendScheduler reSendWithException - Start");
        Long startTime = System.currentTimeMillis();
        try {
            autoCallErrorService.reSendWithException();
        } catch (Exception e) {
            log.info("AutoCallReSendScheduler reSendWithException - Error", e);
        } finally {
            log.info("AutoCallReSendScheduler reSendWithException - End: {} ms", (System.currentTimeMillis() - startTime));
        }
    }
}
