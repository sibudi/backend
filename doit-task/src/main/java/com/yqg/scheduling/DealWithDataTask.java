package com.yqg.scheduling;

import com.yqg.service.pay.RepayService;
import com.yqg.service.system.service.RepayRateService;
import com.yqg.service.third.Inforbip.InforbipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by wanghuaizhou on 2018/7/26.
 */
@Component
@Slf4j
public class DealWithDataTask {

    @Autowired
    private InforbipService inforbipService;

    @Autowired
    private RepayRateService repayRateService;

//    // Check yesterday's loan order
//    @Scheduled(cron = "0 14 17 26 7 ?")
//    public void checkOrdPaySchedule()throws Exception{
//        xiaoMiService.getUserAndOrderDayDataToMi();
//    }
//

    //ahalim: speedup for demo
    @Scheduled(cron = "0/30 * * * * ? ")//@Scheduled(cron = "0 0/1 * * * ? ")
    public void getCallReportFromInfobip00() throws Exception {
        getReportWithMode(0);
    }

    // //@Scheduled(cron = "0 0/1 * * * ? ")
    // public void getCallReportFromInfobip01() throws Exception {
    //     getReportWithMode(1);
    // }

    // //@Scheduled(cron = "0 0/1 * * * ? ")
    // public void getCallReportFromInfobip02() throws Exception {
    //     getReportWithMode(2);
    // }

    // //@Scheduled(cron = "0 0/1 * * * ? ")
    // public void getCallReportFromInfobip03() throws Exception {
    //     getReportWithMode(3);
    // }

    // //@Scheduled(cron = "0 0/1 * * * ? ")
    // public void getCallReportFromInfobip04() throws Exception {
    //     getReportWithMode(4);
    // }

    private void getReportWithMode(int mode) throws Exception {
        log.info("DealWithDataTask getReportWithMode{} - start", mode);
        try {
            this.inforbipService.getReport(mode);
        } catch (Exception e) {
            log.error("ealWithDataTask getReportWithMode{} - error", e);
        }
    }
//
//    @Scheduled(cron = "0 10 08 * * ? ")
//    public void updateRepayRate() throws Exception {
//        this.repayRateService.updateRepayRate();
//    }

//
//    @Scheduled(cron = "0 55 12 16 4 ? ")
//    public void updateRepayRateInit() throws Exception {
//        this.repayRateService.updateRepayRate();
//    }

}
