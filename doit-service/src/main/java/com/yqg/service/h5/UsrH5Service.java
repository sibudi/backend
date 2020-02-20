package com.yqg.service.h5;

import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.order.ChannelTypeEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.BadRequestException;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.LoginSession;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.*;
import com.yqg.service.h5.request.SmsH5Request;
import com.yqg.service.h5.request.UserRegisterH5Request;
import com.yqg.service.h5.response.ImageCodeModelSpec;
import com.yqg.service.h5.response.SmsH5Response;
import com.yqg.service.system.service.IndexService;
import com.yqg.service.system.service.StagingProductWhiteListService;
import com.yqg.service.system.service.SysProductChannelService;
import com.yqg.service.third.sms.SmsServiceUtil;
import com.yqg.service.user.service.UsrService;
import com.yqg.system.entity.StagingProductWhiteList;
import com.yqg.system.entity.SysPaymentChannel;
import com.yqg.system.entity.SysProductChannel;
import com.yqg.service.user.service.SmsService;
import com.yqg.service.user.service.UsrPINService;
import com.yqg.user.dao.UsrDao;
import com.yqg.user.entity.UsrUser;
import com.yqg.service.user.request.UsrRequst;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Didit Dwianto on 2018/1/9.
 */
@Component
@Slf4j
public class UsrH5Service {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private SmsService smsService;

    @Autowired
    private UsrService usrService;

    @Autowired
    private StagingProductWhiteListService stagingProductWhiteListService;

    @Autowired
    private SysProductChannelService productChannelService;

    @Value("${count.smsCount}")
    private String smsCount;

    @Autowired
    private UsrDao usrDao;

    @Autowired
    private SmsServiceUtil smsServiceUtil;

    @Autowired
    private UsrPINService pinService;

    // ??h5 ???????
    public SmsH5Response sendH5SmsCode(SmsH5Request smsH5Request) throws Exception {
        if(StringUtils.isEmpty(smsH5Request.getImgCode()) ||
                StringUtils.isEmpty(smsH5Request.getImgKey()) ||
                StringUtils.isEmpty(smsH5Request.getMobile())){

            log.error("???????,??????");
            throw new ServiceExceptionSpec(ExceptionEnum.USER_RANDOMIMAGE_CHECK_ERROR);
        }
        SmsH5Response response = new SmsH5Response();
        String smskey = "";
        this.checkImgCode(smsH5Request.getImgCode(),smsH5Request.getImgKey());

        String mobileNumber = smsH5Request.getMobile();
        if (!com.yqg.common.utils.StringUtils.isEmpty(mobileNumber)){
            //???????????0??0???
            if(mobileNumber.substring(0,1).equals("0")){
                mobileNumber = mobileNumber.substring(1,mobileNumber.length());
            }

            UsrUser usrUser =new UsrUser();
            //?????????????????????
            String mobileNumberDES = DESUtils.encrypt(mobileNumber);
            usrUser.setMobileNumberDES(mobileNumberDES);
            usrUser.setStatus(1);
            usrUser.setDisabled(0);
            List<UsrUser> userList = this.usrDao.scan(usrUser);
            if(!CollectionUtils.isEmpty(userList)){
                log.error("?????");
                throw new ServiceExceptionSpec(ExceptionEnum.USER_IS_EXIST);
            }

            int smsCount=Integer.valueOf(this.smsCount);//??? ????????

            String redisMobile  = mobileNumber;

            //请求数据落库，SysThirdLogs 使用的手机号不加62
            if (!mobileNumber.startsWith("62")){
                mobileNumber= "62" + mobileNumber ;
            }
            //1.校验短信验证码次数
            this.smsService.getSmsCodeCount(mobileNumber,smsCount);

            StringBuilder stringBuilder=new StringBuilder(RedisContants.SESSION_SMS_KEY+"count");
            stringBuilder.append(mobileNumber);
            stringBuilder.append(DateUtils.DateToString(new Date()));
            String smsNum = redisClient.get(stringBuilder.toString());
            int nowSmsCount = Integer.valueOf(smsNum) - 1;

            //2.发送短信验证码    zenziva -》 inforbip -》 twilio -》zenziva
            if (nowSmsCount == 1){

                smsServiceUtil.sendSmsByInforbip(mobileNumber);
                response.setSmsKey("this is send by Inforbip,don't have smsKey");
            // }else if (nowSmsCount == 2){

            //     smsServiceUtil.sendSmsByTwilio("1",mobileNumber);
            //     response.setSmsKey("this is send by Twilio,don't have smsKey");
            }else {

                String smsCode= SmsCodeUtils.sendSmsCode();
//          content="???"+smsCode+"????????DO-IT?????????!";
                String content = "Kode rahasia Anda adalah "+smsCode+", gunakan kode untuk melanjutkan pendaftaran akun DO-IT. Terima kasih atas kepercayaan Anda.";
                //2.???????
                smsServiceUtil.sendTypeSmsCode("REGISTE",mobileNumber,content);
                //3.???redis
                smskey = this.smsService.setSmsCodeH5(mobileNumber,smsCode);
                response.setSmsKey(smskey);

                //            4.存到数据库
                smsServiceUtil.insertSysSmsCode(redisMobile,smsCode,2);
            }

        }else {
            log.error("??h5????????????");
            throw new ServiceException(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }


        return response;
    }

    /**
     * ???????
     * @throws BadRequestException
     *
     */
    public ImageCodeModelSpec randomImage() throws BadRequestException {
        try {
            String uuid = UUIDGenerateUtil.uuid();
            log.info("uuid:{}", uuid);
            Map<String, Object> map = ImgCodeUtil.generateImgCode(new ByteArrayOutputStream());
            this.redisClient.set(RedisContants.RANDOMIMAGE_SESSION_KEY+uuid, map.get("randomString").toString(), 60 * 5);
            ByteArrayOutputStream outputStream = (ByteArrayOutputStream) map.get("output");
            byte[] img = outputStream.toByteArray();
            String imgBase64 = new BASE64Encoder().encode(img).replaceAll("\n", "");
            ImageCodeModelSpec spec = new ImageCodeModelSpec();
            spec.setImgBase64(imgBase64);
            spec.setImgSessionId(uuid);
            return spec;
        } catch (IOException e) {
            log.error("?????????!");
            throw new BadRequestException();
        }
    }


    /*???????*/
    public void checkImgCode(String imgCode,String imgKey) throws Exception{
        String result = this.redisClient.get(RedisContants.RANDOMIMAGE_SESSION_KEY+imgKey);
        log.info("result = {}"+result);

        if(result == null || !result.equals(imgCode)){
            log.error("???????,??????");
            throw new ServiceExceptionSpec(ExceptionEnum.USER_RANDOMIMAGE_CHECK_ERROR);
        }
//        redisClient.del(RedisContants.RANDOMIMAGE_SESSION_KEY+imgKey);
    }


    // ??
    public LoginSession register(UserRegisterH5Request h5Request)
            throws Exception {
        //remove check otp because register no need otp, they will login to app anyway
        //this.smsService.checkh5SmsCode(h5Request.getMobile(), h5Request.getCode());
        if(StringUtils.isEmpty(h5Request.getImgCode()) ||
                StringUtils.isEmpty(h5Request.getImgKey()) ||
                StringUtils.isEmpty(h5Request.getMobile())){

            log.error("???????,??????");
            throw new ServiceExceptionSpec(ExceptionEnum.USER_RANDOMIMAGE_CHECK_ERROR);
        }
        this.checkImgCode(h5Request.getImgCode(),h5Request.getImgKey());

        //2.??ip
        this.usrService.checkIpTodayIsSubmith5(h5Request.getIPAdress());

        //3. register user with isinvited = 1
        LoginSession session = this.registerForH5(h5Request);

        //4. check if kudo user and user source 84: 1.2 mio installment, if yes then add to stagingproductwhitelist
        this.registerForStagingProductWhitelist(session, h5Request);

        return session;

    }


    private void registerForStagingProductWhitelist(LoginSession session, UserRegisterH5Request h5Request) throws Exception {
        if(Integer.toString(ChannelTypeEnum.KUDO_INSTALLMENT_1200K.getCode()).equals(h5Request.getChannel())){
            //because automatically invited as installment user then need to insert table stagingproductwhitelist

            StagingProductWhiteList productWhiteList = new StagingProductWhiteList();
            productWhiteList.setUuid(UUID.randomUUID().toString());
            productWhiteList.setUserUuid(session.getUserUuid());
            
            String productUuid = productChannelService.getProductUuid(Integer.parseInt(h5Request.getChannel()));
            productWhiteList.setProductUuid(productUuid);
            productWhiteList.setBeachId("PQ_" + (new java.text.SimpleDateFormat("yyyyMMdd_HHmmss")).format(new java.util.Date()));
            productWhiteList.setRuleName("KUDO 1.2mio with every 2 weeks repayment");

            stagingProductWhiteListService.insertWhiteList(productWhiteList);

        }
    }

    /**
     * h5??
     * @param request
     * @return
     * @throws Exception
     */
    public LoginSession registerForH5(UserRegisterH5Request request) throws Exception {

        UsrUser user = new UsrUser();
        //???????????0??0???
        String mobileNumber = request.getMobile();
        if (mobileNumber.substring(0, 1).equals("0")) {
            mobileNumber = mobileNumber.substring(1, mobileNumber.length());
        }

        if (usrService.checkUserMobileNumberEmailIsExist(mobileNumber, request.getEmail())) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_IS_EXIST);
        } 
        else{
            pinService.newPIN(mobileNumber, request.getEmail());
        }

        String setMobileNumberDES = DESUtils.encrypt(mobileNumber);
        user.setMobileNumberDES(setMobileNumberDES);

        String mobile = mobileNumber.substring(0, mobileNumber.length() - 6) + "******";
        user.setMobileNumber(mobile);

        user.setUserSource(Integer.valueOf(request.getChannel()));
        user.setEmailAddress(DESUtils.encrypt(request.getEmail()));

        user.setIsInvited(1); //need is Invited so that not go to BD message
        if (this.usrDao.insert(user) < 1) {
            throw new BadRequestException();
        }

        return this.generateSessionForH5(user);
    }

    /**
     * h5??session
     *
     */
    public LoginSession generateSessionForH5(UsrUser user) throws BadRequestException {
        LoginSession loginSession = new LoginSession();
        String mobileNumber = DESUtils.decrypt(user.getMobileNumberDES());
        loginSession.setMobile(mobileNumber);
        loginSession.setUserUuid(user.getUuid());
        loginSession.setUserRole(user.getUserRole());
        UserSessionUtil.generateAndSetSessionIdForH5(this.redisClient, loginSession);
        return loginSession;
    }
}
