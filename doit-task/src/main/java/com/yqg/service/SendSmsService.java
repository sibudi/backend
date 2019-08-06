package com.yqg.service;

import com.yqg.common.utils.DESUtils;
import com.yqg.order.dao.OrdBlackDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.YituFaildRecord;
import com.yqg.service.third.sms.SmsServiceUtil;
import com.yqg.service.user.service.UsrService;
import com.yqg.system.entity.UserResponse;
import com.yqg.user.dao.UangMobielDao;
import com.yqg.user.dao.UsrDao;
import com.yqg.user.entity.UangMobiel;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by Didit Dwianto on 2018/2/24.
 */
@Service
@Slf4j
public class SendSmsService {

    @Autowired
    private SmsServiceUtil smsServiceUtil;
    @Autowired
    private UsrDao usrDao;
    @Autowired
    private OrdBlackDao ordBlackDao;
    @Autowired
    private UangMobielDao uangMobielDao;
    @Autowired
    private UsrService usrService;



    /**
     *   向do-it 5天内借款成功的用户 发送 五星好评 短信
     * */
    public void sendToLoanSuccessUserWithinFiveDay() throws Exception{

        List<UsrUser> userList = this.usrDao.getUserWithLoanSuccessFiveDay();
        log.info("本次发送短信的的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());

                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                String content = "<Do-It> Pelanggan Yth, kami mengundang anda masuk ke goo.gl/xz5yw2, berikan kami bintang5, pinjaman Anda selanjutnya langsung cair. yuk segera!";
                smsServiceUtil.sendTypeSmsCodeWithType("LOAN_SUCCESS_USER_WITHIN_FIVEDAY",mobileNumber,content,"ZENZIVA");
            }
            log.info("结束发送短信");
        }
    }


    /**
     *   召回  do-it沉默用户  （注册未申请）
     * */
    public void sendSmsToSilenceUser() throws Exception{

        String content = "<Do-It> Yth Nasabah Do-it! Log in ke goo.gl/xz5yw2 dan dptkan 1 juta rupiah. Jika pinjaman Anda berhasil, Anda akn bebas verif selamanya! ";

        List<UsrUser> userList = this.usrDao.getSilenceUser();

        log.info("本次发送短信的的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());

                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                this.smsServiceUtil.sendTypeSmsCode("SILENCE_USER",mobileNumber, content);
            }
            log.info("结束发送短信");
        }
    }

    /**
     *   召回  do-it沉默用户  （申请未提交）
     * */
    public void sendSmsToSilenceUser2() throws Exception{

        String content = "<Do-It>  Slamat! Anda mendpatkn ksempatn utk ajukan kredit s.d. Rp 1 jt, dftarkan diri Anda skrg ke goo.gl/RLfJ8Z ajak teman Anda & dapatkn bonus!";

        List<UsrUser> userList = this.usrDao.getSilenceUser2();

        log.info("本次发送短信的的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());

                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                smsServiceUtil.sendTypeSmsCodeWithType("REGIST_NOT_APPLY",mobileNumber,content,"ZENZIVA");
            }
            log.info("结束发送短信");
        }
    }

    /**
     *   尊敬的Do-It客户您好，付款过程中可以优先使用alfamart支付，如有其他还款问题可以拨打我们的客服热线
     * */
    public void sendSmsToUseAlfamart() throws Exception{

        String content = "[Do-It] Halo! Terkait pelunasan anda, anda bs memilih  jalur Alfamart sebagai pilihan prioritas . Jika menemui kendala, bisa menghubungi CS kami";

        List<UsrUser> userList = this.usrDao.sendSmsToUseAlfamart();

        log.info("付款过程中可以优先使用alfamart支付的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());

                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                smsServiceUtil.sendTypeSmsCodeWithType("USE_ALFAMART_PAY",mobileNumber,content,"ZENZIVA");
            }
            log.info("结束发送短信");
        }
    }

    public static void main(String[] args) {
        String content = "<Do-It> Yth Nasabah Do-it! Log in ke goo.gl/RLfJ8Z dan dapatkn 1 juta rupiah. Jika pinjaman Anda berhasil, Anda akn bebas verif selamanya! Semoga Anda bahagia!";

        log.info(content.length()+"");
    }

    /**
     *   召回  do-it沉默用户  （还款成功后不再复借用户）
     * */
    public void sendSmsToSilenceUser3() throws Exception{

        String content = "[Do-It] Hi Do-Iters, saat ini bunga pinjaman kami sudah lebih kecil, lakukan peminjaman dan dapatkan pinjaman hingga 2 juta di goo.gl/RLfJ8Z";
        List<UserResponse> userList = this.usrDao.getSilenceUser3();

        log.info("the sendSmsToSilenceUser3 count is {}", userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("start send sms");
            for (UserResponse user : userList){

                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                log.info("the sendSmsToSilenceUser3 user mobileNumberDes is {}", mobileNumberDes);
                // 发送提醒短信
                smsServiceUtil.sendTypeSmsCodeWithType("sendSmsToSilenceUser3",mobileNumber,content,"ZENZIVA");
            }
            log.info("end sendSmsToSilenceUser3");
        }
    }


    /**
     *   召回  复审流程问题 取消订单的用户
     * */
    public void sendSmsToCancleUser() throws Exception{


        String content = "<Do-It> Selamat！Pinjaman anda sebesar Rp 1.000.000 di DO-IT telah cair。Silahkan log in ke APP ，refresh，isi ulang data anda，lalu anda bisa mendapatkan uang anda setelah proses verifikasi. ";

        List<UsrUser> userList = this.usrDao.getCancleUser();

        log.info("本次发送短信的的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());

                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                this.smsServiceUtil.sendTypeSmsCode("Not_LOAN_USER",mobileNumber, content);
            }
            log.info("结束发送短信");
        }
    }

    /**
     *   Do-It 沉默用户发送短信
     * */
    public void sendSmsToUangUser() throws Exception{

        String content = "<Do-It> Yth Nasabah Do-it! Log in ke goo.gl/RLfJ8Z dan dptkan 1 juta rupiah. Jika pinjaman Anda berhasil, Anda akn bebas verif selamanya!";

        List<UangMobiel> mobielList = this.uangMobielDao.getUangMobie();

        log.info("本次发送短信的的用户个数"+mobielList.size());
        if (!CollectionUtils.isEmpty(mobielList)){
            log.info("开始发送短信");

            for (UangMobiel uangMobiel : mobielList){

                String mobileNumberDes = uangMobiel.getMobielDes();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                log.info("本次发送短信的的用户手机号："+mobileNumber);
                smsServiceUtil.sendTypeSmsCodeWithType("UANG_USER",mobileNumber,content,"ZENZIVA");

                uangMobiel.setRemark("1");
                this.uangMobielDao.update(uangMobiel);
            }
            log.info("结束发送短信");
        }
    }

    /**
     *   向处于待绑卡的用户发送短信
     * */
    public void sendSmsToUserWithBindCardFaild() throws Exception{
        List<UsrUser> userList = this.usrDao.getUserWithBindCardFaild();
        log.info("本次发送短信的的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());
                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                String content = "<Do-It> Slamat! Anda mendpatkn ksempatn utk ajukan kredit s.d. Rp 1 jt, dftarkan diri Anda skrg ke goo.gl/RLfJ8Z ajak teman Anda & dapatkn bonus!";
                smsServiceUtil.sendTypeSmsCodeWithType("USER_WITH_ORDER_NOT_BIND_CARD",mobileNumber,content,"ZENZIVA");
            }
            log.info("结束发送短信");
        }
    }


    /**
     *   给待放款的用户发送短信
     * */
    public void sendSmsToUserNotLoan() throws Exception{
        List<UsrUser> userList = this.usrDao.getUserWithNotLoan();
        log.info("本次发送短信的的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());
                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                String content = "<Do-It> Slamat! Anda mendpatkn ksempatn utk ajukan kredit s.d. Rp 1 jt, dftarkan diri Anda skrg ke goo.gl/RLfJ8Z ajak teman Anda & dapatkn bonus!";
                smsServiceUtil.sendTypeSmsCodeWithType("USER_WITH_NOT_LOAN",mobileNumber,content,"ZENZIVA");
            }
            log.info("结束发送短信");
        }
    }


    /**
     *   给所有在贷的用户发送短信
     * */
    public void sendSmsToUserWithLoaning() throws Exception{
        List<UsrUser> userList = this.usrDao.getUserWithLoaning();
        log.info("本次发送短信的的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());
                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                String content = "<Do-It> Pelunasan hny menggunakan VA yg ada di APP Do-It. Jgn mempercayai cara lain utk menghindari kegagalan pembayaran. Jika ada masalah HUB  087787117873.";
                smsServiceUtil.sendTypeSmsCodeWithType("USER_WITH_NOT_LOAN",mobileNumber,content,"ZENZIVA");
            }
            log.info("结束发送短信");
        }
    }

    /**
     *   给所有还款没有复借的用户发送短信
     * */
    public void sendSmsToUserWithNotRebrow() throws Exception{
        List<String> mobileList = this.usrDao.getUserMobileWithNotRebrow();
        log.info("本次发送短信的的用户个数"+mobileList.size());
        if (!CollectionUtils.isEmpty(mobileList)){
            log.info("开始发送短信");
            for (String mobileDes : mobileList){
                String mobileNumber = "62" + DESUtils.decrypt(mobileDes);
                log.info("本次发送短信的的用户手机号："+mobileNumber);
                // 发送提醒短信
                String content = "<Do-It>Yth Nasabah Do-It! Log in ke goo.gl/RLfJ8Z dan dptkan 1.2 juta rupiah. Jika pinjaman Anda berhasil, Anda akn bebas verif selamanya! ";
                smsServiceUtil.sendTypeSmsCodeWithType("USER_WITH_NOT_LOAN",mobileNumber,content,"ZENZIVA");
            }
            log.info("结束发送短信");
        }
    }

    /**
     *   给 已借款的用户 发送邀请好友的活动
     * */
    public void sendSmsToUserInviteFriend() throws Exception{

        List<String> mobileList = this.usrDao.getUserMobileWithInviteFriend();
        log.info("本次发送短信的的用户个数"+mobileList.size());
        if (!CollectionUtils.isEmpty(mobileList)){
            log.info("开始发送短信");
            for (String mobileDes : mobileList){
                String mobileNumber = "62" + DESUtils.decrypt(mobileDes);
                log.info("本次发送短信的的用户手机号："+mobileNumber);
                // 发送提醒短信
                String content = "<Do-It> Halo! Butuh uang? Kami memiliki program “Undang Teman”. 50k org  sdh mendapatkan komisi. Tunggu apa lagi & dapatkn uang anda! goo.gl/9gY9De";
                smsServiceUtil.sendTypeSmsCodeWithType("USER_INVITE_FRIEND",mobileNumber,content,"ZENZIVA");
            }
            log.info("结束发送短信");
        }
    }

    /**
     *  给 联系人填写被误拒 的用户发送邀请好友短信
     * */
    public void sendSmsToUserWithAutoCallFaild() throws Exception{

        List<UsrUser> userList = this.usrDao.getUserMobileWithVervifyFaild();
        log.info("本次发送短信的的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());
                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                String content = "<Do-It> Halo! Pengajuan ditolak krn tdk mengisi kontak. Ajukan lg permohonan & lengkapi kontak anda,  anda akn berkesempatan mendptkn Rp 1.200.000.";
                smsServiceUtil.sendTypeSmsCodeWithType("USER_WITH_NOT_LOAN",mobileNumber,content,"ZENZIVA");
            }
            log.info("结束发送短信");
        }
    }

//    /**
//     *  给 税卡认证被拒 的用户发送邀请好友短信
//     * */
//    public void sendSmsToUserWithVervifyFaild() throws Exception{
//
//        List<UsrUser> userList = this.usrDao.getUserMobileWithVervifyFaild();
//        log.info("本次发送短信的的用户个数"+userList.size());
//        if (!CollectionUtils.isEmpty(userList)){
//            log.info("开始发送短信");
//            for (UsrUser user : userList){
//                log.info("本次发送短信的的用户id："+user.getUuid());
//                String mobileNumberDes = user.getMobileNumberDES();
//                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
//                // 发送提醒短信
//                String content = "<Do-It>Halo!  Sistem pengenalan wajah di APP sdh diupdate. Mhn ajukan  permohonan  anda di https://goo.gl/8ub47y. Anda dpt segera mencairkan  Rp. 1 jt";
//                smsServiceUtil.sendTypeSmsCodeWithType("USER_WITH_NOT_LOAN",mobileNumber,content,"ZENZIVA");
//            }
//            log.info("结束发送短信");
//        }
//    }



    /**
     *   给所有借款成功用户发送短信   五星好评 加 邀请好友
     * */
    public void sendSmsToUserInviteFriendWithFiveStar() throws Exception{

        List<String> mobileList = this.usrDao.getUserMobileWithInviteFriend();
        log.info("本次发送短信的的用户个数"+mobileList.size());
        if (!CollectionUtils.isEmpty(mobileList)){
            log.info("开始发送短信");
            for (String mobileDes : mobileList){
                String mobileNumber = "62" + DESUtils.decrypt(mobileDes);
                log.info("本次发送短信的的用户手机号："+mobileNumber);
                // 发送提醒短信
                String content = "<Do-It> Halo! Butuh uang? Kami memiliki program “Undang Teman”. 50k org  sdh mendapatkan komisi. Tunggu apa lagi & dapatkn uang anda! goo.gl/9gY9De";
                smsServiceUtil.sendTypeSmsCodeWithType("USER_INVITE_FRIEND",mobileNumber,content,"ZENZIVA");

                content = "<Do-It> Pelanggan Yth, kami mengundang anda masuk ke goo.gl/xz5yw2, berikan kami bintang5, pinjaman Anda selanjutnya langsung cair. yuk segera!";
                smsServiceUtil.sendTypeSmsCodeWithType("LOAN_SUCCESS_USER_WITHIN_FIVEDAY",mobileNumber,content,"ZENZIVA");
            }
            log.info("结束发送短信");
        }
    }


    /**
     *   风控规则拒绝 召回（gojek邮箱是不一致&gojek手机号是不一致-iOS）
     * */
    public void sendSmsToUserWithRefuce() throws Exception{

        List<UsrUser> userList = this.usrDao.sendSmsToUseWithRefuse();
        log.info("本次发送短信的的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());
                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                String content = "<Do-It> Yth,Informasi Anda kurang lengkap.Silakan ke goo.gl/RLfJ8Z untuk mendaftar ulang&lengkapi informasi,Anda akan mendapat kesempatan Rp.1,2Jt.Terima kasih";
                smsServiceUtil.sendTypeSmsCodeWithType("USER_WITH_REFUSE",mobileNumber,content,"ZENZIVA");
            }
            log.info("结束发送短信");
        }
    }

    /**
     *   风控规则拒绝 召回
     * */
    public void sendSmsToUserWithRefuce2() throws Exception{

        List<UsrUser> userList = this.usrDao.sendSmsToUseWithRefuse2();
        log.info("本次发送短信的的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());
                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                String content = "<Do-It> Yth,Informasi Anda kurang lengkap.Silakan ke goo.gl/RLfJ8Z untuk mendaftar ulang&lengkapi informasi,Anda akan mendapat kesempatan Rp.1,2Jt.Terima kasih";
                smsServiceUtil.sendTypeSmsCodeWithType("USER_WITH_REFUSE",mobileNumber,content,"ZENZIVA");

            }
            log.info("结束发送短信");
        }
    }



    /**
     *   风控规则拒绝 召回
     * */
    public void sendSmsToUserWithRefuce3() throws Exception{

        List<UsrUser> userList = this.usrDao.sendSmsToUseWithRefuse3();
        log.info("本次发送短信的的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());
                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                String content = "<Do-It> Yth,Informasi Anda kurang lengkap.Silakan ke goo.gl/RLfJ8Z untuk mendaftar ulang&lengkapi informasi,Anda akan mendapat kesempatan Rp.1,2Jt.Terima kasih";
                smsServiceUtil.sendTypeSmsCodeWithType("USER_WITH_REFUSE",mobileNumber,content,"ZENZIVA");

            }
            log.info("结束发送短信");
        }
    }


//    /**
//     *   营销短信发送  20181203  10W
//     * */
//    public void sendSmsOn20181203() throws Exception{
//
//            log.info("开始发送短信");
//            smsServiceUtil.sendSms();
//            log.info("结束发送短信");
//
//    }

    /**
     *   每日定时发送  申请未提交
     * */
    public void sendSmsToUserNotVerifyOrder() throws Exception{

        List<UserResponse> userList = this.usrDao.sendSmsToUseWithNotVerifyOrder();
        log.info("sendSmsToUserNotVerifyOrder count is :"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("start sendSmsToUserNotVerifyOrder sms.");
            for (UserResponse user : userList){
                log.info("sendSmsToUserNotVerifyOrder user id is : " + user.getUuid());
                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                String content = "[Do-It]  Yth Nasabah Do-It! Log in ke goo.gl/RLfJ8Z dan dptkan 2 juta rupiah. Jika pinjaman Anda berhasil, Anda akn bebas verif selamanya!";
                smsServiceUtil.sendTypeSmsCodeWithType("sendSmsToUserNotVerifyOrder",mobileNumber,content,"ZENZIVA");

            }
            log.info("end sendSmsToUserNotVerifyOrder");
        }

    }

    public void sendReduceSms() throws Exception{

        List<UserResponse> userList = this.usrDao.sendReduceSms();
        log.info("sendReduceSms count is :"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("start sendReduceSms sms.");
            for (UserResponse user : userList){
                log.info("sendReduceSms user id is : " + user.getUuid());
                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                String content = "[Do-It] Pelanggan Terhormat, verifikasi anda tlh berhasil. Silahkan login ke goo.gl/RLfJ8Z , klik konfirmasi, uang akn sgr masuk rekening anda. Slmt meminjam!";
                smsServiceUtil.sendTypeSmsCodeWithType("sendReduceSms",mobileNumber,content,"ZENZIVA");

            }
            log.info("end sendReduceSms");
        }

    }

    /**
     *   每周定时发送  20w已还款用户 没有再复借
     * */
    public void sendSmsToUserNotReLoanAfter20W() throws Exception{

        List<UsrUser> userList = this.usrDao.sendSmsToUseWithNotReLoanAfter20W();
        log.info("本次发送短信的的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());
                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                String content = "<Do-It> Terima kasih atas pembayaran anda! Krn kredit anda bagus, maka limit anda naik dan bebas verifikasi, log in ke goo.gl/RLfJ8Z untuk pencairan!";
                smsServiceUtil.sendTypeSmsCodeWithType("USER_WITH_NOT_VERYFY_LOAN",mobileNumber,content,"ZENZIVA");

            }
            log.info("结束发送短信");
        }

    }

//    /**
//     *   营销短信发送  20181204   2W
//     * */
//    public void sendSmsOn20181204() throws Exception{
//
//        log.info("开始发送短信");
//        smsServiceUtil.sendSms();
//        log.info("结束发送短信");
//    }

//    /**
//     *   营销短信发送  20181205   15695
//     * */
//    public void sendSmsOn20181205() throws Exception{
//
//        log.info("开始发送短信");
//        smsServiceUtil.sendSms();
//        log.info("结束发送短信");
//    }


//    /**
//     *   营销短信发送  20181206   2w
//     * */
//    public void sendSmsOn20181206() throws Exception{
//
//        log.info("开始发送短信");
//        smsServiceUtil.sendSms();
//        log.info("结束发送短信");
//    }

    /**
     *   营销短信发送  20181207   2w
     * */
    public void sendSmsOn20181207() throws Exception{

        log.info("开始发送短信");
        smsServiceUtil.sendSms("5");
        log.info("结束发送短信");
    }

    /**
     *   营销短信发送  20181208   2w
     * */
    public void sendSmsOn20181208() throws Exception{

        log.info("开始发送短信");
        smsServiceUtil.sendSms("6");
        log.info("结束发送短信");
    }

    /**
     *   营销短信发送  20181209   2w
     * */
    public void sendSmsOn20181209() throws Exception{

        log.info("开始发送短信");
        smsServiceUtil.sendSms("7");
        log.info("结束发送短信");
    }

    /**
     *   营销短信发送  20181210   9000
     * */
    public void sendSmsOn20181210() throws Exception{

        log.info("开始发送短信");
        smsServiceUtil.sendSms("8");
        log.info("结束发送短信");
    }




    /**
     *   风控规则拒绝 召回（公司电话不是大雅加达和爪哇岛和巴厘岛区号开头）
     * */
    public void sendSmsToUserWithRefuse20181210() throws Exception{

        List<UsrUser> userList = this.usrDao.sendSmsToUseWithRefuse20181210();
        log.info("本次发送短信的的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());
                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                String content = "<Do-It> Kabar gembira! Proses verifikasi Do-It kini menjadi lebih mudah! Silahkan coba lagi untuk mendapatkan 1,2 juta. klik disini goo.gl/RLfJ8Z";
                smsServiceUtil.sendTypeSmsCodeWithType("USER_WITH_REFUSE",mobileNumber,content,"ZENZIVA");
            }
            log.info("结束发送短信");
        }
    }

    /**
     *   风控规则拒绝 召回（工作地址不属于雅加达大区和爪哇岛和巴厘岛）
     * */
    public void sendSmsToUserWithRefuse20181211() throws Exception{

        List<UsrUser> userList = this.usrDao.sendSmsToUseWithRefuse2018121101();
        log.info("本次发送短信的的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());
                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                String content = "<Do-It> Kabar gembira! Proses verifikasi Do-It kini menjadi lebih mudah! Silahkan coba lagi untuk mendapatkan 1,2 juta. klik disini goo.gl/RLfJ8Z";
                smsServiceUtil.sendTypeSmsCodeWithType("USER_WITH_REFUSE",mobileNumber,content,"ZENZIVA");
            }
            log.info("结束发送短信");
        }
    }

    /**
     *   风控规则拒绝 召回（手机通话记录为空）
     * */
    public void sendSmsToUserWithRefuse2018121102() throws Exception{

        List<UsrUser> userList = this.usrDao.sendSmsToUseWithRefuse2018121102();
        log.info("本次发送短信的的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());
                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                String content = "<Do-It> Kabar gembira! Proses verifikasi Do-It kini menjadi lebih mudah! Silahkan coba lagi untuk mendapatkan 1,2 juta. klik disini goo.gl/RLfJ8Z";
                smsServiceUtil.sendTypeSmsCodeWithType("USER_WITH_REFUSE",mobileNumber,content,"ZENZIVA");
            }
            log.info("结束发送短信");
        }
    }

    /**
     *   风控规则拒绝 召回（去重通讯录个数）
     * */
    public void sendSmsToUserWithRefuse2018121103() throws Exception{

        List<UsrUser> userList = this.usrDao.sendSmsToUseWithRefuse2018121103();
        log.info("本次发送短信的的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());
                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                String content = "<Do-It> Kabar gembira! Proses verifikasi Do-It kini menjadi lebih mudah! Silahkan coba lagi untuk mendapatkan 1,2 juta. klik disini goo.gl/RLfJ8Z";
                smsServiceUtil.sendTypeSmsCodeWithType("USER_WITH_REFUSE",mobileNumber,content,"ZENZIVA");
            }
            log.info("结束发送短信");
        }
    }

    /**
     *   风控规则拒绝 召回（手机使用时长（单位：天））
     * */
    public void sendSmsToUserWithRefuse2018121104() throws Exception{

        List<UsrUser> userList = this.usrDao.sendSmsToUseWithRefuse2018121104();
        log.info("本次发送短信的的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());
                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                String content = "<Do-It> Kabar gembira! Proses verifikasi Do-It kini menjadi lebih mudah! Silahkan coba lagi untuk mendapatkan 1,2 juta. klik disini goo.gl/RLfJ8Z";
                smsServiceUtil.sendTypeSmsCodeWithType("USER_WITH_REFUSE",mobileNumber,content,"ZENZIVA");
            }
            log.info("结束发送短信");
        }
    }

    /**
     *   营销短信发送  20181220   1.2w
     * */
    public void sendSmsOn2018122001() throws Exception{

        log.info("开始发送短信");
        smsServiceUtil.sendSms("9");
        log.info("结束发送短信");
    }


    /**
     *   营销短信发送  20181220   7w
     * */
    public void sendSmsOn2018122002() throws Exception{

        log.info("开始发送短信");
        smsServiceUtil.sendSms("10");
        log.info("结束发送短信");
    }

    /**
     *   营销短信发送  20181221   7w
     * */
    public void sendSmsOn20181221() throws Exception{

        log.info("开始发送短信");
        smsServiceUtil.sendSms("11");
        log.info("结束发送短信");
    }

    /**
     *   发送风控召回短信
     * */
    public void sendSmsToRisk() throws Exception{
        log.info("开始发送短信");
        smsServiceUtil.sendSmsToRisk("riskSms.xlsx","<Do-It> Kabar gembira! Proses verifikasi Do-It kini menjadi lebih mudah! Silahkan coba lagi untuk mendapatkan 1,2 juta. klik disini goo.gl/RLfJ8Z");
        log.info("开始发送短信");
    }

    /**
     *   召回  do-it沉默用户  （一周内申请未提交）
     * */
    public void sendSmsToSilenceUser2WithOneWeek() throws Exception{

        String content = "<Do-It>  Slamat! Anda mendpatkn ksempatn utk ajukan kredit s.d. Rp 1 jt, dftarkan diri Anda skrg ke goo.gl/RLfJ8Z ajak teman Anda & dapatkn bonus!";

        List<UsrUser> userList = this.usrDao.getSilenceUser2WithOneWeek();

        log.info("本次发送短信的的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());

                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                smsServiceUtil.sendTypeSmsCodeWithType("REGIST_NOT_APPLY",mobileNumber,content,"ZENZIVA");
            }
            log.info("结束发送短信");
        }
    }

    /**
     *   借款成功的用户  海外搞得分享活动  协助发个参与短信
     * */
    public void sendSmsToOutseaUser() throws Exception{

        String content = "<Do-It>  Cuman Selfie dapet 8,5 JUTA?! Ikut aja Do-It Photo Competition di Instagram, periode 22 Des- 31 Des 2018, Syarat dan Ketentuan: https://bit.ly/2EMlxsf";

        List<UsrUser> userList = this.usrDao.getUserWithHadLoan();

        log.info("本次发送短信的的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());

                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                smsServiceUtil.sendTypeSmsCodeWithType("REGIST_NOT_APPLY",mobileNumber,content,"ZENZIVA");
            }
            log.info("结束发送短信");
        }
    }


    /**
     *   营销短信发送  20181227   2w
     * */
    public void sendSmsOn20181227() throws Exception{

        log.info("开始发送短信");
        smsServiceUtil.sendSms("12");
        log.info("结束发送短信");
    }


    /**
     *   发送 待审核用户 安抚短信
     * */
    public void sendSmsToWithWaitingForReview() throws Exception{

        String content = "Yth Nasabah Do-It! Pengajuan anda  dlm proses verifikasi. Status terbaru dpt anda lihat di APP. Harap menunggu hasil verifikasi anda. Tks!";

        List<UsrUser> userList = this.usrDao.getUserWithWaitingForReview();

        log.info("本次发送短信的的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());

                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                smsServiceUtil.sendTypeSmsCodeWithType("WAITING_FOR_REVIEW",mobileNumber,content,"ZENZIVA");
            }
            log.info("结束发送短信");
        }
    }



    /**
     *   营销短信发送  20190102   1w
     * */
    public void sendSmsOn20190102() throws Exception{

        log.info("开始发送短信");
        smsServiceUtil.sendSms("13");
        log.info("结束发送短信");
    }

    /**
     *   营销短信发送  20190109   2.5w
     * */
    public void sendSmsOn20190109() throws Exception{

        log.info("开始发送短信");
        smsServiceUtil.sendSms("14");
        log.info("结束发送短信");
    }


    /**
     *   营销短信发送  20190110   2.3w
     * */
    public void sendSmsOn20190110() throws Exception{

        log.info("开始发送短信");
        smsServiceUtil.sendSms("15");
        log.info("结束发送短信");
    }

    /**
     *   营销短信发送  20190115   2.5w
     * */
    public void sendSmsOn20190115() throws Exception{

        log.info("开始发送短信");
        smsServiceUtil.sendSms("16");
        log.info("结束发送短信");
    }

    /**
     *   营销短信发送  20190119   3w
     * */
    public void sendSmsOn20190119() throws Exception{

        log.info("开始发送短信");
        smsServiceUtil.sendSms("17");
        log.info("结束发送短信");
    }

    /**
     *   召回  do-it沉默用户  （两周内申请未提交）
     * */
    public void sendSmsToSilenceUser2WithTwoWeek() throws Exception{

        String content = "<Do-It>  Slamat! Anda mendpatkn ksempatn utk ajukan kredit s.d. Rp 1 jt, dftarkan diri Anda skrg ke goo.gl/RLfJ8Z ajak teman Anda & dapatkn bonus!";

        List<UsrUser> userList = this.usrDao.getSilenceUser2WithTwoWeek();

        log.info("本次发送短信的的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());

                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                smsServiceUtil.sendTypeSmsCodeWithType("REGIST_NOT_APPLY",mobileNumber,content,"ZENZIVA");
            }
            log.info("结束发送短信");
        }
    }

    /**
     *   召回  do-it沉默用户  （两周之内注册未申请）
     * */
    public void sendSmsToSilenceUserWithTwoWeek() throws Exception{

        String content = "<Do-It> Yth Nasabah Do-it! Log in ke goo.gl/xz5yw2 dan dptkan 1 juta rupiah. Jika pinjaman Anda berhasil, Anda akn bebas verif selamanya! ";

        List<UsrUser> userList = this.usrDao.getSilenceUserWithTwoWeek();

        log.info("本次发送短信的的用户个数"+userList.size());
        if (!CollectionUtils.isEmpty(userList)){
            log.info("开始发送短信");
            for (UsrUser user : userList){
                log.info("本次发送短信的的用户id："+user.getUuid());

                String mobileNumberDes = user.getMobileNumberDES();
                String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
                // 发送提醒短信
                smsServiceUtil.sendTypeSmsCodeWithType("REGIST_NOT_APPLY",mobileNumber,content,"ZENZIVA");
            }
            log.info("结束发送短信");
        }
    }



    /**
     *   营销短信发送  20190124   2w
     * */
    public void sendSmsOn20190124() throws Exception{

        log.info("开始发送短信");
        smsServiceUtil.sendSms("18");
        log.info("结束发送短信");
    }


    /**
     *   营销短信发送  20190125   2w
     * */
    public void sendSmsOn20190125() throws Exception{

        log.info("开始发送短信");
        smsServiceUtil.sendSms("19");
        log.info("结束发送短信");
    }

    /**
     *   营销短信发送  20190126   2w
     * */
    public void sendSmsOn20190126() throws Exception{

        log.info("开始发送短信");
        smsServiceUtil.sendSms("20");
        log.info("结束发送短信");
    }


    /**
     *   营销短信发送  20190218   5w
     * */
    public void sendSmsOn20190218() throws Exception{

        log.info("开始发送短信");
        smsServiceUtil.sendSms("21");
        log.info("结束发送短信");
    }
}
