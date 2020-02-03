package com.yqg.scheduling;

import com.yqg.service.RiskMailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ?????????
 *      ???6????????6?
 * Created by Didit Dwianto on 2018/1/5.
 */
@Component
@Slf4j
public class RiskMailSenderTask {

    @Autowired
    private RiskMailService riskMailService;

    // 8 点  风控每日关键指标邮件  和 每日逾期邮件
    // @Scheduled(cron = "0 0 8 * * ?")
    public void riskMailSender1() throws Exception{
        riskMailService.riskInMailSender();
    }

    // @Scheduled(cron = "0 01 8 * * ?")
    public void riskMailSender10() throws Exception{
        riskMailService.riskInMailSenderWithCall();
        riskMailService.riskMailSender();
        riskMailService.keyIndexSender();
        riskMailService.doitDayCallNumber();
        riskMailService.doitDayReviewSender();

    }

    // 9 点  审核人员每日审核状况
    // @Scheduled(cron = "0 0 9 * * ?")
    public void doitDayReviewSender7() throws Exception{
        riskMailService.doitCollectionMail();
        riskMailService.doitDayReviewSender();
    }

    // 运营日报
    // @Scheduled(cron = "0 10 8 * * ?")
    public void doitDayOperationMail() throws Exception{
        riskMailService.doitDayOperationMail();
    }


    // 10 点  审核人员每日审核状况
    // @Scheduled(cron = "0 0 10 * * ?")
    public void doitDayReviewSender() throws Exception{
        riskMailService.doitCollectionMail();
        riskMailService.doitDayReviewSender();
    }

    // 13 点  审核人员每日审核状况
    // @Scheduled(cron = "0 0 13 * * ?")
    public void doitDayReviewSender2() throws Exception{
        riskMailService.doitCollectionMail();
        riskMailService.doitDayReviewSender();
    }

    // 14 点  审核人员每日审核状况
    // @Scheduled(cron = "0 0 14 * * ?")
    public void doitDayReviewSender3() throws Exception{
        riskMailService.doitDayCallNumber();
        riskMailService.doitCollectionMail();
        riskMailService.doitDayReviewSender();
    }

    // 16 点  审核人员每日审核状况
    // @Scheduled(cron = "0 0 16 * * ?")
    public void doitDayReviewSender4() throws Exception{
        riskMailService.doitCollectionQIMail();
        riskMailService.doitCollectionMail();
        riskMailService.doitDayReviewSender();
    }

    // 17:30   催收质检
    // @Scheduled(cron = "0 30 17 * * ?")
    public void doitCollectionMailSender() throws Exception{
        riskMailService.doitCollectionQIMail();
    }

    // 19 点  审核人员每日审核状况
    // @Scheduled(cron = "0 0 19 * * ?")
    public void doitDayReviewSender6() throws Exception{
        riskMailService.doitCollectionMail();
        riskMailService.doitDayReviewSender();
    }
//
    // 12 点  风控每日关键指标邮件  和 每日逾期邮件
    // @Scheduled(cron = "0 0 11 * * ?")
    public void riskMailSender2() throws Exception{
        riskMailService.riskInMailSender();
        riskMailService.riskInMailSenderWithCall();
        riskMailService.riskMailSender();
        riskMailService.keyIndexSender();
        riskMailService.doitDayCallNumber();
        riskMailService.doitCollectionMail();
        riskMailService.doitCollectionQIMail();
        riskMailService.doitDayReviewSender();
    }


    // 15 点  风控每日关键指标邮件  和 每日逾期邮件
    // @Scheduled(cron = "0 0 14 * * ?")
    public void riskMailSender() throws Exception{
        riskMailService.riskInMailSender();
        riskMailService.riskMailSender();
        riskMailService.keyIndexSender();
        riskMailService.doitCollectionQIMail();
    }


    // 18 点  风控每日关键指标邮件  和 每日逾期邮件
    // @Scheduled(cron = "0 0 17 * * ?")
    public void riskMailSender3() throws Exception{
        riskMailService.riskInMailSender();
        riskMailService.riskInMailSenderWithCall();
        riskMailService.riskMailSender();
        riskMailService.keyIndexSender();
        riskMailService.doitDayCallNumber();
        riskMailService.doitCollectionMail();
        riskMailService.doitDayReviewSender();
    }

    // 21 点  风控每日关键指标邮件  和 每日逾期邮件
    // @Scheduled(cron = "0 0 20 * * ?")
    public void riskMailSender4() throws Exception{
        riskMailService.riskInMailSender();
        riskMailService.riskInMailSenderWithCall();
        riskMailService.riskMailSender();
        riskMailService.keyIndexSender();
        riskMailService.doitCollectionMail();
        riskMailService.doitDayReviewSender();
    }

//    // 审核时报  北京时间，每日 10:30，13:00，16:00，19:00 , 22:00
//    @Scheduled(cron = "0 30 9 * * ?")
//    public void riskMailSender5() throws Exception{
////        riskMailService.chackMailSender();
//    }

    // @Scheduled(cron = "0 0 12 * * ?")
    public void riskMailSender6() throws Exception{
        riskMailService.doitCollectionMail();
        riskMailService.doitCollectionQIMail();
        riskMailService.doitDayReviewSender();
    }

    // @Scheduled(cron = "0 0 15 * * ?")
    public void riskMailSender7() throws Exception{
        riskMailService.doitCollectionMail();
        riskMailService.doitDayReviewSender();
    }

    // @Scheduled(cron = "0 0 18 * * ?")
    public void riskMailSender8() throws Exception{
        riskMailService.doitCollectionMail();
        riskMailService.doitDayReviewSender();
    }

    // @Scheduled(cron = "0 0 21 * * ?")
    public void riskMailSender9() throws Exception{
        riskMailService.doitCollectionMail();
        riskMailService.doitDayReviewSender();
    }

    //  Doit新老用户每日逾期率  北京时间，每日 9:10
    // @Scheduled(cron = "0 12 8 * * ?")
    public void doitDayOverdueRate() throws Exception{
        riskMailService.doitDayOverdueRate();
    }


    //  初审/复审员对应其通过率和D8逾期率与平均逾期率差值表  每周一 北京时间，每日 9:00
    // @Scheduled(cron = "0 0 8 ? * MON")
    public void doitDayReviewWeek() throws Exception{
        riskMailService.doitDayReviewWeek();
    }

    //  测试
    // @Scheduled(cron = "0 20 13 30 4 ?")
    public void test() throws Exception{
        riskMailService.riskInMailSender();
    }
}
