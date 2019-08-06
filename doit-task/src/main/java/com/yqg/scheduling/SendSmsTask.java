package com.yqg.scheduling;

import com.yqg.service.SendSmsService;
import com.yqg.service.system.service.SmsRemindService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Didit Dwianto on 2018/2/24.
 */
@Component
@Slf4j
public class SendSmsTask {

     @Autowired
     private SmsRemindService smsRemindService;

    @Autowired
    private SendSmsService sendSmsService;

    /**
     * @throws Exception
     */
    @Scheduled(cron = "0 15 10 */5 * ?")
    public void sendToLoanSuccessUserWithinFiveDay() throws Exception{
        log.info("sendToLoanSuccessUserWithinFiveDay begin");
        sendSmsService.sendToLoanSuccessUserWithinFiveDay();
        log.info("sendToLoanSuccessUserWithinFiveDay end");
    }

    /**
     * send silence user (the day is 3)
     * @throws Exception
     */
    @Scheduled(cron = "0 0 12 * * ?")
    public void sendSmsToSilenceUser() throws Exception{
        log.info("sendSmsToSilenceUser begin");
        sendSmsService.sendSmsToSilenceUser3();
        log.info("sendSmsToSilenceUser end");
    }


    /**  send not verify
     * @throws Exception
     */
    @Scheduled(cron = "0 0 12 * * ?")
    public void sendSmsToUserNotVerifyOrder() throws Exception{
        log.info("sendSmsToUserNotVerifyOrder begin");
        sendSmsService.sendSmsToUserNotVerifyOrder();
        log.info("sendSmsToUserNotVerifyOrder end");
    }

    /**  send 降额未确认放款
     * @throws Exception
     */
    @Scheduled(cron = "0 0 12 * * ?")
    public void sendReduceSms() throws Exception{
        log.info("sendReduceSms begin");
        sendSmsService.sendReduceSms();
        log.info("sendReduceSms end");
    }

    /**  每周五早上十点定时发送  20w已还款用户 没有再复借
     * @throws Exception
     */
    @Scheduled(cron = "0 0 10 ? * 6")
    public void sendSmsToUserNotReLoanAfter20W() throws Exception{
        log.info("sendSmsToUserNotReLoanAfter20W begin");
        sendSmsService.sendSmsToUserNotReLoanAfter20W();
        log.info("sendSmsToUserNotReLoanAfter20W end");
    }

//
//    /**
//     * 每天给待放款用户发短信
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 10 8 * * ?")
//    public void sendSmsToLoanUser() throws Exception{
//        log.info("sendSmsToLoanUser begin");
//        this.smsRemindService.sendLoanOrderSms();
//        log.info("sendSmsToLoanUser end");
//    }

//    /**
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 55 13 19 6 ?")
//    public void sendSmsToLoanSuccess() throws Exception{
//        log.info("sendSmsToLoanSuccess begin");
//        sendSmsService.sendSmsToLoanSuccess();
//        log.info("sendSmsToLoanSuccess end");
//    }


//    /**
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 15 12 22 6 ?")
//    public void sendSmsToNotLoan() throws Exception{
//        log.info("sendSmsToNotLoan begin");
//        sendSmsService.sendSmsToNotLoan();
//        log.info("sendSmsToNotLoan end");
//    }

//    /**
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 54 16 22 6 ?")
//    public void sendSmsToYituFaild() throws Exception{
//        log.info("sendSmsToYituFaild begin");
//        sendSmsService.sendSmsToYituFaild();
//        log.info("sendSmsToYituFaild end");
//    }

//    /**
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 0 8 25 7 ?")
//    public void sendSmsToInviteFriend() throws Exception{
//        log.info("sendSmsToInviteFriend begin");
//        sendSmsService.sendSmsToInviteFriend();
//        log.info("sendSmsToInviteFriend end");
//    }


//    /**  召回  do-it沉默用户
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 0 9 24 7 ?")
//    public void sendSmsToSilenceUser() throws Exception{
//        log.info("sendSmsToSilenceUser begin");
//        sendSmsService.sendSmsToSilenceUser();
//        log.info("sendSmsToSilenceUser end");
//    }
//
//    /**  召回  do-it沉默用户
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 0 11 01 9 ?")
//    public void sendSmsToSilenceUser2() throws Exception{
//        log.info("sendSmsToSilenceUser2 begin");
//        sendSmsService.sendSmsToSilenceUser2();
//        log.info("sendSmsToSilenceUser2 end");
//    }

//    /**  召回  do-it沉默用户
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 0 9 26 7 ?")
//    public void sendSmsToSilenceUser3() throws Exception{
//        log.info("sendSmsToSilenceUser3 begin");
//        sendSmsService.sendSmsToSilenceUser3();
//        log.info("sendSmsToSilenceUser3 end");
//    }

//    /**  召回  复审流程问题 取消订单的用户
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 0 15 23 7 ?")
//    public void sendSmsToCancleUser() throws Exception{
//        log.info("sendSmsToCancleUser begin");
//        sendSmsService.sendSmsToCancleUser();
//        log.info("sendSmsToCancleUser end");
//    }

//
//    /**  召回  do-it沉默用户
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 50 14 27 7 ?")
//    public void sendSmsToSilenceUserNow() throws Exception{
//        log.info("sendSmsToSilenceUserNow begin");
//        sendSmsService.sendSmsToSilenceUser();
//        sendSmsService.sendSmsToSilenceUser2();
//        log.info("sendSmsToSilenceUserNow end");
//    }
//
//    /**  召回  do-it沉默用户
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 0 9 28 7 ?")
//    public void sendSmsToSilenceUser() throws Exception{
//        log.info("sendSmsToSilenceUser begin");
//        sendSmsService.sendSmsToSilenceUser();
//        sendSmsService.sendSmsToSilenceUser2();
//        log.info("sendSmsToSilenceUser end");
//    }
//
//
//    /**  召回  do-it沉默用户
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 21 14 31 7 ?")
//    public void sendSmsToLoanSuccess() throws Exception{
//        log.info("sendSmsToLoanSuccess begin");
//        sendSmsService.sendSmsToSilenceUser3();
//        log.info("sendSmsToLoanSuccess end");
//    }


//    /**  给待放款的用户发送短信
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 10 15 13 8 ?")
//    public void sendSmsToUserNotLoan() throws Exception{
//        log.info("sendSmsToUserNotLoan begin");
//        sendSmsService.sendSmsToUserNotLoan();
//        log.info("sendSmsToUserNotLoan end");
//    }

//    /**  给绑定银行卡异常的用户发送短信
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 50 15 13 8 ?")
//    public void sendSmsToUserWithBindCardFaild() throws Exception{
//        log.info("sendSmsToUserWithBindCardFaild begin");
//        sendSmsService.sendSmsToUserWithBindCardFaild();
//        log.info("sendSmsToUserWithBindCardFaild end");
//    }

//    /**  给在贷用户发送提醒短信
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 52 16 06 9 ?")
//    public void sendSmsToUserWithLoaning() throws Exception{
//        log.info("sendSmsToUserWithLoaning begin");
//        sendSmsService.sendSmsToUserWithLoaning();
//        log.info("sendSmsToUserWithLoaning end");
//    }

//
//    /**  给 已还款3天没有复借 的用户发送短信
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 0 09 06 9 ?")
//    public void sendSmsToUserWithNotRebrow() throws Exception{
//        log.info("sendSmsToUserWithNotRebrow begin");
//        sendSmsService.sendSmsToUserWithNotRebrow();
//        log.info("sendSmsToUserWithNotRebrow end");
//    }


//    /**  给 已借款 的用户发送邀请好友短信
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 0 09 28 9 ?")
//    public void sendSmsToUserInviteFriend() throws Exception{
//        log.info("sendSmsToUserInviteFriend begin");
//        sendSmsService.sendSmsToUserInviteFriend();
//        log.info("sendSmsToUserInviteFriend end");
//    }
//
//        /**  召回  do-it沉默用户 （注册未申请）
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 0 09 28 9 ?")
//    public void sendSmsToSilenceUser2() throws Exception{
//        log.info("sendSmsToSilenceUser2 begin");
//        sendSmsService.sendSmsToSilenceUser2();
//        log.info("sendSmsToSilenceUser2 end");
//    }

//    /**  给 联系人填写被误拒 的用户发送邀请好友短信
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 40 11 20 9 ?")
//    public void sendSmsToUserWithAutoCallFaild() throws Exception{
//        log.info("sendSmsToUserWithAutoCallFaild begin");
//        sendSmsService.sendSmsToUserWithAutoCallFaild();
//        log.info("sendSmsToUserWithAutoCallFaild end");
//    }


//    /**  召回  do-it沉默用户 （申请未提交）
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 02 16 18 10 ?")
//    public void sendSmsToSilenceUser3() throws Exception{
//        log.info("sendSmsToSilenceUser3 begin");
//        sendSmsService.sendSmsToSilenceUser3();
//        log.info("sendSmsToSilenceUser3 end");
//    }

//    /**   已借款用户 邀请好友
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 02 9 25 10 ?")
//    public void sendSmsToUserInviteFriendWithFiveStar() throws Exception{
//        log.info("sendSmsToUserInviteFriendWithFiveStar begin");
//        sendSmsService.sendSmsToUserInviteFriendWithFiveStar();
//        log.info("sendSmsToUserInviteFriendWithFiveStar end");
//    }


//        /**  召回  do-it沉默用户 （注册未申请）
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 02 9 27 10 ?")
//    public void sendSmsToSilenceUser2() throws Exception{
//        log.info("sendSmsToSilenceUser2 begin");
//        sendSmsService.sendSmsToSilenceUser2();
//        log.info("sendSmsToSilenceUser2 end");
//    }
//
//
//    /**  尊敬的Do-It客户您好，付款过程中可以优先使用alfamart支付，如有其他还款问题可以拨打我们的客服热线
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 45 15 26 10 ?")
//    public void sendSmsToUseAlfamart() throws Exception{
//        log.info("sendSmsToUseAlfamart begin");
//        sendSmsService.sendSmsToUseAlfamart();
//        log.info("sendSmsToUseAlfamart end");
//    }


//        /**  风控规则拒绝 召回
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 15 9 14 11 ?")
//    public void sendSmsToUserWithRefuse() throws Exception{
//        log.info("sendSmsToUserWithRefuce begin");
//        sendSmsService.sendSmsToUserWithRefuce();
//        log.info("sendSmsToUserWithRefuce end");
//    }


//    /**  风控规则拒绝 召回
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 25 15 14 11 ?")
//    public void sendSmsToUserWithRefuse2() throws Exception{
//        log.info("sendSmsToUserWithRefuce2 begin");
//        sendSmsService.sendSmsToUserWithRefuce2();
//        log.info("sendSmsToUserWithRefuce2 end");
//    }


//    /**  风控规则拒绝 召回
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 38 17 16 11 ?")
//    public void sendSmsToUserWithRefuce3() throws Exception{
//        log.info("sendSmsToUserWithRefuce3 begin");
//        sendSmsService.sendSmsToUserWithRefuce3();
//        log.info("sendSmsToUserWithRefuce3 end");
//    }
//
//    /**  风控规则拒绝 召回
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 18 16 20 11 ?")
//    public void sendSmsToUserWithNotRebrow() throws Exception{
//        log.info("sendSmsToUserWithNotRebrow begin");
//        sendSmsService.sendSmsToUserWithNotRebrow();
//        log.info("sendSmsToUserWithNotRebrow end");
//    }
//
//
//    /**  运营短信 拉新
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 39 16 03 12 ?")
//    public void sendSmsOn20181203() throws Exception{
//        log.info("sendSmsOn20181203 begin");
//        sendSmsService.sendSmsOn20181203();
//        log.info("sendSmsOn20181203 end");
//    }

//
//    @Scheduled(cron = "0 0 10 05 12 ?")
//    public void sendSmsToUserNotReLoanAfter20W2() throws Exception{
//        log.info("sendSmsToUserNotReLoanAfter20W begin");
//        sendSmsService.sendSmsToUserNotReLoanAfter20W();
//        log.info("sendSmsToUserNotReLoanAfter20W end");
//    }

//        /**  运营短信 拉新
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 08 11 06 12 ?")
//    public void sendSmsOn20181206() throws Exception{
//        log.info("sendSmsOn20181206 begin");
//        sendSmsService.sendSmsOn20181206();
//        log.info("sendSmsOn20181206 end");
//    }
//
//    /**  运营短信 拉新
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 42 11 07 12 ?")
//    public void sendSmsOn20181207() throws Exception{
//        log.info("sendSmsOn20181207 begin");
//        sendSmsService.sendSmsOn20181207();
//        log.info("sendSmsOn20181207 end");
//    }
//
//    /**  运营短信 拉新
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 08 11 08 12 ?")
//    public void sendSmsOn20181208() throws Exception{
//        log.info("sendSmsOn20181208 begin");
//        sendSmsService.sendSmsOn20181208();
//        log.info("sendSmsOn20181208 end");
//    }
//
//    /**  运营短信 拉新
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 08 11 09 12 ?")
//    public void sendSmsOn20181209() throws Exception{
//        log.info("sendSmsOn20181209 begin");
//        sendSmsService.sendSmsOn20181209();
//        log.info("sendSmsOn20181209 end");
//    }


//    /**  风控规则拒绝 召回
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 0 10 10 12 ?")
//    public void sendSmsToUserWithRefuse20181210() throws Exception{
//        log.info("sendSmsToUserWithRefuse20181210 begin");
//        sendSmsService.sendSmsToUserWithRefuse20181210();
//        log.info("sendSmsToUserWithRefuse20181210 end");
//    }
////
//    @Scheduled(cron = "0 0 10 11 12 ?")
//    public void sendSmsToUserWithRefuse20181211() throws Exception{
//        log.info("sendSmsToUserWithRefuse20181211 begin");
//        sendSmsService.sendSmsToUserWithRefuse20181211();
//        log.info("sendSmsToUserWithRefuse20181211 end");
//    }
//
//    @Scheduled(cron = "0 0 10 12 12 ?")
//    public void sendSmsToUserWithRefuse2018121102() throws Exception{
//        log.info("sendSmsToUserWithRefuse2018121102 begin");
//        sendSmsService.sendSmsToUserWithRefuse2018121102();
//        log.info("sendSmsToUserWithRefuse2018121102 end");
//    }
//
//    @Scheduled(cron = "0 0 10 13 12 ?")
//    public void sendSmsToUserWithRefuse2018121103() throws Exception{
//        log.info("sendSmsToUserWithRefuse2018121103 begin");
//        sendSmsService.sendSmsToUserWithRefuse2018121103();
//        log.info("sendSmsToUserWithRefuse2018121103 end");
//    }
//
//    @Scheduled(cron = "0 0 10 14 12 ?")
//    public void sendSmsToUserWithRefuse2018121104() throws Exception{
//        log.info("sendSmsToUserWithRefuse2018121104 begin");
//        sendSmsService.sendSmsToUserWithRefuse2018121104();
//        log.info("sendSmsToUserWithRefuse2018121104 end");
//    }
//
//        /**  运营短信 拉新
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 02 11 10 12 ?")
//    public void sendSmsOn20181210() throws Exception{
//        log.info("sendSmsOn20181210 begin");
//        sendSmsService.sendSmsOn20181210();
//        log.info("sendSmsOn20181210 end");
//    }
//

////
//        /**  运营短信 拉新   1.2w
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 13 10 21 12 ?")
//    public void sendSmsOn2018122001() throws Exception{
//        log.info("sendSmsOn2018122001 begin");
//        sendSmsService.sendSmsOn2018122001();
//        log.info("sendSmsOn2018122001 end");
//    }
//
//    /**  运营短信 拉新   7w
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 32 12 21 12 ?")
//    public void sendSmsOn2018122002() throws Exception{
//        log.info("sendSmsOn2018122002 begin");
//        sendSmsService.sendSmsOn2018122002();
//        log.info("sendSmsOn2018122002 end");
//    }
//
//    /**  运营短信 拉新   7w
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 0 10 22 12 ?")
//    public void sendSmsOn20181221() throws Exception{
//        log.info("sendSmsOn20181221 begin");
//        sendSmsService.sendSmsOn20181221();
//        log.info("sendSmsOn20181221 end");
//    }

//    @Scheduled(cron = "0 13 10 21 12 ?")
//    public void sendSmsToRisk() throws Exception{
//        log.info("sendSmsToRisk begin");
//        sendSmsService.sendSmsToRisk();
//        log.info("sendSmsToRisk end");
//    }
//
//
//    @Scheduled(cron = "0 0 11 21 12 ?")
//    public void sendSmsToSilenceUser2WithOneWeek() throws Exception{
//        log.info("sendSmsToSilenceUser2WithOneWeek begin");
//        sendSmsService.sendSmsToSilenceUser2WithOneWeek();
//        log.info("sendSmsToSilenceUser2WithOneWeek end");
//    }


//    // 借款成功的用户  海外搞得分享活动  协助发个参与短信
//    @Scheduled(cron = "0 20 17 24 12 ?")
//    public void sendSmsToOutseaUser() throws Exception{
//        log.info("sendSmsToOutseaUser begin");
//        sendSmsService.sendSmsToOutseaUser();
//        log.info("sendSmsToOutseaUser end");
//    }

//
//        @Scheduled(cron = "0 55 13 27 12 ?")
//    public void sendSmsToSilenceUser2WithOneWeek() throws Exception{
//        log.info("sendSmsToSilenceUser2WithOneWeek begin");
//        sendSmsService.sendSmsToSilenceUser2WithOneWeek();
//        log.info("sendSmsToSilenceUser2WithOneWeek end");
//    }

//    /**  运营短信 拉新   2w
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 0 10 27 12 ?")
//    public void sendSmsOn20181227() throws Exception{
//        log.info("sendSmsOn20181227 begin");
//        sendSmsService.sendSmsOn20181227();
//        log.info("sendSmsOn20181227 end");
//    }
//
//
//    @Scheduled(cron = "0 0 10 29 12 ?")
//    public void sendSmsToWithWaitingForReview() throws Exception{
//        log.info("sendSmsToWithWaitingForReview begin");
//        sendSmsService.sendSmsToWithWaitingForReview();
//        log.info("sendSmsToWithWaitingForReview end");
//    }
//
//    @Scheduled(cron = "0 0 10 30 12 ?")
//    public void sendSmsToWithWaitingForReview2() throws Exception{
//        log.info("sendSmsToWithWaitingForReview begin");
//        sendSmsService.sendSmsToWithWaitingForReview();
//        log.info("sendSmsToWithWaitingForReview end");
//    }
//
//    @Scheduled(cron = "0 0 10 31 12 ?")
//    public void sendSmsToWithWaitingForReview3() throws Exception{
//        log.info("sendSmsToWithWaitingForReview begin");
//        sendSmsService.sendSmsToWithWaitingForReview();
//        log.info("sendSmsToWithWaitingForReview end");
//    }
//
//    @Scheduled(cron = "0 0 10 01 1 ?")
//    public void sendSmsToWithWaitingForReview4() throws Exception{
//        log.info("sendSmsToWithWaitingForReview begin");
//        sendSmsService.sendSmsToWithWaitingForReview();
//        log.info("sendSmsToWithWaitingForReview end");
//    }
//
//    @Scheduled(cron = "0 0 10 02 1 ?")
//    public void sendSmsToWithWaitingForReview5() throws Exception{
//        log.info("sendSmsToWithWaitingForReview begin");
//        sendSmsService.sendSmsToWithWaitingForReview();
//        log.info("sendSmsToWithWaitingForReview end");
//    }
//
//    @Scheduled(cron = "0 0 10 03 1 ?")
//    public void sendSmsToWithWaitingForReview6() throws Exception{
//        log.info("sendSmsToWithWaitingForReview begin");
//        sendSmsService.sendSmsToWithWaitingForReview();
//        log.info("sendSmsToWithWaitingForReview end");
//    }
//
//    @Scheduled(cron = "0 0 10 04 1 ?")
//    public void sendSmsToWithWaitingForReview7() throws Exception{
//        log.info("sendSmsToWithWaitingForReview begin");
//        sendSmsService.sendSmsToWithWaitingForReview();
//        log.info("sendSmsToWithWaitingForReview end");
//    }
//
//        /**  运营短信 拉新   2w
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 0 10 03 1 ?")
//    public void sendSmsOn20190102() throws Exception{
//        log.info("sendSmsOn20190102 begin");
//        sendSmsService.sendSmsOn20190102();
//        log.info("sendSmsOn20190102 end");
//    }

//    /**  运营短信 拉新   2.5w
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 0 10 12 1 ?")
//    public void sendSmsOn20190109() throws Exception{
//        log.info("sendSmsOn20190109 begin");
//        sendSmsService.sendSmsOn20190109();
//        log.info("sendSmsOn20190109 end");
//    }
//
//    /**  运营短信 拉新   2.3w
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 0 10 13 1 ?")
//    public void sendSmsOn20190110() throws Exception{
//        log.info("sendSmsOn20190110 begin");
//        sendSmsService.sendSmsOn20190110();
//        log.info("sendSmsOn20190110 end");
//    }

//    @Scheduled(cron = "0 48 13 09 1 ?")
//    public void sendSmsToSilenceUser2WithTwoWeek() throws Exception{
//        log.info("sendSmsToSilenceUser2WithTwoWeek begin");
//        sendSmsService.sendSmsToSilenceUser2WithTwoWeek();
//        log.info("sendSmsToSilenceUser2WithTwoWeek end");
//    }

//    @Scheduled(cron = "0 18 14 11 1 ?")
//    public void sendSmsToSilenceUserWithTwoWeek() throws Exception{
//        log.info("sendSmsToSilenceUserWithTwoWeek begin");
//        sendSmsService.sendSmsToSilenceUserWithTwoWeek();
//        log.info("sendSmsToSilenceUserWithTwoWeek end");
//    }


//    /**  运营短信 拉新   2.5w
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 0 10 15 1 ?")
//    public void sendSmsOn20190115() throws Exception{
//        log.info("sendSmsOn20190115 begin");
//        sendSmsService.sendSmsOn20190115();
//        log.info("sendSmsOn20190115 end");
//    }


//    // 两周内申请未提交
//    @Scheduled(cron = "0 30 15 18 1 ?")
//    public void sendSmsToSilenceUser2WithTwoWeek() throws Exception{
//        log.info("sendSmsToSilenceUser2WithTwoWeek begin");
//        sendSmsService.sendSmsToSilenceUser2WithTwoWeek();
//        log.info("sendSmsToSilenceUser2WithTwoWeek end");
//    }

//    /**  运营短信 拉新   3w
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 0 10 19 1 ?")
//    public void sendSmsOn20190119() throws Exception{
//        log.info("sendSmsOn20190119 begin");
//        sendSmsService.sendSmsOn20190119();
//        log.info("sendSmsOn20190119 end");
//    }

//    /**  运营短信 拉新   2w
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 0 10 24 1 ?")
//    public void sendSmsOn20190124() throws Exception{
//        log.info("sendSmsOn20190124 begin");
//        sendSmsService.sendSmsOn20190124();
//        log.info("sendSmsOn20190124 end");
//    }
//
//    /**  运营短信 拉新   2w
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 0 10 25 1 ?")
//    public void sendSmsOn20190125() throws Exception{
//        log.info("sendSmsOn20190125 begin");
//        sendSmsService.sendSmsOn20190125();
//        log.info("sendSmsOn20190125 end");
//    }
//
//    /**  运营短信 拉新   2w
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 0 10 26 1 ?")
//    public void sendSmsOn20190126() throws Exception{
//        log.info("sendSmsOn20190126 begin");
//        sendSmsService.sendSmsOn20190126();
//        log.info("sendSmsOn20190126 end");
//    }

    @Scheduled(cron = "0 52 14 15 2 ?")
    public void sendSmsToSilenceUserWithTwoWeek() throws Exception{
        log.info("sendSmsToSilenceUserWithTwoWeek begin");
        sendSmsService.sendSmsToSilenceUserWithTwoWeek();
        log.info("sendSmsToSilenceUserWithTwoWeek end");
    }


    /**  运营短信 拉新   2w
     * @throws Exception
     */
    @Scheduled(cron = "0 20 11 18 2 ?")
    public void sendSmsOn20190218() throws Exception{
        log.info("sendSmsOn20190218 begin");
        sendSmsService.sendSmsOn20190218();
        log.info("sendSmsOn20190218 end");
    }

}
