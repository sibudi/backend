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

    //印尼时间每天下午3天重新外呼一次
    @Scheduled(cron = "0 7 9-21 * * ?")
    public void reSend() {
        log.info("start auto call resend...");
        Long startTime = System.currentTimeMillis();
        try {
            autoCallErrorService.reSend();
        } catch (Exception e) {
            log.info("resend error", e);
        } finally {
            log.info("the cost of resend: {} ms", (System.currentTimeMillis() - startTime));
        }
    }
    //主要检查状态在17，但是已经被disabled了的单
    @Scheduled(cron = "0 57 9,12,15,18,20 * * ?")
    public void reSendWithException() {
        log.info("start auto call resend...");
        Long startTime = System.currentTimeMillis();
        try {
            autoCallErrorService.reSendWithException();
        } catch (Exception e) {
            log.info("reSendWithException error", e);
        } finally {
            log.info("the cost of resend: {} ms", (System.currentTimeMillis() - startTime));
        }
    }
}
