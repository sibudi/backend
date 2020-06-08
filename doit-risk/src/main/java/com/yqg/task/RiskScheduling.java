package com.yqg.task;

import com.yqg.service.RiskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Risk
 * Created by Didit Dwianto on 2017/12/21.
 */
@Component
@Slf4j
public class RiskScheduling {

    @Autowired
    private RiskService riskService;


    //ahalim: speedup for demo
    @Scheduled(cron = "0/30 * * * * ? ")
    //@Scheduled(cron = "0 0/1 * * * ? ")
    public void risk0() {
        log.info("Risk0 begin");
        this.riskService.risk(0);
        log.info("Risk0 end0");
    }

    //ahalim: speedup for demo
    @Scheduled(cron = "0/30 * * * * ? ")
    //@Scheduled(cron = "0 0/1 * * * ? ")
    public void risk1() {
        log.info("Risk1 begin");
        this.riskService.risk(1);
        log.info("Risk1 end");
    }

    //ahalim: speedup for demo
    @Scheduled(cron = "0/30 * * * * ? ")
    //@Scheduled(cron = "0 0/1 * * * ? ")
    public void risk2() {
        log.info("Risk2 begin");
        this.riskService.risk(2);
        log.info("Risk2 end");
    }


    //ahalim: speedup for demo
    @Scheduled(cron = "0/30 * * * * ? ")
    //@Scheduled(cron = "0 0/1 * * * ? ")
    public void risk3() {
        log.info("Risk3 begin");
        this.riskService.risk(3);
        log.info("Risk3 end");
    }

    //ahalim: speedup for demo
    @Scheduled(cron = "0/30 * * * * ? ")
    //@Scheduled(cron = "0 0/1 * * * ? ")
    public void risk4() {
        log.info("Risk4 begin");
        this.riskService.risk(4);
        log.info("Risk4 end");
    }

    //ahalim: speedup for demo
    @Scheduled(cron = "0/30 * * * * ? ")
    //@Scheduled(cron = "0 0/1 * * * ? ")
    public void risk5() {
        log.info("Risk5 begin");
        this.riskService.risk(5);
        log.info("Risk5 end");
    }

    //ahalim: speedup for demo
    @Scheduled(cron = "0/30 * * * * ? ")
    //@Scheduled(cron = "0 0/1 * * * ? ")
    public void risk6() {
        log.info("Risk6 begin");
        this.riskService.risk(6);
        log.info("Risk6 end");
    }

    //ahalim: speedup for demo
    @Scheduled(cron = "0/30 * * * * ? ")
    //@Scheduled(cron = "0 0/1 * * * ? ")
    public void risk7() {
        log.info("Risk7 begin");
        this.riskService.risk(7);
        log.info("Risk7 end");
    }

    //ahalim: speedup for demo
    @Scheduled(cron = "0/30 * * * * ? ")
    //@Scheduled(cron = "0 0/1 * * * ? ")
    public void risk8() {
        log.info("Risk8 begin");
        this.riskService.risk(8);
        log.info("Risk8 end");
    }

    //ahalim: speedup for demo
    @Scheduled(cron = "0/30 * * * * ? ")
    //@Scheduled(cron = "0 0/1 * * * ? ")
    public void risk9() {
        log.info("Risk9 begin");
        this.riskService.risk(9);
        log.info("Risk9 end");
    }
    //ahalim: speedup for demo
    @Scheduled(cron = "0/30 * * * * ? ")
    //@Scheduled(cron = "0 0/1 * * * ? ")
    public void risk10() {
        log.info("Risk10 begin");
        this.riskService.risk(10);
        log.info("Risk10 end");
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
//            log.info("Risk{} begin", orderIdSuffix);
//           // riskService.risk(orderIdSuffix);
//            if(orderIdSuffix==7){
//                try {
//                    Thread.sleep(1000*60*4);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            log.info("Risk{} end", orderIdSuffix);
//        }
//    }


}
