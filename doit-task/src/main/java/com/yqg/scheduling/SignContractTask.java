package com.yqg.scheduling;

import com.yqg.service.scheduling.SignContractTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SignContractTask {

    @Autowired
    private SignContractTaskService signContractTaskService;

    @Scheduled(cron = "0 0/2 * * * ?")
    public void sendToLoanSuccessUserWithinFiveDay() {
        log.info("start check need to sign orders begin");
        try {
            signContractTaskService.checkNeedToSignContractOrder();
        } catch (Exception e) {
            log.error("the task for check need to sign error", e);
        }
        log.info("start check need to sign orders end");
    }
}
