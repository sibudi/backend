package com.yqg.scheduling;


import com.yqg.service.CheakLoanBalanceService;
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
public class CheakAccountBalanceTask {

    @Autowired
    private CheakLoanBalanceService cheakLoanBalanceService;


    /**
     * @throws Exception
     */
    // @Scheduled(cron = "0 10 08 * * ?")
    public void cheakLoanAccountBalanceOnSpecifiedTime() throws Exception{
        log.info("cheakLoanAccountBalance begin");
        cheakLoanBalanceService.cheakLoanAccountBalanceEveryHour(false);
        log.info("cheakLoanAccountBalance end");
    }



}
