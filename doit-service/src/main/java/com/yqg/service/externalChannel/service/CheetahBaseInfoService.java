package com.yqg.service.externalChannel.service;

import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.order.OrdStepTypeEnum;
import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.common.enums.user.UsrBankCardBinEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.LoginSession;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.externalChannel.entity.ExternalOrderRelation;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.externalChannel.enums.ExternalChannelEnum;
import com.yqg.service.externalChannel.request.CheetahBaseInfo;
import com.yqg.service.externalChannel.request.CheetahOrdInfoEditRequest;
import com.yqg.service.externalChannel.response.CheetahResponse;
import com.yqg.service.externalChannel.transform.CheetahBaseInfoExtractor;
import com.yqg.service.externalChannel.utils.CheetahResponseBuilder;
import com.yqg.service.externalChannel.utils.CheetahResponseCode;
import com.yqg.service.order.OrdService;
import com.yqg.service.order.UploadInfoService;
import com.yqg.service.order.request.OrdRequest;
import com.yqg.service.order.request.UploadAppsRequest;
import com.yqg.service.order.request.UploadCallRecordsRequest;
import com.yqg.service.order.request.UploadMsgsRequest;
import com.yqg.service.order.response.OrderOrderResponse;
import com.yqg.service.user.request.*;
import com.yqg.service.user.service.UserLinkManService;
import com.yqg.service.user.service.UsrBankService;
import com.yqg.service.user.service.UsrBaseInfoService;
import com.yqg.service.user.service.UsrService;
import com.yqg.system.dao.SysProductDao;
import com.yqg.system.entity.SysProduct;
import com.yqg.user.dao.UsrBankDao;
import com.yqg.user.dao.UsrCertificationInfoDao;
import com.yqg.user.entity.UsrBank;
import com.yqg.user.entity.UsrCertificationInfo;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Author: tonggen
 * Date: 2018/12/27
 * time: 5:53 PM
 */
@Service
@Slf4j
public class CheetahBaseInfoService {

    @Autowired
    private ExternalChannelDataService externalChannelDataService;

    @Autowired
    private CheetahBaseInfoExtractor baseInfoExtractor;

    @Autowired
    private OrdService ordService;

    @Autowired
    private UsrService usrService;

    @Autowired
    private UsrBaseInfoService usrBaseInfoService;

    @Autowired
    private UploadInfoService uploadInfoService;

    @Autowired
    private UsrCertificationInfoDao usrCertificationInfoDao;

    @Autowired
    private SysProductDao sysProductDao;

    @Autowired
    private UsrBankDao usrBankDao;

    @Autowired
    private UserLinkManService userLinkManService;

    @Autowired
    private UsrBankService usrBankService;

    @Autowired
    private RedisClient redisClient;

    @Transactional(rollbackFor = Exception.class)
    public void addBaseInfo(CheetahBaseInfo baseInfo) throws Exception {
        ExternalOrderRelation existOrderRelation = externalChannelDataService
                .getExternalOrderRelationByExternalOrderNo(baseInfo.getOrderInfo().getId());

        if (existOrderRelation != null) {
            //检查订单信息
            OrdOrder dbExistOrder = ordService.getOrderByOrderNo(existOrderRelation.getOrderNo());
            if (dbExistOrder != null && dbExistOrder.getStatus() != OrdStateEnum.SUBMITTING
                    .getCode()) {
                //订单已经存在而且非待提交状态
                throw new IllegalArgumentException("the exist order cannot modify");
            }
        }
        if (baseInfo.getDeviceInfo() == null) {
            log.warn("the device info is empty");
            throw new IllegalArgumentException("invalid param");
        }
        //在做其他处理之前统一处理图片上传到本地服务
//        baseInfoExtractor.processImage(baseInfo);

        //检查参数是否为空，格式是否正确
        //转为未Do-It各个接口需要的参数
        UsrRequst userInfo = baseInfoExtractor.fetchUserSignUpInfo(baseInfo);
        OrdRequest ordRequest = baseInfoExtractor.fetchOrderRequestInfo(baseInfo);
        UsrRolesRequest userRoleRequest = baseInfoExtractor.fetchUserRoleRequest();//只有工作的
//        List<SaveUserPhotoRequest> userPhotoList = baseInfoExtractor
//                .fetchUserPhotoRequest(baseInfo);
        UsrIdentityInfoRequest usrIdentityInfoRequest = baseInfoExtractor
                .fetchUserIdentityInfo(baseInfo);
        UsrWorkBaseInfoRequest workerBaseInfo = baseInfoExtractor.fetchWorkBaseInfo(baseInfo);
        UsrWorkInfoRequest workerInfo = baseInfoExtractor.fetchWorkInfo(baseInfo);
        UploadMsgsRequest shortMsgList = baseInfoExtractor.fetchMsgList(baseInfo);
        UploadCallRecordsRequest callRecordList = baseInfoExtractor.fetchCallRecordList(baseInfo);
        UploadAppsRequest installedAppRequest = baseInfoExtractor.fetchInstalledAppList(baseInfo);

        //判定用户是否可借[防止调用可接接口和推送基本新接口之间相隔很长时间]
        UsrUser searchInfo = new UsrUser();
        searchInfo.setMobileNumberDES(DESUtils.encrypt(userInfo.getMobileNumber()));
        List<UsrUser> userList = usrService.getUserInfo(searchInfo);
        String userUuid;
        UsrUser dbUser = null;
        boolean needUpdateUser = false;
        if (CollectionUtils.isEmpty(userList)) {
            //无用户注册用户
            LoginSession loginSession = usrService.logup(userInfo);
            userUuid = loginSession.getUserUuid();
            //更新用户名身份证
            needUpdateUser = true;
        } else {
            //有用户，更新用户信息
            dbUser = userList.get(0);
            if (StringUtils.isEmpty(dbUser.getIdCardNo()) || StringUtils
                    .isEmpty(dbUser.getRealName())) {
                needUpdateUser = true;
            } else {
                if (!usrIdentityInfoRequest.getName().toUpperCase().equals(dbUser.getRealName().toUpperCase())
                        ) {
                    throw new IllegalArgumentException("the cheetah , the realName and id card cannot modify");
                }
            }
            userUuid = dbUser.getUuid();

            //用户做个登录处理[放款的时候有Do-It用户的特殊控制]
            userInfo.setUserUuid(userUuid);
            usrService.addUsrLoginHistory(userInfo);
        }
        log.info("cheetah用户的id为" + userUuid);

        //调用下单接口
        ordRequest.setUserUuid(userUuid);
        //产品产品id，对应期限直接从配置表获取
        if (baseInfo.getOrderInfo().getProductId() == 0) {
            throw new Exception("without product configuration");
        }
        SysProduct sysProduct = new SysProduct();
        sysProduct.setId(baseInfo.getOrderInfo().getProductId());
        List<SysProduct> lists = sysProductDao.scan(sysProduct);
        if (CollectionUtils.isEmpty(lists)) {
            throw new Exception("without product configuration");
        }
        ordRequest.setProductUuid(lists.get(0).getUuid());
        ordRequest.setOrderType(2);
        OrderOrderResponse orderResponse = ordService.toOrder(ordRequest, redisClient);

        //记录cheetah订单和doit订单的关联关系
        if (existOrderRelation == null) {
            externalChannelDataService
                    .addExternalOrderRelation(baseInfo.getOrderInfo().getId(),
                            orderResponse.getOrderNo(), userUuid,
                            ExternalChannelEnum.CHEETAH.name());
        } else {
            log.info("base info already received");
        }

        //选择角色
        userRoleRequest.setOrderNo(orderResponse.getOrderNo());
        userRoleRequest.setUserUuid(userUuid);
        if (dbUser != null && dbUser.getUserRole() != null && dbUser.getUserRole() != 0) {
            //角色已经选择
            log.info("update userRole param: {} ", JsonUtils.serialize(userRoleRequest));
            usrBaseInfoService.updateUserRoleForCashCash(userRoleRequest);
        } else {
            usrBaseInfoService.rolesChoose(userRoleRequest);
        }

        //保存身份图片
//        for (SaveUserPhotoRequest elem : userPhotoList) {
//            elem.setUserUuid(userUuid);
//            usrBaseInfoService.saveUserPhoto(elem);
//        }
        //身份校验接口

        UsrCertificationInfo usrCertificationInfo = new UsrCertificationInfo();
        usrCertificationInfo.setUserUuid(userUuid);
        usrCertificationInfo.setDisabled(0);
        usrCertificationInfo.setCertificationType(CertificationEnum.USER_IDENTITY.getType());
        List<UsrCertificationInfo> usrCertificationInfoList = this.usrCertificationInfoDao
                .scan(usrCertificationInfo);

        if (CollectionUtils.isEmpty(usrCertificationInfoList)) {
            usrIdentityInfoRequest.setOrderNo(orderResponse.getOrderNo());
            usrIdentityInfoRequest.setUserUuid(userUuid);
            usrBaseInfoService.advanceVerify(usrIdentityInfoRequest);
        }

        //更新用户身份信息
        if (needUpdateUser) {
            usrBaseInfoService.getAndUpdateUser(usrIdentityInfoRequest);
        }
        //工作人员
        workerBaseInfo.setUserUuid(userUuid);
        workerBaseInfo.setOrderNo(orderResponse.getOrderNo());
        workerInfo.setUserUuid(userUuid);
        workerInfo.setOrderNo(orderResponse.getOrderNo());
        usrBaseInfoService.addWorkBaseInfo(workerBaseInfo);
        usrBaseInfoService.addUsrWorkInfo(workerInfo);
//        }

//        //记录联系人信息
//        contactUser.setOrderNo(orderResponse.getOrderNo());
//        contactUser.setUserUuid(userUuid);
//        usrBaseInfoService.addLinkManInfo(contactUser);
        //记录抓取的数据
//        contactList.setOrderNo(orderResponse.getOrderNo());
//        contactList.setUserUuid(userUuid);
        installedAppRequest.setOrderNo(orderResponse.getOrderNo());
        installedAppRequest.setUserUuid(userUuid);
        shortMsgList.setOrderNo(orderResponse.getOrderNo());
        shortMsgList.setUserUuid(userUuid);
        callRecordList.setOrderNo(orderResponse.getOrderNo());
        callRecordList.setUserUuid(userUuid);
//        uploadInfoService.uploadContacts(contactList);
        uploadInfoService.uploadApps(installedAppRequest);
        uploadInfoService.uploadMsgs(shortMsgList);
        uploadInfoService.uploadCallRecords(callRecordList);

        //保存联系人
        LinkManRequest linkManRequest = baseInfoExtractor.fetchContactUserInfo(baseInfo);
        linkManRequest.setOrderNo(orderResponse.getOrderNo());
        linkManRequest.setUserUuid(userUuid);

        //记录联系人信息
        userLinkManService.addEmergencyLinkmans(linkManRequest, false);
        //更新订单步骤到联系人信息
        usrBaseInfoService.updateOrderStep(linkManRequest.getOrderNo(), linkManRequest.getUserUuid(), OrdStepTypeEnum.CONTACT_INFO.getType());

        //税卡
//        if (!StringUtils.isEmpty(additionalInfo.getApplyDetail().getTaxCardNumber())) {
//            UsrSubmitCerInfoRequest request = new UsrSubmitCerInfoRequest();
//            request.setCertificationType(CertificationEnum.STEUERKARTED.getType());
//            Map<String, String> data = new HashMap<>();
//            data.put("account", additionalInfo.getApplyDetail().getTaxCardNumber());
//            request.setCertificationData(JsonUtils.serialize(data));
//            request.setOrderNo(relation.getOrderNo());
//            request.setUserUuid(relation.getUserUuid());
//            usrService.submitCertificationInfo(request);
//        }
//        //保存补充信息
//        supplementInfoRequest.setUserUuid(userInfo.getUuid());
//        usrService.submitSupplementInfo(supplementInfoRequest);


        //银行卡绑卡
        log.info("====== start cheetahBankCardBin ======");
        cheetahBankCardBin(baseInfo, orderResponse.getOrderNo(), userUuid);

    }


    private void cheetahBankCardBin(CheetahBaseInfo baseInfo, String orderNo, String userUuid)
            throws ServiceException, ServiceExceptionSpec {
        CheetahBaseInfo.BankInfoBean bankInfo = baseInfo.getBankInfo();
        if (bankInfo != null && StringUtils.isNotEmpty(bankInfo.getBankCode())) {
            UsrBankRequest userBankRequest = new UsrBankRequest();
            userBankRequest.setThirdType(2);
            userBankRequest.setBankNumberNo(bankInfo.getAccountNumber());
            userBankRequest.setBankCardName(bankInfo.getAccountName());
            userBankRequest.setBankCode(bankInfo.getBankCode());
            userBankRequest.setOrderNo(orderNo);
            userBankRequest.setUserUuid(userUuid);
            usrBankService.bindBankCard(userBankRequest,redisClient);
            // 判断是否绑定成功
//            List<UsrBank> userBanks =  usrBankDao.getSuccess(userUuid);
//            for(UsrBank item:userBanks){
//                if(item.getStatus().equals(UsrBankCardBinEnum.SUCCESS.getType())||item.getStatus().equals(UsrBankCardBinEnum.FAILED.getType())){
//                    if (item.getThirdType().equals(2)) {
//                        // (0=未验证，1=待验证,2=成功,3=失败)',
//                        if(item.getStatus()==2){
//                            status = "1";
//                        }else if(item.getStatus()==3){
//                            status ="2";
//                        }
//                    }
//                }
//            }
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public CheetahResponse orderInfoEdit(CheetahOrdInfoEditRequest baseInfo) throws Exception {
        ExternalOrderRelation existOrderRelation = externalChannelDataService
                .getExternalOrderRelationByExternalOrderNo(baseInfo.getOrderInfo().getId());

        if (existOrderRelation == null) {
            log.error("cannot find orderInfo ,param={}", JsonUtils.serialize(baseInfo));
            return CheetahResponseBuilder.buildResponse(CheetahResponseCode.ORDER_NOT_EXIT_14001);
        }
        OrdOrder ordOrder = ordService.getOrderByOrderNo(existOrderRelation.getOrderNo());

        SaveUserPhotoRequest saveUserPhotoRequest = new SaveUserPhotoRequest();
        saveUserPhotoRequest.setUserUuid(ordOrder.getUserUuid());

        //保存身份图片
        if (baseInfo.getUserInfo() != null && StringUtils.isNotEmpty(baseInfo.getUserInfo().getIdFrontPhoto())) {
            saveUserPhotoRequest.setPhotoUrl(baseInfo.getUserInfo().getIdFrontPhoto());
            saveUserPhotoRequest.setPhotoType("0");
            usrBaseInfoService.saveUserPhoto(saveUserPhotoRequest);
        }
        //保存手持图片
        if (baseInfo.getUserInfo() != null && StringUtils.isNotEmpty(baseInfo.getUserInfo().getHandHeldPhoto())) {
            saveUserPhotoRequest.setPhotoUrl(baseInfo.getUserInfo().getHandHeldPhoto());
            saveUserPhotoRequest.setPhotoType("1");
            usrBaseInfoService.saveUserPhoto(saveUserPhotoRequest);
        }

        //绑卡失败重新绑卡
        if (baseInfo.getBankInfo() != null) {
            log.info("====== start cheetahBankCardAgain ======");

            UsrBankRequest req = new UsrBankRequest();
            req.setBankCardName(baseInfo.getUserInfo().getFullName());
            req.setBankCode(baseInfo.getBankInfo().getBankCode());
            req.setBankNumberNo(baseInfo.getBankInfo().getAccountNumber());
            req.setOrderNo(ordOrder.getUuid());
            req.setUserUuid(ordOrder.getUserUuid());
            req.setThirdType(1);
            //检查订单

            if (ordOrder.getStatus() == OrdStateEnum.LOAN_FAILD.getCode()
                    && "BANK_CARD_ERROR".equals(ordOrder.getRemark())) {
                //放款因为银行卡失败,调用重绑卡
                log.info("orderNo:{}, cheetahBankAagin loan error.", ordOrder.getUuid());
                usrBankService.changeOrderBankCard(req);
            } else {
                //检查订单对应银行卡
                UsrBank orderBank = usrBankService.getBankCardInfo(ordOrder.getUserBankUuid());
                if (orderBank.getStatus() == UsrBankCardBinEnum.FAILED.getType()) {
                    //bank card failed-->
                    usrBankService.rebindBankCardForFailed(req);
                }
            }
        }
        return CheetahResponseBuilder.buildResponse(CheetahResponseCode.CODE_OK_0).withData(null);

    }
}
