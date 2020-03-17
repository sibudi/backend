package com.yqg.service.user.service;

import com.alibaba.fastjson.JSONObject;
import com.yqg.activity.dao.UsrInviteRecordDao;
import com.yqg.activity.entity.UsrInviteRecord;
import com.yqg.base.multiDataSource.annotation.WriteDataSource;
import com.yqg.collection.dao.ManCollectorInfoDao;
import com.yqg.collection.dao.entity.ManCollectorInfo;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.constants.SysParamContants;
import com.yqg.common.enums.order.OrdStepTypeEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.system.MoboxDataEnum;
import com.yqg.common.enums.system.ThirdDataTypeEnum;
import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.common.enums.user.CertificationResultEnum;
import com.yqg.common.enums.user.UserSourceEnum;
import com.yqg.common.enums.user.UsrAttachmentEnum;
import com.yqg.common.exceptions.BadRequestException;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.BaseRequest;
import com.yqg.common.models.LoginSession;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.*;
import com.yqg.mongo.dao.MoboxDataMongoDal;
import com.yqg.mongo.entity.MoboxDataMongo;
import com.yqg.mongo.entity.OrderThirdDataMongo;
import com.yqg.mongo.entity.UserIziVerifyResultMongo;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.order.OrdThirdDataService;
import com.yqg.service.scheduling.task.IziWhatsAppTask;
import com.yqg.service.system.service.SysParamService;
import com.yqg.service.third.izi.IziService;
import com.yqg.service.third.izi.IziWhatsAppService;
import com.yqg.service.third.izi.response.IziResponse;
import com.yqg.service.third.yitu.FileHelper;
import com.yqg.service.third.yitu.YiTuService;
import com.yqg.service.user.request.*;
import com.yqg.service.user.service.UsrPINService;
import com.yqg.service.user.response.UsrAttachmentResponse;
import com.yqg.service.user.response.UsrCertificationResponse;
import com.yqg.system.dao.SysDeviceIdWhiteListDao;
import com.yqg.system.dao.SysDicDao;
import com.yqg.system.dao.SysDicItemDao;
import com.yqg.system.entity.SysDeviceIdWhiteList;
import com.yqg.system.entity.SysDic;
import com.yqg.system.entity.SysDicItem;
import com.yqg.third.entity.IziWhatsAppDetailEntity;
import com.yqg.user.dao.*;
import com.yqg.user.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Component
@Slf4j
public class UsrService {

    @Autowired
    private UsrFeedBackDao usrFeedBackDao;
    @Autowired
    private UsrDao usrDao;
    @Autowired
    private ManSysUserDao manSysUserDao;
    @Autowired
    private OrdDao orderDao;
    @Autowired
    private UsrInviteRecordDao usrInviteRecordDao;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private SmsService smsService;
    @Autowired
    private UsrPINService pinService;
    @Autowired
    private UsrCertificationDao usrCertificationDao;
    @Autowired
    private UsrAttachmentInfoDao usrAttachmentInfoDao;
    @Autowired
    private UsrLoginHistoryDao usrLoginHistoryDao;
    @Autowired
    private YiTuService yiTuService;
    @Autowired
    private RegisterDeviceInfoDao registerDeviceInfoDao;
    @Autowired
    private UsrBaseInfoService usrBaseInfoService;
    @Autowired
    private OrdThirdDataService ordThirdDataService;
    @Autowired
    private SysParamService sysParamService;
    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private SysDeviceIdWhiteListDao sysDeviceIdWhiteListDao;

    @Autowired
    private UsrEvaluateScoreDao usrEvaluateScoreDao;

    @Autowired
    private ManCollectorInfoDao manCollectorInfoDao;

    @Autowired
    private SysDicDao sysDicDao;

    @Autowired
    private SysDicItemDao sysDicItemDao;
    @Autowired
    private IziWhatsAppService iziWhatsAppService;
    @Autowired
    private IziService iziService;
    @Autowired
    private UserBackupLinkmanService userBackupLinkmanService;
    @Autowired
    private ExecutorService executorService;
    @Autowired
    private MoboxDataMongoDal moboxDataMongoDal;

    // ??????
    public void userFeedBack(UsrFeedBackRequest usrFeedBackRequest) throws Exception {

        // ????
        UsrUser usrUser = new UsrUser();
        usrUser.setUuid(usrFeedBackRequest.getUserUuid());
        usrUser.setDisabled(0);
        usrUser.setStatus(1);
        List<UsrUser> userList = this.usrDao.scan(usrUser);
        if (CollectionUtils.isEmpty(userList)) {
            log.error("?????");
            throw new ServiceException(ExceptionEnum.USER_NOT_FOUND);
        }
        UsrUser user = userList.get(0);
        // ??????
        UsrFeedBack feedBack = new UsrFeedBack();
        feedBack.setUserUuid(user.getUuid());
        feedBack.setUserMobile(user.getMobileNumber());
        feedBack.setFeedBackContent(usrFeedBackRequest.getFeedBackContent());
        feedBack.setFeedBackImages(usrFeedBackRequest.getFeedBackImages());
        feedBack.setUpdateTime(new Date());
        feedBack.setCreateTime(new Date());
        feedBack.setUuid(UUIDGenerateUtil.uuid());
        feedBack.setNetType(usrFeedBackRequest.getNet_type());
        feedBack.setSystemVersion(usrFeedBackRequest.getSystem_version());
        feedBack.setAppVersion(usrFeedBackRequest.getClient_version());
        feedBack.setClientType(usrFeedBackRequest.getClient_type());
        feedBack.setChannelSn(usrFeedBackRequest.getChannel_sn());
        feedBack.setChannelName(usrFeedBackRequest.getChannel_name());
        feedBack.setDeviceId(UUID.randomUUID().toString());
        feedBack.setResolution(usrFeedBackRequest.getResolution());
        feedBack.setIPAdress(usrFeedBackRequest.getIPAdress());
        feedBack.setSourceType(usrFeedBackRequest.getSourceType());
        feedBack.setCollectionName(usrFeedBackRequest.getCollectionName());
        this.usrFeedBackDao.insert(feedBack);
    }

    /**
     * ???????
     *
     * @param usrRequst
     * @return
     * @throws Exception
     */
    @WriteDataSource
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false)
    public LoginSession signup(UsrRequst usrRequst) throws Exception {

        LoginSession loginSession = new LoginSession();
        String mobileNumber = usrRequst.getMobileNumber();
        //???????????0??0???
        if (mobileNumber.substring(0, 1).equals("0")) {
            mobileNumber = mobileNumber.substring(1, mobileNumber.length());
        }
        //ahalim: Disable SMS
        smsService.checkSmsCode(mobileNumber, usrRequst.getSmsCode());

        List<UsrUser> userList = this.scanUser(usrRequst);
        if (CollectionUtils.isEmpty(userList)) {
            // 限制iOS新用户注册
            if (usrRequst.getClient_type().equals("iOS")) {
                throw new ServiceException(ExceptionEnum.SYSTEM_UPGRADE);
            }
            loginSession = logup(usrRequst);
        } else {
            loginSession = login(usrRequst);
        }
        return loginSession;
    }

    //Janhsen: temporary to make sure sms only works for OTP
    @WriteDataSource
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false)
    public LoginSession signupV2(UsrRequst usrRequst) throws Exception {

        LoginSession loginSession = new LoginSession();
        String mobileNumber = usrRequst.getMobileNumber();
        //???????????0??0???
        if (mobileNumber.substring(0, 1).equals("0")) {
            mobileNumber = mobileNumber.substring(1, mobileNumber.length());
        }
        //ahalim: Disable SMS
        smsService.checkSmsCodeV2(mobileNumber, usrRequst.getSmsCode());

        List<UsrUser> userList = this.scanUser(usrRequst);
        if (CollectionUtils.isEmpty(userList)) {
            // 限制iOS新用户注册
            if (usrRequst.getClient_type().equals("iOS")) {
                throw new ServiceException(ExceptionEnum.SYSTEM_UPGRADE);
            }
            loginSession = logup(usrRequst);
        } else {
            loginSession = login(usrRequst);
        }
        return loginSession;
    }

    @WriteDataSource
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false)
    public void signupV3(UsrRequst usrRequst) throws Exception {
        String mobileNumber = usrRequst.getMobileNumber();
        if (mobileNumber.substring(0, 1).equals("0")) {
            mobileNumber = mobileNumber.substring(1, mobileNumber.length());
        }

        if (checkUserMobileNumberEmailIsExist(usrRequst)) {
            throw new ServiceException(ExceptionEnum.USER_IS_EXIST);
        } 
        else{
            if (usrRequst.getClient_type().equals("iOS")) {
                throw new ServiceException(ExceptionEnum.SYSTEM_UPGRADE);
            }
            pinService.newPIN(usrRequst.getMobileNumber(), usrRequst.getEmail());
            addUser(usrRequst);
        }
    }

    @WriteDataSource
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false)
    public LoginSession signin(UsrRequst usrRequst) throws Exception {

        LoginSession loginSession = new LoginSession();
        String mobileNumber = usrRequst.getMobileNumber();
        if (mobileNumber.substring(0, 1).equals("0")) {
            mobileNumber = mobileNumber.substring(1, mobileNumber.length());
        }

        List<UsrUser> userList = this.scanUser(usrRequst);
        if (!CollectionUtils.isEmpty(userList)) {
            if("".equals(userList.get(0).getEmailAddress()) || userList.get(0).getEmailAddress() == null){
                throw new ServiceException(ExceptionEnum.INVALID_EMAIL_NOT_FOUND);
            }
            else{
                String emailAddress = DESUtils.decrypt(userList.get(0).getEmailAddress());
                if(pinService.isLoginTemporary(usrRequst.getMobileNumber(), emailAddress, usrRequst.getCurrentPIN())){
                    loginSession = login(usrRequst);
                }
                else{
                    loginSession = login(usrRequst);
                    loginSession.setIsTempPIN(1);
                }
            }
        }
        else{
            throw new ServiceException(ExceptionEnum.USER_NOT_FOUND);
        }
        
        return loginSession;
    }

    @WriteDataSource
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false)
    public void changePIN(UsrRequst usrRequst) throws Exception {

        String mobileNumber = usrRequst.getMobileNumber();
        if (mobileNumber.substring(0, 1).equals("0")) {
            mobileNumber = mobileNumber.substring(1, mobileNumber.length());
        }

        List<UsrUser> userList = this.scanUser(usrRequst);
        if (!CollectionUtils.isEmpty(userList)) {
            if("".equals(userList.get(0).getEmailAddress()) || userList.get(0).getEmailAddress() == null){
                throw new ServiceException(ExceptionEnum.INVALID_EMAIL_NOT_FOUND);
            }
            else{
                String emailAddress = DESUtils.decrypt(userList.get(0).getEmailAddress());
                pinService.changePIN(usrRequst.getMobileNumber(), emailAddress, usrRequst.getCurrentPIN(), usrRequst.getNewPIN());
            }
        }
        else{
            throw new ServiceException(ExceptionEnum.INVALID_PIN);
        }
    }

    @WriteDataSource
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false)
    public void forgotPIN(UsrRequst usrRequst) throws Exception {
        String mobileNumber = usrRequst.getMobileNumber();
        if (mobileNumber.substring(0, 1).equals("0")) {
            mobileNumber = mobileNumber.substring(1, mobileNumber.length());
        }

        List<UsrUser> userList = this.scanUser(usrRequst);
        if (!CollectionUtils.isEmpty(userList)) {
            if("".equals(userList.get(0).getEmailAddress()) || userList.get(0).getEmailAddress() == null){
                throw new ServiceException(ExceptionEnum.INVALID_EMAIL_NOT_FOUND);
            }
            else{
                String emailAddress = DESUtils.decrypt(userList.get(0).getEmailAddress());
                usrRequst.setEmail(emailAddress);
                pinService.forgotPIN(usrRequst.getMobileNumber(), emailAddress);
            }
        }
        else{
            throw new ServiceException(ExceptionEnum.USER_NOT_FOUND);
        }
    }

    @WriteDataSource
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false)
    public Boolean verifyOTP(UsrRequst usrRequst) throws Exception {

        String mobileNumber = usrRequst.getMobileNumber();
        if (mobileNumber.substring(0, 1).equals("0")) {
            mobileNumber = mobileNumber.substring(1, mobileNumber.length());
        }

        UsrUser user = new UsrUser();
        user.setUuid(usrRequst.getUserUuid());
        user.setDisabled(0);
        List<UsrUser> users = usrDao.scan(user);
        user.setIsMobileValidated(1);
        if(users.isEmpty()){
            throw new ServiceException(ExceptionEnum.USER_NOT_FOUND);
        }
        else{
            users.get(0).setIsMobileValidated(1);
            usrDao.update(users.get(0));
        }

        return smsService.verifyOTP(mobileNumber, usrRequst.getSmsCode());
    }

    @WriteDataSource
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false)
    public Boolean isMobileValidated(UsrRequst usrRequst) throws Exception {

        UsrUser user = new UsrUser();
        user.setUuid(usrRequst.getUserUuid());
        user.setDisabled(0);
        List<UsrUser> users = usrDao.scan(user);

        if(users.isEmpty()){
            throw new ServiceException(ExceptionEnum.USER_NOT_FOUND);
        }
        else{
            return !(users.get(0).getIsMobileValidated() == 0);
        }
    }

    @WriteDataSource
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false)
    public Boolean isMobileValidated(String userUuid) throws Exception {

        UsrUser user = new UsrUser();
        user.setUuid(userUuid);
        user.setDisabled(0);
        List<UsrUser> users = usrDao.scan(user);

        if(users.isEmpty()){
            throw new ServiceException(ExceptionEnum.USER_NOT_FOUND);
        }
        else{
            return !(users.get(0).getIsMobileValidated() == 0);
        }
    }

    @WriteDataSource
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false)
    public JSONObject inviteSignup(UsrRequst usrRequst) throws Exception {
        JSONObject jsonObject = new JSONObject();
        String mobileNumber = usrRequst.getMobileNumber();
        // TODO: 2018/8/20  测试环境注释 正式环境一定要打开一定要打开一定要打开一定要打开
        if (mobileNumber.substring(0, 1).equals("0")) {
            mobileNumber = mobileNumber.substring(1, mobileNumber.length());
        }
        smsService.checkh5SmsCode(mobileNumber, usrRequst.getSmsCode());

        List<UsrUser> userList = this.scanUser(usrRequst);
        if (CollectionUtils.isEmpty(userList)) {
            UsrUser user = new UsrUser();
            if (mobileNumber.substring(0, 1).equals("0")) {
                mobileNumber = mobileNumber.substring(1, mobileNumber.length());
            }
            String mobile = mobileNumber.substring(0, mobileNumber.length() - 6) + "******";
            user.setMobileNumber(mobile);
            String setMobileNumberDES = DESUtils.encrypt(mobileNumber);
            user.setMobileNumberDES(setMobileNumberDES);
            user.setUserSource(3);
            user.setUserType(1);
            user.setUuid(UUIDGenerateUtil.uuid());
            user.setUserSource(Integer.valueOf(usrRequst.getUserSource()));
            this.usrDao.insert(user);

//  同时绑定邀请关系 begin
            String userUuid = usrRequst.getInvite();
            bindInvite(user, userUuid);
//绑定邀请关系 end

            jsonObject.put("flag", 0);
        } else {
            jsonObject.put("flag", 1);
        }
        return jsonObject;
    }


    /**
     * ????
     *
     * @param usrRequst
     * @return
     * @throws Exception
     */
    @WriteDataSource
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false)
    public LoginSession logup(UsrRequst usrRequst) throws ServiceException {

        String deviceNumber = UUID.randomUUID().toString();

        String lockKey = "logup" + usrRequst.getUserUuid();
        if (!redisClient.lockRepeatWithSeconds(lockKey, 60)) {
            log.error("------------in progress------------");
            throw new ServiceException(ExceptionEnum.ORDER_COMMIT_REPEAT);
        }
        try {
            RegisterDeviceInfo registerDeviceInfo = new RegisterDeviceInfo();
            registerDeviceInfo.setDeviceNumber(deviceNumber);
            registerDeviceInfo.setDisabled(0);

            Boolean isWhiteUser = false;
            if (!StringUtils.isEmpty(usrRequst.getMac())) {
                // 首先检查是否在设备白名单里面
                SysDeviceIdWhiteList whiteScan = new SysDeviceIdWhiteList();
                whiteScan.setDeviceId(deviceNumber);
                whiteScan.setDisabled(0);
                List<SysDeviceIdWhiteList> whiteLists = this.sysDeviceIdWhiteListDao.scan(whiteScan);
                if (CollectionUtils.isEmpty(whiteLists)) {
                    // 在检查该设备是否注册过
                    List<RegisterDeviceInfo> registerDeviceInfoList = registerDeviceInfoDao.scan(registerDeviceInfo);
                    if (!CollectionUtils.isEmpty(registerDeviceInfoList)) {
                        log.info("??????????", registerDeviceInfoList);
                        throw new ServiceException(ExceptionEnum.USER_REGIST_DEVICENO_IDENTICAL);
                    }
                } else {
                    isWhiteUser = true;
                }
            }
            UsrUser user = new UsrUser();
            //???????????0??0???
            String mobileNumber = CheakTeleUtils.telephoneNumberValid2(usrRequst.getMobileNumber());
            if (mobileNumber.substring(0, 1).equals("0")) {
                mobileNumber = mobileNumber.substring(1, mobileNumber.length());
            }
            //????6???
            String mobile = mobileNumber.substring(0, mobileNumber.length() - 6) + "******";
            user.setMobileNumber(mobile);
            //?????
            String setMobileNumberDES = DESUtils.encrypt(mobileNumber);
            user.setMobileNumberDES(setMobileNumberDES);
            //客户端类型
            user.setUserSource(usrRequst.getClient_type().equals("iOS") ? 2 : 1);
            if (usrRequst.getChannel_name().equals("Samsung")) {
                user.setUserSource(20);
            } else if (usrRequst.getChannel_name().equals("CashCash")) {
                user.setUserSource(Integer.valueOf(UserSourceEnum.CashCash.getCode()));
            } else if (usrRequst.getChannel_name().equals("Cheetah")) {
                user.setUserSource(Integer.valueOf(UserSourceEnum.Cheetah.getCode()));
            }
            if (UserSourceEnum.CashCash.getCode().equals(usrRequst.getUserSource())) {
                user.setUserSource(Integer.valueOf(usrRequst.getUserSource()));
            }
            user.setUserType(1);

            String result = redisClient.get(RedisContants.SMS_OTP_OFF);
            if(result.equals("1")){
                user.setIsMobileValidated(1);
                user.setRemark("sms otp is off");
            } else {
                user.setIsMobileValidated(0);
            }
            
            user.setEmailAddress(DESUtils.decrypt(usrRequst.getEmail()));
            this.usrDao.insert(user);

            //???????????????
            if (!StringUtils.isEmpty(deviceNumber)) {
                //????????
                if (!isWhiteUser) {

                    registerDeviceInfo.setUserUuid(user.getUuid());
                    registerDeviceInfo.setIpAddress(usrRequst.getIPAdress());
                    registerDeviceInfo.setDeviceType(usrRequst.getClient_type());
                    registerDeviceInfo.setMacAddress(usrRequst.getMac());
                    registerDeviceInfo.setFcmToken(usrRequst.getFcmToken());
                    registerDeviceInfo.setIpAddress(usrRequst.getIPAdress());
                    this.registerDeviceInfoDao.insert(registerDeviceInfo);
                }
            }
            //????????
            usrRequst.setUserUuid(user.getUuid());
            this.addUsrLoginHistory(usrRequst);
            return this.generateSession(user, 0);

        }finally {
            redisClient.unlockRepeat(lockKey);
        }
    }

    private void addUser(UsrRequst usrRequst) throws ServiceException {

        String deviceNumber = UUID.randomUUID().toString();
        RegisterDeviceInfo registerDeviceInfo = new RegisterDeviceInfo();
        registerDeviceInfo.setDeviceNumber(deviceNumber);
        //janhsen: no need check disabled = 0 because devicenumber is unique in table without disabled
        //registerDeviceInfo.setDisabled(0);

        Boolean isWhiteUser = false;
        if (!StringUtils.isEmpty(deviceNumber)) {

            // 首先检查是否在设备白名单里面
            SysDeviceIdWhiteList whiteScan = new SysDeviceIdWhiteList();
            whiteScan.setDeviceId(deviceNumber);
            whiteScan.setDisabled(0);
            List<SysDeviceIdWhiteList> whiteLists = this.sysDeviceIdWhiteListDao.scan(whiteScan);
            if (CollectionUtils.isEmpty(whiteLists)) {

                // 在检查该设备是否注册过
                List<RegisterDeviceInfo> registerDeviceInfoList = registerDeviceInfoDao.scan(registerDeviceInfo);
                if (!CollectionUtils.isEmpty(registerDeviceInfoList)) {
                    log.info("??????????", registerDeviceInfoList);
                    throw new ServiceException(ExceptionEnum.USER_REGIST_DEVICENO_IDENTICAL);
                }
            } else {
                isWhiteUser = true;
            }
        }
        UsrUser user = new UsrUser();
        //???????????0??0???
        String mobileNumber = CheakTeleUtils.telephoneNumberValid2(usrRequst.getMobileNumber());
        if (mobileNumber.substring(0, 1).equals("0")) {
            mobileNumber = mobileNumber.substring(1, mobileNumber.length());
        }
        //????6???
        String mobile = mobileNumber.substring(0, mobileNumber.length() - 6) + "******";
        user.setMobileNumber(mobile);
        //?????
        String setMobileNumberDES = DESUtils.encrypt(mobileNumber);
        user.setMobileNumberDES(setMobileNumberDES);
        //客户端类型
        user.setUserSource(usrRequst.getClient_type().equals("iOS") ? 2 : 1);
        if (usrRequst.getChannel_name().equals("Samsung")) {
            user.setUserSource(20);
        } else if (usrRequst.getChannel_name().equals("CashCash")) {
            user.setUserSource(Integer.valueOf(UserSourceEnum.CashCash.getCode()));
        } else if (usrRequst.getChannel_name().equals("Cheetah")) {
            user.setUserSource(Integer.valueOf(UserSourceEnum.Cheetah.getCode()));
        }
        if (UserSourceEnum.CashCash.getCode().equals(usrRequst.getUserSource())) {
            user.setUserSource(Integer.valueOf(usrRequst.getUserSource()));
        }
        user.setUserType(1);
        
        String result = redisClient.get(RedisContants.SMS_OTP_OFF);
        if(result.equals("1")){
            user.setIsMobileValidated(1);
            user.setRemark("sms otp is off");
        } else {
            user.setIsMobileValidated(0);
        }
        
        user.setEmailAddress(DESUtils.encrypt(usrRequst.getEmail()));
        this.usrDao.insert(user);

        //???????????????
        if (!StringUtils.isEmpty(deviceNumber)) {
            //????????
            if (!isWhiteUser) {
                registerDeviceInfo.setUserUuid(user.getUuid());
                registerDeviceInfo.setIpAddress(usrRequst.getIPAdress());
                registerDeviceInfo.setDeviceType(usrRequst.getClient_type());
                registerDeviceInfo.setMacAddress(usrRequst.getMac());
                registerDeviceInfo.setFcmToken(usrRequst.getFcmToken());
                this.registerDeviceInfoDao.insert(registerDeviceInfo);
            }
        }
        //????????
        usrRequst.setUserUuid(user.getUuid());
        this.addUsrLoginHistory(usrRequst);
    }

    private void bindInvite(UsrUser user, String userUuid) {
        if (!StringUtils.isEmpty(userUuid)) {
            userUuid = userUuid.replaceAll(" ", "+");
            log.info("邀请好友发出邀请用户id为" + userUuid);
            userUuid = DESUtils.decrypt(userUuid);
            UsrUser user1 = getUserByUuid(userUuid);
            UsrInviteRecord record = new UsrInviteRecord();
            record.setUserUuid(userUuid);
            record.setMobileNumber(user1.getMobileNumber());
            record.setInvitedUserUuid(user.getUuid());
            record.setInvitedMobileNumber(user.getMobileNumber());
            record.setFriendSource(0);
            record.setRegTime(user.getCreateTime());
            record.setType(1);//已注册
            record.setStatus(1);//邀请人佣金发放状态 待发放
            String account = this.sysParamService.getSysParamValue(SysParamContants.ACTIVITY_INVITE_LV1);
            record.setAmount(new BigDecimal(account));
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            record.setDisabled(0);
            this.usrInviteRecordDao.insert(record);
        }
    }


    /**
     * ????
     *
     * @param usrRequst
     * @return
     * @throws Exception
     */
    public LoginSession login(UsrRequst usrRequst) throws ServiceException {
        String deviceNumber = UUID.randomUUID().toString();

        List<UsrUser> users = this.scanUser(usrRequst);
        if (CollectionUtils.isEmpty(users)) {
            log.info("????", "users--->" + users);
            throw new ServiceException(ExceptionEnum.USER_LOGIN_ERROR);
        }

        usrRequst.setUserUuid(users.get(0).getUuid());
        users.get(0).setEmailAddress(DESUtils.decrypt(users.get(0).getEmailAddress()));

        RegisterDeviceInfo registerDeviceInfo = new RegisterDeviceInfo();
        registerDeviceInfo.setUserUuid(users.get(0).getUuid());
        registerDeviceInfo.setDisabled(0);
        List<RegisterDeviceInfo> registerDeviceInfoList = registerDeviceInfoDao.scan(registerDeviceInfo);

        try{
            if (!CollectionUtils.isEmpty(registerDeviceInfoList)) {
                RegisterDeviceInfo regDeviceInfo = registerDeviceInfoList.get(0);
                regDeviceInfo.setFcmToken(usrRequst.getFcmToken());
                regDeviceInfo.setDeviceNumber(deviceNumber);
                regDeviceInfo.setDeviceType(usrRequst.getClient_type());
                regDeviceInfo.setMacAddress(usrRequst.getMac());
                regDeviceInfo.setIpAddress(usrRequst.getIPAdress());
                registerDeviceInfoDao.update(regDeviceInfo);
            }
            else{
                if(!StringUtils.isEmpty(usrRequst.getMac())){
                    RegisterDeviceInfo regDeviceInfo = new RegisterDeviceInfo();
                    regDeviceInfo.setUserUuid(users.get(0).getUuid());
                    regDeviceInfo.setFcmToken(usrRequst.getFcmToken());
                    regDeviceInfo.setDeviceNumber(deviceNumber);
                    regDeviceInfo.setDeviceType(usrRequst.getClient_type());
                    regDeviceInfo.setMacAddress(usrRequst.getMac());
                    regDeviceInfo.setIpAddress(usrRequst.getIPAdress());
                    registerDeviceInfoDao.insert(regDeviceInfo);
                }
            }
        }
        catch(DataIntegrityViolationException e){
            throw new ServiceException(ExceptionEnum.USER_REGIST_DEVICENO_IDENTICAL); 
        }

        this.addUsrLoginHistory(usrRequst);
        return this.generateSession(users.get(0), 1);
    }


    /**
     * ??????
     *
     * @param usrRequst
     */
    public void addUsrLoginHistory(UsrRequst usrRequst) {
        //????????

        String deviceNumber = UUID.randomUUID().toString();

        UsrLoginHistory usrLoginHistory = new UsrLoginHistory();
        usrLoginHistory.setUserUuid(usrRequst.getUserUuid());
        usrLoginHistory.setDeviceNomber(deviceNumber);
        usrLoginHistory.setDeviceType(usrRequst.getClient_type());
        usrLoginHistory.setMacAddress(usrRequst.getMac());
        usrLoginHistory.setIpAddress(usrRequst.getIPAdress());
        usrLoginHistory.setNetworkType(usrRequst.getNet_type());
        usrLoginHistory.setMobileSysVersionNo(usrRequst.getSystem_version());
        usrLoginHistory.setMarketChannelNo(usrRequst.getChannel_sn());
        usrLoginHistory.setApplicationVersionNo(usrRequst.getClient_version());
        usrLoginHistory.setLbsX(usrRequst.getLbsX());
        usrLoginHistory.setLbsY(usrRequst.getLbsY());
        usrLoginHistoryDao.insert(usrLoginHistory);
    }


    /**
     * ????????????--???????
     *
     * @param usrRequst
     * @return
     * @throws BadRequestException
     * @throws ServiceException
     */
    public List<UsrUser> scanUser(UsrRequst usrRequst)
            throws ServiceException {
        UsrUser usrUser = new UsrUser();
        if (!StringUtils.isEmpty(usrRequst.getMobileNumber())) {
            String mobileNumber = CheakTeleUtils.telephoneNumberValid2(usrRequst.getMobileNumber());
            if (mobileNumber.substring(0, 1).equals("0")) {
                mobileNumber = mobileNumber.substring(1, mobileNumber.length());
            }

            if(StringUtils.isEmpty(mobileNumber)){
                throw new ServiceException(ExceptionEnum.INVALID_MOBILE_NO_OR_PIN);
            }
            usrUser.setMobileNumberDES(DESUtils.encrypt(mobileNumber));
        }
        else{
            throw new ServiceException(ExceptionEnum.INVALID_MOBILE_NO_OR_PIN);
        }
        
        if (!StringUtils.isEmpty(usrRequst.getUserUuid())) {
            usrUser.setUuid(usrRequst.getUserUuid());
        }
        if (!StringUtils.isEmpty(usrRequst.getRealName())) {
            usrUser.setRealName(usrRequst.getRealName());
        }
        usrUser.setStatus(1);
        usrUser.setDisabled(0);
        List<UsrUser> userList = this.usrDao.scan(usrUser);
        return userList;
    }

    public Boolean checkUserMobileNumberEmailIsExist(UsrRequst usrRequst) throws ServiceException {
        String mobileNumber = CheakTeleUtils.telephoneNumberValid2(usrRequst.getMobileNumber());
        if (mobileNumber.substring(0, 1).equals("0")) {
            mobileNumber = mobileNumber.substring(1, mobileNumber.length());
        }

        String mobileNumberDES = DESUtils.encrypt(mobileNumber);
        String emailAddressDES = DESUtils.encrypt(usrRequst.getEmail());
        
        return (this.usrDao.getUserByMobileOrEmail(mobileNumberDES, emailAddressDES) != 0);
    }

    public Boolean checkUserMobileNumberEmailIsExist(String mobileNo, String email) throws ServiceException {
        String mobileNumber = CheakTeleUtils.telephoneNumberValid2(mobileNo);
        if (mobileNumber.substring(0, 1).equals("0")) {
            mobileNumber = mobileNumber.substring(1, mobileNumber.length());
        }

        String mobileNumberDES = DESUtils.encrypt(mobileNumber);
        String emailAddressDES = DESUtils.encrypt(email);
        
        return (this.usrDao.getUserByMobileOrEmail(mobileNumberDES, emailAddressDES) != 0);
    }

    /**
     * ??session
     */
    public LoginSession generateSession(UsrUser user, Integer isLogin) throws ServiceException {
        LoginSession loginSession = new LoginSession();
        String mobileNumber = DESUtils.decrypt(user.getMobileNumberDES());
        loginSession.setMobile(mobileNumber);
        loginSession.setUserUuid(user.getUuid());
        loginSession.setUserRole(user.getUserRole());
        UserSessionUtil.generateAndSetSessionId(this.redisClient, loginSession);
        this.generateSmsCodeKey(this.redisClient, loginSession, user.getMobileNumberDES());

        String idCardNo = org.apache.commons.lang.StringUtils.defaultIfEmpty(user.getIdCardNo(), "");
        String realName = org.apache.commons.lang.StringUtils.defaultIfEmpty(user.getRealName(), "");
        loginSession.setIdCard(idCardNo);
        loginSession.setRealName(realName);
        loginSession.setIsLogin(isLogin);
        loginSession.setEmailAddress(user.getEmailAddress());
        loginSession.setIsMobileValidated(user.getIsMobileValidated());
        return loginSession;
    }


    /**
     * ????????key
     *
     * @param redisClient
     * @param loginSession
     * @throws BadRequestException
     */
    public static void generateSmsCodeKey(RedisClient redisClient, LoginSession loginSession, String mobileNumberDES) throws ServiceException {
        String mobileNumber = DESUtils.decrypt(mobileNumberDES);
        String uuid = UUIDGenerateUtil.uuid();
        loginSession.setSmsCodeKey(uuid);
        redisClient.set(RedisContants.SESSION_SMS_LOGIN_KEY + uuid, mobileNumber, 60 * 60 * 24 * 30);
    }


    /**
     * ??smsKey????
     *
     * @param usrRequst
     * @return
     * @throws ServiceException
     * @throws BadRequestException
     */
    public LoginSession smsAutoLogin(UsrRequst usrRequst) throws ServiceException {
        //1.???????key
        this.checkSmsCodeKey(this.redisClient, usrRequst.getSmsKey(), usrRequst.getMobileNumber());
        List<UsrUser> users = this.scanUser(usrRequst);
        if (CollectionUtils.isEmpty(users)) {
            log.info("????", "users--->" + users);
            throw new ServiceException(ExceptionEnum.USER_LOGIN_ERROR);
        }
        return this.generateSession(users.get(0), 1);
    }

    /**
     * ??smsKey????
     *
     * @param redisClient
     * @param smsKey
     * @param mobileNumber
     * @throws ServiceException
     */
    public static void checkSmsCodeKey(RedisClient redisClient, String smsKey, String mobileNumber) throws ServiceException {
        String smsLoginKey = redisClient.get(RedisContants.SESSION_SMS_LOGIN_KEY + smsKey);
        if (smsLoginKey == null || !smsLoginKey.equals(mobileNumber)) {
            log.info("???????????" + "users--->" + smsLoginKey);
            throw new ServiceException(ExceptionEnum.USER_SMS_CODE_AUTO_LOGIN_ERROR);
        }
        redisClient.del(RedisContants.SESSION_SMS_LOGIN_KEY + smsKey);
    }

    /**
     * ????
     *
     * @param baseRequest
     * @throws ServiceException
     */
    public void logout(BaseRequest baseRequest) throws ServiceException {
        if (!UserSessionUtil.delLoginSession(this.redisClient, baseRequest.getSessionId())) {
            log.info("????", "");
            throw new ServiceException(ExceptionEnum.USER_LOGOUT_ERROR);
        }
    }


    /**
     * ?????????
     *
     * @param request
     * @return
     */
    public UsrCertificationResponse initCertificationView(BaseRequest request) {

        List<UsrCertificationInfo> scanList = this.usrCertificationDao.getCertificationResults(request.getUserUuid());
        UsrCertificationResponse response = new UsrCertificationResponse();

        // ????? ????? ???? ???? ??????????? ????????  ????????
        response.setCertificationResult(0);
        response.setCertificationResult2(0);

        if (!CollectionUtils.isEmpty(scanList)) {
            for (UsrCertificationInfo info : scanList) {
                if (info.getCertificationType() == CertificationEnum.FACE_IDENTITY.getType()) {
                    response.setCertificationResult(info.getCertificationResult());
                } else if (info.getCertificationType() == CertificationEnum.VIDEO_IDENTITY.getType()) {
                    response.setCertificationResult2(info.getCertificationResult());
                }
            }
        }
        return response;
    }

    /**
     * ???????????
     *
     * @param infoRequest
     * @return
     */
    @Transactional
    public void submitCertificationInfo(UsrSubmitCerInfoRequest infoRequest) throws Exception {
//        // ??????????? 120??
        if (!StringUtils.isEmpty(infoRequest.getOrderNo())) {
            String key = infoRequest.getOrderNo() + infoRequest.getCertificationType();
            if (!redisClient.lockRepeat120(key)) {
                log.error("???????");
                throw new ServiceException(ExceptionEnum.ADDLINKMANINFO_COMMIT_REPEAT);
            }
        }

        // 2????  3????  4 facebook
        int certiType = infoRequest.getCertificationType();
        switch (certiType) {

            case 2:
//                TODO: ????
                UsrAttachmentInfo search = new UsrAttachmentInfo();
                search.setUserUuid(infoRequest.getUserUuid());
                search.setAttachmentType(UsrAttachmentEnum.ID_CARD.getType());
                List<UsrAttachmentInfo> infoList = usrAttachmentInfoDao.scan(search);

                if (!CollectionUtils.isEmpty(infoList)) {
                    // ??????????
                    UsrAttachmentInfo idInfo = infoList.get(0);
                    // ????
                    String userIdenIconUrl = idInfo.getAttachmentUrl();
                    String faceIdenIconUrl = infoRequest.getCertificationData();
                    String userIdenContent = FileHelper.getImageStrFromUrl(userIdenIconUrl);
                    this.yiTuService.verifyFacePackage(userIdenContent,
                            faceIdenIconUrl, infoRequest.getOrderNo(), infoRequest.getUserUuid(), infoRequest.getSessionId());

                } else {

                    log.info("???????");
                    throw new ServiceException(ExceptionEnum.USER_NO_VIFIFY);
                }

                // ???????? ????
                dealWithCertificationInfo(certiType, infoRequest.getUserUuid(), CertificationResultEnum.AUTH_SUCCESS.getType());

                break;

            case 3:

                // ?????????
                insertAttachment(infoRequest.getUserUuid(), infoRequest.getAttachmentUrl(), String.valueOf(UsrAttachmentEnum.VIDEO.getType()));
                // ?????? ????
                dealWithCertificationInfo(certiType, infoRequest.getUserUuid(), CertificationResultEnum.AUTH_SUCCESS.getType());
                break;

            case 4: {
                //            TODO:  ???? ???
                UsrCertificationInfo certificationInfo = new UsrCertificationInfo();
                certificationInfo.setUserUuid(infoRequest.getUserUuid());
                certificationInfo.setCertificationType(CertificationEnum.FACEBOOK_IDENTITY.getType());
                List<UsrCertificationInfo> scanList = this.usrCertificationDao.scan(certificationInfo);
                if (CollectionUtils.isEmpty(scanList)) {

                    certificationInfo.setCertificationResult(CertificationResultEnum.AUTH_SUCCESS.getType());
                    this.usrCertificationDao.insert(certificationInfo);

                } else {
                    UsrCertificationInfo update = scanList.get(0);
                    certificationInfo.setCertificationResult(CertificationResultEnum.AUTH_SUCCESS.getType());
                    this.usrCertificationDao.update(update);
                }

                // TODO: ???????
                OrderThirdDataMongo scan = this.ordThirdDataService.getThridDataByOrderNo(infoRequest.getOrderNo(), ThirdDataTypeEnum.FACEBOOK_IDENTITY_DATA);
                if (scan != null) {

                    this.ordThirdDataService.update(scan, infoRequest.getCertificationData());
                } else {

                    this.ordThirdDataService.add(infoRequest.getCertificationData(), infoRequest.getOrderNo(), infoRequest.getUserUuid(), ThirdDataTypeEnum.FACEBOOK_IDENTITY_DATA, 1);
                }

                break;
            }
            case 12:
            case 13:
                // ??   // ???
            {   //            TODO:  ???? ???
                UsrCertificationInfo certificationInfo = new UsrCertificationInfo();
                certificationInfo.setUserUuid(infoRequest.getUserUuid());
                if (infoRequest.getCertificationType() == 12) {
                    certificationInfo.setCertificationType(CertificationEnum.STEUERKARTED.getType());
                } else {
                    certificationInfo.setCertificationType(CertificationEnum.INSURANCE_CARD.getType());
                }

                List<UsrCertificationInfo> scanList = this.usrCertificationDao.scan(certificationInfo);
                if (CollectionUtils.isEmpty(scanList)) {

                    certificationInfo.setCertificationData(infoRequest.getCertificationData());
                    certificationInfo.setCertificationResult(CertificationResultEnum.NOT_AUTH.getType());
                    this.usrCertificationDao.insert(certificationInfo);

                } else {
                    UsrCertificationInfo update = scanList.get(0);
                    update.setCertificationData(infoRequest.getCertificationData());
                    update.setCertificationResult(CertificationResultEnum.NOT_AUTH.getType());
                    this.usrCertificationDao.update(update);
                }
                break;
            }
            case 15:
            case 16:
            case 17:
            case 18: {   //15 NPWP 16 BPJS  17 运营商  18 Linkedin
                UsrCertificationInfo certificationInfo = new UsrCertificationInfo();
                certificationInfo.setUserUuid(infoRequest.getUserUuid());
                if (infoRequest.getCertificationType() == 15) {
                    certificationInfo.setCertificationType(CertificationEnum.NPWP.getType());
                }
                if (infoRequest.getCertificationType() == 16) {
                    certificationInfo.setCertificationType(CertificationEnum.BPJS.getType());
                }
                if (infoRequest.getCertificationType() == 17) {
                    certificationInfo.setCertificationType(CertificationEnum.OPERATOR.getType());
                } else {
                    certificationInfo.setCertificationType(CertificationEnum.LINKEDIN.getType());
                }
                List<UsrCertificationInfo> scanList = this.usrCertificationDao.scan(certificationInfo);
                if (CollectionUtils.isEmpty(scanList)) {
                    certificationInfo.setCertificationData(infoRequest.getCertificationData());
                    certificationInfo.setCertificationResult(CertificationResultEnum.AUTH_SUCCESS.getType());
                    this.usrCertificationDao.insert(certificationInfo);

                } else {
                    UsrCertificationInfo update = scanList.get(0);
                    update.setCertificationData(infoRequest.getCertificationData());
                    this.usrCertificationDao.update(update);
                }
                break;
            }
        }
    }

    // ??????
    public void dealWithCertificationInfo(int type, String userUuid, int result) {

        UsrCertificationInfo search = new UsrCertificationInfo();
        search.setCertificationType(type);
        search.setUserUuid(userUuid);
        search.setDisabled(0);
        List<UsrCertificationInfo> infoList = this.usrCertificationDao.scan(search);
        if (!CollectionUtils.isEmpty(infoList)) {
            UsrCertificationInfo updateEntity = infoList.get(0);
            updateEntity.setCertificationResult(result);
            this.usrCertificationDao.update(updateEntity);
        } else {

            search.setCertificationResult(result);
            this.usrCertificationDao.insert(search);
        }
    }


    /**
     * ??????
     *
     * @param infoRequest
     * @return
     */
    @Transactional
    public void submitSupplementInfo(UsrSubmitSupplementInfoRequest infoRequest) throws ServiceException {

        String imageUrls = infoRequest.getImageUrls();
        List<Map<String, Object>> list = JsonUtils.deserialize(imageUrls, List.class);
        for (Map<String, Object> map : list) {
            if (map.get("url") != null) {
                insertAttachment(infoRequest.getUserUuid(), map.get("url").toString(), map.get("type").toString());
            }
        }
        log.info(list.toString());

        // ???orderNo????   ??????
        if (!StringUtils.isEmpty(infoRequest.getOrderNo())) {

            this.usrBaseInfoService.updateOrderStep(infoRequest.getOrderNo(), infoRequest.getUserUuid(), OrdStepTypeEnum.EXTRA_INFO.getType());
        } else {

            // ????? ???????????  ???2-7.3-7.4-7??? ?????
            List<OrdOrder> orderList = this.orderDao.loneOrderList(infoRequest.getUserUuid());
            if (!CollectionUtils.isEmpty(orderList)) {
                OrdOrder order = orderList.get(0);
                this.usrBaseInfoService.updateOrderStep(order.getUuid(), infoRequest.getUserUuid(), OrdStepTypeEnum.EXTRA_INFO.getType());
            }
        }
    }

    public void insertAttachment(String userUuid, String attachmentUrl, String attachmentType) {

        String fullPathURL = this.uploadPath + attachmentUrl;
        if (attachmentUrl != null && (attachmentUrl.toLowerCase().startsWith("http:") || attachmentUrl.toLowerCase().startsWith("https:"))) {
            fullPathURL = attachmentUrl;
            attachmentUrl = attachmentUrl.replaceAll(this.uploadPath, "");
        }
        UsrAttachmentInfo search = new UsrAttachmentInfo();
        search.setDisabled(0);
        search.setUserUuid(userUuid);
        search.setAttachmentType(Integer.parseInt(attachmentType));
        List<UsrAttachmentInfo> searchList = this.usrAttachmentInfoDao.scan(search);
        if (CollectionUtils.isEmpty(searchList)) {
            //  ??

            search.setAttachmentUrl(fullPathURL);
            search.setAttachmentSavePath(attachmentUrl);
            this.usrAttachmentInfoDao.insert(search);

        } else {

            UsrAttachmentInfo updateEntity = new UsrAttachmentInfo();
            if (!StringUtils.isEmpty(fullPathURL) && !StringUtils.isEmpty(attachmentUrl)) {

                updateEntity.setUuid(searchList.get(0).getUuid());
                updateEntity.setUpdateTime(new Date());
                updateEntity.setAttachmentUrl(fullPathURL);
                updateEntity.setAttachmentSavePath(attachmentUrl);
                //  ??
                this.usrAttachmentInfoDao.update(updateEntity);
            }

        }
    }


    /**
     * ??????
     *
     * @param request
     * @return
     */
    public List<UsrAttachmentResponse> initSupplementInfo(BaseRequest request) throws Exception {

        //????????
        UsrUser usrUser = new UsrUser();
        usrUser.setUuid(request.getUserUuid());
        usrUser.setDisabled(0);
//        usrUser.setStatus(1);
        List<UsrUser> userList = this.usrDao.scan(usrUser);
        if (userList.isEmpty()) {
            log.info("?????");
            throw new ServiceException(ExceptionEnum.USER_NOT_FOUND);
        }
        UsrUser user = userList.get(0);
        Integer userRole = user.getUserRole();
        if (userRole != 0) {
            // ?????

            List<UsrAttachmentInfo> list;
            if (userRole == 1) {
                // ??
                list = this.usrAttachmentInfoDao.getAttachmenInfoByStudent(request.getUserUuid());
            } else {
                // ???
                list = this.usrAttachmentInfoDao.getAttachmenInfoByWorker(request.getUserUuid());

            }
            if (!CollectionUtils.isEmpty(list)) {

                List<UsrAttachmentResponse> responseList = new ArrayList<>();
                for (UsrAttachmentInfo info : list) {
                    UsrAttachmentResponse response = new UsrAttachmentResponse();
                    response.setAttachmentType(info.getAttachmentType());
                    response.setAttachmentUrl(usrBaseInfoService.setUsrAttachmentUrl(info));
                    response.setAttachmentName(getAttachmenNameWithType(info.getAttachmentType()));
                    responseList.add(response);
                }
                return responseList;

            } else {

                return null;
            }

        } else {
            log.info("???????");
            throw new ServiceException(ExceptionEnum.USER_NOT_CHOOSE_IDENTITY);
        }

    }

    public String getAttachmenNameWithType(int attachmentType) {
        String attachmentName = "";
        switch (attachmentType) {
            case 4:
                attachmentName = "信用卡";
                break;
            case 5:
                attachmentName = "驾驶证";
                break;
            case 6:
                attachmentName = "护照";
                break;
            case 7:
                attachmentName = "家庭卡";
                break;
            case 8:
                attachmentName = "工资卡";
                break;
            case 9:
                attachmentName = "银行卡流水";
                break;
            case 17:
                attachmentName = "保险卡";
                break;
        }
        return attachmentName;
    }

    // ??userUuid  ?? user
    public UsrUser getUserByUuid(String userUuid) {
        UsrUser user = new UsrUser();
        user.setUuid(userUuid);
        user.setDisabled(0);
        user.setStatus(1);
        List<UsrUser> users = this.usrDao.scan(user);
        if (CollectionUtils.isEmpty(users)) {
            return user;
        }
        return users.get(0);
    }

    /**
     * select usrUser ,contain invaild usrUser
     *
     * @Author:huwei
     * @Date:18.8.30-16:20
     */
    public UsrUser getAllUserByUuid(String userUuid) {
        UsrUser user = new UsrUser();
        user.setUuid(userUuid);
        user.setDisabled(0);
//        user.setStatus(1);
        List<UsrUser> users = this.usrDao.scan(user);
        if (CollectionUtils.isEmpty(users)) {
            return user;
        }
        return users.get(0);
    }

    // ??userUuid ??????
    public String getUserNameByUuid(String userUuid) {
        UsrUser user = new UsrUser();
        user.setUuid(userUuid);
        user.setDisabled(0);
        user.setStatus(1);
        List<UsrUser> users = this.usrDao.scan(user);
        if (CollectionUtils.isEmpty(users)) {
            return "";
        }
        return users.get(0).getRealName();
    }

    /**
     * ??????
     */
    public Integer cheakUserRole(BaseRequest request) throws Exception {

        //????????
        UsrUser usrUser = new UsrUser();
        usrUser.setUuid(request.getUserUuid());
        usrUser.setDisabled(0);
        usrUser.setStatus(1);
        List<UsrUser> userList = this.usrDao.scan(usrUser);
        if (userList.isEmpty()) {
            log.info("?????");
            throw new ServiceException(ExceptionEnum.USER_NOT_FOUND);
        }

        return userList.get(0).getUserRole();
    }


    //????ip????????
    public void checkIpTodayIsSubmith5(String ip) throws ServiceException, ServiceExceptionSpec {
        String sysParamValue = this.sysParamService.getSysParamValue(SysParamContants.IP_ADDRESS_COUNT);
        int ipSetCount = 10;
        if (sysParamValue != null) {
            ipSetCount = Integer.valueOf(sysParamValue);
        }
        if (ip.indexOf(":") != -1) {
            ip = "127.0.0.1";
        }
        //??????>????,??false,????true
        String ipCountStr = redisClient.get(RedisContants.CACHE_IP_ADDRESS_COUNT_KEY + DateUtils.formDate(new Date(), "yyyy-MM-dd HH") + ip);
        int ipCount = 0;
        if (ipCountStr == null) {
            ipCount = 0;
        } else {
            ipCount = Integer.valueOf(ipCountStr);
        }
        if (ipCount >= ipSetCount) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_IP_ADDRESS_COUNT);
        }
        ipCount++;
        redisClient.set(RedisContants.CACHE_IP_ADDRESS_COUNT_KEY + DateUtils.formDate(new Date(), "yyyy-MM-dd HH") + ip, String.valueOf(ipCount), 60 * 60 * 2);
    }

    public List<UsrUser> getUserInfo(UsrUser searchInfo) {
        searchInfo.setDisabled(0);
        searchInfo.setStatus(1);
        return usrDao.scan(searchInfo);
    }

    public Integer updateUser(UsrUser update) {
        return usrDao.update(update);
    }


    /**
     * 用户对催收员评价
     *
     * @param request
     * @throws Exception
     */
    public void insertCollectionScore(UsrEvaluateScoreRequest request) throws Exception {

        if (StringUtils.isEmpty(request.getOrderNo())
                || StringUtils.isEmpty(request.getCollectionUuid())) {
            throw new ServiceException(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        UsrEvaluateScore score = new UsrEvaluateScore();
        score.setDisabled(0);
        score.setOrderNo(request.getOrderNo());
        score.setUserUuid(request.getCollectionUuid());
        score.setServiceMentality(request.getServiceMentality());
        score.setCommunicationBility(request.getCommunicationBility());
        score.setType(1);
        //封装催收人员属于哪个阶段
        score.setPostId(getPostId(request.getCollectionUuid()));
        //封装是谁评价的
        OrdOrder ordOrder = new OrdOrder();
        ordOrder.setDisabled(0);
        ordOrder.setUuid(request.getOrderNo());
        List<OrdOrder> ordOrders = orderDao.scan(ordOrder);
        if (!CollectionUtils.isEmpty(ordOrders)) {
            UsrUser usrUser = new UsrUser();
            usrUser.setDisabled(0);
            usrUser.setUuid(ordOrders.get(0).getUserUuid());
            List<UsrUser> usrUsers = usrDao.scan(usrUser);
            if (!CollectionUtils.isEmpty(usrUsers)) {
                score.setCreateUser(usrUsers.get(0).getId());
                score.setUpdateUser(usrUsers.get(0).getId());
            }
        }

        usrEvaluateScoreDao.insert(score);
    }


    public void addCheckIziWhatsAppTask(final String userUuid) {
        executorService.submit(new IziWhatsAppTask(this, userUuid));
    }


    /**
     * 保存同盾设备指纹
     */
    public void saveTongDunBlackBox(UsrBankRequest request) {

        if (!StringUtils.isEmpty(request.getOrderNo()) && !StringUtils.isEmpty(request.getBlackBox())) {

            MoboxDataMongo mongo = new MoboxDataMongo();
            mongo.setCreateTime(new Date());
            mongo.setUpdateTime(new Date());
            mongo.setType(MoboxDataEnum.BLACK_BOX_DATA.getType());
            mongo.setData(request.getBlackBox());
            mongo.setOrderNo(request.getOrderNo());
            mongo.setUserUuid(request.getUserUuid());
            this.moboxDataMongoDal.insert(mongo);
        }
    }

    /**
     * izi检查用户whatsapp信息
     *
     * @param userUuid
     */
    public void checkIziWhatsappOpen(String userUuid) {
        try {
            log.info("check izi whatsapp for user: {}", userUuid);
            //查询whatsapp账号[用用户的手机号]
//            String ownerWhatsapp = userDetailService.getWhatsAppAccount(userUuid, false);
            UsrUser user = this.getUserByUuid(userUuid);
            String ownerWhatsapp = DESUtils.decrypt(user.getMobileNumberDES());
            //查询当前订单号
            List<OrdOrder> orders = orderDao.getOrder(userUuid);
            if (CollectionUtils.isEmpty(orders)) {
                log.info("the order is empty for userUuid: {} to invoke izi whatsapp", userUuid);
                return;
            }
            Optional<OrdOrder> currentOrder = orders.stream().filter(elem -> elem.getStatus() == 1 || elem.getStatus() == 2).findFirst();
            if (!currentOrder.isPresent()) {
                log.info("no current order for userUuid: {} to invoke izi whatsapp", userUuid);
                return;
            }

            List<UserIziVerifyResultMongo> lastRecordList = iziService.getLatestWhatsAppCheckResultList(currentOrder.get().getUuid());

            List<String> finishedNumbers = new ArrayList<>();
            if (!CollectionUtils.isEmpty(lastRecordList)) {

                //查出非checking 状态的号码，标识已经有确定状态
                finishedNumbers =
                        lastRecordList.stream().filter(elem -> !StringUtils.isEmpty(elem.getWhatsapp()) && !"checking".equals(elem.getWhatsapp())).map(elem -> {
                            String formatNumber = CheakTeleUtils.telephoneNumberValid2(elem.getWhatsAppNumber());
                            if (StringUtils.isEmpty(formatNumber)) {
                                return elem.getWhatsAppNumberType() + "|" + elem.getWhatsAppNumber();
                            } else {
                                return elem.getWhatsAppNumberType() + "|" + formatNumber;
                            }
                        }).collect(Collectors.toList());
            }


//            if (!isWhatsappChecked(ownerWhatsapp, "0", finishedNumbers) && !StringUtils.isEmpty(ownerWhatsapp)) {
//                IziResponse iziResponse = iziService.whatsAppIsOpen(ownerWhatsapp, currentOrder.get().getUuid(), userUuid);
//                iziService.saveIziWhatsApp(currentOrder.get().getUuid(), userUuid, ownerWhatsapp, JsonUtils.deserialize(JsonUtils.serialize(iziResponse),
//                        IziService.IziWhatsappDetail.class), "0");
//            }

            //号码详情版whatsapp
            IziWhatsAppDetailEntity ownerWhatsAppDetail = iziWhatsAppService.getLatestIziWhatsDetail(userUuid,0,ownerWhatsapp);
            if (ownerWhatsAppDetail == null || "checking".equalsIgnoreCase(ownerWhatsAppDetail.getWhatsapp())) {
                //没取到结果
                iziService.getWhatsAppDetail(ownerWhatsapp, currentOrder.get(), 0);
            }


            List<UsrLinkManInfo> usrList = userBackupLinkmanService.getLinkManInfo(userUuid);
            //检查相同号码是否已经跑过而且而且成功
            for (UsrLinkManInfo elem : usrList) {
                if (StringUtils.isEmpty(elem.getContactsMobile())) {
                    continue;
                }
                String number = CheakTeleUtils.telephoneNumberValid2(elem.getContactsMobile());
                if (StringUtils.isEmpty(number)) {
                    number = elem.getContactsMobile();
                }
                if (StringUtils.isEmpty(number)) {
                    continue;
                }
                if (isWhatsappChecked(number, elem.getSequence().toString(), finishedNumbers)) {
                    continue;
                }
                IziResponse elemResponse = iziService.whatsAppIsOpen(number, currentOrder.get().getUuid(), userUuid);
                iziService.saveIziWhatsApp(currentOrder.get().getUuid(), userUuid, number, JsonUtils.deserialize(JsonUtils.serialize(elemResponse),
                        IziService.IziWhatsappDetail.class), elem.getSequence().toString());
            }
        } catch (Exception e) {
            log.info("invoke izi error for whatsapp,userUuid: " + userUuid, e);
        }
    }

    private boolean isWhatsappChecked(String currentCheckWhatsapp, String sequence, List<String> checkedNumbers) {
        if (CollectionUtils.isEmpty(checkedNumbers)) {
            return false;
        }
        // the number is empty then set true
        if (StringUtils.isEmpty(currentCheckWhatsapp)) {
            return true;
        }
        boolean alreadyChecked = checkedNumbers.contains(sequence + "|" + currentCheckWhatsapp);
        if (alreadyChecked) {
            log.info("already checked for whatsapp: {}", currentCheckWhatsapp);
        }
        return alreadyChecked;
    }

    /**
     * 通过催收uuid获得当前催收员所在的催收期
     *
     * @param manUserUuid
     * @return
     */
    private Integer getPostId(String manUserUuid) {

        ManUser manUser = new ManUser();
        manUser.setDisabled(0);
        manUser.setStatus(0);
        manUser.setUuid(manUserUuid);
        List<ManUser> manUsers = manSysUserDao.scan(manUser);
        if (CollectionUtils.isEmpty(manUsers)) {
            return 0;
        }
        manUser = manUsers.get(0);
        //不是委外直接取得返回
        if (manUser.getThird().equals(1)) {
            return getResultFromColletor(manUser.getId());
        }

        final String idStr = manUser.getId().toString();
        //如果是委外 先查询出来母账号的id ，再通过id查询结果
        SysDic sysDic = new SysDic();
        sysDic.setDisabled(0);
        sysDic.set_likeField(" dicCode like 'THIRD_COMPANY%' ");
        List<SysDic> sysDics = sysDicDao.scan(sysDic);
        if (CollectionUtils.isEmpty(sysDics)) {
            return 0;
        }
        for (SysDic elem : sysDics) {
            Integer id = Integer.valueOf(elem.getDicCode().replace("THIRD_COMPANY_", ""));
            //查询子账号
            SysDicItem sysDicItem = new SysDicItem();
            sysDicItem.setDisabled(0);
            sysDicItem.setDicId(String.valueOf(elem.getId()));
            List<SysDicItem> sysDicItems = sysDicItemDao.scan(sysDicItem);
            if (!CollectionUtils.isEmpty(sysDicItems)) {
                long count = sysDicItems.stream().filter(e -> e.getDicItemValue().equals(idStr)).count();
                if (count > 0) {
                    return getResultFromColletor(id);
                }
            }
        }

        return 0;
    }

    /**
     * 查询催收期结果
     *
     * @param id
     * @return
     */
    private Integer getResultFromColletor(Integer id) {

        List<ManCollectorInfo> collectorInfos =
                manCollectorInfoDao.listCollectorInfo(id);

        if (CollectionUtils.isEmpty(collectorInfos)) {
            return 0;
        }
        return collectorInfos.get(0).getPostId();
    }


    public UsrUser getUserInfoByMobileDesc(String mobileDesc) {
        if (StringUtils.isEmpty(mobileDesc)) {
            return null;
        }
        UsrUser searchParam = new UsrUser();
        searchParam.setDisabled(0);
        searchParam.setMobileNumberDES(mobileDesc);
        List<UsrUser> userList = usrDao.scan(searchParam);
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        } else {
            return userList.get(0);
        }
    }

    public void updateFcmToken(BaseRequest request) throws ServiceException{
        String deviceNumber = UUID.randomUUID().toString();

        String fcmTokenCache = redisClient.get("fcm_" + request.getUserUuid());
        if(StringUtils.isEmpty(fcmTokenCache)){
            RegisterDeviceInfo registerDeviceInfo = registerDeviceInfoDao.getRegisterDeviceByUserUuid(request.getUserUuid());
            if(registerDeviceInfo != null){
                if(StringUtils.isEmpty(registerDeviceInfo.getFcmToken()) || !registerDeviceInfo.getFcmToken().equals(request.getFcmToken()) ){
                    registerDeviceInfo.setFcmToken(request.getFcmToken());
                    registerDeviceInfoDao.update(registerDeviceInfo);
                    redisClient.set("fcm_" + request.getUserUuid(), request.getFcmToken());
                }
            }
            else{
                try{
                    if(!StringUtils.isEmpty(deviceNumber)){
                        RegisterDeviceInfo newRegDeviceInfo = new RegisterDeviceInfo();
                        newRegDeviceInfo.setCreateUser(0);
                        newRegDeviceInfo.setUserUuid(request.getUserUuid());
                        newRegDeviceInfo.setDeviceNumber(deviceNumber);
                        newRegDeviceInfo.setDeviceType(request.getClient_type());
                        newRegDeviceInfo.setIpAddress(request.getIPAdress());
                        newRegDeviceInfo.setFcmToken(request.getFcmToken());
                        newRegDeviceInfo.setMacAddress(request.getMac());
                        registerDeviceInfoDao.insert(newRegDeviceInfo);
    
                        redisClient.set("fcm_" + request.getUserUuid(), request.getFcmToken());
                    }
                    else{
                        log.info("No device id for user: {0}", request.getUserUuid());
                        throw new ServiceException(ExceptionEnum.USER_REGIST_DEVICENO_IDENTICAL);
                    }
                }
                catch(DataIntegrityViolationException e){
                    throw new ServiceException(ExceptionEnum.USER_REGIST_DEVICENO_IDENTICAL); 
                }
            }
        }
        else{
            if(!fcmTokenCache.equals(request.getFcmToken()) && !StringUtils.isEmpty(request.getFcmToken())){
                RegisterDeviceInfo registerDeviceInfo = registerDeviceInfoDao.getRegisterDeviceByUserUuid(request.getUserUuid());
                if(registerDeviceInfo != null){
                    registerDeviceInfoDao.updateFcmToken(request.getUserUuid(), request.getFcmToken());
                    redisClient.set("fcm_" + request.getUserUuid(), request.getFcmToken());
                }
                else{
                    try{
                        if(!StringUtils.isEmpty(deviceNumber)){
                            RegisterDeviceInfo newRegDeviceInfo = new RegisterDeviceInfo();
                            newRegDeviceInfo.setCreateUser(0);
                            newRegDeviceInfo.setUserUuid(request.getUserUuid());
                            newRegDeviceInfo.setDeviceNumber(deviceNumber);
                            newRegDeviceInfo.setDeviceType(request.getClient_type());
                            newRegDeviceInfo.setIpAddress(request.getIPAdress());
                            newRegDeviceInfo.setFcmToken(request.getFcmToken());
                            newRegDeviceInfo.setMacAddress(request.getMac());
                            registerDeviceInfoDao.insert(newRegDeviceInfo);
    
                            redisClient.set("fcm_" + request.getUserUuid(), request.getFcmToken());
                        }
                        else{
                            log.info("No device id for user: {0}", request.getUserUuid());
                            throw new ServiceException(ExceptionEnum.USER_REGIST_DEVICENO_IDENTICAL);
                        }
                    }
                    catch(DataIntegrityViolationException e){
                        throw new ServiceException(ExceptionEnum.USER_REGIST_DEVICENO_IDENTICAL); 
                    }
                }
            }
        }
    }
}

