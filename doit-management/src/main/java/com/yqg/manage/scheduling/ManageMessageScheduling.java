package com.yqg.manage.scheduling;

import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.enums.ManOrderRemarkTypeEnum;
import com.yqg.manage.scheduling.check.ManageMessageSend;
import com.yqg.manage.service.loan.LoanDataStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jacob
 */
@RestController
@RequestMapping("/manage")
public class ManageMessageScheduling {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ManageMessageSend manageMessageSend;

    @Autowired
    private LoanDataStatisticsService loanDataStatisticsService;

    @Autowired
    private OverDueOrderStatistics overDueOrderStatistics;


    @RequestMapping(value = "/sendTeleReviewAlertMessage/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> sendTeleReviewAlertMessage() throws Exception {
        logger.info("<=============>    复审消息提醒任务扫描 start     <=================>");
        manageMessageSend.sendCollectionAlertMessage(ManOrderRemarkTypeEnum.TELE_REVIEW.getType());
        logger.info("<=============>    复审消息提醒任务扫描 end<=================> ");
        return ResponseEntitySpecBuilder.success();
    }
    // 每隔半小时检查复审备注提醒消息,并发送
//    @Scheduled(cron = "0 */1 * * * ?")
//    public void sendTeleReviewAlertMessage() throws Exception{
//        logger.info("<=============>    复审消息提醒任务扫描     <=================>");
//        this.manageMessageSend.sendCollectionAlertMessage(ManOrderRemarkTypeEnum.TELE_REVIEW.getType());
//        logger.info("<=============>    复审消息提醒任务扫描 end <=================> ");
//    }

    @RequestMapping(value = "/saveLoanData2Mysql/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> saveLoanData2Mysql() throws Exception {
        logger.info("将数据App内的数据分析保存到Mysql");
        this.loanDataStatisticsService.saveLoanData2Mysql();
        logger.info("将数据App内的数据分析保存到Mysql end");
        Thread.sleep(10000);    //间隔10s
        logger.info("将数据App内的今日数据分析保存到Mysql");
        this.loanDataStatisticsService.putTodayData2Mysql();
        logger.info("将数据App内的今日数据分析保存到Mysql end");
        return ResponseEntitySpecBuilder.success();
    }
//    // 将数据App内的数据分析保存到Mysql
//    @Scheduled(cron = "0 45 23 * * ?")
//    public void saveLoanData2Mysql() throws Exception{
//        logger.info("将数据App内的数据分析保存到Mysql");
//        this.loanDataStatisticsService.saveLoanData2Mysql();
//        logger.info("将数据App内的数据分析保存到Mysql end");
//        Thread.sleep(10000);    //间隔10s
//        logger.info("将数据App内的今日数据分析保存到Mysql");
//        this.loanDataStatisticsService.putTodayData2Mysql();
//        logger.info("将数据App内的今日数据分析保存到Mysql end");
//    }

    @RequestMapping(value = "/saveAppData2Redis/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> saveAppData2Redis() throws Exception {
        logger.info("将数据App内的数据分析保存到Redis");
        this.loanDataStatisticsService.saveLoanData2Redis();
        logger.info("将数据App内的数据分析保存到Redis end");
        Thread.sleep(10000);    //间隔10s
        logger.info("将数据App内的今日数据分析保存到Redis");
        this.loanDataStatisticsService.putTodayData2Redis();
        logger.info("将数据App内的今日数据分析保存到Redis end");
        return ResponseEntitySpecBuilder.success();
    }
    // 将数据App内的数据分析保存到Redis
//    @Scheduled(cron = "0 0/12 * * * ?")
//    public void saveAppData2Redis() throws Exception{
//        logger.info("将数据App内的数据分析保存到Redis");
//        this.loanDataStatisticsService.saveLoanData2Redis();
//        logger.info("将数据App内的数据分析保存到Redis end");
//        Thread.sleep(10000);    //间隔10s
//        logger.info("将数据App内的今日数据分析保存到Redis");
//        this.loanDataStatisticsService.putTodayData2Redis();
//        logger.info("将数据App内的今日数据分析保存到Redis end");
//    }

    @RequestMapping(value = "/updateOverDueDailyData/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> updateOverDueDailyData() throws Exception {
        logger.info("统计数据app内逾期数据");
        this.overDueOrderStatistics.updateOverDueDailyData();
        logger.info("统计数据app内逾期数据 end");
        return ResponseEntitySpecBuilder.success();
    }

    //统计数据app内逾期数据
//    @Scheduled(cron = "0 40 1 * * ?")
//    public void updateOverDueDailyData() throws Exception {
//        logger.info("统计数据app内逾期数据");
//        this.overDueOrderStatistics.updateOverDueDailyData();
//        logger.info("统计数据app内逾期数据 end");
//    }

    @RequestMapping(value = "/updateRepayDailyData/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> updateRepayDailyData() throws Exception {
        logger.info("数据app逾期订单还款数据统计更新");
        this.overDueOrderStatistics.updateRepayDailyData();
        logger.info("数据app逾期订单还款数据统计更新 end");
        return ResponseEntitySpecBuilder.success();
    }
    //数据app逾期订单还款数据统计更新
//    @Scheduled(cron = "0 0/60 * * * ?")
//    public void updateRepayDailyData() throws Exception {
//        logger.info("数据app逾期订单还款数据统计更新");
//        this.overDueOrderStatistics.updateRepayDailyData();
//        logger.info("数据app逾期订单还款数据统计更新 end");
//    }

    // 初始化数据app数据
    @RequestMapping(value = "/dataAPPInit/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> dataAPPInit() throws Exception {
        this.loanDataStatisticsService.saveLoanData2Redis();
        Thread.sleep(10000);    //间隔10s
        this.loanDataStatisticsService.putTodayData2Redis();
        this.overDueOrderStatistics.initData();

        logger.info("updateRepayDailyData start");
        this.overDueOrderStatistics.updateRepayDailyData();
        logger.info("updateRepayDailyData end");
        return ResponseEntitySpecBuilder.success();
    }
//    @Scheduled(cron = "0 45 14 23 7 ?")
//    public void dataAPPInit() throws Exception{
//
//        this.loanDataStatisticsService.saveLoanData2Redis();
//        Thread.sleep(10000);    //间隔10s
//        this.loanDataStatisticsService.putTodayData2Redis();
//        this.overDueOrderStatistics.initData();
//
//        logger.info("updateRepayDailyData start");
//        this.overDueOrderStatistics.updateRepayDailyData();
//        logger.info("updateRepayDailyData end");
//
////        this.overDueOrderStatistics.updateOverDueDailyData();
//    }

}
