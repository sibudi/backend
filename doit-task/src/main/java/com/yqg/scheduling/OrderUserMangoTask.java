package com.yqg.scheduling;

import com.yqg.service.RepairOrderDataService;
import com.yqg.service.scheduling.OrderUserMangoScheduling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Didit Dwianto on 2017/11/28.
 */
@Component
@Slf4j
public class OrderUserMangoTask {

    @Autowired
    private OrderUserMangoScheduling userMangoScheduling;

    @Autowired
    private RepairOrderDataService repairOrderDataService;
    /**
     * @throws Exception
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void saveOrderUserDataToMango() throws Exception{
        log.info("saveOrderUserDataToMango begin");
        userMangoScheduling.saveOrderUserDataToMango();
        log.info("saveOrderUserDataToMango end");
    }


//    @Scheduled(cron = "0 39 10 26 11 ?")
//    public void repairData6() {
//        log.info("repairData6 begin");
//        this.repairOrderDataService.repairData6();
//        log.info("repairData6 end");
//    }


}