package com.yqg.task;

import com.yqg.service.RiskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 审核
 * Created by Didit Dwianto on 2017/12/21.
 */
@Component
@Slf4j
public class RiskScheduling {

    @Autowired
    private RiskService riskService;


    @Scheduled(cron = "0 0/1 * * * ? ")
    public void risk0() {// 审核
        log.info("审核0 begin");
        this.riskService.risk(0);
        log.info("审核0 end0");
    }

    @Scheduled(cron = "0 0/1 * * * ? ")
    public void risk1() {// 审核
        log.info("审核1 begin");
        this.riskService.risk(1);
        log.info("审核1 end");
    }

    @Scheduled(cron = "0 0/1 * * * ? ")
    public void risk2() {// 审核
        log.info("审核2 begin");
        this.riskService.risk(2);
        log.info("审核2 end");
    }


    @Scheduled(cron = "0 0/1 * * * ? ")
    public void risk3() {// 审核
        log.info("审核3 begin");
        this.riskService.risk(3);
        log.info("审核3 end");
    }

    @Scheduled(cron = "0 0/1 * * * ? ")
    public void risk4() {// 审核
        log.info("审核4 begin");
        this.riskService.risk(4);
        log.info("审核4 end");
    }

    @Scheduled(cron = "0 0/1 * * * ? ")
    public void risk5() {// 审核
        log.info("审核5 begin");
        this.riskService.risk(5);
        log.info("审核5 end");
    }

    @Scheduled(cron = "0 0/1 * * * ? ")
    public void risk6() {// 审核
        log.info("审核6 begin");
        this.riskService.risk(6);
        log.info("审核6 end");
    }

    @Scheduled(cron = "0 0/1 * * * ? ")
    public void risk7() {// 审核
        log.info("审核7 begin");
        this.riskService.risk(7);
        log.info("审核7 end");
    }

    @Scheduled(cron = "0 0/1 * * * ? ")
    public void risk8() {// 审核
        log.info("审核8 begin");
        this.riskService.risk(8);
        log.info("审核8 end");
    }

    @Scheduled(cron = "0 0/1 * * * ? ")
    public void risk9() {// 审核
        log.info("审核9 begin");
        this.riskService.risk(9);
        log.info("审核9 end");
    }
    @Scheduled(cron = "0 0/1 * * * ? ")
    public void risk10() {// 审核
        log.info("审核10 begin");
        this.riskService.risk(10);
        log.info("审核10 end");
    }

//    @Scheduled(cron = "0 0/1 * * * ? ")
//    public void riskTask(){
//        //启动10个线程，并等待10个线程结束在处理
//        for(int i=0;i<10;i++){
//            executorService.submit(new RiskThread(i,riskService));
//        }
//
//    }
//
//    class RiskThread implements Runnable {
//        private int orderIdSuffix;//订单id的后缀
//        private RiskService riskService;
//
//        public RiskThread(int suffix, RiskService riskService) {
//            this.orderIdSuffix = suffix;
//            this.riskService = riskService;
//        }
//
//        @Override
//        public void run() {
//            log.info("审核{} begin", orderIdSuffix);
//           // riskService.risk(orderIdSuffix);
//            if(orderIdSuffix==7){
//                try {
//                    Thread.sleep(1000*60*4);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            log.info("审核{} end", orderIdSuffix);
//        }
//    }


}
