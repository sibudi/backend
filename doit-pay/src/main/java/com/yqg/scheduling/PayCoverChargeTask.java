package com.yqg.scheduling;

import com.yqg.service.LoanCoverChargeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务
 */
@Component
@Slf4j
public class PayCoverChargeTask {


    @Autowired
    private LoanCoverChargeService loanCoverChargeService;

//    /**
//     * 放款服务费推送
//     */
//    @Scheduled(cron = "0 0/5 * * * ? ")
//    public void loanCoverCharge() {
//        log.info("======================================loanCoverCharge begin ==========================================");
//        this.loanCoverChargeService.loanCoverCharge();
//        log.info("======================================loanCoverCharge end ==========================================");
//    }
//
//    /**
//     * 获取放款服务费推送结果
//     */
//    @Scheduled(cron = "0 0/3 7-21 * * ? ")
//    public void cheakLoanCoverCharge() {
//        log.info("======================================cheakLoanCoverCharge begin ==========================================");
//        this.loanCoverChargeService.cheakLoanCoverChargeByOrder();
//        log.info("======================================cheakLoanCoverCharge end ==========================================");
//    }


    /**
     * 放款服务费转账（直接转到指定账户)
     */
    @Scheduled(cron = "0 0/1 * * * ? ")
    public void transferCoverCharge() {
        log.info("======================================transferCoverCharge begin ==========================================");
        this.loanCoverChargeService.transferCoverCharge();
        log.info("======================================transferCoverCharge end ==========================================");
    }
}


