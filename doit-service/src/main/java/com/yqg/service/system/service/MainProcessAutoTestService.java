package com.yqg.service.system.service;

import com.yqg.common.enums.order.OrdStepTypeEnum;
import com.yqg.common.models.LoginSession;
import com.yqg.common.models.SmsRequest;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.JsonUtils;
import com.yqg.service.order.OrdService;
import com.yqg.service.order.request.OrdRequest;
import com.yqg.service.order.response.OrderOrderResponse;
import com.yqg.service.user.request.*;
import com.yqg.service.user.response.BackupLinkmanResponse;
import com.yqg.service.user.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/***
 * 主流程自动化测试
 */
@Service
@Slf4j
public class MainProcessAutoTestService {
    @Autowired
    private SmsService smsService;
    @Autowired
    private UsrService usrService;
    @Autowired
    private OrdService ordService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private UsrBaseInfoService usrBaseInfoService;
    @Autowired
    private UserLinkManService userLinkManService;
    @Autowired
    private UsrBankService usrBankService;

    @Transactional(rollbackFor = Exception.class)
    public void loanForWorker() throws Exception {
        log.info("start send sms");
        String strSmsReq = "{\"net_type\":\"wifi\",\"system_version\":\"8.1.0\",\"client_type\":\"android\",\"channel_sn\":\"10002\"," +
                "\"channel_name\":\"playStore\",\"deviceId\":\"\",\"client_version\":\"1.6.6\",\"resolution\":\"1424*720\",\"IPAdress\":\"192.168.0.40\",\"sign\":\"IwZWFkZjcwZjU0ZjIyZmQxZGY5ODhhZmY2MGEyNm\",\"timestamp\":\"1555057056\",\"sessionId\":\"\",\"userUuid\":null,\"mac\":null,\"wifimac\":null,\"lbsX\":null,\"lbsY\":null,\"deviceSysModel\":\"CPH1803\",\"androidId\":\"1f2a15fa166806bf\",\"mobileNumber\":\"81398644017\",\"smsType\":\"LOGIN\",\"iDCardNo\":null,\"verifyType\":\"1\",\"idcardNo\":null,\"ipadress\":\"192.168.0.40\"}";
        SmsRequest smsRequest = JsonUtils.deserialize(strSmsReq, SmsRequest.class);
        smsRequest.setMobileNumber("80000000000");//测试账号
        smsService.sendSmsCode(smsRequest);
        log.info("start to signup with smsCode");

        String strSignup = "{\"net_type\":\"wifi\",\"system_version\":\"8.1.0\",\"client_type\":\"android\",\"channel_sn\":\"10002\"," +
                "\"channel_name\":\"playStore\",\"deviceId\":\"\",\"client_version\":\"1.6.6\",\"resolution\":\"1424*720\",\"IPAdress\":\"192.168.0" +
                ".40\",\"sign\":\"ZiMDA2MGJmYTBiNDQ3ZWUzYzBiMzM3MmUwZjNkM2\",\"timestamp\":\"1555057115\",\"sessionId\":\"\",\"userUuid\":null," +
                "\"mac\":null,\"wifimac\":null,\"lbsX\":null,\"lbsY\":null,\"deviceSysModel\":\"CPH1803\",\"androidId\":\"1f2a15fa166806bf\"," +
                "\"mobileNumber\":\"81398644017\",\"smsCode\":\"8888\",\"role\":null,\"smsKey\":null,\"realName\":null,\"userSource\":null," +
                "\"invite\":null,\"ipadress\":\"192.168.0.40\"}";

        UsrRequst usrRequst = JsonUtils.deserialize(strSignup, UsrRequst.class);
        usrRequst.setMobileNumber("80000000000");
        LoginSession signUpResult = usrService.signup(usrRequst);
        log.info("the userId is: " + signUpResult.getUserUuid());


        log.info("start to add order information");
        String toOrderParam = "{\"net_type\":\"wifi\",\"system_version\":\"8.1.0\",\"client_type\":\"android\",\"channel_sn\":\"10002\"," +
                "\"channel_name\":\"playStore\",\"deviceId\":\"\",\"client_version\":\"1.6.6\",\"resolution\":\"1424*720\",\"IPAdress\":\"192.168.0" +
                ".40\",\"sign\":\"NiMWI3MWQ1MWYwM2JhZWM5NTYwMWJlZThmMTlkMG\",\"timestamp\":\"1555057455\",\"sessionId\":\"51f644e6098346aea8ca226de26c9962\",\"userUuid\":\"3EE0C673995E43E5B9EA331358FC1EDF\",\"mac\":\"CE:16:E6:2E:BC:A1\",\"wifimac\":null,\"lbsX\":\"121.4727923\",\"lbsY\":\"31.2092972\",\"deviceSysModel\":\"CPH1803\",\"androidId\":\"1f2a15fa166806bf\",\"productUuid\":\"314\",\"detailed\":\"\",\"deviceType\":\"android\",\"deviceName\":\"CPH1803\",\"phoneBrand\":\"OPPO\",\"totalMemory\":\"1.92吉字节\",\"remainMemory\":\"828兆字节\",\"totalSpace\":\"9.70吉字节\",\"remainSpace\":\"2.69吉字节\",\"IMEI\":\"\",\"IMSI\":\"\",\"SimNumber\":\"\",\"cpuType\":\"qcom\",\"lastPowerOnTime\":\"2019-04-11 03:01:16\",\"dnsStr\":\"\",\"isRoot\":\"0\",\"memoryCardCapacity\":\"9.49吉字节\",\"wifiList\":\"{\\\"wifiList\\\":[{\\\"BSSID\\\":\\\"d2:76:e7:97:8a:57\\\",\\\"SSID\\\":\\\"ShanJi-GUEST\\\",\\\"isConnected\\\":\\\"0\\\",\\\"level\\\":\\\"-37\\\",\\\"timestamp\\\":\\\"6234-03-27 06:42:29\\\"},{\\\"BSSID\\\":\\\"d0:76:e7:d7:8a:57\\\",\\\"SSID\\\":\\\"ShanJi-ISAT\\\",\\\"isConnected\\\":\\\"1\\\",\\\"level\\\":\\\"-38\\\",\\\"timestamp\\\":\\\"6234-03-27 06:42:09\\\"},{\\\"BSSID\\\":\\\"d2:76:e7:97:96:33\\\",\\\"SSID\\\":\\\"ShanJi-GUEST\\\",\\\"isConnected\\\":\\\"0\\\",\\\"level\\\":\\\"-48\\\",\\\"timestamp\\\":\\\"6234-03-27 06:42:44\\\"},{\\\"BSSID\\\":\\\"d0:76:e7:d7:96:33\\\",\\\"SSID\\\":\\\"ShanJi-ISAT\\\",\\\"isConnected\\\":\\\"0\\\",\\\"level\\\":\\\"-49\\\",\\\"timestamp\\\":\\\"6234-03-27 06:42:36\\\"},{\\\"BSSID\\\":\\\"e6:6a:6a:56:2f:43\\\",\\\"SSID\\\":\\\"DIRECT-ZIDESKTOP-NR1P62TmsWD\\\",\\\"isConnected\\\":\\\"0\\\",\\\"level\\\":\\\"-52\\\",\\\"timestamp\\\":\\\"6234-03-27 06:42:52\\\"},{\\\"BSSID\\\":\\\"d0:76:e7:d7:91:b3\\\",\\\"SSID\\\":\\\"ShanJi-ISAT\\\",\\\"isConnected\\\":\\\"0\\\",\\\"level\\\":\\\"-54\\\",\\\"timestamp\\\":\\\"6234-03-27 06:43:00\\\"},{\\\"BSSID\\\":\\\"d2:76:e7:97:91:b3\\\",\\\"SSID\\\":\\\"ShanJi-GUEST\\\",\\\"isConnected\\\":\\\"0\\\",\\\"level\\\":\\\"-54\\\",\\\"timestamp\\\":\\\"6234-03-27 06:43:07\\\"},{\\\"BSSID\\\":\\\"66:13:79:7e:8a:3f\\\",\\\"SSID\\\":\\\"DIRECT-NLDESKTOP-IGTU88VmsUQ\\\",\\\"isConnected\\\":\\\"0\\\",\\\"level\\\":\\\"-65\\\",\\\"timestamp\\\":\\\"6234-03-27 06:43:15\\\"}]}\",\"mobileLanguage\":\"zh\",\"isSimulator\":\"0\",\"battery\":\"100%\",\"pictureNumber\":\"0\",\"province\":\"\",\"city\":\"\",\"bigDirect\":\"\",\"smallDirect\":\"\",\"orderType\":null,\"orderNo\":null,\"imei\":\"\",\"imsi\":\"\",\"simNumber\":\"\",\"ipadress\":\"192.168.0.40\"}";
        OrdRequest ordRequest = JsonUtils.deserialize(toOrderParam, OrdRequest.class);
        ordRequest.setUserUuid(signUpResult.getUserUuid());


        OrderOrderResponse orderResponse = ordService.toOrder(ordRequest, redisClient);
        log.info("the orderNo is: {}", orderResponse.getOrderNo());
        log.info("start to choose role");

        String strRoleRequest = "{\"net_type\":\"wifi\",\"system_version\":\"8.1.0\",\"client_type\":\"android\",\"channel_sn\":\"10002\"," +
                "\"channel_name\":\"playStore\",\"deviceId\":\"\",\"client_version\":\"1.6.6\",\"resolution\":\"1424*720\",\"IPAdress\":\"192.168.0.40\",\"sign\":\"gxOGFlN2NlYzFhY2M0NTc0OTk2OTg0OWUzYjQ5Y2\",\"timestamp\":\"1555057460\",\"sessionId\":\"51f644e6098346aea8ca226de26c9962\",\"userUuid\":\"3EE0C673995E43E5B9EA331358FC1EDF\",\"mac\":null,\"wifimac\":null,\"lbsX\":null,\"lbsY\":null,\"deviceSysModel\":\"CPH1803\",\"androidId\":\"1f2a15fa166806bf\",\"role\":2,\"orderNo\":\"011904121624144331\",\"ipadress\":\"192.168.0.40\"}";


        UsrRolesRequest usrRolesRequest = JsonUtils.deserialize(strRoleRequest, UsrRolesRequest.class);
        usrRolesRequest.setUserUuid(signUpResult.getUserUuid());
        usrRolesRequest.setOrderNo(orderResponse.getOrderNo());

        usrBaseInfoService.rolesChoose(usrRolesRequest);


        log.info("start to save ktp photo...");
        String saveKtpReq = "{\"net_type\":\"wifi\",\"system_version\":\"8.1.0\",\"client_type\":\"android\",\"channel_sn\":\"10002\"," +
                "\"channel_name\":\"playStore\",\"deviceId\":\"\",\"client_version\":\"1.6.6\",\"resolution\":\"1424*720\",\"IPAdress\":\"192.168.0.40\",\"sign\":\"JhZmUwY2Y0ZGQ5MzhiOTNmYTNjYjdkYWM1Nzg2OT\",\"timestamp\":\"1555057481\",\"sessionId\":\"51f644e6098346aea8ca226de26c9962\",\"userUuid\":\"3EE0C673995E43E5B9EA331358FC1EDF\",\"mac\":null,\"wifimac\":null,\"lbsX\":null,\"lbsY\":null,\"deviceSysModel\":\"CPH1803\",\"androidId\":\"1f2a15fa166806bf\",\"photoUrl\":\"/MyUpload/ID_CARD/C/W/498a58524f9b2e23796474b99b5e1a18.jpg\",\"photoType\":\"0\",\"ipadress\":\"192.168.0.40\"}";
        SaveUserPhotoRequest saveUserPhotoRequest = JsonUtils.deserialize(saveKtpReq, SaveUserPhotoRequest.class);
        saveUserPhotoRequest.setUserUuid(signUpResult.getUserUuid());
        usrBaseInfoService.saveUserPhoto(saveUserPhotoRequest);



        log.info("start to save selfie photo...");
        String saveSelfieReq = "{\"net_type\":\"wifi\",\"system_version\":\"8.1.0\",\"client_type\":\"android\",\"channel_sn\":\"10002\"," +
                "\"channel_name\":\"playStore\",\"deviceId\":\"\",\"client_version\":\"1.6.6\",\"resolution\":\"1424*720\",\"IPAdress\":\"192.168.0.40\",\"sign\":\"E1MTdkNDljZWVlMzhkNmQ0ZGQxMDE5NzM3ZGI0YT\",\"timestamp\":\"1555057493\",\"sessionId\":\"51f644e6098346aea8ca226de26c9962\",\"userUuid\":\"3EE0C673995E43E5B9EA331358FC1EDF\",\"mac\":null,\"wifimac\":null,\"lbsX\":null,\"lbsY\":null,\"deviceSysModel\":\"CPH1803\",\"androidId\":\"1f2a15fa166806bf\",\"photoUrl\":\"/MyUpload/SELEIE_PHOTO/M/Q/c956a2937f506c27679c12dfcd55f0fd.jpg\",\"photoType\":\"2\",\"ipadress\":\"192.168.0.40\"}";
        SaveUserPhotoRequest saveSelfieRequest = JsonUtils.deserialize(saveSelfieReq, SaveUserPhotoRequest.class);
        saveSelfieRequest.setUserUuid(signUpResult.getUserUuid());

        usrBaseInfoService.saveUserPhoto(saveSelfieRequest);

        log.info("start realName verify.");
        String identityReq = "{\"net_type\":\"wifi\",\"system_version\":\"8.1.0\",\"client_type\":\"android\",\"channel_sn\":\"10002\"," +
                "\"channel_name\":\"playStore\",\"deviceId\":\"\",\"client_version\":\"1.6.6\",\"resolution\":\"1424*720\",\"IPAdress\":\"192.168.0" +
                ".40\",\"sign\":\"E5MDM0YmRiZTNiMWFmNDA5NzIyNjU4N2VhYzYyMD\",\"timestamp\":\"1555057543\"," +
                "\"sessionId\":\"51f644e6098346aea8ca226de26c9962\",\"userUuid\":\"3EE0C673995E43E5B9EA331358FC1EDF\",\"mac\":null," +
                "\"wifimac\":null,\"lbsX\":null,\"lbsY\":null,\"deviceSysModel\":\"CPH1803\",\"androidId\":\"1f2a15fa166806bf\",\"orderNo\":null," +
                "\"name\":\"zengxiangcai\",\"idCardNo\":\"876847698888888\",\"sex\":null,\"idCardPhoto\":null,\"handIdCardPhoto\":null," +
                "\"ipadress\":\"192.168.0.40\"}";

        UsrIdentityInfoRequest identityInfoRequest = JsonUtils.deserialize(identityReq,UsrIdentityInfoRequest.class);

        usrBaseInfoService.advanceVerify(identityInfoRequest);
        identityInfoRequest.setUserUuid(signUpResult.getUserUuid());
        identityInfoRequest.setOrderNo(orderResponse.getOrderNo());

        log.info("start to save identity info");
        String strIdentity = "{\"net_type\":\"wifi\",\"system_version\":\"8.1.0\",\"client_type\":\"android\",\"channel_sn\":\"10002\"," +
                "\"channel_name\":\"playStore\",\"deviceId\":\"\",\"client_version\":\"1.6.6\",\"resolution\":\"1424*720\",\"IPAdress\":\"192.168.0.40\",\"sign\":\"FjZTk3NTc3YjcwMjhlM2NmYTNhYTRiZDU3YjBhYz\",\"timestamp\":\"1555057553\",\"sessionId\":\"51f644e6098346aea8ca226de26c9962\",\"userUuid\":\"3EE0C673995E43E5B9EA331358FC1EDF\",\"mac\":null,\"wifimac\":null,\"lbsX\":null,\"lbsY\":null,\"deviceSysModel\":\"CPH1803\",\"androidId\":\"1f2a15fa166806bf\",\"orderNo\":\"011904121624144331\",\"name\":\"hfjdnffnf\",\"idCardNo\":\"8768476949449898\",\"sex\":2,\"idCardPhoto\":\"/MyUpload/ID_CARD/C/W/498a58524f9b2e23796474b99b5e1a18.jpg\",\"handIdCardPhoto\":\"\",\"ipadress\":\"192.168.0.40\"}";
        identityInfoRequest = JsonUtils.deserialize(strIdentity,UsrIdentityInfoRequest.class);
        identityInfoRequest.setUserUuid(signUpResult.getUserUuid());
        identityInfoRequest.setOrderNo(orderResponse.getOrderNo());

        usrBaseInfoService.getAndUpdateUser(identityInfoRequest);



        log.info("add worker basic info");
        String strWorkerBaseInfo = "{\"net_type\":\"wifi\",\"system_version\":\"8.1.0\",\"client_type\":\"android\",\"channel_sn\":\"10002\"," +
                "\"channel_name\":\"playStore\",\"deviceId\":\"\",\"client_version\":\"1.6.6\",\"resolution\":\"1424*720\",\"IPAdress\":\"192.168.0.40\",\"sign\":\"EwODdiZTQ3MWU0YWQyMzg5Y2EyMDk4YzNkNzk3MD\",\"timestamp\":\"1555057719\",\"sessionId\":\"51f644e6098346aea8ca226de26c9962\",\"userUuid\":\"3EE0C673995E43E5B9EA331358FC1EDF\",\"mac\":null,\"wifimac\":null,\"lbsX\":\"121.4729338\",\"lbsY\":\"31.2088558\",\"deviceSysModel\":\"CPH1803\",\"androidId\":\"1f2a15fa166806bf\",\"orderNo\":\"011904121624144331\",\"email\":\"hjdfnfnnffmfk@yahoo.com\",\"academic\":\"Sekolah Menengah Pertama\",\"birthday\":\"12/4/2019\",\"religion\":\"Kristen Katolik\",\"motherName\":\"jcnfmf\",\"childrenAmount\":864,\"maritalStatus\":1,\"province\":\"JAWA TENGAH\",\"city\":\"Kabupaten Demak\",\"bigDirect\":\"Karanganyar\",\"smallDirect\":\"Ketanjung\",\"detailed\":\"bkdnff\",\"addressType\":0,\"borrowUse\":\"Peralatan rumah tangga\",\"birthProvince\":\"JAWA TENGAH\",\"birthCity\":\"Kabupaten Brebes\",\"birthBigDirect\":\"Bumiayu\",\"birthSmallDirect\":\"Kalinusu\",\"monthlyIncome\":null,\"npwp\":\"\",\"insuranceCardPhoto\":null,\"whatsappAccount\":\"\",\"kkCardPhoto\":null,\"ipadress\":\"192.168.0.40\"}";

        UsrWorkBaseInfoRequest workBaseInfoRequest = JsonUtils.deserialize(strWorkerBaseInfo,UsrWorkBaseInfoRequest.class);
        workBaseInfoRequest.setUserUuid(signUpResult.getUserUuid());
        workBaseInfoRequest.setOrderNo(orderResponse.getOrderNo());

        usrBaseInfoService.addWorkBaseInfo(workBaseInfoRequest);


        log.info("add worker detail info");

        String usrWorkerDetail = "{\"net_type\":\"wifi\",\"system_version\":\"8.1.0\",\"client_type\":\"android\"," +
                "\"channel_sn\":\"10002\",\"channel_name\":\"playStore\",\"deviceId\":\"\",\"client_version\":\"1.6.6\",\"resolution\":\"1424*720\",\"IPAdress\":\"192.168.0.40\",\"sign\":\"k5MjE1ZTI5MGIwOWU3ODUwODE2YzU3Y2Q2OTljMj\",\"timestamp\":\"1555057751\",\"sessionId\":\"51f644e6098346aea8ca226de26c9962\",\"userUuid\":\"3EE0C673995E43E5B9EA331358FC1EDF\",\"mac\":null,\"wifimac\":null,\"lbsX\":\"121.4726207\",\"lbsY\":\"31.2099065\",\"deviceSysModel\":\"CPH1803\",\"androidId\":\"1f2a15fa166806bf\",\"orderNo\":\"011904121624144331\",\"companyName\":\"bckd nf\",\"positionName\":\"Pengemudi\",\"monthlyIncome\":\"8.649.956\",\"companyPhone\":\"5764-97949894\",\"province\":\"JAWA BARAT\",\"city\":\"Kabupaten Cianjur\",\"bigDirect\":\"Cianjur\",\"smallDirect\":\"Pamoyanan\",\"detailed\":\"nncndfnf\",\"addressType\":1,\"dependentBusiness\":\"Conglomerate / Holding Company\",\"employeeNumber\":\"\",\"extensionNumber\":\"\",\"ipadress\":\"192.168.0.40\"}";

        UsrWorkInfoRequest workerDetail = JsonUtils.deserialize(usrWorkerDetail,UsrWorkInfoRequest.class);
        workerDetail.setUserUuid(signUpResult.getUserUuid());
        workerDetail.setOrderNo(orderResponse.getOrderNo());

        usrBaseInfoService.addUsrWorkInfo(workerDetail);



        log.info("add linkman");

        String linkmanStr = "{\"net_type\":\"wifi\",\"system_version\":\"8.1.0\",\"client_type\":\"android\",\"channel_sn\":\"10002\"," +
                "\"channel_name\":\"playStore\",\"deviceId\":\"\",\"client_version\":\"1.6.6\",\"resolution\":\"1424*720\",\"IPAdress\":\"192.168.0.40\",\"sign\":\"hhZDMzMjgzNTYzNmVkOTkwZDIxMTI0OTY3YzBjOG\",\"timestamp\":\"1555057806\",\"sessionId\":\"51f644e6098346aea8ca226de26c9962\",\"userUuid\":\"3EE0C673995E43E5B9EA331358FC1EDF\",\"mac\":null,\"wifimac\":null,\"lbsX\":null,\"lbsY\":null,\"deviceSysModel\":\"CPH1803\",\"androidId\":\"1f2a15fa166806bf\",\"linkmanList\":[{\"_start\":null,\"_pageSize\":null,\"_orderBy\":null,\"_condition\":null,\"_inField\":null,\"_inValues\":null,\"_likeField\":null,\"_likeKeyword\":null,\"id\":null,\"disabled\":null,\"uuid\":null,\"createUser\":null,\"createTime\":null,\"updateUser\":null,\"updateTime\":null,\"remark\":null,\"userUuid\":null,\"contactsName\":\"bjdddn\",\"relation\":\"Saudara / saudari\",\"contactsMobile\":\"894497979644\",\"sequence\":2,\"waOrLine\":\"bhdbjdnff\",\"formatMobile\":null},{\"_start\":null,\"_pageSize\":null,\"_orderBy\":null,\"_condition\":null,\"_inField\":null,\"_inValues\":null,\"_likeField\":null,\"_likeKeyword\":null,\"id\":null,\"disabled\":null,\"uuid\":null,\"createUser\":null,\"createTime\":null,\"updateUser\":null,\"updateTime\":null,\"remark\":null,\"userUuid\":null,\"contactsName\":\"vjxdnjcjf\",\"relation\":\"Pasangan\",\"contactsMobile\":\"86495684958\",\"sequence\":4,\"waOrLine\":\"vhdbdcjd\",\"formatMobile\":null},{\"_start\":null,\"_pageSize\":null,\"_orderBy\":null,\"_condition\":null,\"_inField\":null,\"_inValues\":null,\"_likeField\":null,\"_likeKeyword\":null,\"id\":null,\"disabled\":null,\"uuid\":null,\"createUser\":null,\"createTime\":null,\"updateUser\":null,\"updateTime\":null,\"remark\":null,\"userUuid\":null,\"contactsName\":\"vjddncckd\",\"relation\":\"Teman\",\"contactsMobile\":\"849977649898(\",\"sequence\":5,\"waOrLine\":\"bjddjd\",\"formatMobile\":null},{\"_start\":null,\"_pageSize\":null,\"_orderBy\":null,\"_condition\":null,\"_inField\":null,\"_inValues\":null,\"_likeField\":null,\"_likeKeyword\":null,\"id\":null,\"disabled\":null,\"uuid\":null,\"createUser\":null,\"createTime\":null,\"updateUser\":null,\"updateTime\":null,\"remark\":null,\"userUuid\":null,\"contactsName\":\"ncjfnf\",\"relation\":\"Ayah\",\"contactsMobile\":\"864994588644\",\"sequence\":1,\"waOrLine\":\"vdjndfjjf\",\"formatMobile\":null}],\"orderNo\":\"011904121624144331\",\"ipadress\":\"192.168.0.40\"}";

        LinkManRequest linkManRequest = JsonUtils.deserialize(linkmanStr,LinkManRequest.class);
        linkManRequest.setUserUuid(signUpResult.getUserUuid());
        linkManRequest.setOrderNo(orderResponse.getOrderNo());


        BackupLinkmanResponse backupResponse = userLinkManService.addEmergencyLinkmans(linkManRequest, false);
        //更新订单步骤到联系人信息
        usrBaseInfoService.updateOrderStep(linkManRequest.getOrderNo(), linkManRequest.getUserUuid(), OrdStepTypeEnum.CONTACT_INFO.getType());

        log.info("add bank card info");

        String strBankInfo = "{\"net_type\":\"wifi\",\"system_version\":\"8.1.0\",\"client_type\":\"android\",\"channel_sn\":\"10002\"," +
                "\"channel_name\":\"playStore\",\"deviceId\":\"\",\"client_version\":\"1.6.6\",\"resolution\":\"1424*720\",\"IPAdress\":\"192.168.0.40\",\"sign\":\"MxMThkOWRjMGQ0MjgwNzExMjkwZWNkZDZjMzNjNj\",\"timestamp\":\"1555057816\",\"sessionId\":\"51f644e6098346aea8ca226de26c9962\",\"userUuid\":\"3EE0C673995E43E5B9EA331358FC1EDF\",\"mac\":null,\"wifimac\":null,\"lbsX\":null,\"lbsY\":null,\"deviceSysModel\":\"CPH1803\",\"androidId\":\"1f2a15fa166806bf\",\"bankCode\":\"CIMB\",\"bankNumberNo\":\"8679486985949988\",\"bankCardName\":\"hfjdnffnf\",\"orderNo\":\"011904121624144331\",\"thirdType\":0,\"blackBox\":\"eyJvcyI6ImFuZHJvaWQiLCJ2ZXJzaW9uIjoiMy4yLjEiLCJwYWNrYWdlcyI6ImNvbS5xeS5kb2l0KiYxLjYuNiIsInByb2ZpbGVfdGltZSI6MTM1NiwiaW50ZXJ2YWxfdGltZSI6MzcxMCwidG9rZW5faWQiOiJKRmdpeXduMStvYmh5aDZxNU9XdEtrQlRHS1FwdVwvNmFtOFwvVENRUVRpQWtYeHB6QVJweHR4bWdLQ2hQT2JWNllKZkZSYmYzVmU2N0dmdnB1NTZVdWpreUtYZTd6RVhoaHdSWVhLcms1d29VPSJ9\",\"ipadress\":\"192.168.0.40\"}";

        UsrBankRequest userBankRequest = JsonUtils.deserialize(strBankInfo,UsrBankRequest.class);
        userBankRequest.setUserUuid(signUpResult.getUserUuid());
        userBankRequest.setOrderNo(orderResponse.getOrderNo());


        usrBankService.bindBankCard(userBankRequest, redisClient);

        log.info("now the order is going to check bank card and status is 2");

        throw new Exception("order data need to be rollback.");
    }
}
