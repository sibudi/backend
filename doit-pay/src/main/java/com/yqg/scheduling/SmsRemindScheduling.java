package com.yqg.scheduling;

import com.yqg.service.RepairOrderService;
import com.yqg.service.system.service.SmsRemindService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Didit Dwianto on 2018/1/12.
 */
@Component
@Slf4j
public class SmsRemindScheduling {

    @Autowired
    private SmsRemindService smsRemindService;
    @Autowired
    private RepairOrderService repairOrderService;

//    // TO ????? ????8?
//    @Scheduled(cron = "0 01 08 * * ?")
//    public void refundBbeforeRemindT0() {
//        log.info("refundBbeforeRemindT0 begin");
//        this.smsRemindService.refundBeforeRemindT0();
//        log.info("refundBbeforeRemindT0 end");
//    }


//    // TO T-3 T-1 ????? ????4?
//    @Scheduled(cron = "0 01 16 * * ?")
//    public void refundBbeforeRemind() {
//        log.info("refundBbeforeRemind begin");
//        this.smsRemindService.refundBeforeRemind();
//        log.info("refundBbeforeRemind end");
//    }
//
//    //  T+1 T+3 T+5 ???? ????4?
//    @Scheduled(cron = "0 01 16 * * ?")
//    public void refundAfterRemind() {
//        log.info("refundAfterRemind begin");
//        this.smsRemindService.refundAfterRemind();
//        log.info("refundAfterRemind end");
//    }

//    @Scheduled(cron = "0 54 17 27 11 ?")
//    public void repairData() {
//        log.info("repairData begin");
//        this.repairOrderService.repairData();
//        log.info("repairData end");
//    }

}
