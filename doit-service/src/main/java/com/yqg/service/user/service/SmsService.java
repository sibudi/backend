package com.yqg.service.user.service;

import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.SmsTypeEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.BadRequestException;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.SmsRequest;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.*;
import com.yqg.service.third.sms.SmsServiceUtil;
import com.yqg.service.user.response.SmsCheckInforbipResponse;
import com.yqg.service.user.response.SmsVeriResponse;
import com.yqg.user.dao.UsrDao;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Component
@Slf4j
public class SmsService {

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private UsrDao usrDao;
    @Value("${count.smsCount}")
    private String smsCount;
    @Autowired
    private SmsServiceUtil smsServiceUtil;

    private List<String> testNumbers = Arrays.asList("81398644017","80000000000");
    /**
     * ???????????
     * @param mobileNumber
     * @param smsCode
     */
    public  void checkSmsCode(String mobileNumber, String smsCode) throws Exception {

        //判断手机号第一位是否是0，是0就去掉
        if(mobileNumber.substring(0,1).equals("0")){
            mobileNumber = mobileNumber.substring(1,mobileNumber.length());
        }
        String newMobileNumber = "62"+mobileNumber;

        StringBuilder stringBuilder=new StringBuilder(RedisContants.SESSION_SMS_KEY+"count");
        stringBuilder.append(newMobileNumber);
        stringBuilder.append(DateUtils.DateToString(new Date()));
        String smsNum = redisClient.get(stringBuilder.toString());
        int smsCount = 0;
        if (!StringUtils.isEmpty(smsNum)){
            smsCount  = Integer.valueOf(smsNum) - 1;
        }
        log.info("sms count:{}",smsCount);

//        zenziva -》 inforbip -》 twilio -》zenziva

        if (smsCount == 1){
            if(!testNumbers.contains(mobileNumber)){
                SmsCheckInforbipResponse response = smsServiceUtil.sendSmsByInforbipVerify(smsCode,newMobileNumber);
                if (!response.getVerified().equals("true")){
                    throw new ServiceException(ExceptionEnum.USER_CHECK_SMS_CODE_ERROR);
                }
            }
        }else if (smsCount == 2){

            if(!testNumbers.contains(mobileNumber)){
                SmsVeriResponse response = smsServiceUtil.sendSmsByTwilioVerify(smsCode,newMobileNumber);
                if (!response.getCode().equals("0")){
//           log.info("验证码错误----------->",smsCode);
                    throw new ServiceException(ExceptionEnum.USER_CHECK_SMS_CODE_ERROR);
                }
            }
        }else {

            StringBuilder stringBuilder2=new StringBuilder(RedisContants.SESSION_SMS_KEY);
            stringBuilder2.append(newMobileNumber);
            String code = redisClient.get(stringBuilder2.toString());
            log.info("smscode{}---------------------------------------code{}",smsCode,code);
            if(!smsCode.equals(code)) {
//                log.info("????????----------->",code);
                throw new ServiceException(ExceptionEnum.USER_CHECK_SMS_CODE_ERROR);
            }
        }

    }

    //Janhsen: temporary to make sure sms only send for OTP
    public  void checkSmsCodeV2(String mobileNumber, String smsCode) throws Exception {

        //判断手机号第一位是否是0，是0就去掉
        if(mobileNumber.substring(0,1).equals("0")){
            mobileNumber = mobileNumber.substring(1,mobileNumber.length());
        }
        String newMobileNumber = "62"+mobileNumber;

        StringBuilder stringBuilder=new StringBuilder(RedisContants.SESSION_SMS_KEY+"count");
        stringBuilder.append(newMobileNumber);
        stringBuilder.append(DateUtils.DateToString(new Date()));
        String smsNum = redisClient.get(stringBuilder.toString());
        int smsCount = 0;
        if (!StringUtils.isEmpty(smsNum)){
            smsCount  = Integer.valueOf(smsNum) - 1;
        }
        log.info("checkSmsCodeV2 sms count:{}",smsCount);

//        zenziva -》 inforbip -》 twilio -》zenziva

        if (smsCount == 1){
            UsrUser usrUser =new UsrUser();
            String mobileNumberDES = DESUtils.encrypt(mobileNumber);
            usrUser.setMobileNumberDES(mobileNumberDES);
            usrUser.setStatus(1);
            usrUser.setDisabled(0);
            List<UsrUser> userList = this.usrDao.scan(usrUser);
            String content="";
            if (CollectionUtils.isEmpty(userList)) {
                content = "<Do-It> Kode Anda adalah "+smsCode+", gunakan kode untuk pendaftaran di Do-It. Terima kasih atas kepercayaan Anda.";
            }else {
                content="<Do-It> Kode masuk Anda adalah "+smsCode+".ini adalah rahasia, DILARANG MEMBERIKAN KODE KE SIAPAPUN.";
            }

            smsServiceUtil.sendTypeSmsCodeWithTypeV2("LOGIN",mobileNumber,content,"ZENZIVA");

            log.info("checkSmsCodeV2 sms using ZENZIVA {0} {1}" + mobileNumber, content);
        }else if (smsCount == 2){

            if(!testNumbers.contains(mobileNumber)){
                SmsCheckInforbipResponse response = smsServiceUtil.sendSmsByInforbipVerify2(smsCode,newMobileNumber);
                if (!response.getVerified().equals("true")){
                    throw new ServiceException(ExceptionEnum.USER_CHECK_SMS_CODE_ERROR);
                }
            }

            log.info("checkSmsCodeV2 sms using Inforbip {0} {1}", smsCode,newMobileNumber);

//             if(!testNumbers.contains(mobileNumber)){
//                 SmsVeriResponse response = smsServiceUtil.sendSmsByTwilioVerify(smsCode,newMobileNumber);
//                 if (!response.getCode().equals("0")){
// //           log.info("验证码错误----------->",smsCode);
//                     throw new ServiceException(ExceptionEnum.USER_CHECK_SMS_CODE_ERROR);
//                 }
//             }
        }else {

            StringBuilder stringBuilder2=new StringBuilder(RedisContants.SESSION_SMS_KEY);
            stringBuilder2.append(newMobileNumber);
            String code = redisClient.get(stringBuilder2.toString());
            log.info("smscode{}---------------------------------------code{}",smsCode,code);
            if(!smsCode.equals(code)) {
//                log.info("????????----------->",code);
                throw new ServiceException(ExceptionEnum.USER_CHECK_SMS_CODE_ERROR);
            }

            log.info("sms using other {0} {1}", smsCode,code);
        }

    }
    public void sendSmsCode(SmsRequest smsRequest) throws Exception {

        //判断手机号第一位是否是0，是0就去掉
        String mobileNumber = smsRequest.getMobileNumber();
        

        //invite
        String mobileNumber2 = smsRequest.getMobileNumber();
        
        if (!StringUtils.isEmpty(mobileNumber)){

            if(testNumbers.contains(mobileNumber)){
                //3.???redis
                this.setSmsCode("62"+mobileNumber,"8888");
            }else {
                if(mobileNumber.substring(0,1).equals("0")){
                    mobileNumber = mobileNumber.substring(1,mobileNumber.length());
                }
                //???????SysThirdLogs ????????62
                mobileNumber="62"+mobileNumber;
                UsrUser usrUser =new UsrUser();
                String mobileNumberDES = DESUtils.encrypt(mobileNumber);

                //invite
                String mobileNumberDES2 = DESUtils.encrypt(mobileNumber2);
                log.info("mobileNumberDES:"+mobileNumberDES2);


                //invite
                List<Integer> scanListCheck = this.usrDao.isInvitedAndNeedRepay(mobileNumberDES2);
                if (CollectionUtils.isEmpty(scanListCheck)){
                    throw new ServiceException(ExceptionEnum.USER_NOT_INVITED);
                }

                usrUser.setMobileNumberDES(mobileNumberDES);
                usrUser.setStatus(1);
                usrUser.setDisabled(0);
                

                List<UsrUser> userList = this.usrDao.scan(usrUser);
                String content="";
                String smsCode= SmsCodeUtils.sendSmsCode();
                if (CollectionUtils.isEmpty(userList)) {
                    content = "<Do-It> Kode Anda adalah "+smsCode+", gunakan kode untuk pendaftaran di Do-It. Terima kasih atas kepercayaan Anda.";
                }else {
                    content="<Do-It> Kode masuk Anda adalah "+smsCode+".ini adalah rahasia, DILARANG MEMBERIKAN KODE KE SIAPAPUN.";
                }
                int smsCount=Integer.valueOf(this.smsCount);//??? ????????
                //1.校验短信验证码次数
                this.getSmsCodeCount(mobileNumber,smsCount);
                if (!mobileNumber.startsWith("62")){
                    mobileNumber= "62" + mobileNumber ;
                }

                StringBuilder stringBuilder=new StringBuilder(RedisContants.SESSION_SMS_KEY+"count");
                stringBuilder.append(mobileNumber);
                stringBuilder.append(DateUtils.DateToString(new Date()));
                String smsNum = redisClient.get(stringBuilder.toString());
                int nowSmsCount = Integer.valueOf(smsNum) - 1;


                //2.发送短信验证码    zenziva -》 inforbip -》 twilio -》zenziva
                if (nowSmsCount == 1){
                    smsServiceUtil.sendSmsByInforbip(mobileNumber);
                }else if (nowSmsCount == 2){
                    smsServiceUtil.sendSmsByTwilio(smsRequest.getVerifyType(),mobileNumber);
                }else {

                    smsServiceUtil.sendTypeSmsCodeWithType("LOGIN",mobileNumber,content,"ZENZIVA");
                    //3.保存到redis
                    this.setSmsCode(mobileNumber,smsCode);
                    //            4.存到数据库
                    smsServiceUtil.insertSysSmsCode(smsRequest.getMobileNumber(),smsCode,2);
                }
            }
        }else {
            throw new ServiceException(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
    }

    public void sendSmsCodeV2(SmsRequest smsRequest) throws Exception {

        //判断手机号第一位是否是0，是0就去掉
        String mobileNumber = smsRequest.getMobileNumber();

        //invite
        String mobileNumber2 = smsRequest.getMobileNumber();

        if (!StringUtils.isEmpty(mobileNumber)){

            if(testNumbers.contains(mobileNumber)){
                //3.???redis
                this.setSmsCode("62"+mobileNumber,"8888");
            }else {
                if(mobileNumber.substring(0,1).equals("0")){
                    mobileNumber = mobileNumber.substring(1,mobileNumber.length());
                }
                //???????SysThirdLogs ????????62
                mobileNumber="62"+mobileNumber;
                UsrUser usrUser =new UsrUser();

                String mobileNumberDES = DESUtils.encrypt(mobileNumber);

                //invite
                String mobileNumberDES2 = DESUtils.encrypt(mobileNumber2);


                //invite
                List<Integer> scanListCheck = this.usrDao.isInvitedAndNeedRepay(mobileNumberDES2);
                if (CollectionUtils.isEmpty(scanListCheck)){
                    throw new ServiceException(ExceptionEnum.USER_NOT_INVITED);
                }

                usrUser.setMobileNumberDES(mobileNumberDES);
                usrUser.setStatus(1);
                usrUser.setDisabled(0);
                List<UsrUser> userList = this.usrDao.scan(usrUser);
                String content="";
                String smsCode= SmsCodeUtils.sendSmsCode();
                if (CollectionUtils.isEmpty(userList)) {
                    content = "<Do-It> Kode Anda adalah "+smsCode+", gunakan kode untuk pendaftaran di Do-It. Terima kasih atas kepercayaan Anda.";
                }else {
                    content="<Do-It> Kode masuk Anda adalah "+smsCode+".ini adalah rahasia, DILARANG MEMBERIKAN KODE KE SIAPAPUN.";
                }
                int smsCount=Integer.valueOf(this.smsCount);//??? ????????
                //1.校验短信验证码次数
                this.getSmsCodeCount(mobileNumber,smsCount);
                if (!mobileNumber.startsWith("62")){
                    mobileNumber= "62" + mobileNumber ;
                }

                StringBuilder stringBuilder=new StringBuilder(RedisContants.SESSION_SMS_KEY+"count");
                stringBuilder.append(mobileNumber);
                stringBuilder.append(DateUtils.DateToString(new Date()));
                String smsNum = redisClient.get(stringBuilder.toString());
                int nowSmsCount = Integer.valueOf(smsNum) - 1;

                if (nowSmsCount == 1){
                    smsServiceUtil.sendSmsByInforbipV2(mobileNumber);

                    log.info("sendSmsCodeV2 sms using Inforbip {0}", mobileNumber);
                }
                else {

                    smsServiceUtil.sendTypeSmsCodeWithTypeV2("LOGIN",mobileNumber,content,"ZENZIVA");
                    //3.保存到redis
                    this.setSmsCode(mobileNumber,smsCode);
                    //            4.存到数据库
                    smsServiceUtil.insertSysSmsCode(smsRequest.getMobileNumber(),smsCode,2);

                    log.info("sendSmsCodeV2 sms using ZENZIVA {0} {1}", mobileNumber, smsCode);
                }
            }
        }else {
            throw new ServiceException(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
    }

    
    /**
     * ?????????
     * @param mobileNumber
     * @return
     */
    public void getSmsCodeCount(String mobileNumber, int smsCount) throws ServiceException {
        StringBuilder stringBuilder=new StringBuilder(RedisContants.SESSION_SMS_KEY+"count");
        stringBuilder.append(mobileNumber);
        stringBuilder.append(DateUtils.DateToString(new Date()));
        String smsNum = redisClient.get(stringBuilder.toString());
        log.info("sms count:{}",smsNum);
        int count=0;
        if(smsNum!=null&&Integer.valueOf(smsNum)>= smsCount){
            log.info("???????????",count);
            throw new ServiceException(ExceptionEnum.USER_SMS_CODE_COUNT_ERROR);
        }
        if(smsNum==null){
            count++;
        }else{
            count=Integer.valueOf(smsNum)+1;
        }
        redisClient.set(stringBuilder.toString(),String.valueOf(count), RedisContants.EXPIRES_COUNT_SECOND);
    }


    /**
     * ???redis
     * @param mobileNumber
     * @param smsCode
     */
    public void setSmsCode(String mobileNumber, String smsCode){
        StringBuilder stringBuilder=new StringBuilder(RedisContants.SESSION_SMS_KEY);
        stringBuilder.append(mobileNumber);
//        stringBuilder.append(smsCode);
        redisClient.set(stringBuilder.toString(),String.valueOf(smsCode), RedisContants.EXPIRES_SECOND);
    }

    /**vd
     * set smsCode
     * @param mobile
     * @param smsCode
     */
    public String setSmsCodeH5(String mobile, String smsCode) throws BadRequestException {

        StringBuilder stringBuilder=new StringBuilder(RedisContants.SESSION_SMS_KEY);
        String str= SignUtils.generateMd5(mobile+smsCode+ SmsTypeEnum.REGISTER);
        stringBuilder.append(str);
        log.info("mobile = "+mobile +"smsCode = "+smsCode + "md5Str = " + str);
        redisClient.set(stringBuilder.toString(),String.valueOf(smsCode), RedisContants.EXPIRES_SECOND);
        return str;
    }

    /**
     * ??h5???????????
     * @param
     * @param mobile
     * @param smsCode
     * @param
     */
    public void checkh5SmsCode(String mobile, String smsCode) throws Exception {
        //???????????0??0???

        //判断手机号第一位是否是0，是0就去掉
        if(mobile.substring(0,1).equals("0")){
            mobile = mobile.substring(1,mobile.length());
        }
        String newMobileNumber = "62"+mobile;

        StringBuilder stringBuilder=new StringBuilder(RedisContants.SESSION_SMS_KEY+"count");
        stringBuilder.append(newMobileNumber);
        stringBuilder.append(DateUtils.DateToString(new Date()));
        String smsNum = redisClient.get(stringBuilder.toString());
        int smsCount = 0;
        if (!StringUtils.isEmpty(smsNum)){
            smsCount  = Integer.valueOf(smsNum) - 1;
        }
        log.info("sms count:{}",smsCount);


//        zenziva -》 inforbip -》 twilio -》zenziva
        if (smsCount == 1){

            SmsCheckInforbipResponse response = smsServiceUtil.sendSmsByInforbipVerify(smsCode,newMobileNumber);
            if (!response.getVerified().equals("true")){
                throw new ServiceException(ExceptionEnum.USER_CHECK_SMS_CODE_ERROR);
            }

        }else if (smsCount == 2){

            SmsVeriResponse response = smsServiceUtil.sendSmsByTwilioVerify(smsCode,newMobileNumber);
            if (!response.getCode().equals("0")){
//           log.info("验证码错误----------->",smsCode);
                throw new ServiceException(ExceptionEnum.USER_CHECK_SMS_CODE_ERROR);
            }

        }else {

            StringBuilder stringBuilder2=new StringBuilder(RedisContants.SESSION_SMS_KEY);
            String str= SignUtils.generateMd5(newMobileNumber+smsCode+ SmsTypeEnum.REGISTER);
            stringBuilder2.append(str);
            log.info("mobile = "+newMobileNumber +"smsCode = "+smsCode + "md5Str = " + str);
            String redisString = redisClient.get(stringBuilder2.toString());
            if(redisString==null){
                log.error("????????");
                throw new ServiceExceptionSpec(ExceptionEnum.USER_CHECK_SMS_CODE_ERROR);
            }else {
                if(!redisString.equals(smsCode)){
                    log.error("????????");
                    throw new ServiceExceptionSpec(ExceptionEnum.USER_CHECK_SMS_CODE_ERROR);
                }
            }
            this.redisClient.del(stringBuilder.toString());
        }

    }

    /**
     *   给实名失败 且没有填写税卡的用户发送短信
     * */
    public void sendToRejectUserByNotRealNameAndNoSteuerkarted(UsrUser usrUser) throws Exception {

        log.info("本次发送短信的的用户id：" + usrUser.getUuid());

        String mobileNumberDes = usrUser.getMobileNumberDES();
        String mobileNumber = "62" + DESUtils.decrypt(mobileNumberDes);
        // 发送提醒短信   您在平台填写的信息不足，请登录do-it补充税卡号后，重新申请
        String content = "<Do-It> informasi kurang lengkap, silakan tambahkan informasi NPWP Anda untuk mendaftar lagi\n";
        this.smsServiceUtil.sendTypeSmsCode("LOAN_SUCCESS_USER_WITHIN_FIVEDAY", mobileNumber, content);
    }
}
