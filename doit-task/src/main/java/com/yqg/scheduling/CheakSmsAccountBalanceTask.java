package com.yqg.scheduling;


import com.yqg.service.CheakSmsBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Didit Dwianto on 2018/1/2.
 */
@Component
@Slf4j
public class CheakSmsAccountBalanceTask {

    @Autowired
    private CheakSmsBalanceService cheakSmsBalanceService;



    /**
     * @throws Exception
     */
    //@Scheduled(cron = "0 10 08 * * ?")
    public void cheakSmsAccountBalanceOnSpecifiedTime() throws Exception{
        log.info("cheakSmsAccountBalance begin");
        cheakSmsBalanceService.cheakSmsAccountBalanceEveryHour(false);
        log.info("cheakSmsAccountBalance end");
    }
//
}
