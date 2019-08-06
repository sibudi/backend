package com.yqg.scheduling;

import com.yqg.service.CheckOrdPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Jacob
 * Date: 08/02/2018
 * Time: 3:09 PM
 */

@Component
@Slf4j
public class CheckOrdPayTask {

    @Autowired
    CheckOrdPayService checkOrdPayService;

    // 检查昨天的放款订单
    //@Scheduled(cron = "0 30 5 * * ?")
    public void checkOrdPaySchedule(){
        checkOrdPayService.checkOrdPay();
    }

}
