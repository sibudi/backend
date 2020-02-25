package com.yqg.service.system.service;

import com.yqg.common.constants.SysParamContants;
import com.yqg.common.utils.*;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdBill;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.notification.request.NotificationRequest;
import com.yqg.service.notification.service.EmailNotificationService;
import com.yqg.service.notification.service.FcmNotificationService;
import com.yqg.service.order.OrdBillService;
import com.yqg.service.third.sms.SmsServiceUtil;
import com.yqg.service.user.service.UsrService;
import com.yqg.user.dao.RegisterDeviceInfoDao;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Setiya Budi on 2020/02/14.
 */
@Service
@Slf4j
public class FcmRemindService {

    @Autowired
    private EmailNotificationService emailNotificationService;
    @Autowired
    private FcmNotificationService fcmNotificationService;
    @Autowired
    private UsrService usrService;
    @Autowired
    private SysParamService sysParamService;
    @Autowired
    private SmsServiceUtil smsServiceUtil;
    @Autowired
    private OrdDao ordDao;
    @Autowired
    private OrdBillService ordBillService;
    @Autowired
    private RegisterDeviceInfoDao registerDeviceInfoDao;

    /**
     * T0 ,??8???
     */
    public void refundBeforeRemindT0() {
        // ??
        String sysParamValue = this.sysParamService.getSysParamValue(SysParamContants.REFUND_BEFORE_REMIND_OFF_NO_8);

        if (!StringUtils.isEmpty(sysParamValue) && Integer.valueOf(sysParamValue) == 1) {

            List<OrdOrder> orderOrders = this.ordDao.getInRepayOrderListWithNotOverdue();

            int overdueDay = 0;
            String content = "";
            String fcmToken = "";
            String repayAmount = "";

            if (!CollectionUtils.isEmpty(orderOrders)) {
                for (OrdOrder order : orderOrders) {

                    UsrUser userUser = this.usrService.getUserByUuid(order.getUserUuid());

                    try {
                        if(order.getOrderType().equals("3")) {
                            OrdBill scan = new OrdBill();
                            scan.setDisabled(0);
                            scan.setOrderNo(order.getUuid());
                            scan.setUserUuid(order.getUserUuid());
                            List<OrdBill> bills = ordBillService.billInfo(scan);
                            OrdBill bill = bills.get(0);
                            repayAmount = StringUtils.formatMoney(bill.getBillAmout().doubleValue()).replaceAll(",",".").toString();
                        } else {
                            repayAmount = StringUtils.formatMoney(order.getAmountApply().doubleValue()).replaceAll(",",".").toString();
                        }

                        overdueDay = (int) DateUtils.daysBetween(DateUtils.formDate(order.getRefundTime(), "yyyy-MM-dd"), DateUtils.formDate(new Date(), "yyyy-MM-dd"));

                        if (overdueDay == 0) {

                            log.info("????????:{}", order.getUuid());
                            //???????
                            content = String.format("Tagihan Do-It sebesar Rp%s akan jatuh tempo hari ini. Bayarkan segera melalui aplikasi untuk menghindari denda.", repayAmount);

                            ////budi remark sendSmsToUser(content,userUser,1);////
                            NotificationRequest notificationRequest = new NotificationRequest();
                            fcmToken = registerDeviceInfoDao.getFcmTokenByUserUuid(order.getUserUuid());
                            ArrayList<String> registration_ids = new ArrayList<String>();
                            registration_ids.add(fcmToken);
                            /*registration_ids.add(
                                    "dVXQjWBEGk4:APA91bGeGg9Zrhm9irByF8aCSrml_wdVAaE4Or9kRyVxlaFJuuCrOR9XOs77BfifiDnu7Gny8iC-24YaC80xXuOsTfd2R2z9YCpPeb65MKJHrQhHuUYgV3mAuYp5ZofuIOr1rfsrwMZd");*/
                            notificationRequest.setRegistration_ids(registration_ids);

                            notificationRequest.setSubject("Tagihan Do-It Anda jatuh tempo hari ini");
                            notificationRequest.setMessage(content);
                            
                            //log.info(data.toString());
                            fcmNotificationService.SendNotification(notificationRequest);

                            notificationRequest.setTo(DESUtils.decrypt(userUser.getEmailAddress()));
                            emailNotificationService.SendNotification(notificationRequest);
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
            String fcmToken = "";
            String repayAmount = "";
            
            if (!CollectionUtils.isEmpty(orderOrders)) {

                for (OrdOrder order : orderOrders) {

                    UsrUser userUser = this.usrService.getUserByUuid(order.getUserUuid());

                    try {
                        
                        if(order.getOrderType().equals("3")) {
                            OrdBill scan = new OrdBill();
                            scan.setDisabled(0);
                            scan.setOrderNo(order.getUuid());
                            scan.setUserUuid(order.getUserUuid());
                            List<OrdBill> bills = ordBillService.billInfo(scan);
                            OrdBill bill = bills.get(0);
                            repayAmount = StringUtils.formatMoney(bill.getBillAmout().doubleValue()).replaceAll(",",".").toString();
                        } else {
                            repayAmount = StringUtils.formatMoney(order.getAmountApply().doubleValue()).replaceAll(",",".").toString();
                        }
                        
                        overdueDay = (int) DateUtils.daysBetween(DateUtils.formDate(order.getRefundTime(), "yyyy-MM-dd"), DateUtils.formDate(new Date(), "yyyy-MM-dd"));

                        if (overdueDay == 0) {
                            //???????
                            content = String.format("Tagihan Do-It sebesar Rp%s akan jatuh tempo hari ini. Bayarkan segera melalui aplikasi untuk menghindari denda.", repayAmount);

                            //sendSmsToUser(content,userUser,1);
                            NotificationRequest notificationRequest = new NotificationRequest();
                            
                            fcmToken = registerDeviceInfoDao.getFcmTokenByUserUuid(order.getUserUuid());
                            ArrayList<String> registration_ids = new ArrayList<String>();
                            registration_ids.add(fcmToken);
                            notificationRequest.setRegistration_ids(registration_ids);

                            notificationRequest.setSubject("Tagihan Do-It Anda jatuh tempo hari ini");
                            notificationRequest.setMessage(content);
                            
                            fcmNotificationService.SendNotification(notificationRequest);

                            notificationRequest.setTo(DESUtils.decrypt(userUser.getEmailAddress()));
                            emailNotificationService.SendNotification(notificationRequest);

                        } else if (overdueDay == -1) {
                            //???????
                            content = String.format("Tagihan Do-It sebesar Rp%s akan jatuh tempo 1 hari lagi. Bayarkan sebelum %s untuk menghindari denda.", repayAmount, DateUtils.formDate(order.getRefundTime(), "dd/MM/yyyy"));
                            //sendSmsToUser(content,userUser,2);
                            NotificationRequest notificationRequest = new NotificationRequest();
                            
                            fcmToken = registerDeviceInfoDao.getFcmTokenByUserUuid(order.getUserUuid());
                            ArrayList<String> registration_ids = new ArrayList<String>();
                            registration_ids.add(fcmToken);
                            notificationRequest.setRegistration_ids(registration_ids);

                            notificationRequest.setSubject("Tagihan Do-It Anda jatuh tempo 1 hari lagi");
                            notificationRequest.setMessage(content);

                            fcmNotificationService.SendNotification(notificationRequest);

                            notificationRequest.setTo(DESUtils.decrypt(userUser.getEmailAddress()));
                            emailNotificationService.SendNotification(notificationRequest);
                        } else if (overdueDay == -3) {
                                //???????
                            content = String.format("Tagihan Do-It sebesar Rp%s akan jatuh tempo 3 hari lagi. Bayarkan sebelum %s untuk menghindari denda.", repayAmount, DateUtils.formDate(order.getRefundTime(), "dd/MM/yyyy"));
        
                            //sendSmsToUser(content,userUser,3);
                            NotificationRequest notificationRequest = new NotificationRequest();
                            
                            fcmToken = registerDeviceInfoDao.getFcmTokenByUserUuid(order.getUserUuid());
                            ArrayList<String> registration_ids = new ArrayList<String>();
                            registration_ids.add(fcmToken);
                            notificationRequest.setRegistration_ids(registration_ids);

                            notificationRequest.setSubject("Tagihan Do-It Anda jatuh tempo 3 hari lagi");
                            notificationRequest.setMessage(content);
                            
                            fcmNotificationService.SendNotification(notificationRequest);

                            notificationRequest.setTo(DESUtils.decrypt(userUser.getEmailAddress()));
                            emailNotificationService.SendNotification(notificationRequest);
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
            String fcmToken = "";
            
            if (!CollectionUtils.isEmpty(orderOrders)) {
                for (OrdOrder order : orderOrders) {

                    UsrUser userUser = this.usrService.getUserByUuid(order.getUserUuid());

                    try {
                        overdueDay = (int) DateUtils.daysBetween(DateUtils.formDate(order.getRefundTime(), "yyyy-MM-dd"), DateUtils.formDate(new Date(), "yyyy-MM-dd"));

                        if (overdueDay == 1) {
                            //???????
                            content = "Tagihan Do-It Anda terlambat 1 hari. Bayarkan segera melalui aplikasi untuk menghindari denda berjalan.";

                            //sendSmsToUser(content,userUser,4);
                            NotificationRequest notificationRequest = new NotificationRequest();
                            
                            fcmToken = registerDeviceInfoDao.getFcmTokenByUserUuid(order.getUserUuid());
                            ArrayList<String> registration_ids = new ArrayList<String>();
                            registration_ids.add(fcmToken);
                            notificationRequest.setRegistration_ids(registration_ids);

                            notificationRequest.setSubject("Tagihan Do-It Anda terlambat 1 hari");
                            notificationRequest.setMessage(content);
                            
                            fcmNotificationService.SendNotification(notificationRequest);

                            notificationRequest.setTo(DESUtils.decrypt(userUser.getEmailAddress()));
                            emailNotificationService.SendNotification(notificationRequest);
                        } else if (overdueDay == 3) {
                            //???????
                            content = "Tagihan Do-It Anda terlambat 3 hari. Bayarkan segera melalui aplikasi untuk menghindari denda berjalan.";

                            //sendSmsToUser(content,userUser,5);
                            NotificationRequest notificationRequest = new NotificationRequest();
                            
                            fcmToken = registerDeviceInfoDao.getFcmTokenByUserUuid(order.getUserUuid());
                            ArrayList<String> registration_ids = new ArrayList<String>();
                            registration_ids.add(fcmToken);
                            notificationRequest.setRegistration_ids(registration_ids);

                            notificationRequest.setSubject("Tagihan Do-It Anda terlambat 3 hari");
                            notificationRequest.setMessage(content);
                            
                            fcmNotificationService.SendNotification(notificationRequest);

                            notificationRequest.setTo(DESUtils.decrypt(userUser.getEmailAddress()));
                            emailNotificationService.SendNotification(notificationRequest);
                        } else if (overdueDay == 5) {
                            //???????
                            content = "Tagihan Do-It Anda terlambat 5 hari. Bayarkan segera melalui aplikasi untuk menghindari denda berjalan.";
                            //sendSmsToUser(content,userUser,6);
                            NotificationRequest notificationRequest = new NotificationRequest();
                            
                            fcmToken = registerDeviceInfoDao.getFcmTokenByUserUuid(order.getUserUuid());
                            ArrayList<String> registration_ids = new ArrayList<String>();
                            registration_ids.add(fcmToken);
                            notificationRequest.setRegistration_ids(registration_ids);

                            notificationRequest.setSubject("Tagihan Do-It Anda terlambat 5 hari");
                            notificationRequest.setMessage(content);
                            
                            fcmNotificationService.SendNotification(notificationRequest);

                            notificationRequest.setTo(DESUtils.decrypt(userUser.getEmailAddress()));
                            emailNotificationService.SendNotification(notificationRequest);
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
    
}