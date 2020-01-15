package com.yqg.scheduling;

import com.yqg.service.RepairOrderDataService;
import com.yqg.service.scheduling.RiskDataSynService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/***
 * ordRiskRecord 数据同步
 */
@Component
@Slf4j
public class RiskDataSysnTask {

    @Autowired
    RiskDataSynService riskDataSynService;
    @Autowired
    RepairOrderDataService repairOrderDataService;

    /***
     * 每隔两分钟将mongo数据同步到风控库的mysql表中
     */
    @Scheduled(cron = "0 0/2 * * * ?")
    public void mongo2MySQL() {
        try {
            riskDataSynService.syncMongo2MySQL();
        } catch (Exception e) {
                log.info("data sync error", e);
        }
    }

    /***
     * 每天凌晨0点13分对数据表数据清理
     */
    //@Scheduled(cron = "19 13 0 * * ?")
    public void splitOrderRiskRecordTable() {
        try {
            riskDataSynService.splitTable();
        } catch (Exception e) {
            log.info("data sync error", e);
        }
    }


    /**
     *    每天凌晨12点20分 更改P2P的单数限制
     * */
    //@Scheduled(cron = "0 20 0 * * ?")
    public void updateP2pOrderCount() {
        log.info("updateP2pOrderCount begin");
        this.repairOrderDataService.updateP2pOrderCount();
        log.info("updateP2pOrderCount end");
    }


    /**
     *    每天凌晨12点20分 更改P2P的单数限制
     * */
    // @Scheduled(cron = "0 20 0 * * ?")
    public void updateDayLoanAmount() {
        log.info("updateDayLoanAmount begin");
        this.repairOrderDataService.updateDayLoanAmount();
        log.info("updateDayLoanAmount end");
    }

//    /**
//     *  修复下单锁中 无超时时间的key
//     * */
//    @Scheduled(cron = "0 50 17 17 6 ?")
//    public void sendToLoanSuccessUserWithinFiveDay() {
//        this.repairOrderDataService.repairData6();
//    }

//    //每隔三分钟同步一次mongo 的izi数据到风控库
//    @Scheduled(cron = "13 0/3 * * * ?")
//    public void syncIziData(){
//
//    }
//
//    /**
//     *   将数据库usrUser 表中的 des加密切换到AES加密
//     * */
//    @Scheduled(cron = "0 0/1 * * * ?")
//    public void repairData() {
//        this.repairOrderDataService.repairData7();
//        this.repairOrderDataService.repairData8();
//        this.repairOrderDataService.repairData9();
//    }

}
