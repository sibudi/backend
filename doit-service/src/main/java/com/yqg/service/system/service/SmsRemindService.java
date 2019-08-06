package com.yqg.service.system.service;

import com.yqg.common.constants.SysParamContants;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.DateUtils;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.third.sms.SmsServiceUtil;
import com.yqg.service.user.service.UsrService;
import com.yqg.user.dao.LoanUserDao;
import com.yqg.user.entity.LoanUser;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by Didit Dwianto on 2018/1/12.
 */
@Service
@Slf4j
public class SmsRemindService {

    @Autowired
    private UsrService usrService;
    @Autowired
    private SysParamService sysParamService;
    @Autowired
    private SmsServiceUtil smsServiceUtil;
    @Autowired
    private OrdDao ordDao;
    @Autowired
    private LoanUserDao loanUserDao;

    /**
     *   T0  ,??8???
     */
    public void refundBeforeRemindT0() {
        //??
        String sysParamValue = this.sysParamService.getSysParamValue(SysParamContants.REFUND_BEFORE_REMIND_OFF_NO_8);

        if (!StringUtils.isEmpty(sysParamValue) && Integer.valueOf(sysParamValue) == 1) {

            List<OrdOrder> orderOrders = this.ordDao.getInRepayOrderListWithNotOverdue();

            int overdueDay = 0;
            String content = "";
            if (!CollectionUtils.isEmpty(orderOrders)) {
                for (OrdOrder order : orderOrders) {

                    UsrUser userUser = this.usrService.getUserByUuid(order.getUserUuid());
                    try {
                        overdueDay = (int) DateUtils.daysBetween(DateUtils.formDate(order.getRefundTime(), "yyyy-MM-dd"), DateUtils.formDate(new Date(), "yyyy-MM-dd"));

                        if (overdueDay == 0) {

                            log.info("????????:{}", order.getUuid());
                            //???????
                            content = "<Do-It> Pelanggan Yth,ini adalah hari terakhir pelunasan.untuk menghindari denda,harap segera lunasi. kunjungi goo.gl/RLfJ8Z. Thx.";

                            sendSmsToUser(content,userUser,1);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

//                this.sendCollectMessagePerson("??????????????,?????????8?");
            }

        }
    }

    /**
     * ??? T-3 T-1  T0  ,??4???
     */
    public void refundBeforeRemind() {
        //??
        String sysParamValue = this.sysParamService.getSysParamValue(SysParamContants.REFUND_BEFORE_REMIND_OFF_NO_16);

        if (!StringUtils.isEmpty(sysParamValue) && Integer.valueOf(sysParamValue) == 1) {

            List<OrdOrder> orderOrders = this.ordDao.getInRepayOrderListWithNotOverdue();

            int overdueDay = 0;
            String content = "";
            if (!CollectionUtils.isEmpty(orderOrders)) {
                for (OrdOrder order : orderOrders) {

                    UsrUser userUser = this.usrService.getUserByUuid(order.getUserUuid());
                    try {
                        overdueDay = (int) DateUtils.daysBetween(DateUtils.formDate(order.getRefundTime(), "yyyy-MM-dd"), DateUtils.formDate(new Date(), "yyyy-MM-dd"));

                        if (overdueDay == 0) {
                            //???????
                            content = "<Do-It> Pelanggan Yth,ini adalah hari terakhir pelunasan.untuk menghindari denda,harap segera lunasi. kunjungi goo.gl/RLfJ8Z. Thx.";

                            sendSmsToUser(content,userUser,1);
//                            try {
//                                JPushUtil.jpushAllAlias(order.getUserUuid(), content);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }

                        } else if (overdueDay == -1) {
                            //???????
                            content = "<Do-It> Pelanggan Yth, durasi Anda tinggal 1 hari. untuk mencegah biaya denda,harap segera melakukan pelunasan.kunjungi goo.gl/RLfJ8Z.Thx.";

                            sendSmsToUser(content,userUser,2);
//                            try {
//                                JPushUtil.jpushAllAlias(order.getUserUuid(), content);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
                        } else if (overdueDay == -3) {
                                //???????
                            content = "<Do-It> Pelanggan Yth, durasi Anda tinggal 3 hari. untuk mencegah biaya denda,harap segera melakukan pelunasan.kunjungi goo.gl/RLfJ8Z.Thx.";
                                //logger.info(content);
                            sendSmsToUser(content,userUser,3);
//                                try {
//                                    JPushUtil.jpushAllAlias(order.getUserUuid(), content);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
                            }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

//                this.sendCollectMessagePerson("??????????????,?????????4?");
            }

        }
    }

    /**
     * ??? T1 T2 T3 ??4? ??
     */
    public void refundAfterRemind() {
        //??
        String sysParamValue = this.sysParamService.getSysParamValue(SysParamContants.REFUND_AFTER_REMIND_OFF_NO_16);

        if (!StringUtils.isEmpty(sysParamValue) && Integer.valueOf(sysParamValue) == 1) {

            List<OrdOrder> orderOrders = this.ordDao.getInRepayOrderListWithOverdue();

            int overdueDay = 0;
            String content = "";
            if (!CollectionUtils.isEmpty(orderOrders)) {
                for (OrdOrder order : orderOrders) {

                    UsrUser userUser = this.usrService.getUserByUuid(order.getUserUuid());
                    try {
                        overdueDay = (int) DateUtils.daysBetween(DateUtils.formDate(order.getRefundTime(), "yyyy-MM-dd"), DateUtils.formDate(new Date(), "yyyy-MM-dd"));

                        if (overdueDay == 1) {
                            //???????
                            content = "<Do-It> Anda terlambat 1 hari! demi menjaga reputasi? kelancaran di DO-IT, harap segera lunasi pinjaman. Kunjungi goo.gl/RLfJ8Z. Thx.";

                            sendSmsToUser(content,userUser,4);
//                            try {
//                                JPushUtil.jpushAllAlias(order.getUserUuid(), content);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }

                        } else if (overdueDay == 3) {
                            //???????
                            content = "<Do-It> Anda terlambat 3 hari! demi menjaga reputasi? kelancaran di DO-IT, harap segera lunasi pinjaman. Kunjungi goo.gl/RLfJ8Z. Thx.";

                            sendSmsToUser(content,userUser,5);
//                            try {
//                                JPushUtil.jpushAllAlias(order.getUserUuid(), content);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
                        } else if (overdueDay == 5) {
                            //???????
                            content = "<Do-It> Anda terlambat 5 hari! demi menjaga reputasi? kelancaran di DO-IT, harap segera lunasi pinjaman. Kunjungi goo.gl/RLfJ8Z. Thx.";
                            //logger.info(content);
                            sendSmsToUser(content,userUser,6);
//                                try {
//                                    JPushUtil.jpushAllAlias(order.getUserUuid(), content);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

//                this.sendCollectMessagePerson("?????????????,?????????4?");
            }

        }
    }


    public void sendSmsToUser(String content,UsrUser user,Integer type){
        // ???????
        try {
            String mobileNumberDes = user.getMobileNumberDES();
            String mobileNumber = "62"+ DESUtils.decrypt(mobileNumberDes);
//            String mobileNumber = "81398644017";
//            type  1T0  2T-1 3T-3 4T+1 5T+3 6T+5  7???? 8????
            if (type == 1){
                smsServiceUtil.sendTypeSmsCode("REMIND_SMS_T0",mobileNumber,content);
            }else if (type == 2){
                smsServiceUtil.sendTypeSmsCode("REMIND_SMS_T-1",mobileNumber,content);
            }else if (type == 3){
                smsServiceUtil.sendTypeSmsCode("REMIND_SMS_T-3",mobileNumber,content);
            }else if (type == 4){
                smsServiceUtil.sendTypeSmsCode("REMIND_SMS_T+1",mobileNumber,content);
            }else if (type == 5){
                smsServiceUtil.sendTypeSmsCode("REMIND_SMS_T+3",mobileNumber,content);
            }else if (type == 6){
                smsServiceUtil.sendTypeSmsCode("REMIND_SMS_T+5",mobileNumber,content);
            }else if (type == 7){
                smsServiceUtil.sendTypeSmsCode("REMIND_SMS_REPAY_SUCCESS",mobileNumber,content);
            }else if (type == 8){
                smsServiceUtil.sendTypeSmsCode("REMIND_SMS_ORDER_PASS_CHEAK",mobileNumber,content);
            }


        }catch (Exception e){
            log.info("????????");
            e.printStackTrace();
        }
    }


    public void sendLoanOrderSms(){

        String content = "Halo Nasabah terhormat, pinjaman Anda telah disetujui, dan Anda akan menerima 1 juta rupiah silahkan log in  goo.gl/RLfJ8Z  dana akan langsung cair ke kartu bank anda.";

        // 读取配置 获取每天发送待放款用户短信的个数
        String sysParamValue = this.sysParamService.getSysParamValue(SysParamContants.NUMBER_OF_SMSCOUNT);
        List<LoanUser> loanUserList = this.loanUserDao.getLoanUserListWithCount(Integer.valueOf(sysParamValue));
        log.info("发送待放款用户短信的个数为："+loanUserList.size());
        if (!CollectionUtils.isEmpty(loanUserList)){
            for (LoanUser user:loanUserList){
                try {
                    String mobileNumber = "62"+ user.getUserMobile();
                    smsServiceUtil.sendTypeSmsCode("Not_LOAN_USER",mobileNumber,content);

                    // 发送之后将isSend置为1
                    LoanUser update = new LoanUser();
                    update.setUserUuid(user.getUserUuid());
                    update.setIsSend(1);
                    update.setId(user.getId());
                    update.setUpdateTime(new Date());
                    this.loanUserDao.update(update);

                }catch (Exception e){
                    log.info("发送发送待放款用户短信异常");
                    log.error(e.getMessage(),e);
                }
            }
        }
        log.info("发送待放款用户短信完成");
    }
}