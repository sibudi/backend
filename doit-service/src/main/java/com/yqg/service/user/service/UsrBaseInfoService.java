package com.yqg.service.user.service;

import com.alibaba.fastjson.JSONObject;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.constants.SysParamContants;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.order.OrdStepTypeEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.common.enums.user.CertificationResultEnum;
import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.enums.user.UsrAttachmentEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.BaseRequest;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.*;
import com.yqg.mongo.dao.OrderUserDataDal;
import com.yqg.mongo.entity.OrderUserDataMongo;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.dao.OrdHistoryDao;
import com.yqg.order.dao.OrdStepDao;
import com.yqg.order.entity.OrdHistory;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdStep;
import com.yqg.service.order.OrdDeviceExtendInfoService;
import com.yqg.service.system.service.SysParamService;
import com.yqg.service.third.izi.IziService;
import com.yqg.service.third.izi.response.IziResponse;
import com.yqg.service.third.jxl.JXLService;
import com.yqg.service.third.jxl.response.JXLBaseResponse;
import com.yqg.service.third.jxl.response.JXLBaseResponse.IdentityVerifyData;
import com.yqg.service.third.yitu.YiTuService;
import com.yqg.service.user.model.*;
import com.yqg.service.user.request.*;
import com.yqg.service.user.response.UsrStudentInfoResponse;
import com.yqg.service.util.CustomEmojiUtils;
import com.yqg.service.util.ImageUtil;
import com.yqg.service.util.LoginSysUserInfoHolder;
import com.yqg.user.dao.*;
import com.yqg.user.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Service
@Slf4j
public class UsrBaseInfoService {
    @Autowired
    private UsrDao usrDao;
    @Autowired
    private UserAttachmentInfoService userAttachmentInfoService;
    @Autowired
    private UsrAttachmentInfoDao usrAttachmentInfoDao;
    @Autowired
    private UsrWorkDetailDao usrWorkDetailDao;
    @Autowired
    private UsrStudentDetailDao usrStudentDetailDao;
    @Autowired
    private UsrLinkManDao usrLinkManDao;
    @Autowired
    private UsrAddressDetailDao usrAddressDetailDao;
    @Autowired
    private UsrCertificationInfoDao usrCertificationInfoDao;
    @Autowired
    private OrdDao orderDao;
    @Autowired
    private OrdStepDao ordStepDao;
    @Autowired
    private SysParamService sysParamService;
    @Autowired
    private OrderUserDataDal orderUserDataDao;
    @Autowired
    private UsrService usrService;

    @Autowired
    private OrdHistoryDao ordHistoryDao;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private UsrCertificationDao usrCertificationDao;
    @Autowired
    private IziService iziService;

    @Value("${upload.path}")
    private String path; //????????

    @Value("${upload.imagePath}")
    private String imagePath; //

    @Value("${jxl.jxlIdentityUrl}")
    private String jxlIdentityUrl;

    @Autowired
    private JXLService jxlService;

    @Autowired
    private UserVerifyResultService userVerifyResultService;

    @Autowired
    UsrHouseWifeDetailDao usrHouseWifeDetailDao;

    @Autowired
    private UsrCertificationService usrCertificationService;

    @Autowired
    private YiTuService yiTuService;
    @Autowired
    private OrdDeviceExtendInfoService ordDeviceExtendInfoService;


    /**
     * ????????
     * @param usrRolesRequest
     * @throws ServiceException
     */
    @Transactional
    public void rolesChoose(UsrRolesRequest usrRolesRequest) throws ServiceException{
        // ????
        UsrUser usrUser = new UsrUser();
        usrUser.setUuid(usrRolesRequest.getUserUuid());
        usrUser.setDisabled(0);
        usrUser.setStatus(1);
        List<UsrUser> userList =  this.usrDao.scan(usrUser);
        if (userList.isEmpty()){
            log.error("rolesChoose - Pengguna tidak terdaftar");
            throw new ServiceException(ExceptionEnum.USER_NOT_FOUND);
        }
        // ?????0???????????????
        if (userList.get(0).getUserRole() == 0){
            usrUser = userList.get(0);
            usrUser.setUserRole(usrRolesRequest.getRole());
            this.usrDao.update(usrUser);
        }else {
            log.error("????????");
            throw new ServiceException(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
       if (!StringUtils.isEmpty(usrRolesRequest.getOrderNo())){
           // ???????
           updateOrderStep(usrRolesRequest.getOrderNo(),usrRolesRequest.getUserUuid(),OrdStepTypeEnum.CHOOSE_ROLE.getType());
       }
    }

    @Transactional
    public void updateUserRoleForCashCash(UsrRolesRequest usrRolesRequest) throws ServiceException {
        UsrUser usrUser = new UsrUser();
        usrUser.setUuid(usrRolesRequest.getUserUuid());
        usrUser.setDisabled(0);
        usrUser.setStatus(1);
        List<UsrUser> userList = this.usrDao.scan(usrUser);
        if (CollectionUtils.isEmpty(userList)) {
            throw new ServiceException(ExceptionEnum.USER_NOT_FOUND);
        }
        UsrUser dbUser  = userList.get(0);
        if (usrRolesRequest.getRole().equals(dbUser.getUserRole())) {
            log.info("the user role is same, userUuid: {} ",dbUser.getUuid());
            return;
        }
        dbUser.setUserRole(usrRolesRequest.getRole());
        this.usrDao.update(dbUser);
    }

    /**
     * ??????
     * @param identityInfoRequest
     * @return
     */
    @Transactional
    public Boolean advanceVerify(UsrIdentityInfoRequest identityInfoRequest) throws ServiceException{
//        String userUuid = identityInfoRequest.getUserUuid();
//        String name = identityInfoRequest.getName().trim();
        String idCardNo = identityInfoRequest.getIdCardNo().trim();
        //????????????
        UsrUser user = new UsrUser();
        user.setDisabled(0);
        user.setStatus(1);
        user.setIdCardNo(idCardNo);
        List<UsrUser> dbUserList = this.usrDao.scan(user);
        if(!CollectionUtils.isEmpty(dbUserList)){
            boolean isOwner = false;
            UsrUser dbUser = dbUserList.get(0);
            if(dbUserList.size()==1){
                isOwner = dbUser.getUuid().equals(identityInfoRequest.getUserUuid());
            }
            if(!isOwner){
                log.error("user idCardNo already exists, idCardNo: {}, userUuid: {}",idCardNo,identityInfoRequest.getUserUuid());
                throw new ServiceException(ExceptionEnum.USER_ID_CARD_IS_EXIST);
            }else{
                //同一个用户实名验证已经过了则不需要继续实名：
                UsrCertificationInfo identityCertificationInfo = usrCertificationService.getSuccessVerificationInfo(identityInfoRequest.getUserUuid(),
                        CertificationEnum
                                .USER_IDENTITY);
                if (StringUtils.isNotEmpty(dbUser.getRealName()) && dbUser.getRealName().equals(identityInfoRequest.getName()) && identityCertificationInfo != null) {
                    log.info("the user already verified, {}",JsonUtils.serialize(identityCertificationInfo));
                    return true;
                }
            }
        }

        //????????
//        String identityCount = this.sysParamService.getSysParamValue(SysParamContants.NUMBER_OF_IDENTITY);//????????
//        this.getCertificationCount(userUuid,CertificationEnum.USER_IDENTITY,Integer.parseInt(identityCount));
        //advance????
//        Boolean flag = this.identityCheckService.identityCheck(userUuid,name,idCardNo);
//        Boolean flag = true;
        //???????
        UsrCertificationInfo usrCertificationInfo = new UsrCertificationInfo();
        usrCertificationInfo.setUserUuid(identityInfoRequest.getUserUuid());
        usrCertificationInfo.setDisabled(0);
        usrCertificationInfo.setCertificationType(CertificationEnum.USER_IDENTITY.getType());
        List<UsrCertificationInfo> usrCertificationInfoList = this.usrCertificationInfoDao.scan(usrCertificationInfo);

        if (usrCertificationInfoList.isEmpty()){
//            if (flag){
            usrCertificationInfo.setCertificationResult(CertificationResultEnum.NOT_AUTH.getType());
//            }else {
//                usrCertificationInfo.setCertificationResult(CertificationResultEnum.AUTH_FAILD.getType());
//            }
            this.usrCertificationInfoDao.insert(usrCertificationInfo);
        }else {
            usrCertificationInfo = usrCertificationInfoList.get(0);
//            if (flag){
            usrCertificationInfo.setCertificationResult(CertificationResultEnum.NOT_AUTH.getType());
//            }else {
//                usrCertificationInfo.setCertificationResult(CertificationResultEnum.AUTH_FAILD.getType());
//            }
            this.usrCertificationInfoDao.update(usrCertificationInfo);
        }
//        if (!flag){
//            //// TODO: 2017/11/30 ?????????
//            log.error("????????????????");
//            throw new ServiceException(ExceptionEnum.USER_INFO_ERROR);
//        }

        //检查实名验证次数：
        String currentDate = DateUtils.formDate(new Date(),DateUtils.FMT_YYYYMMDD);
        String key = RedisContants.REALNAME_VERIFY_PER_DAY_LIMIT+identityInfoRequest.getUserUuid()+currentDate;
        String limit = redisClient.get(key);
        if(StringUtils.isEmpty(limit)){
            redisClient.set(key,"1",60*60*24);
        }else{
            int intLimit = Integer.valueOf(limit);
            if (intLimit > 5) {
                log.info("exceed the limit perDay, userUuid: {}", identityInfoRequest.getUserUuid());
                return true;
            }
        }
        //当前传入的身份证号码和db中一致，且db中已经验证通过，则不再验证
        List<OrdOrder> ordList = orderDao.getLatestOrder(identityInfoRequest.getUserUuid());
        String orderNo = CollectionUtils.isEmpty(ordList) ? "" : ordList.get(0).getUuid();
        //izi realName verification
        // budi: disable izy check nama & ktp
        /*userVerifyResultService.initVerifyResult(orderNo,identityInfoRequest.getUserUuid(), UsrVerifyResult.VerifyTypeEnum.IZI_REAL_NAME);
        IziResponse iziResponse = iziService.getIdentityCheck3(identityInfoRequest.getName(), identityInfoRequest.getIdCardNo(), orderNo,
                identityInfoRequest.getUserUuid());
        boolean match = false;
        if (iziResponse != null && "OK".equalsIgnoreCase(iziResponse.getStatus()) && iziResponse.getMessage() != null) {
            IziResponse.IziIdentityCheckDetail detail = JsonUtils.deserialize(JsonUtils.serialize(iziResponse.getMessage()),
                    IziResponse.IziIdentityCheckDetail.class);
            match = detail.getMatch();
        }
        userVerifyResultService.updateVerifyResult(orderNo, identityInfoRequest.getUserUuid(), iziResponse == null ? null : JsonUtils.serialize(iziResponse),
                match ? UsrVerifyResult.VerifyResultEnum.SUCCESS : UsrVerifyResult.VerifyResultEnum.FAILED, UsrVerifyResult.VerifyTypeEnum.IZI_REAL_NAME);

        if(match){
            usrCertificationService.updateUsrCertificationInfo(identityInfoRequest.getUserUuid(),
                    CertificationResultEnum.AUTH_SUCCESS.getType(), CertificationEnum.USER_IDENTITY.getType(), "IZI");
        }else {
            // jxl realName verification
            userVerifyResultService.initVerifyResult(orderNo,identityInfoRequest.getUserUuid(), UsrVerifyResult.VerifyTypeEnum.KTP);
            JXLBaseResponse jxlBaseResponse = jxlService.identityVerify(identityInfoRequest.getIdCardNo(), identityInfoRequest.getName());
            boolean jxlMatch = false;
            if (jxlBaseResponse != null && jxlBaseResponse.isResponseSuccess() && jxlBaseResponse.getData() != null) {
                IdentityVerifyData data = JsonUtils.deserialize(JsonUtils.serialize(jxlBaseResponse.getData()), IdentityVerifyData.class);
                jxlMatch = "SUCCESS".equalsIgnoreCase(data.getVerifyResult());
                if (jxlMatch) {
                    usrCertificationService.updateUsrCertificationInfo(identityInfoRequest.getUserUuid(),
                            CertificationResultEnum.AUTH_SUCCESS.getType(), CertificationEnum.USER_IDENTITY.getType(), "JXL");
                }
            }
            userVerifyResultService.updateVerifyResult(orderNo, identityInfoRequest.getUserUuid(), jxlBaseResponse == null ? null : JsonUtils.serialize(jxlBaseResponse),
                    jxlMatch ? UsrVerifyResult.VerifyResultEnum.SUCCESS : UsrVerifyResult.VerifyResultEnum.FAILED, UsrVerifyResult.VerifyTypeEnum.KTP);
        }*/
        return true;
    }

    /**
     *   jxl realName verify (ktp)
     * */
    public boolean jxlIdentity(String url , JXLIdentityRequest request) throws ServiceException{

        log.info("ktp request with param: {}",JsonUtils.serialize(request));
        String result = HttpTools.post(url, null, JSONObject.toJSON(request).toString(), 30000, 10000);
        log.info("ktp result with response: {}",result);

        //记录实名验证[init]
        userVerifyResultService.initVerifyResult(request.getOrderNo(),request.getUserUuid(), UsrVerifyResult.VerifyTypeEnum.KTP);

        //result = "{\"code\":\"10000\",\"report_task_token\":\"zxc-test\"}";
        if (StringUtils.isNotBlank(result)){
            JSONObject res = JSONObject.parseObject(result);
            String code = res.get("code").toString();
            if (code.equals("10000")) {
                UserCertificationInfoInRedis cerInfoRedis = new UserCertificationInfoInRedis();
                cerInfoRedis.setUserUuid(request.getUserUuid());
                cerInfoRedis.setOrderNo(request.getOrderNo());
                cerInfoRedis.setCertType("identity");
                cerInfoRedis.setReport_task_token(res.get("report_task_token").toString());
                this.redisClient.listAdd(RedisContants.USER_CERTIFICATION_LIST ,cerInfoRedis);
                return true;
            }else{
                //记录结果
                userVerifyResultService.updateVerifyResult(request.getOrderNo(), request.getUserUuid(), result, UsrVerifyResult.VerifyResultEnum
                         .FAILED, UsrVerifyResult.VerifyTypeEnum.KTP);
            }
        }
        return false;
    }


    /**
     * ??????????
     * @param userUuid             //??userUuid
     * @param certificationEnum   //????
     * @param certificationCount  //???????
     */
    public void getCertificationCount(String userUuid,CertificationEnum certificationEnum,int certificationCount) throws ServiceException{
        StringBuilder stringBuilder = new StringBuilder(RedisContants.CERTIFICATION_COUNT_KEY);
        stringBuilder.append(userUuid).append(certificationEnum);
        String counts = redisClient.get(stringBuilder.toString());
        log.info("Redis???{}?????{}",certificationEnum,counts);
        int count = 0;
        if (counts != null && Integer.parseInt(counts) > certificationCount){
            log.error("??????????");
            throw new ServiceException(ExceptionEnum.CERTIFICATION_COUNT_IS_OVER);
        }
        if (counts == null){
            count++;
        }else {
            count = Integer.parseInt(counts) + 1;
        }
        //Redis??1???
        redisClient.set(stringBuilder.toString(),String.valueOf(count),RedisContants.EXPIRES_COUNT_SECOND);
    }

    /**
     * checkQualityOrNot true is the quality inspection order query (query the deleted image url)
     * @param userUuid
     * @return
     */
    public UsrIdentityModel getIdentityInfo(String userUuid, Boolean checkQualityOrNot) throws Exception{
        UsrIdentityModel usrIdentityModel = new UsrIdentityModel();
        UsrUser usrUser = new UsrUser();
        usrUser.setUuid(userUuid);
        usrUser.setDisabled(0);
//        usrUser.setStatus(1);
        List<UsrUser> userList =  this.usrDao.scan(usrUser);
        if (!userList.isEmpty()){
            usrUser = userList.get(0);
            usrIdentityModel.setRealName(usrUser.getRealName());
            usrIdentityModel.setIdCardNo(usrUser.getIdCardNo());
            usrIdentityModel.setSex(usrUser.getSex());

            boolean idCardFlag = false;
            UsrAttachmentInfo usrAttachmentInfo;
            if (checkQualityOrNot) {
                //If it is an audit quality inspection, first check whether there is deleted data
                usrAttachmentInfo = getDisabledUsrAttachmentInfo(userUuid, UsrAttachmentEnum.ID_CARD.getType());
                if (usrAttachmentInfo != null) {
                    usrIdentityModel.setIdCardUrl(setUsrAttachmentUrl(usrAttachmentInfo));
                    idCardFlag = true;
                }
            }
            if (!idCardFlag) {
                usrAttachmentInfo = new UsrAttachmentInfo();
                usrAttachmentInfo.setUserUuid(userUuid);
                usrAttachmentInfo.setDisabled(0);
                usrAttachmentInfo.setAttachmentType(UsrAttachmentEnum.ID_CARD.getType());
                List<UsrAttachmentInfo> usrAttachmentInfoList = this.usrAttachmentInfoDao.scan(usrAttachmentInfo);
                if (!usrAttachmentInfoList.isEmpty()){
                    usrAttachmentInfo = usrAttachmentInfoList.get(0);
                    usrIdentityModel.setIdCardUrl(setUsrAttachmentUrl(usrAttachmentInfo));
                }
            }
            boolean handIdFlag = false;
            UsrAttachmentInfo usrAttachmentInfo1;
            if (checkQualityOrNot) {
                //If it is an audit quality inspection, first check whether there is deleted data
                usrAttachmentInfo1 = getDisabledUsrAttachmentInfo(userUuid, UsrAttachmentEnum.SELFIE.getType());
                if (usrAttachmentInfo1 != null) {
                    usrIdentityModel.setHandIdCardUrl(setUsrAttachmentUrl(usrAttachmentInfo1));
                    handIdFlag = true;
                } else {
                    //If it is an audit quality inspection, first check whether there is deleted data
                    usrAttachmentInfo1 = getDisabledUsrAttachmentInfo(userUuid, UsrAttachmentEnum.HAND_ID_CARD.getType());
                    if (usrAttachmentInfo1 != null) {
                        usrIdentityModel.setHandIdCardUrl(setUsrAttachmentUrl(usrAttachmentInfo1));
                        handIdFlag = true;
                    }
                }
            }
            if (!handIdFlag) {
                usrAttachmentInfo1 = new UsrAttachmentInfo();
                usrAttachmentInfo1.setUserUuid(userUuid);
                usrAttachmentInfo1.setDisabled(0);
                usrAttachmentInfo1.setAttachmentType(UsrAttachmentEnum.SELFIE.getType());
                List<UsrAttachmentInfo> usrAttachmentInfoList1 = this.usrAttachmentInfoDao.scan(usrAttachmentInfo1);
                if (!usrAttachmentInfoList1.isEmpty()){
                    usrAttachmentInfo1 = usrAttachmentInfoList1.get(0);
                    usrIdentityModel.setHandIdCardUrl(setUsrAttachmentUrl(usrAttachmentInfo1));
                } else {
                    usrAttachmentInfo1.setAttachmentType(UsrAttachmentEnum.HAND_ID_CARD.getType());
                    usrAttachmentInfoList1 = this.usrAttachmentInfoDao.scan(usrAttachmentInfo1);
                    if (!usrAttachmentInfoList1.isEmpty()){
                        usrAttachmentInfo1 = usrAttachmentInfoList1.get(0);
                        usrIdentityModel.setHandIdCardUrl(setUsrAttachmentUrl(usrAttachmentInfo1));
                    }
                }
            }

//            UsrAttachmentInfo usrAttachmentInfo2 = new UsrAttachmentInfo();
//            usrAttachmentInfo2.setUserUuid(userUuid);
//            usrAttachmentInfo2.setDisabled(0);
//            usrAttachmentInfo2.setAttachmentType(UsrAttachmentEnum.INSURANCE_CARD.getType());
//            List<UsrAttachmentInfo> usrAttachmentInfoList2 = this.usrAttachmentInfoDao.scan(usrAttachmentInfo2);
//            if (!usrAttachmentInfoList2.isEmpty()){
//                usrAttachmentInfo2 = usrAttachmentInfoList2.get(0);
//                setUsrAttachmentUrl(usrIdentityModel, usrAttachmentInfo2, UsrAttachmentEnum.HAND_ID_CARD);
//            }
            return usrIdentityModel ;
        }
       return null;
    }

    /**
     * Get full access to pictures via:
     * 1. showStreamOnBrowser (imagePath)
     * 2. usrAttachmentInfo attachmentUrl
     * @param usrAttachmentInfo
     * @return
     */
    public String setUsrAttachmentUrl(UsrAttachmentInfo usrAttachmentInfo) {
        if (!isCashCash(usrAttachmentInfo.getAttachmentUrl())) {
            log.info("Set attachment url via showStreamOnBrowser");
            return this.imagePath + ImageUtil.encryptUrl(usrAttachmentInfo.getAttachmentSavePath()) + "&sessionId="
                    + LoginSysUserInfoHolder.getUsrSessionId();
        } else {
            log.info("Set attachment url via attachmentUrl");
            return usrAttachmentInfo.getAttachmentUrl();
        }
    }


    private boolean isCashCash (String url) {
        if (StringUtils.isNotEmpty(url)) {
            if (url.contains("http://www.do-it.id") || url.contains("http://image.uanguang.id")) {
                return false;
            }
            return true;
        }
        return false;
    }
    private UsrAttachmentInfo getDisabledUsrAttachmentInfo(String userUuid, Integer userAttachmentType) {

        List<UsrAttachmentInfo> usrAttachmentInfoList = this.usrAttachmentInfoDao
                .getDisabledUsrAttachmentInfo(userUuid, userAttachmentType);
        if (!usrAttachmentInfoList.isEmpty()){
            return usrAttachmentInfoList.get(0);
        }
        return null;
    }

    /**
     * ??????????
     * @param orderNo
     * @return
     */
    public void checkOrderStep(String orderNo,int orderStep) throws ServiceException{

        if(!StringUtils.isEmpty(orderNo)){
        OrdOrder orderOrder = new OrdOrder();
        orderOrder.setDisabled(0);
        orderOrder.setStatus(OrdStateEnum.SUBMITTING.getCode());
        orderOrder.setUuid(orderNo);
        List<OrdOrder> orderList = this.orderDao.scan(orderOrder);
        if (orderList.isEmpty()){
            log.error("checkOrderStep - permohonan tidak sedang dalam proses");
            throw new ServiceException(ExceptionEnum.ORDER_IS_NOT_APPLYING);
        }
    }
        else {
            log.error("parameter orderNo is empty");
            throw new ServiceException(ExceptionEnum.ORDER_NOT_FOUND);
        }
    }

    /**
     * ????????
     * @param identityInfoRequest
     * @return
     * @throws ServiceException
     */
    @Transactional
    public UsrUser getAndUpdateUser(UsrIdentityInfoRequest identityInfoRequest) throws ServiceException{
        //????????????
        this.checkOrderStep(identityInfoRequest.getOrderNo(), OrdStepTypeEnum.CHOOSE_ROLE.getType());
        //????????
        UsrUser usrUser = new UsrUser();
        usrUser.setUuid(identityInfoRequest.getUserUuid());
        usrUser.setDisabled(0);
        usrUser.setStatus(1);
        List<UsrUser> userList =  this.usrDao.scan(usrUser);
        if (userList.isEmpty()){
            log.error("?????");
            throw new ServiceException(ExceptionEnum.USER_NOT_FOUND);
        }
        usrUser = userList.get(0);
        usrUser.setRealName(identityInfoRequest.getName());
        usrUser.setIdCardNo(identityInfoRequest.getIdCardNo());
        usrUser.setSex(identityInfoRequest.getSex());
        usrUser.setAge(CardIdUtils.getAgeByIdCard(identityInfoRequest.getIdCardNo()));
        this.usrDao.update(usrUser);

        if (!StringUtils.isEmpty(identityInfoRequest.getIdCardPhoto())){
            //???????????
            AttachmentModel attachmentModel = new AttachmentModel();
            attachmentModel.setUserUuid(identityInfoRequest.getUserUuid());
            //???????(??)
            attachmentModel.setAttachmentType(UsrAttachmentEnum.ID_CARD.getType());
            attachmentModel.setAttachmentSavePath(identityInfoRequest.getIdCardPhoto());
            attachmentModel.setAttachmentUrl(this.path + identityInfoRequest.getIdCardPhoto());
            this.addAttachments(attachmentModel);
        }

        if (!StringUtils.isEmpty(identityInfoRequest.getHandIdCardPhoto())){
            //???????????
            AttachmentModel attachmentModel = new AttachmentModel();
            attachmentModel.setUserUuid(identityInfoRequest.getUserUuid());
            //??????(??)
            attachmentModel.setAttachmentType(UsrAttachmentEnum.HAND_ID_CARD.getType());
            attachmentModel.setAttachmentSavePath(identityInfoRequest.getHandIdCardPhoto());
            attachmentModel.setAttachmentUrl(this.path + identityInfoRequest.getHandIdCardPhoto());
            this.addAttachments(attachmentModel);
        }

        // Test by image comparison
        Long startTime = System.currentTimeMillis();
        UsrAttachmentInfo search = new UsrAttachmentInfo();
        search.setDisabled(0);
        search.setUserUuid(identityInfoRequest.getUserUuid());
        search.setAttachmentType(UsrAttachmentEnum.ID_CARD.getType());
        List<UsrAttachmentInfo> infoList = usrAttachmentInfoDao.scan(search);

        UsrAttachmentInfo search2 = new UsrAttachmentInfo();
        search2.setDisabled(0);
        search2.setUserUuid(identityInfoRequest.getUserUuid());
        search2.setAttachmentType(UsrAttachmentEnum.SELFIE.getType());
        List<UsrAttachmentInfo> infoList2 = usrAttachmentInfoDao.scan(search2);

        if (!CollectionUtils.isEmpty(infoList) && !CollectionUtils.isEmpty(infoList2)) {

            UsrAttachmentInfo idInfo = infoList.get(0);
            String userIdenIconUrl = idInfo.getAttachmentSavePath();
            log.info("User's ID Card url: "+userIdenIconUrl);
            //ahalim: use userAttachmentInfoService instead
            //String userIdenContent = FileHelper.getImageBase64Content(userIdenIconUrl);
            String userIdenContent = userAttachmentInfoService.getBase64AttachmentStream(userIdenIconUrl);
                    
            UsrAttachmentInfo selInfo = infoList2.get(0);
            String selIconUrl = selInfo.getAttachmentSavePath();
            log.info("User's selfie url: "+selIconUrl);
            //ahalim: use userAttachmentInfoService instead
            //String selIconContent = FileHelper.getImageBase64Content(selIconUrl);
            String selIconContent = userAttachmentInfoService.getBase64AttachmentStream(selIconUrl);
                    
            this.yiTuService.verifyFacePackage(userIdenContent,
                    selIconContent, identityInfoRequest.getOrderNo(), identityInfoRequest.getUserUuid(), identityInfoRequest.getSessionId());
            log.info("Duration of image verification: {}",System.currentTimeMillis() - startTime);
        }

//      更新订单步骤
        this.updateOrderStep(identityInfoRequest.getOrderNo(),identityInfoRequest.getUserUuid(), OrdStepTypeEnum.IDENTITY.getType());

        return usrUser;
    }

    /**
     *    ?????? ????????
     * */
    public void saveUserPhoto(SaveUserPhotoRequest saveUserPhotoRequest){

        //???????????
        AttachmentModel attachmentModel = new AttachmentModel();
        attachmentModel.setUserUuid(saveUserPhotoRequest.getUserUuid());
        //???????(??)
        if (saveUserPhotoRequest.getPhotoType().equals("0")){
            attachmentModel.setAttachmentType(UsrAttachmentEnum.ID_CARD.getType());
        }else if(saveUserPhotoRequest.getPhotoType().equals("1")){
            attachmentModel.setAttachmentType(UsrAttachmentEnum.HAND_ID_CARD.getType());
        }else if(saveUserPhotoRequest.getPhotoType().equals("2")){
            attachmentModel.setAttachmentType(UsrAttachmentEnum.SELFIE.getType());
        }
        attachmentModel.setAttachmentSavePath(saveUserPhotoRequest.getPhotoUrl());
        attachmentModel.setAttachmentUrl(this.path + saveUserPhotoRequest.getPhotoUrl());
        this.addAttachments(attachmentModel);
    }


    /**
     * ?????????????
     * @param orderNo
     * @param userUuid
     * @param step
     * @throws ServiceException
     */
    @Transactional
    public void updateOrderStep(String orderNo,String userUuid,Integer step) throws ServiceException{
        OrdOrder orderOrder = new OrdOrder();
        orderOrder.setUuid(orderNo);
        orderOrder.setUserUuid(userUuid);
        orderOrder.setDisabled(0);
        List<OrdOrder> orderOrderList = this.orderDao.scan(orderOrder);
            if (!orderOrderList.isEmpty() ){
                if (orderOrderList.get(0).getOrderStep() < step){
                    orderOrder = orderOrderList.get(0);
                    orderOrder.setOrderStep(step);
                    this.orderDao.update(orderOrder);//?????
                    OrdStep ordStep = new OrdStep();
                    ordStep.setUserUuid(userUuid);
                    ordStep.setStep(step);
                    ordStep.setProductUuid(orderOrder.getProductUuid());
                    ordStep.setOrderId(orderOrder.getUuid());
                    ordStep.setStepChangeTime(new Date());
                    this.ordStepDao.insert(ordStep);//???????
                }
            }else {
                log.error("?????");
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new ServiceException(ExceptionEnum.ORDER_NOT_FOUND);
            }
    }

    /**
     * ?????????????
     * @param orderNo
     * @param userUuid
     * @param status
     * @throws ServiceException
     */
    @Transactional
    public void updateOrderState(String orderNo,String userUuid,Integer status,UsrBank userBank) throws ServiceException, ServiceExceptionSpec {
        OrdOrder orderOrder = new OrdOrder();
        orderOrder.setUserUuid(userUuid);
        orderOrder.setUuid(orderNo);
        orderOrder.setDisabled(0);
        // ??????useruuid?????list
        List<OrdOrder> orderOrderList = this.orderDao.scan(orderOrder);
        if (!orderOrderList.isEmpty()){
            if(orderOrderList.get(0).getStatus() < status){
                orderOrder = orderOrderList.get(0);
                orderOrder.setStatus(status);// ??????
                orderOrder.setUserUuid(userUuid);// ????useruuid
                orderOrder.setUserBankUuid(userBank.getUuid());// ????user???id
                orderOrder.setApplyTime(new Date());// ???????????????????????
                this.orderDao.update(orderOrder); // ?????
                OrdHistory ordHistory = new OrdHistory();
                ordHistory.setUserUuid(orderOrder.getUserUuid());
                ordHistory.setStatus(status);
                ordHistory.setProductUuid(orderOrder.getProductUuid());
                ordHistory.setOrderId(orderNo);
                ordHistory.setStatusChangeTime(new Date());
                ordHistoryDao.insert(ordHistory);// ?????????
                /**
                 *    20190313 修改发放奖励节点  必须是订单实名通过后
                 * */
//                //判断是否为需要发放佣金的订单
//                if (status == OrdStateEnum.MACHINE_CHECKING.getCode()){
//                    this.inviteService.checkInvitedOrder(orderOrder.getUuid());
//                }
            }
        }else {
            log.error("?????");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ServiceException(ExceptionEnum.ORDER_NOT_FOUND);
        }
    }

    public HouseWifeInfoResponse getHouseWifeDetail (String userUuid) {
        UsrHouseWifeDetail usrHouseWifeDetail = new UsrHouseWifeDetail();
        usrHouseWifeDetail.setDisabled(0);
        usrHouseWifeDetail.setUserUuid(userUuid);
        List<UsrHouseWifeDetail> response = this.usrHouseWifeDetailDao.scan(usrHouseWifeDetail);
        if (CollectionUtils.isEmpty(response)) {
            return new HouseWifeInfoResponse();
        }
        HouseWifeInfoResponse houseWifeInfoResponse = new HouseWifeInfoResponse();
        org.springframework.beans.BeanUtils.copyProperties(response.get(0), houseWifeInfoResponse);
        //封装工作地址
        UsrAddressDetail usrAddressDetail = new UsrAddressDetail();
        usrAddressDetail.setUserUuid(userUuid);
        usrAddressDetail.setDisabled(0);
        usrAddressDetail.setAddressType(UsrAddressEnum.FAMILY.getType());
        List<UsrAddressDetail> usrAddressDetailList = this.usrAddressDetailDao.scan(usrAddressDetail);
        if (!usrAddressDetailList.isEmpty()){
            usrAddressDetail = usrAddressDetailList.get(0);
            String address = usrAddressDetail.getProvince() + " " + usrAddressDetail.getCity() + " " +
                    usrAddressDetail.getBigDirect() + " " + usrAddressDetail.getSmallDirect() + " " +
                    usrAddressDetail.getDetailed();
            houseWifeInfoResponse.setWorkAddressDetail(address);
        }

        UsrAddressDetail usrAddressDetail1 = new UsrAddressDetail();
        usrAddressDetail1.setUserUuid(userUuid);
        usrAddressDetail1.setDisabled(0);
        usrAddressDetail1.setAddressType(UsrAddressEnum.HOME.getType());
        List<UsrAddressDetail> usrAddressDetailList1 = this.usrAddressDetailDao.scan(usrAddressDetail1);
        if (!usrAddressDetailList1.isEmpty()){
            usrAddressDetail1 = usrAddressDetailList1.get(0);
            houseWifeInfoResponse.setProvince(usrAddressDetail1.getProvince());
            houseWifeInfoResponse.setCity(usrAddressDetail1.getCity());
            houseWifeInfoResponse.setBigDirect(usrAddressDetail1.getBigDirect());
            houseWifeInfoResponse.setSmallDirect(usrAddressDetail1.getSmallDirect());
            houseWifeInfoResponse.setDetailed(usrAddressDetail1.getDetailed());
        }
        // ?????????
        UsrAddressDetail usrBirthAddressDetail = new UsrAddressDetail();
        usrBirthAddressDetail.setUserUuid(userUuid);
        usrBirthAddressDetail.setDisabled(0);
        usrBirthAddressDetail.setAddressType(UsrAddressEnum.BIRTH.getType());
        List<UsrAddressDetail> usrBirthAddressDetailList = this.usrAddressDetailDao.scan(usrBirthAddressDetail);
        if (!usrBirthAddressDetailList.isEmpty()){
            usrBirthAddressDetail = usrBirthAddressDetailList.get(0);
            houseWifeInfoResponse.setBirthProvince(usrBirthAddressDetail.getProvince());
            houseWifeInfoResponse.setBirthCity(usrBirthAddressDetail.getCity());
            houseWifeInfoResponse.setBirthBigDirect(usrBirthAddressDetail.getBigDirect());
            houseWifeInfoResponse.setBirthSmallDirect(usrBirthAddressDetail.getSmallDirect());
        }


        UsrCertificationInfo certificationInfo2 = new UsrCertificationInfo();
        certificationInfo2.setUserUuid(userUuid);
        certificationInfo2.setCertificationType(CertificationEnum.WHATS_APP.getType());
        List<UsrCertificationInfo> scanList2 = this.usrCertificationDao.scan(certificationInfo2);
        if (!CollectionUtils.isEmpty(scanList2)) {
            UsrCertificationInfo update = scanList2.get(0);
            if (!StringUtils.isEmpty(update.getCertificationData())){
                if (JsonUtils.isJSONValid(update.getCertificationData())){
                    JSONObject jsonObj = JSONObject.parseObject(update.getCertificationData());
                    if (jsonObj != null) {
                        houseWifeInfoResponse.setWhatsappAccount(jsonObj.getString("account"));
                    }
                }
            }
        }

        AttachmentModel attachmentModel = new AttachmentModel();
        attachmentModel.setUserUuid(userUuid);
        attachmentModel.setAttachmentType(UsrAttachmentEnum.INSURANCE_CARD.getType());
        List<UsrAttachmentInfo> usrAttachmentInfoList = this.getAttachment(attachmentModel);
        if (!usrAttachmentInfoList.isEmpty()){
            //houseWifeInfoResponse.setInsuranceCardPhoto(usrAttachmentInfoList.get(0).getAttachmentUrl());
            //ahalim: get url from local/oss if available
            houseWifeInfoResponse.setInsuranceCardPhoto(setUsrAttachmentUrl(usrAttachmentInfoList.get(0)));
        }
        return houseWifeInfoResponse;
    }
    /**
     * ?????? ????
     * @param userUuid
     * @return
     */
    public UsrWorkBaseInfoModel getWorkBaseInfo(String userUuid){
        UsrWorkBaseInfoModel usrWorkBaseInfoModel = new UsrWorkBaseInfoModel();
        //??????
        UsrWorkDetail usrWorkDetail = new UsrWorkDetail();
        usrWorkDetail.setUserUuid(userUuid);
        usrWorkDetail.setDisabled(0);
        List<UsrWorkDetail> usrWorkDetailList = this.usrWorkDetailDao.scan(usrWorkDetail);
        if (!usrWorkDetailList.isEmpty()){
            usrWorkDetail = usrWorkDetailList.get(0);
            usrWorkBaseInfoModel.setUserUuid(userUuid);
            usrWorkBaseInfoModel.setEmail(usrWorkDetail.getEmail());
            usrWorkBaseInfoModel.setAcademic(usrWorkDetail.getAcademic());
            usrWorkBaseInfoModel.setMaritalStatus(usrWorkDetail.getMaritalStatus());
            usrWorkBaseInfoModel.setBirthday(usrWorkDetail.getBirthday());
            usrWorkBaseInfoModel.setReligion(usrWorkDetail.getReligion());
            usrWorkBaseInfoModel.setMotherName(usrWorkDetail.getMotherName());
            usrWorkBaseInfoModel.setChildrenAmount(usrWorkDetail.getChildrenAmount());
            if(StringUtils.isNotEmpty(usrWorkDetail.getBorrowUse())){
                usrWorkBaseInfoModel.setBorrowUse(usrWorkDetail.getBorrowUse());
            }
        }
        //????????
        UsrAddressDetail usrAddressDetail = new UsrAddressDetail();
        usrAddressDetail.setUserUuid(userUuid);
        usrAddressDetail.setDisabled(0);
        usrAddressDetail.setAddressType(UsrAddressEnum.HOME.getType());
        List<UsrAddressDetail> usrAddressDetailList = this.usrAddressDetailDao.scan(usrAddressDetail);
        if (!usrAddressDetailList.isEmpty()){
            usrAddressDetail = usrAddressDetailList.get(0);
            usrWorkBaseInfoModel.setProvince(usrAddressDetail.getProvince());
            usrWorkBaseInfoModel.setCity(usrAddressDetail.getCity());
            usrWorkBaseInfoModel.setBigDirect(usrAddressDetail.getBigDirect());
            usrWorkBaseInfoModel.setSmallDirect(usrAddressDetail.getSmallDirect());
            usrWorkBaseInfoModel.setDetailed(usrAddressDetail.getDetailed());
        }
        // ?????????
        UsrAddressDetail usrBirthAddressDetail = new UsrAddressDetail();
        usrBirthAddressDetail.setUserUuid(userUuid);
        usrBirthAddressDetail.setDisabled(0);
        usrBirthAddressDetail.setAddressType(UsrAddressEnum.BIRTH.getType());
        List<UsrAddressDetail> usrBirthAddressDetailList = this.usrAddressDetailDao.scan(usrBirthAddressDetail);
        if (!usrBirthAddressDetailList.isEmpty()){
            usrBirthAddressDetail = usrBirthAddressDetailList.get(0);
            usrWorkBaseInfoModel.setBirthProvince(usrBirthAddressDetail.getProvince());
            usrWorkBaseInfoModel.setBirthCity(usrBirthAddressDetail.getCity());
            usrWorkBaseInfoModel.setBirthBigDirect(usrBirthAddressDetail.getBigDirect());
            usrWorkBaseInfoModel.setBirthSmallDirect(usrBirthAddressDetail.getSmallDirect());
        }

        UsrCertificationInfo certificationInfo = new UsrCertificationInfo();
        certificationInfo.setUserUuid(userUuid);
        certificationInfo.setCertificationType(CertificationEnum.STEUERKARTED.getType());
        List<UsrCertificationInfo> scanList = this.usrCertificationDao.scan(certificationInfo);
        if (!CollectionUtils.isEmpty(scanList)) {
            UsrCertificationInfo update = scanList.get(0);
           if (!StringUtils.isEmpty(update.getCertificationData())){
               if (JsonUtils.isJSONValid(update.getCertificationData())){
                   JSONObject jsonObj = JSONObject.parseObject(update.getCertificationData());
                   if (jsonObj != null) {
                       usrWorkBaseInfoModel.setNpwp(jsonObj.getString("account"));
                   }
               }
           }
        }

        UsrCertificationInfo certificationInfo2 = new UsrCertificationInfo();
        certificationInfo2.setUserUuid(userUuid);
        certificationInfo2.setCertificationType(CertificationEnum.WHATS_APP.getType());
        List<UsrCertificationInfo> scanList2 = this.usrCertificationDao.scan(certificationInfo2);
        if (!CollectionUtils.isEmpty(scanList2)) {
            UsrCertificationInfo update = scanList2.get(0);
            if (!StringUtils.isEmpty(update.getCertificationData())){
                if (JsonUtils.isJSONValid(update.getCertificationData())){
                    JSONObject jsonObj = JSONObject.parseObject(update.getCertificationData());
                    if (jsonObj != null) {
                        usrWorkBaseInfoModel.setWhatsappAccount(jsonObj.getString("account"));
                    }
                }
            }
        }

        AttachmentModel attachmentModel = new AttachmentModel();
        attachmentModel.setUserUuid(userUuid);
        attachmentModel.setAttachmentType(UsrAttachmentEnum.INSURANCE_CARD.getType());
        List<UsrAttachmentInfo> usrAttachmentInfoList = this.getAttachment(attachmentModel);
        if (!usrAttachmentInfoList.isEmpty()){
            //usrWorkBaseInfoModel.setInsuranceCardPhoto(usrAttachmentInfoList.get(0).getAttachmentUrl());
            //ahalim: get url from local/oss if available
            usrWorkBaseInfoModel.setInsuranceCardPhoto(setUsrAttachmentUrl(usrAttachmentInfoList.get(0)));
        
        }


        attachmentModel.setAttachmentType(UsrAttachmentEnum.KK.getType());
        List<UsrAttachmentInfo> usrAttachmentInfoList2 = this.getAttachment(attachmentModel);
        if (!usrAttachmentInfoList2.isEmpty()){
            //usrWorkBaseInfoModel.setKkCardPhoto(usrAttachmentInfoList2.get(0).getAttachmentUrl());
            //ahalim: get url from local/oss if available
            usrWorkBaseInfoModel.setKkCardPhoto(setUsrAttachmentUrl(usrAttachmentInfoList2.get(0)));
        }


        if (usrWorkDetailList.isEmpty() && usrAddressDetailList.isEmpty() && usrBirthAddressDetailList.isEmpty()){
            return null;
        }
        return usrWorkBaseInfoModel;
    }

    /**
     * 添加或更新 工作人员 基本信息
     * @param workBaseInfoRequest
     * @return
     */
    @Transactional
    public void addWorkBaseInfo(UsrWorkBaseInfoRequest workBaseInfoRequest) throws Exception{
        //首先判断订单步骤是否正确
        this.checkOrderStep(workBaseInfoRequest.getOrderNo(), OrdStepTypeEnum.IDENTITY.getType());

        //添加或修改基本信息
        UsrWorkDetail usrWorkDetail = new UsrWorkDetail();
        usrWorkDetail.setUserUuid(workBaseInfoRequest.getUserUuid());
        usrWorkDetail.setDisabled(0);
        List<UsrWorkDetail> usrWorkDetailList = this.usrWorkDetailDao.scan(usrWorkDetail);
        if (usrWorkDetailList.isEmpty()){
//            usrWorkDetail.setEmail(workBaseInfoRequest.getEmail());
//            usrWorkDetail.setAcademic(workBaseInfoRequest.getAcademic());
//            usrWorkDetail.setMaritalStatus(workBaseInfoRequest.getMaritalStatus());
//            usrWorkDetail.setBirthday(workBaseInfoRequest.getBirthday());
//            usrWorkDetail.setReligion(workBaseInfoRequest.getReligion());
//            usrWorkDetail.setMotherName(workBaseInfoRequest.getMotherName());
//            usrWorkDetail.setChildrenAmount(workBaseInfoRequest.getChildrenAmount());
//            if(StringUtils.isNotEmpty(workBaseInfoRequest.getBorrowUse())){
//                usrWorkDetail.setBorrowUse(workBaseInfoRequest.getBorrowUse());
//            }
//            this.usrWorkDetailDao.insert(usrWorkDetail);

            BeanUtils.copyProperties(usrWorkDetail,workBaseInfoRequest);
            this.usrWorkDetailDao.insert(usrWorkDetail);
        }else{
            usrWorkDetail = usrWorkDetailList.get(0);
//            usrWorkDetail.setEmail(workBaseInfoRequest.getEmail());
//            usrWorkDetail.setAcademic(workBaseInfoRequest.getAcademic());
//            usrWorkDetail.setMaritalStatus(workBaseInfoRequest.getMaritalStatus());
//            usrWorkDetail.setBirthday(workBaseInfoRequest.getBirthday());
//            usrWorkDetail.setReligion(workBaseInfoRequest.getReligion());
//            usrWorkDetail.setMotherName(workBaseInfoRequest.getMotherName());
//            usrWorkDetail.setChildrenAmount(workBaseInfoRequest.getChildrenAmount());
//            if(StringUtils.isNotEmpty(workBaseInfoRequest.getBorrowUse())){
//                usrWorkDetail.setBorrowUse(workBaseInfoRequest.getBorrowUse());
//            }
            BeanUtils.copyProperties(usrWorkDetail,workBaseInfoRequest);
            this.usrWorkDetailDao.update(usrWorkDetail);
        }

        updateEmailUsrUser(workBaseInfoRequest.getUserUuid(), workBaseInfoRequest.getEmail());

        //添加或修改居住地址信息
        if(!StringUtils.isEmpty(workBaseInfoRequest.getProvince())){
            AddressModel addressModel = new AddressModel();
            addressModel.setUserUuid(workBaseInfoRequest.getUserUuid());
            addressModel.setAddressType(UsrAddressEnum.HOME.getType());
            addressModel.setProvince(workBaseInfoRequest.getProvince());
            addressModel.setCity(workBaseInfoRequest.getCity());
            addressModel.setBigDirect(workBaseInfoRequest.getBigDirect());
            addressModel.setSmallDirect(workBaseInfoRequest.getSmallDirect());
            addressModel.setDetailed(workBaseInfoRequest.getDetailed());
            addressModel.setLbsX(workBaseInfoRequest.getLbsX());
            addressModel.setLbsY(workBaseInfoRequest.getLbsY());
            this.addOrUpdateAddress(addressModel);
        }

        //添加或修改出生地址信息
        if(!StringUtils.isEmpty(workBaseInfoRequest.getBirthProvince())){
            AddressModel addressModel = new AddressModel();
            addressModel.setUserUuid(workBaseInfoRequest.getUserUuid());
            addressModel.setAddressType(UsrAddressEnum.BIRTH.getType());
            addressModel.setProvince(workBaseInfoRequest.getBirthProvince());
            addressModel.setCity(workBaseInfoRequest.getBirthCity());
            addressModel.setBigDirect(workBaseInfoRequest.getBirthBigDirect());
            addressModel.setSmallDirect(workBaseInfoRequest.getBirthSmallDirect());
            this.addOrUpdateAddress(addressModel);
        }

        if (!StringUtils.isEmpty(workBaseInfoRequest.getNpwp())){
            UsrCertificationInfo certificationInfo = new UsrCertificationInfo();
            certificationInfo.setUserUuid(workBaseInfoRequest.getUserUuid());
            certificationInfo.setCertificationType(CertificationEnum.STEUERKARTED.getType());
            List<UsrCertificationInfo> scanList = this.usrCertificationDao.scan(certificationInfo);

            Map<String, String> data = new HashMap<>();
            data.put("account", workBaseInfoRequest.getNpwp());
            String npwpJson =  JsonUtils.serialize(data);

            if (CollectionUtils.isEmpty(scanList)) {
                certificationInfo.setCertificationData(npwpJson);
                certificationInfo.setCertificationResult(CertificationResultEnum.NOT_AUTH.getType());
                this.usrCertificationDao.insert(certificationInfo);

            } else {
                UsrCertificationInfo update = scanList.get(0);
                update.setCertificationData(npwpJson);
                update.setUpdateTime(null);
//                update.setCertificationResult(CertificationResultEnum.NOT_AUTH.getType());
                this.usrCertificationDao.update(update);
            }
        }

        if (!StringUtils.isEmpty(workBaseInfoRequest.getInsuranceCardPhoto())){
            //???????????
            AttachmentModel attachmentModel = new AttachmentModel();
            attachmentModel.setUserUuid(workBaseInfoRequest.getUserUuid());
            //??????(??)
            attachmentModel.setAttachmentType(UsrAttachmentEnum.INSURANCE_CARD.getType());
            attachmentModel.setAttachmentSavePath(workBaseInfoRequest.getInsuranceCardPhoto());
            attachmentModel.setAttachmentUrl(this.path + workBaseInfoRequest.getInsuranceCardPhoto());
            this.addAttachments(attachmentModel);
        }

        // 添加家庭卡
        if (!StringUtils.isEmpty(workBaseInfoRequest.getKkCardPhoto())){

            AttachmentModel attachmentModel = new AttachmentModel();
            attachmentModel.setUserUuid(workBaseInfoRequest.getUserUuid());
            attachmentModel.setAttachmentType(UsrAttachmentEnum.KK.getType());
            attachmentModel.setAttachmentSavePath(workBaseInfoRequest.getKkCardPhoto());
            attachmentModel.setAttachmentUrl(this.path + workBaseInfoRequest.getKkCardPhoto());
            this.addAttachments(attachmentModel);
        }

        if (!StringUtils.isEmpty(workBaseInfoRequest.getWhatsappAccount())){

            UsrCertificationInfo certificationInfo = new UsrCertificationInfo();
            certificationInfo.setUserUuid(workBaseInfoRequest.getUserUuid());
            certificationInfo.setCertificationType(CertificationEnum.WHATS_APP.getType());
            List<UsrCertificationInfo> scanList = this.usrCertificationDao.scan(certificationInfo);

            Map<String, String> data = new HashMap<>();
            data.put("account", workBaseInfoRequest.getWhatsappAccount());
            String wpJson =  JsonUtils.serialize(data);

            if (CollectionUtils.isEmpty(scanList)) {
                certificationInfo.setCertificationData(wpJson);
                certificationInfo.setCertificationResult(CertificationResultEnum.NOT_AUTH.getType());
                this.usrCertificationDao.insert(certificationInfo);

            } else {
                UsrCertificationInfo update = scanList.get(0);
                update.setCertificationData(wpJson);
//                update.setCertificationResult(CertificationResultEnum.NOT_AUTH.getType());
                this.usrCertificationDao.update(update);
            }
        }

        //更新订单步骤到基本信息
        this.updateOrderStep(workBaseInfoRequest.getOrderNo(),workBaseInfoRequest.getUserUuid(), OrdStepTypeEnum.BASIC_INFO.getType());
    }


    /**
     * ?????? ????
     * @param userUuid
     * @return
     */
    public UsrWorkInfoModel getUsrWorkInfo(String userUuid) throws Exception{
        UsrWorkInfoModel usrWorkInfoModel = new UsrWorkInfoModel();
        //??????
        UsrWorkDetail usrWorkDetail = new UsrWorkDetail();
        usrWorkDetail.setUserUuid(userUuid);
        usrWorkDetail.setDisabled(0);
        List<UsrWorkDetail> usrWorkDetailList = this.usrWorkDetailDao.scan(usrWorkDetail);
        if (!usrWorkDetailList.isEmpty()){
            usrWorkDetail = usrWorkDetailList.get(0);
            usrWorkInfoModel.setCompanyName(usrWorkDetail.getCompanyName());
            usrWorkInfoModel.setPositionName(usrWorkDetail.getPositionName());
            usrWorkInfoModel.setMonthlyIncome(usrWorkDetail.getMonthlyIncome());
            usrWorkInfoModel.setCompanyPhone(usrWorkDetail.getCompanyPhone());
            usrWorkInfoModel.setDependentBusiness(usrWorkDetail.getDependentBusiness());
            usrWorkInfoModel.setEmployeeNumber(usrWorkDetail.getEmployeeNumber());
            usrWorkInfoModel.setExtensionNumber(usrWorkDetail.getExtensionNumber());
        }
        //??????
        AddressModel addressModel = new AddressModel();
        addressModel.setUserUuid(userUuid);
        addressModel.setAddressType(UsrAddressEnum.COMPANY.getType());
        List<UsrAddressDetail> list = this.getAddressByUserUuidAndType(addressModel);
        if (!list.isEmpty()){
            UsrAddressDetail usrAddressDetail = list.get(0);
            //TODO ?????????????
            //???? ??????
            BeanUtils.copyProperties(usrWorkInfoModel,usrAddressDetail);
//            usrWorkInfoModel.setProvince(usrAddressDetail.getProvince());
//            usrWorkInfoModel.setCity(usrAddressDetail.getCity());
//            usrWorkInfoModel.setBigDirect(usrAddressDetail.getBigDirect());
//            usrWorkInfoModel.setSmallDirect(usrAddressDetail.getSmallDirect());
//            usrWorkInfoModel.setDetailed(usrAddressDetail.getDetailed());
        }
        if (usrWorkDetailList.isEmpty() && list.isEmpty()){
            return null;
        }
        return usrWorkInfoModel;
    }

    private void updateEmailUsrUser(String userUuid, String email) throws Exception{
        //check email already exist
        String emailDES = DESUtils.encrypt(email.toLowerCase());
        UsrUser user = new UsrUser();
        user.setDisabled(0);
        user.setEmailAddress(emailDES);
        List<UsrUser> userSameEmails = usrDao.scan(user);
        List<UsrUser> filteredSameEmails = userSameEmails.stream()
            .filter(a->!a.getUuid().equals(userUuid)).collect(Collectors.toList());

        if(!CollectionUtils.isEmpty(filteredSameEmails)){
            throw new ServiceException(ExceptionEnum.INVALID_EMAIL_DUPLICATE);
        }

        //janhsen: update column email address
        user = new UsrUser();
        user.setDisabled(0);
        user.setUuid(userUuid);
        List<UsrUser> users = this.usrDao.scan(user);
        if(!CollectionUtils.isEmpty(users)){
            users.get(0).setEmailAddress(emailDES);
            this.usrDao.update(users.get(0));
        }
    }

    /**
     * ????? ???? ????
     * @param usrWorkInfoRequest
     */
    @Transactional
    public void addUsrWorkInfo(UsrWorkInfoRequest usrWorkInfoRequest) throws ServiceException{
        //????????????
        this.checkOrderStep(usrWorkInfoRequest.getOrderNo(), OrdStepTypeEnum.BASIC_INFO.getType());
        //?????????
        UsrWorkDetail usrWorkDetail = new UsrWorkDetail();
        usrWorkDetail.setUserUuid(usrWorkInfoRequest.getUserUuid());
        usrWorkDetail.setDisabled(0);
        List<UsrWorkDetail> usrWorkDetailList = this.usrWorkDetailDao.scan(usrWorkDetail);
        if (usrWorkDetailList.isEmpty()){
            usrWorkDetail.setCompanyName(usrWorkInfoRequest.getCompanyName());
            usrWorkDetail.setPositionName(usrWorkInfoRequest.getPositionName());
            usrWorkDetail.setMonthlyIncome(usrWorkInfoRequest.getMonthlyIncome());
            usrWorkDetail.setCompanyPhone(usrWorkInfoRequest.getCompanyPhone());
            usrWorkDetail.setDependentBusiness(usrWorkInfoRequest.getDependentBusiness());
            usrWorkDetail.setEmployeeNumber(usrWorkInfoRequest.getEmployeeNumber());
            usrWorkDetail.setExtensionNumber(usrWorkInfoRequest.getExtensionNumber());
            this.usrWorkDetailDao.insert(usrWorkDetail);
        }else {
            usrWorkDetail = usrWorkDetailList.get(0);
            usrWorkDetail.setCompanyName(usrWorkInfoRequest.getCompanyName());
            usrWorkDetail.setPositionName(usrWorkInfoRequest.getPositionName());
            usrWorkDetail.setMonthlyIncome(usrWorkInfoRequest.getMonthlyIncome());
            usrWorkDetail.setCompanyPhone(usrWorkInfoRequest.getCompanyPhone());
            usrWorkDetail.setDependentBusiness(usrWorkInfoRequest.getDependentBusiness());
            usrWorkDetail.setEmployeeNumber(usrWorkInfoRequest.getEmployeeNumber());
            usrWorkDetail.setExtensionNumber(usrWorkInfoRequest.getExtensionNumber());
            this.usrWorkDetailDao.update(usrWorkDetail);
        }
        //?????????
        AddressModel addressModel = new AddressModel();
        addressModel.setUserUuid(usrWorkInfoRequest.getUserUuid());
        addressModel.setAddressType(UsrAddressEnum.COMPANY.getType());
        addressModel.setProvince(usrWorkInfoRequest.getProvince());
        addressModel.setCity(usrWorkInfoRequest.getCity());
        addressModel.setBigDirect(usrWorkInfoRequest.getBigDirect());
        addressModel.setSmallDirect(usrWorkInfoRequest.getSmallDirect());
        addressModel.setDetailed(usrWorkInfoRequest.getDetailed());
        addressModel.setLbsX(usrWorkInfoRequest.getLbsX());
        addressModel.setLbsY(usrWorkInfoRequest.getLbsY());
        this.addOrUpdateAddress(addressModel);

        //???????????
        this.updateOrderStep(usrWorkInfoRequest.getOrderNo(), usrWorkInfoRequest.getUserUuid(), OrdStepTypeEnum.WORK_INFO.getType());
    }


    /**
     * 反显家庭主妇基本信息
     * @param userUuid
     * @return
     */
    public UsrWorkBaseInfoModel getHousewifekBaseInfo(String userUuid){
        UsrWorkBaseInfoModel usrWorkBaseInfoModel = new UsrWorkBaseInfoModel();
        //??????
        UsrHouseWifeDetail usrHouseWifeDetail = new UsrHouseWifeDetail();
        usrHouseWifeDetail.setUserUuid(userUuid);
        usrHouseWifeDetail.setDisabled(0);
        List<UsrHouseWifeDetail> usrWorkDetailList = this.usrHouseWifeDetailDao.scan(usrHouseWifeDetail);
        if (!usrWorkDetailList.isEmpty()){
            usrHouseWifeDetail = usrWorkDetailList.get(0);
            usrWorkBaseInfoModel.setUserUuid(userUuid);
            usrWorkBaseInfoModel.setEmail(usrHouseWifeDetail.getEmail());
            usrWorkBaseInfoModel.setAcademic(usrHouseWifeDetail.getAcademic());
            usrWorkBaseInfoModel.setMaritalStatus(usrHouseWifeDetail.getMaritalStatus());
            usrWorkBaseInfoModel.setBirthday(usrHouseWifeDetail.getBirthday());
            usrWorkBaseInfoModel.setReligion(usrHouseWifeDetail.getReligion());
            usrWorkBaseInfoModel.setMotherName(usrHouseWifeDetail.getMotherName());
            usrWorkBaseInfoModel.setChildrenAmount(usrHouseWifeDetail.getChildrenAmount());
            if(StringUtils.isNotEmpty(usrHouseWifeDetail.getBorrowUse())){
                usrWorkBaseInfoModel.setBorrowUse(usrHouseWifeDetail.getBorrowUse());
            }
        }
        //????????
        UsrAddressDetail usrAddressDetail = new UsrAddressDetail();
        usrAddressDetail.setUserUuid(userUuid);
        usrAddressDetail.setDisabled(0);
        usrAddressDetail.setAddressType(UsrAddressEnum.HOME.getType());
        List<UsrAddressDetail> usrAddressDetailList = this.usrAddressDetailDao.scan(usrAddressDetail);
        if (!usrAddressDetailList.isEmpty()){
            usrAddressDetail = usrAddressDetailList.get(0);
            usrWorkBaseInfoModel.setProvince(usrAddressDetail.getProvince());
            usrWorkBaseInfoModel.setCity(usrAddressDetail.getCity());
            usrWorkBaseInfoModel.setBigDirect(usrAddressDetail.getBigDirect());
            usrWorkBaseInfoModel.setSmallDirect(usrAddressDetail.getSmallDirect());
            usrWorkBaseInfoModel.setDetailed(usrAddressDetail.getDetailed());
        }
        // ?????????
        UsrAddressDetail usrBirthAddressDetail = new UsrAddressDetail();
        usrBirthAddressDetail.setUserUuid(userUuid);
        usrBirthAddressDetail.setDisabled(0);
        usrBirthAddressDetail.setAddressType(UsrAddressEnum.BIRTH.getType());
        List<UsrAddressDetail> usrBirthAddressDetailList = this.usrAddressDetailDao.scan(usrBirthAddressDetail);
        if (!usrBirthAddressDetailList.isEmpty()){
            usrBirthAddressDetail = usrBirthAddressDetailList.get(0);
            usrWorkBaseInfoModel.setBirthProvince(usrBirthAddressDetail.getProvince());
            usrWorkBaseInfoModel.setBirthCity(usrBirthAddressDetail.getCity());
            usrWorkBaseInfoModel.setBirthBigDirect(usrBirthAddressDetail.getBigDirect());
            usrWorkBaseInfoModel.setBirthSmallDirect(usrBirthAddressDetail.getSmallDirect());
        }

        UsrCertificationInfo certificationInfo = new UsrCertificationInfo();
        certificationInfo.setUserUuid(userUuid);
        certificationInfo.setCertificationType(CertificationEnum.STEUERKARTED.getType());
        List<UsrCertificationInfo> scanList = this.usrCertificationDao.scan(certificationInfo);
        if (!CollectionUtils.isEmpty(scanList)) {
            UsrCertificationInfo update = scanList.get(0);
            if (!StringUtils.isEmpty(update.getCertificationData())){
                if (JsonUtils.isJSONValid(update.getCertificationData())){
                    JSONObject jsonObj = JSONObject.parseObject(update.getCertificationData());
                    if (jsonObj != null) {
                        usrWorkBaseInfoModel.setNpwp(jsonObj.getString("account"));
                    }
                }
            }
        }

        UsrCertificationInfo certificationInfo2 = new UsrCertificationInfo();
        certificationInfo2.setUserUuid(userUuid);
        certificationInfo2.setCertificationType(CertificationEnum.WHATS_APP.getType());
        List<UsrCertificationInfo> scanList2 = this.usrCertificationDao.scan(certificationInfo2);
        if (!CollectionUtils.isEmpty(scanList2)) {
            UsrCertificationInfo update = scanList2.get(0);
            if (!StringUtils.isEmpty(update.getCertificationData())){
                if (JsonUtils.isJSONValid(update.getCertificationData())){
                    JSONObject jsonObj = JSONObject.parseObject(update.getCertificationData());
                    if (jsonObj != null) {
                        usrWorkBaseInfoModel.setWhatsappAccount(jsonObj.getString("account"));
                    }
                }
            }
        }

        AttachmentModel attachmentModel = new AttachmentModel();
        attachmentModel.setUserUuid(userUuid);
        attachmentModel.setAttachmentType(UsrAttachmentEnum.INSURANCE_CARD.getType());
        List<UsrAttachmentInfo> usrAttachmentInfoList = this.getAttachment(attachmentModel);
        if (!usrAttachmentInfoList.isEmpty()){
            //usrWorkBaseInfoModel.setInsuranceCardPhoto(usrAttachmentInfoList.get(0).getAttachmentUrl());
            //ahalim: get url from local/oss if available
            usrWorkBaseInfoModel.setInsuranceCardPhoto(setUsrAttachmentUrl(usrAttachmentInfoList.get(0)));
    }
        attachmentModel.setAttachmentType(UsrAttachmentEnum.KK.getType());
        List<UsrAttachmentInfo> usrAttachmentInfoList2 = this.getAttachment(attachmentModel);
        if (!usrAttachmentInfoList2.isEmpty()){
            //usrWorkBaseInfoModel.setKkCardPhoto(usrAttachmentInfoList2.get(0).getAttachmentUrl());
            //ahalim: get url from local/oss if available
            usrWorkBaseInfoModel.setKkCardPhoto(setUsrAttachmentUrl(usrAttachmentInfoList2.get(0)));
        }

        if (usrWorkDetailList.isEmpty() && usrAddressDetailList.isEmpty() && usrBirthAddressDetailList.isEmpty()){
            return null;
        }
        return usrWorkBaseInfoModel;
    }

    /**
     * 添加或更新 家庭主妇 基本信息
     * @param workBaseInfoRequest
     * @return
     */
    @Transactional
    public void addHousewifeBaseInfo(UsrWorkBaseInfoRequest workBaseInfoRequest) throws Exception{
        //首先判断订单步骤是否正确
        this.checkOrderStep(workBaseInfoRequest.getOrderNo(), OrdStepTypeEnum.IDENTITY.getType());
        //添加或修改基本信息
        UsrHouseWifeDetail usrHouseWifeDetail = new UsrHouseWifeDetail();
        usrHouseWifeDetail.setUserUuid(workBaseInfoRequest.getUserUuid());
        usrHouseWifeDetail.setDisabled(0);
        List<UsrHouseWifeDetail> usrHouseWifeDetailList = this.usrHouseWifeDetailDao.scan(usrHouseWifeDetail);
        if (usrHouseWifeDetailList.isEmpty()){
            BeanUtils.copyProperties(usrHouseWifeDetail,workBaseInfoRequest);
            this.usrHouseWifeDetailDao.insert(usrHouseWifeDetail);
        }else{
            usrHouseWifeDetail = usrHouseWifeDetailList.get(0);
            BeanUtils.copyProperties(usrHouseWifeDetail,workBaseInfoRequest);
            this.usrHouseWifeDetailDao.update(usrHouseWifeDetail);
        }

        updateEmailUsrUser(workBaseInfoRequest.getUserUuid(), workBaseInfoRequest.getEmail());

        //添加或修改居住地址信息
        if(!StringUtils.isEmpty(workBaseInfoRequest.getProvince())){
            AddressModel addressModel = new AddressModel();
            addressModel.setUserUuid(workBaseInfoRequest.getUserUuid());
            addressModel.setAddressType(UsrAddressEnum.HOME.getType());
            addressModel.setProvince(workBaseInfoRequest.getProvince());
            addressModel.setCity(workBaseInfoRequest.getCity());
            addressModel.setBigDirect(workBaseInfoRequest.getBigDirect());
            addressModel.setSmallDirect(workBaseInfoRequest.getSmallDirect());
            addressModel.setDetailed(workBaseInfoRequest.getDetailed());
            addressModel.setLbsX(workBaseInfoRequest.getLbsX());
            addressModel.setLbsY(workBaseInfoRequest.getLbsY());
            this.addOrUpdateAddress(addressModel);
        }

        //添加或修改出生地址信息
        if(!StringUtils.isEmpty(workBaseInfoRequest.getBirthProvince())){
            AddressModel addressModel = new AddressModel();
            addressModel.setUserUuid(workBaseInfoRequest.getUserUuid());
            addressModel.setAddressType(UsrAddressEnum.BIRTH.getType());
            addressModel.setProvince(workBaseInfoRequest.getBirthProvince());
            addressModel.setCity(workBaseInfoRequest.getBirthCity());
            addressModel.setBigDirect(workBaseInfoRequest.getBirthBigDirect());
            addressModel.setSmallDirect(workBaseInfoRequest.getBirthSmallDirect());
            this.addOrUpdateAddress(addressModel);
        }

        if (!StringUtils.isEmpty(workBaseInfoRequest.getNpwp())){
            UsrCertificationInfo certificationInfo = new UsrCertificationInfo();
            certificationInfo.setUserUuid(workBaseInfoRequest.getUserUuid());
            certificationInfo.setCertificationType(CertificationEnum.STEUERKARTED.getType());
            List<UsrCertificationInfo> scanList = this.usrCertificationDao.scan(certificationInfo);

            Map<String, String> data = new HashMap<>();
            data.put("account", workBaseInfoRequest.getNpwp());
            String npwpJson =  JsonUtils.serialize(data);

            if (CollectionUtils.isEmpty(scanList)) {
                certificationInfo.setCertificationData(npwpJson);
                certificationInfo.setCertificationResult(CertificationResultEnum.NOT_AUTH.getType());
                this.usrCertificationDao.insert(certificationInfo);

            } else {
                UsrCertificationInfo update = scanList.get(0);
                update.setCertificationData(npwpJson);
                update.setUpdateTime(null);
//                update.setCertificationResult(CertificationResultEnum.NOT_AUTH.getType());
                this.usrCertificationDao.update(update);
            }
        }

        if (!StringUtils.isEmpty(workBaseInfoRequest.getInsuranceCardPhoto())){
            //???????????
            AttachmentModel attachmentModel = new AttachmentModel();
            attachmentModel.setUserUuid(workBaseInfoRequest.getUserUuid());
            //??????(??)
            attachmentModel.setAttachmentType(UsrAttachmentEnum.INSURANCE_CARD.getType());
            attachmentModel.setAttachmentSavePath(workBaseInfoRequest.getInsuranceCardPhoto());
            attachmentModel.setAttachmentUrl(this.path + workBaseInfoRequest.getInsuranceCardPhoto());
            this.addAttachments(attachmentModel);
        }

        // 添加家庭卡
        if (!StringUtils.isEmpty(workBaseInfoRequest.getKkCardPhoto())){
            AttachmentModel attachmentModel = new AttachmentModel();
            attachmentModel.setUserUuid(workBaseInfoRequest.getUserUuid());
            attachmentModel.setAttachmentType(UsrAttachmentEnum.KK.getType());
            attachmentModel.setAttachmentSavePath(workBaseInfoRequest.getKkCardPhoto());
            attachmentModel.setAttachmentUrl(this.path + workBaseInfoRequest.getKkCardPhoto());
            this.addAttachments(attachmentModel);
        }

        if (!StringUtils.isEmpty(workBaseInfoRequest.getWhatsappAccount())){

            UsrCertificationInfo certificationInfo = new UsrCertificationInfo();
            certificationInfo.setUserUuid(workBaseInfoRequest.getUserUuid());
            certificationInfo.setCertificationType(CertificationEnum.WHATS_APP.getType());
            List<UsrCertificationInfo> scanList = this.usrCertificationDao.scan(certificationInfo);

            Map<String, String> data = new HashMap<>();
            data.put("account", workBaseInfoRequest.getWhatsappAccount());
            String wpJson =  JsonUtils.serialize(data);

            if (CollectionUtils.isEmpty(scanList)) {
                certificationInfo.setCertificationData(wpJson);
                certificationInfo.setCertificationResult(CertificationResultEnum.NOT_AUTH.getType());
                this.usrCertificationDao.insert(certificationInfo);

            } else {
                UsrCertificationInfo update = scanList.get(0);
                update.setCertificationData(wpJson);
//                update.setCertificationResult(CertificationResultEnum.NOT_AUTH.getType());
                this.usrCertificationDao.update(update);
            }
        }

        //更新订单步骤到基本信息
        this.updateOrderStep(workBaseInfoRequest.getOrderNo(),workBaseInfoRequest.getUserUuid(), OrdStepTypeEnum.BASIC_INFO.getType());
    }


    /**
     * 反显家庭主妇相关信息
     * @param userUuid
     * @return
     */
    public UsrHouseWifeInfoModel getUsrHousewifeInfo(String userUuid) throws Exception{
        UsrHouseWifeInfoModel houseWifeInfoModel = new UsrHouseWifeInfoModel();
        //??????
        UsrHouseWifeDetail houseWifeDetail = new UsrHouseWifeDetail();
        houseWifeDetail.setUserUuid(userUuid);
        houseWifeDetail.setDisabled(0);
        List<UsrHouseWifeDetail> houseWifeDetailsList = this.usrHouseWifeDetailDao.scan(houseWifeDetail);
        if (!houseWifeDetailsList.isEmpty()){
            houseWifeDetail = houseWifeDetailsList.get(0);
            houseWifeInfoModel.setHomeMouthIncome(houseWifeDetail.getHomeMouthIncome());
            houseWifeInfoModel.setIncomeType(houseWifeDetail.getIncomeType());
            houseWifeInfoModel.setIncomtSource(houseWifeDetail.getIncomtSource());
            houseWifeInfoModel.setSourceName(houseWifeDetail.getSourceName());
            houseWifeInfoModel.setSourceTel(houseWifeDetail.getSourceTel());
            houseWifeInfoModel.setMouthIncome(houseWifeDetail.getMouthIncome());
            houseWifeInfoModel.setWorkType(houseWifeDetail.getWorkType());
            houseWifeInfoModel.setIsCompanyUser(houseWifeDetail.getIsCompanyUser());
            if (houseWifeDetail.getIsCompanyUser() == 1){
                houseWifeInfoModel.setCompanyName(houseWifeDetail.getCompanyName());
                houseWifeInfoModel.setCompanyPhone(houseWifeDetail.getCompanyPhone());
            }else {
                houseWifeInfoModel.setIncomeWithNoCom(houseWifeDetail.getIncomeWithNoCom());
            }

        }

        AddressModel addressModel = new AddressModel();
        addressModel.setUserUuid(userUuid);
        addressModel.setAddressType(UsrAddressEnum.FAMILY.getType());
        List<UsrAddressDetail> list = this.getAddressByUserUuidAndType(addressModel);
        if (!list.isEmpty()){
            UsrAddressDetail usrAddressDetail = list.get(0);
            BeanUtils.copyProperties(houseWifeInfoModel,usrAddressDetail);
        }
        if (houseWifeDetailsList.isEmpty() && list.isEmpty()){
            return null;
        }
        return houseWifeInfoModel;
    }

    /**
     * 添加家庭主妇相关信息
     * @param usrHousewifeRequest
     */
    @Transactional
    public void addUsrHousewifeInfo(UsrHousewifeRequest usrHousewifeRequest) throws ServiceException{
        // 校验订单状态
        this.checkOrderStep(usrHousewifeRequest.getOrderNo(), OrdStepTypeEnum.BASIC_INFO.getType());

        UsrHouseWifeDetail houseWifeDetail = new UsrHouseWifeDetail();
        houseWifeDetail.setUserUuid(usrHousewifeRequest.getUserUuid());
        houseWifeDetail.setDisabled(0);
        List<UsrHouseWifeDetail> houseWifeDetailsList = this.usrHouseWifeDetailDao.scan(houseWifeDetail);
        if (houseWifeDetailsList.isEmpty()){
            houseWifeDetail.setHomeMouthIncome(usrHousewifeRequest.getHomeMouthIncome());
            houseWifeDetail.setIncomeType(usrHousewifeRequest.getIncomeType());
            houseWifeDetail.setIncomtSource(usrHousewifeRequest.getIncomtSource());
            houseWifeDetail.setSourceName(usrHousewifeRequest.getSourceName());
            houseWifeDetail.setSourceTel(usrHousewifeRequest.getSourceTel());
            houseWifeDetail.setMouthIncome(usrHousewifeRequest.getMouthIncome());
            houseWifeDetail.setWorkType(usrHousewifeRequest.getWorkType());
            houseWifeDetail.setIsCompanyUser(usrHousewifeRequest.getIsCompanyUser());
            if (usrHousewifeRequest.getIsCompanyUser() == 1){
                houseWifeDetail.setCompanyName(usrHousewifeRequest.getCompanyName());
                houseWifeDetail.setCompanyPhone(usrHousewifeRequest.getCompanyPhone());
            }else {
                houseWifeDetail.setIncomeWithNoCom(usrHousewifeRequest.getIncomeWithNoCom());
            }
            this.usrHouseWifeDetailDao.insert(houseWifeDetail);
        }else {
            houseWifeDetail = houseWifeDetailsList.get(0);
            houseWifeDetail.setHomeMouthIncome(usrHousewifeRequest.getHomeMouthIncome());
            houseWifeDetail.setIncomeType(usrHousewifeRequest.getIncomeType());
            houseWifeDetail.setIncomtSource(usrHousewifeRequest.getIncomtSource());
            houseWifeDetail.setSourceName(usrHousewifeRequest.getSourceName());
            houseWifeDetail.setSourceTel(usrHousewifeRequest.getSourceTel());
            houseWifeDetail.setMouthIncome(usrHousewifeRequest.getMouthIncome());
            houseWifeDetail.setWorkType(usrHousewifeRequest.getWorkType());
            houseWifeDetail.setIsCompanyUser(usrHousewifeRequest.getIsCompanyUser());
            if (usrHousewifeRequest.getIsCompanyUser() == 1){
                houseWifeDetail.setCompanyName(usrHousewifeRequest.getCompanyName());
                houseWifeDetail.setCompanyPhone(usrHousewifeRequest.getCompanyPhone());
            }else {
                houseWifeDetail.setIncomeWithNoCom(usrHousewifeRequest.getIncomeWithNoCom());
            }
            this.usrHouseWifeDetailDao.update(houseWifeDetail);
        }
        // 地址
        AddressModel addressModel = new AddressModel();
        addressModel.setUserUuid(usrHousewifeRequest.getUserUuid());
        addressModel.setAddressType(UsrAddressEnum.FAMILY.getType());
        addressModel.setProvince(usrHousewifeRequest.getProvince());
        addressModel.setCity(usrHousewifeRequest.getCity());
        addressModel.setBigDirect(usrHousewifeRequest.getBigDirect());
        addressModel.setSmallDirect(usrHousewifeRequest.getSmallDirect());
        addressModel.setDetailed(usrHousewifeRequest.getDetailed());
        addressModel.setLbsX(usrHousewifeRequest.getLbsX());
        addressModel.setLbsY(usrHousewifeRequest.getLbsY());
        this.addOrUpdateAddress(addressModel);

        //???????????
        this.updateOrderStep(usrHousewifeRequest.getOrderNo(), usrHousewifeRequest.getUserUuid(), OrdStepTypeEnum.WORK_INFO.getType());
    }

    /**
     * ???????????
     * @param addressModel
     * @return
     */
    private List<UsrAddressDetail> getAddressByUserUuidAndType(AddressModel addressModel){
        UsrAddressDetail usrAddressDetail = new UsrAddressDetail();
        usrAddressDetail.setUserUuid(addressModel.getUserUuid());
        usrAddressDetail.setDisabled(0);
        usrAddressDetail.setAddressType(addressModel.getAddressType());
        List<UsrAddressDetail> usrAddressDetailList = this.usrAddressDetailDao.scan(usrAddressDetail);
        return usrAddressDetailList;
    }

    /**
     * ?????????????
     * @param addressModel
     */
    private void addOrUpdateAddress(AddressModel addressModel){
        UsrAddressDetail usrAddressDetail = new UsrAddressDetail();
        usrAddressDetail.setUserUuid(addressModel.getUserUuid());
        usrAddressDetail.setDisabled(0);
        usrAddressDetail.setAddressType(addressModel.getAddressType());
        List<UsrAddressDetail> usrAddressDetailList = this.usrAddressDetailDao.scan(usrAddressDetail);
        if (usrAddressDetailList.isEmpty()){
            usrAddressDetail.setProvince(addressModel.getProvince());
            usrAddressDetail.setCity(addressModel.getCity());
            usrAddressDetail.setBigDirect(addressModel.getBigDirect());
            usrAddressDetail.setSmallDirect(addressModel.getSmallDirect());
            usrAddressDetail.setDetailed(addressModel.getDetailed());
            usrAddressDetail.setLbsX(addressModel.getLbsX());
            usrAddressDetail.setLbsY(addressModel.getLbsY());
            String region = AddressUtils.getRegionByCity(addressModel.getCity());
            if(StringUtils.isNotEmpty(region)){
                usrAddressDetail.setRegion(region);
            }
            this.usrAddressDetailDao.insert(usrAddressDetail);
        }else {
            usrAddressDetail = usrAddressDetailList.get(0);
            usrAddressDetail.setProvince(addressModel.getProvince());
            usrAddressDetail.setCity(addressModel.getCity());
            usrAddressDetail.setBigDirect(addressModel.getBigDirect());
            usrAddressDetail.setSmallDirect(addressModel.getSmallDirect());
            usrAddressDetail.setDetailed(addressModel.getDetailed());
            usrAddressDetail.setLbsX(addressModel.getLbsX());
            usrAddressDetail.setLbsY(addressModel.getLbsY());
            String region = AddressUtils.getRegionByCity(addressModel.getCity());
            if(StringUtils.isNotEmpty(region)){
                usrAddressDetail.setRegion(region);
            }
            this.usrAddressDetailDao.update(usrAddressDetail);
        }
    }

    /**
     * ???? ????
     * @param userUuid
     * @return
     */
    public UsrStudentBaseInfoModel getStudentBaseInfo(String userUuid) throws Exception{

        UsrStudentBaseInfoModel usrStudentBaseInfoModel = new UsrStudentBaseInfoModel();
        //???? ????
        UsrStudentDetail usrStudentDetail = new UsrStudentDetail();
        usrStudentDetail.setUserUuid(userUuid);
        usrStudentDetail.setDisabled(0);
        List<UsrStudentDetail> usrStudentDetailList = this.usrStudentDetailDao.scan(usrStudentDetail);
        if (!usrStudentDetailList.isEmpty()){
            usrStudentDetail = usrStudentDetailList.get(0);
            usrStudentBaseInfoModel.setEmail(usrStudentDetail.getEmail());
            usrStudentBaseInfoModel.setAcademic(usrStudentDetail.getAcademic());
            usrStudentBaseInfoModel.setBirthday(usrStudentDetail.getBirthday());
            usrStudentBaseInfoModel.setFamilyMemberAmount(usrStudentDetail.getFamilyMemberAmount());
            usrStudentBaseInfoModel.setFamilyAnnualIncome(usrStudentDetail.getFamilyAnnualIncome());
            usrStudentBaseInfoModel.setFatherName(usrStudentDetail.getFatherName());
            usrStudentBaseInfoModel.setFatherMobile(usrStudentDetail.getFatherMobile());
            usrStudentBaseInfoModel.setFatherPosition(usrStudentDetail.getFatherPosition());
            usrStudentBaseInfoModel.setMotherName(usrStudentDetail.getMotherName());
            usrStudentBaseInfoModel.setMotherMobile(usrStudentDetail.getMotherMobile());
            usrStudentBaseInfoModel.setMotherPosition(usrStudentDetail.getMotherPosition());
            usrStudentBaseInfoModel.setDwellingCondition(usrStudentDetail.getDwellingCondition());
            if(StringUtils.isNotEmpty(usrStudentDetail.getBorrowUse())){
                usrStudentBaseInfoModel.setBorrowUse(usrStudentDetail.getBorrowUse());
            }
            usrStudentBaseInfoModel.setMouthCost(usrStudentDetail.getMouthCost());
            usrStudentBaseInfoModel.setMouthCostSource(usrStudentDetail.getMouthCostSource());
            usrStudentBaseInfoModel.setIsPartTime(usrStudentDetail.getIsPartTime());
            usrStudentBaseInfoModel.setPartTimeName(usrStudentDetail.getPartTimeName());

            // 有兼职
            if (usrStudentDetail.getIsPartTime().equals("1")){
                usrStudentBaseInfoModel.setPartTimeType(usrStudentDetail.getPartTimeType());
                usrStudentBaseInfoModel.setPartTimeIncome(usrStudentDetail.getPartTimeIncome());
                usrStudentBaseInfoModel.setPartTimeProveTele(usrStudentDetail.getPartTimeProveTele());
                usrStudentBaseInfoModel.setPartTimeProveName(usrStudentDetail.getPartTimeProveName());
            }

            usrStudentBaseInfoModel.setClassmateName(usrStudentDetail.getClassmateName());
            usrStudentBaseInfoModel.setClassmateTele(usrStudentDetail.getClassmateTele());
        }

        //?? ??????
        AddressModel addressModel = new AddressModel();
        addressModel.setAddressType(UsrAddressEnum.HOME.getType());
        addressModel.setUserUuid(userUuid);
        List<UsrAddressDetail> list = this.getAddressByUserUuidAndType(addressModel);
        if (!list.isEmpty()){
            UsrAddressDetail usrAddressDetail = list.get(0);
            BeanUtils.copyProperties(usrStudentBaseInfoModel,usrAddressDetail);
        }
        //?? ???????
        AddressModel birthAddressModel = new AddressModel();
        birthAddressModel.setAddressType(UsrAddressEnum.BIRTH.getType());
        birthAddressModel.setUserUuid(userUuid);
        List<UsrAddressDetail> birthAddressList = this.getAddressByUserUuidAndType(birthAddressModel);
        if (!birthAddressList.isEmpty()){
            UsrAddressDetail usrAddressDetail = birthAddressList.get(0);
            usrStudentBaseInfoModel.setBirthProvince(usrAddressDetail.getProvince());
            usrStudentBaseInfoModel.setBirthCity(usrAddressDetail.getCity());
            usrStudentBaseInfoModel.setBirthBigDirect(usrAddressDetail.getBigDirect());
            usrStudentBaseInfoModel.setBirthSmallDirect(usrAddressDetail.getSmallDirect());
        }

        UsrCertificationInfo certificationInfo2 = new UsrCertificationInfo();
        certificationInfo2.setUserUuid(userUuid);
        certificationInfo2.setCertificationType(CertificationEnum.WHATS_APP.getType());
        List<UsrCertificationInfo> scanList2 = this.usrCertificationDao.scan(certificationInfo2);
        if (!CollectionUtils.isEmpty(scanList2)) {
            UsrCertificationInfo update = scanList2.get(0);
            if (!StringUtils.isEmpty(update.getCertificationData())){
                if (JsonUtils.isJSONValid(update.getCertificationData())){
                    JSONObject jsonObj = JSONObject.parseObject(update.getCertificationData());
                    if (jsonObj != null) {
                        usrStudentBaseInfoModel.setWhatsappAccount(jsonObj.getString("account"));
                    }
                }
            }
        }

        AttachmentModel attachmentModel = new AttachmentModel();
        attachmentModel.setUserUuid(userUuid);
        attachmentModel.setAttachmentType(UsrAttachmentEnum.KK.getType());
        List<UsrAttachmentInfo> usrAttachmentInfoList = this.getAttachment(attachmentModel);
        if (!usrAttachmentInfoList.isEmpty()){
            //usrStudentBaseInfoModel.setKkCardPhoto(usrAttachmentInfoList.get(0).getAttachmentUrl());
            //ahalim: get url from local/oss if available
            usrStudentBaseInfoModel.setKkCardPhoto(setUsrAttachmentUrl(usrAttachmentInfoList.get(0)));
        }

        if (usrStudentDetailList.isEmpty() && list.isEmpty()&& birthAddressList.isEmpty()){
            return null;
        }
        return usrStudentBaseInfoModel;
    }

    /**
     * ?? ?????
     * @param userUuid
     * @return
     */
    public UsrLinkManModel getLinkManInfo(String userUuid){
        UsrLinkManModel usrLinkManModel = new UsrLinkManModel();
//        UsrLinkManInfo usrLinkManInfo = new UsrLinkManInfo();
//        usrLinkManInfo.setUserUuid(userUuid);
//        usrLinkManInfo.setDisabled(0);
        List<UsrLinkManInfo> linkManInfoList = this.usrLinkManDao.getUsrLinkManWithUserUuid(userUuid);
        if (!linkManInfoList.isEmpty()){
            for (UsrLinkManInfo usrLinkManInfo1 :linkManInfoList){
                if (usrLinkManInfo1.getSequence() == 1){
                    usrLinkManModel.setContactsName1(usrLinkManInfo1.getContactsName());
                    usrLinkManModel.setContactsMobile1(usrLinkManInfo1.getContactsMobile());
                    usrLinkManModel.setRelation1(usrLinkManInfo1.getRelation());
                } else if (usrLinkManInfo1.getSequence() == 2){
                    usrLinkManModel.setContactsName2(usrLinkManInfo1.getContactsName());
                    usrLinkManModel.setContactsMobile2(usrLinkManInfo1.getContactsMobile());
                    usrLinkManModel.setRelation2(usrLinkManInfo1.getRelation());
                } else {
                    usrLinkManModel.setAlternatePhoneNo(usrLinkManInfo1.getContactsMobile());
                }
            }
        }else {
            return null;
        }
        return usrLinkManModel;
    }

    /**
     * ????? ?????
     * @param usrContactInfoRequest
     */
    @Transactional
    public void addLinkManInfo(UsrContactInfoRequest usrContactInfoRequest) throws ServiceException{


        //首先判断订单步骤是否正确
        this.checkOrderStep(usrContactInfoRequest.getOrderNo(), OrdStepTypeEnum.WORK_INFO.getType());

        if (!StringUtils.isNotEmpty(usrContactInfoRequest.getContactsMobile1())
                ||!StringUtils.isNotEmpty(usrContactInfoRequest.getContactsMobile2())){
            log.error("上传联系人用户接口缺少参数");
            throw new ServiceException(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }

        usrContactInfoRequest.setContactsName1(CustomEmojiUtils.removeEmoji(usrContactInfoRequest.getContactsName1()));
        usrContactInfoRequest.setContactsName2(CustomEmojiUtils.removeEmoji(usrContactInfoRequest.getContactsName2()));

        try {
            if(StringUtils.isNotEmpty(usrContactInfoRequest.getAlternatePhoneNo())){
                // 判断备选联系人是否是已经注册的用户
                UsrUser usr = new UsrUser();
                log.info("加密手机号:"+DESUtils.encrypt(usrContactInfoRequest.getAlternatePhoneNo()));
                usr.setMobileNumberDES(DESUtils.encrypt(usrContactInfoRequest.getAlternatePhoneNo()));
                usr.setDisabled(0);
                List<UsrUser> scanList = this.usrDao.scan(usr);
                if (!CollectionUtils.isEmpty(scanList)){
                    log.error("备选联系人已经是Do-It注册用户");
                    throw new ServiceException(ExceptionEnum.USER_ALTERNET_PHONENO_IS_EXIT);
                }

                // 保存备选联系人
                UsrLinkManInfo usrLinkManInfo3 = new UsrLinkManInfo();
                usrLinkManInfo3.setUserUuid(usrContactInfoRequest.getUserUuid());
                usrLinkManInfo3.setSequence(3);
                usrLinkManInfo3.setDisabled(0);
                List<UsrLinkManInfo> linkManInfoList3 = this.usrLinkManDao.scan(usrLinkManInfo3);
                if (linkManInfoList3.isEmpty()){
                    usrLinkManInfo3.setContactsMobile(usrContactInfoRequest.getAlternatePhoneNo());
                    this.usrLinkManDao.insert(usrLinkManInfo3);
                }else {
                    usrLinkManInfo3 = linkManInfoList3.get(0);
                    usrLinkManInfo3.setContactsMobile(usrContactInfoRequest.getAlternatePhoneNo());
                    this.usrLinkManDao.update(usrLinkManInfo3);
                }
            }

            // 保存或更新第一联系人
            UsrLinkManInfo usrLinkManInfo = new UsrLinkManInfo();
            usrLinkManInfo.setUserUuid(usrContactInfoRequest.getUserUuid());
            usrLinkManInfo.setSequence(1);
            usrLinkManInfo.setDisabled(0);
            List<UsrLinkManInfo> linkManInfoList = this.usrLinkManDao.scan(usrLinkManInfo);
            String formatMobile1 = CheakTeleUtils.telephoneNumberValid2(usrContactInfoRequest.getContactsMobile1());
            usrLinkManInfo.setFormatMobile(formatMobile1);
            if (linkManInfoList.isEmpty()){
                usrLinkManInfo.setContactsName(usrContactInfoRequest.getContactsName1());
                usrLinkManInfo.setRelation(usrContactInfoRequest.getRelation1());
                usrLinkManInfo.setContactsMobile(usrContactInfoRequest.getContactsMobile1());
                this.usrLinkManDao.insert(usrLinkManInfo);
            }else {
                usrLinkManInfo = linkManInfoList.get(0);
                usrLinkManInfo.setContactsName(usrContactInfoRequest.getContactsName1());
                usrLinkManInfo.setRelation(usrContactInfoRequest.getRelation1());
                usrLinkManInfo.setContactsMobile(usrContactInfoRequest.getContactsMobile1());
                this.usrLinkManDao.update(usrLinkManInfo);
            }

            // 保存或更新第二联系人
            UsrLinkManInfo usrLinkManInfo2 = new UsrLinkManInfo();
            usrLinkManInfo2.setUserUuid(usrContactInfoRequest.getUserUuid());
            usrLinkManInfo2.setSequence(2);
            usrLinkManInfo2.setDisabled(0);
            List<UsrLinkManInfo> linkManInfoList2 = this.usrLinkManDao.scan(usrLinkManInfo2);
            String formatMobile2 = CheakTeleUtils.telephoneNumberValid2(usrContactInfoRequest.getContactsMobile2());
            usrLinkManInfo2.setFormatMobile(formatMobile2);
            if (linkManInfoList2.isEmpty()){
                usrLinkManInfo2.setContactsName(usrContactInfoRequest.getContactsName2());
                usrLinkManInfo2.setRelation(usrContactInfoRequest.getRelation2());
                usrLinkManInfo2.setContactsMobile(usrContactInfoRequest.getContactsMobile2());
                this.usrLinkManDao.insert(usrLinkManInfo2);
            }else {
                usrLinkManInfo2 = linkManInfoList2.get(0);
                usrLinkManInfo2.setContactsName(usrContactInfoRequest.getContactsName2());
                usrLinkManInfo2.setRelation(usrContactInfoRequest.getRelation2());
                usrLinkManInfo2.setContactsMobile(usrContactInfoRequest.getContactsMobile2());
                this.usrLinkManDao.update(usrLinkManInfo2);
            }

            ordDeviceExtendInfoService.saveExtendInfo(Arrays.asList(usrLinkManInfo,usrLinkManInfo2));
        }catch (Exception e){
            log.error("上传联系人用户接口异常",e);
            throw new ServiceException(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }

        //更新订单步骤到联系人信息
        this.updateOrderStep(usrContactInfoRequest.getOrderNo(), usrContactInfoRequest.getUserUuid(), OrdStepTypeEnum.CONTACT_INFO.getType());
    }

    /**
     * ????? ?? ????
     * @param studentBaseInfoRequest
     */
    @Transactional
    public UsrStudentInfoResponse addOrUpdateStudentBaseInfo(UsrStudentBaseInfoRequest studentBaseInfoRequest) throws Exception{
        UsrStudentInfoResponse response = new  UsrStudentInfoResponse();
        //????????????
        this.checkOrderStep(studentBaseInfoRequest.getOrderNo(), OrdStepTypeEnum.IDENTITY.getType());
        //????? ????
        UsrStudentDetail usrStudentDetail = new UsrStudentDetail();
        usrStudentDetail.setUserUuid(studentBaseInfoRequest.getUserUuid());
        usrStudentDetail.setDisabled(0);
        List<UsrStudentDetail> usrStudentDetailList = this.usrStudentDetailDao.scan(usrStudentDetail);
        if (usrStudentDetailList.isEmpty()){
            BeanUtils.copyProperties(usrStudentDetail,studentBaseInfoRequest);
            this.usrStudentDetailDao.insert(usrStudentDetail);
        }else {
            usrStudentDetail = usrStudentDetailList.get(0);
            BeanUtils.copyProperties(usrStudentDetail,studentBaseInfoRequest);
            this.usrStudentDetailDao.update(usrStudentDetail);
        }

        //添加或修改 居住地址信息
        if(!StringUtils.isEmpty(studentBaseInfoRequest.getProvince())){
            AddressModel addressModel = new AddressModel();
            BeanUtils.copyProperties(addressModel,studentBaseInfoRequest);
            addressModel.setAddressType(UsrAddressEnum.HOME.getType());
            this.addOrUpdateAddress(addressModel);
        }
        //????? ??????
        AddressModel birthAddressModel = new AddressModel();
        birthAddressModel.setUserUuid(studentBaseInfoRequest.getUserUuid());
        birthAddressModel.setAddressType(UsrAddressEnum.BIRTH.getType());
        birthAddressModel.setProvince(studentBaseInfoRequest.getBirthProvince());
        birthAddressModel.setCity(studentBaseInfoRequest.getBirthCity());
        birthAddressModel.setBigDirect(studentBaseInfoRequest.getBirthBigDirect());
        birthAddressModel.setSmallDirect(studentBaseInfoRequest.getBirthSmallDirect());
        this.addOrUpdateAddress(birthAddressModel);

        updateEmailUsrUser(studentBaseInfoRequest.getUserUuid(), studentBaseInfoRequest.getEmail());

        String academic = studentBaseInfoRequest.getAcademic();
        int age = getAgeFromBirthday(studentBaseInfoRequest.getBirthday());
        //  1. 学历是小学 初中  高中  年龄大于18
        //  2. 学历是专科 本科 年龄大于等于28
        int role = 0;
        if (((academic.equals("Sekolah dasar")||academic.equals("Sekolah Menengah Pertama")||academic.equals("Sekolah Menengah Atas")) && age>18)
              || ((academic.equals("Diploma")||academic.equals("Sarjana")) && age>=28)   ){
            role = 2;
            response.setIsChangeRole("1");
        }else {
            role = 1;
        }

        //  判断用户是否是复借用户 不是复借用户再更新
        List<OrdOrder> orderList = this.orderDao.isLoanAgain(studentBaseInfoRequest.getUserUuid());
        if (CollectionUtils.isEmpty(orderList)){
            UsrUser user  = this.usrService.getUserByUuid(studentBaseInfoRequest.getUserUuid());
            user.setUserRole(role);
            this.usrDao.update(user);
        }


        if (!StringUtils.isEmpty(studentBaseInfoRequest.getWhatsappAccount())){

            UsrCertificationInfo certificationInfo = new UsrCertificationInfo();
            certificationInfo.setUserUuid(studentBaseInfoRequest.getUserUuid());
            certificationInfo.setCertificationType(CertificationEnum.WHATS_APP.getType());
            List<UsrCertificationInfo> scanList = this.usrCertificationDao.scan(certificationInfo);

            Map<String, String> data = new HashMap<>();
            data.put("account", studentBaseInfoRequest.getWhatsappAccount());
            String wpJson =  JsonUtils.serialize(data);

            if (CollectionUtils.isEmpty(scanList)) {
                certificationInfo.setCertificationData(wpJson);
                certificationInfo.setCertificationResult(CertificationResultEnum.NOT_AUTH.getType());
                this.usrCertificationDao.insert(certificationInfo);

            } else {
                UsrCertificationInfo update = scanList.get(0);
                update.setCertificationData(wpJson);
//                update.setCertificationResult(CertificationResultEnum.NOT_AUTH.getType());
                this.usrCertificationDao.update(update);
            }
        }
        // 添加家庭卡
        if (!StringUtils.isEmpty(studentBaseInfoRequest.getKkCardPhoto())){
            AttachmentModel attachmentModel = new AttachmentModel();
            attachmentModel.setUserUuid(studentBaseInfoRequest.getUserUuid());
            attachmentModel.setAttachmentType(UsrAttachmentEnum.KK.getType());
            attachmentModel.setAttachmentSavePath(studentBaseInfoRequest.getKkCardPhoto());
            attachmentModel.setAttachmentUrl(this.path + studentBaseInfoRequest.getKkCardPhoto());
            this.addAttachments(attachmentModel);
        }

        //???????????
        this.updateOrderStep(studentBaseInfoRequest.getOrderNo(),studentBaseInfoRequest.getUserUuid(), OrdStepTypeEnum.BASIC_INFO.getType());

        return response;
    }

    // 根据生日获取年龄
    public int getAgeFromBirthday(String birthday){
        String[]  birthArray =  birthday.split("/");
        if (birthArray.length >= 3){
            Calendar a=Calendar.getInstance();
            return a.get(Calendar.YEAR) - Integer.valueOf(birthArray[2]);
        }
        return 0;
    }

    /**
     * ???? ????
     * @param userUuid
     * @return
     */
    public UsrSchoolInfoModel getStudentSchoolInfo(String userUuid) throws Exception{
        UsrSchoolInfoModel usrSchoolInfoModel = new UsrSchoolInfoModel();
        //??????
        UsrStudentDetail usrStudentDetail = new UsrStudentDetail();
        usrStudentDetail.setUserUuid(userUuid);
        usrStudentDetail.setDisabled(0);
        List<UsrStudentDetail> usrStudentDetailList = this.usrStudentDetailDao.scan(usrStudentDetail);
        if (!usrStudentDetailList.isEmpty()){
            usrStudentDetail = usrStudentDetailList.get(0);
            BeanUtils.copyProperties(usrSchoolInfoModel,usrStudentDetail);
        }

        //?? ??????
        AddressModel addressModel = new AddressModel();
        addressModel.setAddressType(UsrAddressEnum.SCHOOL.getType());
        addressModel.setUserUuid(userUuid);
        List<UsrAddressDetail> list = this.getAddressByUserUuidAndType(addressModel);
        if (!list.isEmpty()){
            UsrAddressDetail usrAddressDetail = list.get(0);
            BeanUtils.copyProperties(usrSchoolInfoModel,usrAddressDetail);
        }

        //?? ??????
        AttachmentModel attachmentModel = new AttachmentModel();
        attachmentModel.setUserUuid(userUuid);
        List<UsrAttachmentInfo> usrAttachmentInfoList = null;
        //???
        attachmentModel.setAttachmentType(UsrAttachmentEnum.STUDENT_CARD.getType());
        usrAttachmentInfoList = this.getAttachment(attachmentModel);
        if (!usrAttachmentInfoList.isEmpty()){
            usrSchoolInfoModel.setStudentCardUrl(setUsrAttachmentUrl(usrAttachmentInfoList.get(0)));
        }
        //???????
        attachmentModel.setAttachmentType(UsrAttachmentEnum.SCHOLARSHIP.getType());
        usrAttachmentInfoList = this.getAttachment(attachmentModel);
        if (!usrAttachmentInfoList.isEmpty()){
            usrSchoolInfoModel.setScholarshipUrl(setUsrAttachmentUrl(usrAttachmentInfoList.get(0)));
        }
        //??????
        attachmentModel.setAttachmentType(UsrAttachmentEnum.ENGLISH.getType());
        usrAttachmentInfoList = this.getAttachment(attachmentModel);
        if (!usrAttachmentInfoList.isEmpty()){
            usrSchoolInfoModel.setEnglishUrl(setUsrAttachmentUrl(usrAttachmentInfoList.get(0)));
        }
        //???????
        attachmentModel.setAttachmentType(UsrAttachmentEnum.COMPUTER.getType());
        usrAttachmentInfoList = this.getAttachment(attachmentModel);
        if (!usrAttachmentInfoList.isEmpty()){
            usrSchoolInfoModel.setComputerUrl(setUsrAttachmentUrl(usrAttachmentInfoList.get(0)));
        }
        //?????
        attachmentModel.setAttachmentType(UsrAttachmentEnum.SCHOOL_CARD.getType());
        usrAttachmentInfoList = this.getAttachment(attachmentModel);
        if (!usrAttachmentInfoList.isEmpty()){
            usrSchoolInfoModel.setSchoolCardUrl(setUsrAttachmentUrl(usrAttachmentInfoList.get(0)));
        }
        //??????
        attachmentModel.setAttachmentType(UsrAttachmentEnum.OTHER_CERTIFICATION.getType());
        usrAttachmentInfoList = this.getAttachment(attachmentModel);
        if (!usrAttachmentInfoList.isEmpty()){
            usrSchoolInfoModel.setOtherCertificateUrl(setUsrAttachmentUrl(usrAttachmentInfoList.get(0)));
        }
        List<UsrAttachmentInfo> schoolList = this.usrAttachmentInfoDao.getSchoolAttachmenInfoByUserUuid(userUuid);
        if (usrStudentDetailList.isEmpty() && list.isEmpty() && schoolList.isEmpty()){
            return null;
        }
        return usrSchoolInfoModel;
    }

    /**
     * ???? ????
     * @param attachmentModel
     * @return
     */
    private List<UsrAttachmentInfo> getAttachment(AttachmentModel attachmentModel){
        UsrAttachmentInfo usrAttachmentInfo = new UsrAttachmentInfo();
        usrAttachmentInfo.setUserUuid(attachmentModel.getUserUuid());
        usrAttachmentInfo.setDisabled(0);
        usrAttachmentInfo.setAttachmentType(attachmentModel.getAttachmentType());
        List<UsrAttachmentInfo> usrAttachmentInfoList = this.usrAttachmentInfoDao.scan(usrAttachmentInfo);
        return usrAttachmentInfoList;
    }

    /**
     * ????? ?? ????
     * @param usrSchoolInfoRequest
     */
    @Transactional
    public void addOrUpdateStudentSchoolInfo(UsrSchoolInfoRequest usrSchoolInfoRequest) throws Exception{
        //????????????
        this.checkOrderStep(usrSchoolInfoRequest.getOrderNo(), OrdStepTypeEnum.BASIC_INFO.getType());
        //???????????
        UsrStudentDetail usrStudentDetail = new UsrStudentDetail();
        usrStudentDetail.setUserUuid(usrSchoolInfoRequest.getUserUuid());
        usrStudentDetail.setDisabled(0);
        List<UsrStudentDetail> usrStudentDetailList = this.usrStudentDetailDao.scan(usrStudentDetail);
        if (usrStudentDetailList.isEmpty()){
            usrStudentDetail.setSchoolName(usrSchoolInfoRequest.getSchoolName());
            usrStudentDetail.setMajor(usrSchoolInfoRequest.getMajor());
            usrStudentDetail.setStartSchoolDate(usrSchoolInfoRequest.getStartSchoolDate());
            usrStudentDetail.setStudentNo(usrSchoolInfoRequest.getStudentNo());
            this.usrStudentDetailDao.insert(usrStudentDetail);
        }else {
            usrStudentDetail = usrStudentDetailList.get(0);
            usrStudentDetail.setSchoolName(usrSchoolInfoRequest.getSchoolName());
            usrStudentDetail.setMajor(usrSchoolInfoRequest.getMajor());
            usrStudentDetail.setStartSchoolDate(usrSchoolInfoRequest.getStartSchoolDate());
            usrStudentDetail.setStudentNo(usrSchoolInfoRequest.getStudentNo());
            this.usrStudentDetailDao.update(usrStudentDetail);
        }

        //???????????
        AddressModel addressModel = new AddressModel();
        BeanUtils.copyProperties(addressModel, usrSchoolInfoRequest);
        addressModel.setAddressType(UsrAddressEnum.SCHOOL.getType());
        this.addOrUpdateAddress(addressModel);

        //???????????
        String userUuid = usrSchoolInfoRequest.getUserUuid();
        //???(??)
        String studentCard = usrSchoolInfoRequest.getStudentCardUrl();
        this.saveStudentAttachment(userUuid,studentCard,UsrAttachmentEnum.STUDENT_CARD.getType());
        //???????(??)
        String scholarship = usrSchoolInfoRequest.getScholarshipUrl();
        this.saveStudentAttachment(userUuid,scholarship,UsrAttachmentEnum.SCHOLARSHIP.getType());
        //??????(??)
        String english = usrSchoolInfoRequest.getEnglishUrl();
        this.saveStudentAttachment(userUuid,english,UsrAttachmentEnum.ENGLISH.getType());
        //???????(??)
        String computer = usrSchoolInfoRequest.getComputerUrl();
        this.saveStudentAttachment(userUuid,computer,UsrAttachmentEnum.COMPUTER.getType());
        //?????(??)
        String schoolCard = usrSchoolInfoRequest.getSchoolCardUrl();
        this.saveStudentAttachment(userUuid,schoolCard,UsrAttachmentEnum.SCHOOL_CARD.getType());
        //????????(??)
        String other = usrSchoolInfoRequest.getOtherCertificateUrl();
        this.saveStudentAttachment(userUuid,other,UsrAttachmentEnum.OTHER_CERTIFICATION.getType());

        //???????????
        this.updateOrderStep(usrSchoolInfoRequest.getOrderNo(), usrSchoolInfoRequest.getUserUuid(), OrdStepTypeEnum.WORK_INFO.getType());
    }

    /**
     * ?????????
     * @param userUuid
     * @param photoUrl
     * @param photoType
     */
    public void saveStudentAttachment(String userUuid,String photoUrl,int photoType){
        AttachmentModel attachmentModel = new AttachmentModel();
        attachmentModel.setUserUuid(userUuid);
        if (!StringUtils.isEmpty(photoUrl) && !photoUrl.startsWith(this.path)){
            attachmentModel.setAttachmentType(photoType);
            attachmentModel.setAttachmentSavePath(photoUrl);
            attachmentModel.setAttachmentUrl(this.path + photoUrl);
            this.addAttachments(attachmentModel);
        }
    }

    /**
     * ??????????????
     * @param attachmentModel
     */
    private void addAttachments(AttachmentModel attachmentModel){
        UsrAttachmentInfo usrAttachmentInfo = new UsrAttachmentInfo();
        usrAttachmentInfo.setUserUuid(attachmentModel.getUserUuid());
        usrAttachmentInfo.setDisabled(0);
        usrAttachmentInfo.setAttachmentType(attachmentModel.getAttachmentType());
        List<UsrAttachmentInfo> usrAttachmentInfoList = this.usrAttachmentInfoDao.scan(usrAttachmentInfo);
        //防止老版本传值带域名
        if (StringUtils.isNotBlank(attachmentModel.getAttachmentSavePath()) &&
                attachmentModel.getAttachmentSavePath().startsWith("http")) {
            attachmentModel.setAttachmentUrl(attachmentModel.getAttachmentSavePath());
            attachmentModel.setAttachmentSavePath(attachmentModel.getAttachmentSavePath().replace(this.path, ""));
        }
        if (usrAttachmentInfoList.isEmpty()){
            usrAttachmentInfo.setAttachmentSavePath(attachmentModel.getAttachmentSavePath());
            usrAttachmentInfo.setAttachmentUrl(attachmentModel.getAttachmentUrl());
            this.usrAttachmentInfoDao.insert(usrAttachmentInfo);
        }else {
            usrAttachmentInfo = usrAttachmentInfoList.get(0);
            if (attachmentModel.getAttachmentSavePath() != null ){
                usrAttachmentInfo.setAttachmentSavePath(attachmentModel.getAttachmentSavePath());
                usrAttachmentInfo.setAttachmentUrl(attachmentModel.getAttachmentUrl());
            }
            this.usrAttachmentInfoDao.update(usrAttachmentInfo);
        }
    }

    /**
     * ?????????????????
     * @param userSubmitCerInfoRequest
     * @return
     */
    @Transactional
    public Boolean getCertificationInfo(UsrSubmitCerInfoRequest userSubmitCerInfoRequest) throws ServiceException{
        //????????????
        this.checkOrderStep(userSubmitCerInfoRequest.getOrderNo(), OrdStepTypeEnum.CONTACT_INFO.getType());
        UsrCertificationInfo usrCertificationInfo = new UsrCertificationInfo();
        usrCertificationInfo.setUserUuid(userSubmitCerInfoRequest.getUserUuid());
        usrCertificationInfo.setDisabled(0);
        usrCertificationInfo.setCertificationResult(CertificationResultEnum.AUTH_SUCCESS.getType());
        String offNo = this.sysParamService.getSysParamValue(SysParamContants.USER_CERTIFICATION_ON);
        String[] list = offNo.split("#");
        for (String type : list) {
            usrCertificationInfo.setCertificationType(Integer.parseInt(type));
            List<UsrCertificationInfo> usrCertificationInfoList = this.usrCertificationInfoDao.scan(usrCertificationInfo);
            if (usrCertificationInfoList.isEmpty()) {
                return false;
            }
        }
        //???????????
        this.updateOrderStep(userSubmitCerInfoRequest.getOrderNo(),userSubmitCerInfoRequest.getUserUuid(), OrdStepTypeEnum.CHECK_INFO.getType());
        return true;
    }

    /**
     * ??mango
     * @param data
     * @param orderNo
     * @param userUuid
     * @param type
     * @param status
     */
    public void add(Object data, String orderNo, String userUuid, OrdStepTypeEnum type, int status) {
        Date date = new Date();
        OrderUserDataMongo orderUserData = new OrderUserDataMongo();
        orderUserData.setCreateTime(date);
        orderUserData.setUpdateTime(date);
        orderUserData.setUserUuid(userUuid);
        orderUserData.setOrderNo(orderNo);
        orderUserData.setInfoType(type.getType());
        orderUserData.setData(data);
        orderUserData.setStatus(status);
        this.orderUserDataDao.insert(orderUserData);
    }

    /**
     * ??false???mango???????????
     * ??true???mango????????mango
     * @param orderNo
     * @param typeEnum
     * @return
     */
    public Boolean getMangoOrderData(String orderNo,OrdStepTypeEnum typeEnum){
        Boolean flag = true;
        OrderUserDataMongo orderUserData = new OrderUserDataMongo();
        orderUserData.setOrderNo(orderNo);
        orderUserData.setInfoType(typeEnum.getType());
        orderUserData.setStatus(1);
        List<OrderUserDataMongo> orderUserDataMongoList = this.orderUserDataDao.find(orderUserData);
        if (!orderUserDataMongoList.isEmpty()){
            flag = false;
        }
        return flag;
    }

    /**
     * 通过类型获得数据
     * @param orderNo
     * @param typeEnum
     * @return
     */
    public OrderUserDataMongo getMangoOrderDataByType(String orderNo,OrdStepTypeEnum typeEnum){
        OrderUserDataMongo orderUserData = new OrderUserDataMongo();
        orderUserData.setOrderNo(orderNo);
        orderUserData.setInfoType(typeEnum.getType());
        orderUserData.setStatus(1);
        List<OrderUserDataMongo> orderUserDataMongoList = this.orderUserDataDao.find(orderUserData);
        if (orderUserDataMongoList.isEmpty()){
            return null;
        }
        return orderUserDataMongoList.get(0);
    }

    /**
     * ???? ????????????????mango
     * @param userUuid
     * @param orderNo
     */
    @Transactional
    public void savaOrderInfoToMango(String userUuid,String orderNo) throws Exception{
        //?????mango??????????????
        UsrUser usrUser = this.usrDao.getUserInfoByIdIgnoreDisable(userUuid);
        if (usrUser == null) {
            log.warn("User id  {} for order {} not found in database", userUuid, orderNo);
        }
        //??????
        if (getMangoOrderData(orderNo, OrdStepTypeEnum.IDENTITY)){
            Object identityData = this.getIdentityInfo(userUuid, false);
            this.add(identityData,orderNo,userUuid, OrdStepTypeEnum.IDENTITY,1);
        }
        //1?? 2???
        if (usrUser.getUserRole() == 1){
            //????
            if (getMangoOrderData(orderNo, OrdStepTypeEnum.BASIC_INFO)){
                Object studentBaseData = this.getStudentBaseInfo(userUuid);
                this.add(studentBaseData,orderNo,userUuid, OrdStepTypeEnum.BASIC_INFO,1);
            }
            if (getMangoOrderData(orderNo, OrdStepTypeEnum.WORK_INFO)){
                Object schoolData = this.getStudentSchoolInfo(userUuid);
                this.add(schoolData,orderNo,userUuid, OrdStepTypeEnum.WORK_INFO,1);
            }
        }else if (usrUser.getUserRole() == 2){
            //?????
            if (getMangoOrderData(orderNo, OrdStepTypeEnum.BASIC_INFO)){
                Object workBaseData = this.getWorkBaseInfo(userUuid);
                this.add(workBaseData,orderNo,userUuid, OrdStepTypeEnum.BASIC_INFO,1);
            }
            if (getMangoOrderData(orderNo, OrdStepTypeEnum.WORK_INFO)){
                Object workData = this.getUsrWorkInfo(userUuid);
                this.add(workData,orderNo,userUuid, OrdStepTypeEnum.WORK_INFO,1);
            }
        }
        //?????
        if (getMangoOrderData(orderNo, OrdStepTypeEnum.CONTACT_INFO)){
            Object linkManData = this.getLinkManInfo(userUuid);
            this.add(linkManData,orderNo,userUuid, OrdStepTypeEnum.CONTACT_INFO,1);
        }
        BaseRequest request = new BaseRequest();
        request.setUserUuid(userUuid);
        //????
        if (getMangoOrderData(orderNo, OrdStepTypeEnum.CHECK_INFO)){
           Object checkData = this.usrAttachmentInfoDao.getCheckAttachmenInfoByUserUuid(userUuid);
            this.add(checkData,orderNo,userUuid,OrdStepTypeEnum.CHECK_INFO,1);
        }
        //????
        if (getMangoOrderData(orderNo, OrdStepTypeEnum.EXTRA_INFO)){
            Object otherInfo = this.usrService.initSupplementInfo(request);
            this.add(otherInfo,orderNo,userUuid, OrdStepTypeEnum.EXTRA_INFO,1);
        }

    }


    /**
     * 删除归档数据
//     * @param userUuid
     * @param orderNo
     * @throws Exception
     */
    @Transactional
    public void deleteOrderInfoToMango(String orderNo) throws Exception{

        OrderUserDataMongo orderUserDataMongo ;
        orderUserDataMongo = getMangoOrderDataByType(orderNo, OrdStepTypeEnum.IDENTITY);
        if (orderUserDataMongo != null) {
            orderUserDataMongo.setStatus(0);
            this.orderUserDataDao.updateById(orderUserDataMongo);
        }
        orderUserDataMongo = getMangoOrderDataByType(orderNo, OrdStepTypeEnum.BASIC_INFO);
        if (orderUserDataMongo != null) {
            orderUserDataMongo.setStatus(0);
            this.orderUserDataDao.updateById(orderUserDataMongo);
        }
        orderUserDataMongo = getMangoOrderDataByType(orderNo, OrdStepTypeEnum.WORK_INFO);
        if (orderUserDataMongo != null) {
            orderUserDataMongo.setStatus(0);
            this.orderUserDataDao.updateById(orderUserDataMongo);
        }
        orderUserDataMongo = getMangoOrderDataByType(orderNo, OrdStepTypeEnum.CONTACT_INFO);
        if (orderUserDataMongo != null) {
            orderUserDataMongo.setStatus(0);
            this.orderUserDataDao.updateById(orderUserDataMongo);
        }
        orderUserDataMongo = getMangoOrderDataByType(orderNo, OrdStepTypeEnum.CHECK_INFO);
        if (orderUserDataMongo != null) {
            orderUserDataMongo.setStatus(0);
            this.orderUserDataDao.updateById(orderUserDataMongo);
        }
        orderUserDataMongo = getMangoOrderDataByType(orderNo, OrdStepTypeEnum.EXTRA_INFO);
        if (orderUserDataMongo != null) {
            orderUserDataMongo.setStatus(0);
            this.orderUserDataDao.updateById(orderUserDataMongo);
        }
    }
}
