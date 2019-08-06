package com.yqg.scheduling;

import com.yqg.service.order.OrdService;
import com.yqg.service.user.service.UsrContactService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Jacob on 2018/4/24.
 */
@Component
@Slf4j
public class GetUserContactFromMongoTask {


    @Autowired
    private OrdService ordService;

//    @Scheduled(cron = "0 54 19 20 6 ?")
//    public void getLoanOrderTask() throws Exception{
//        log.info("getLoanOrderTele begin");
//        this.ordService.getLoanOrderTele();
//        log.info("getLoanOrderTele end");
//    }


//    @Scheduled(cron = "0 21 20 16 7 ?")
//    public void getLoanOrderTask2() throws Exception{
//        log.info("getLoanOrderTele2 begin");
//        this.ordService.getLoanOrderTele2();
//        log.info("getLoanOrderTele2 end");
//    }


//    @Autowired
//    private UsrContactService usrContactService;
//
//    @Scheduled(cron = "0 32 20 24 4 ?")
//    public void checkOrdPaySchedule() throws Exception{
//        usrContactService.getUserContactsFromMongo();
//    }
}
