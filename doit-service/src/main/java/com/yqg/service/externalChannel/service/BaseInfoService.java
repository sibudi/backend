package com.yqg.service.externalChannel.service;

import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.models.LoginSession;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.externalChannel.entity.ExternalOrderRelation;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.externalChannel.enums.ExternalChannelEnum;
import com.yqg.service.externalChannel.request.Cash2BaseInfo;
import com.yqg.service.externalChannel.request.Cash2BaseInfo.Cash2UserType;
import com.yqg.service.externalChannel.response.Cash2H5ContractResponse;
import com.yqg.service.externalChannel.transform.BaseInfoExtractor;
import com.yqg.service.order.OrdService;
import com.yqg.service.order.UploadInfoService;
import com.yqg.service.order.request.OrdRequest;
import com.yqg.service.order.request.UploadAppsRequest;
import com.yqg.service.order.request.UploadCallRecordsRequest;
import com.yqg.service.order.request.UploadContactRequest;
import com.yqg.service.order.request.UploadMsgsRequest;
import com.yqg.service.order.response.OrderOrderResponse;
import com.yqg.service.user.request.SaveUserPhotoRequest;
import com.yqg.service.user.request.UsrContactInfoRequest;
import com.yqg.service.user.request.UsrIdentityInfoRequest;
import com.yqg.service.user.request.UsrRequst;
import com.yqg.service.user.request.UsrRolesRequest;
import com.yqg.service.user.request.UsrSchoolInfoRequest;
import com.yqg.service.user.request.UsrStudentBaseInfoRequest;
import com.yqg.service.user.request.UsrWorkBaseInfoRequest;
import com.yqg.service.user.request.UsrWorkInfoRequest;
import com.yqg.service.user.service.UsrBaseInfoService;
import com.yqg.service.user.service.UsrService;
import com.yqg.system.dao.SysAppH5Dao;
import com.yqg.system.entity.SysAppH5;
import com.yqg.system.entity.SysProduct;
import com.yqg.user.dao.UsrCertificationInfoDao;
import com.yqg.user.entity.UsrCertificationInfo;
import com.yqg.user.entity.UsrUser;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/6
 * @Email zengxiangcai@yishufu.com
 * 基本信息
 ****/

@Service
@Slf4j
public class BaseInfoService {

    @Autowired
    private BaseInfoExtractor baseInfoExtractor;

    @Autowired
    private UsrService usrService;

    @Autowired
    private OrdService ordService;

    @Autowired
    private UsrBaseInfoService usrBaseInfoService;

    @Autowired
    private UploadInfoService uploadInfoService;

    @Autowired
    private ExternalChannelDataService externalChannelDataService;

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private SysAppH5Dao sysAppH5Dao;
    @Autowired
    private OrdDao ordDao;

    @Autowired
    private UsrCertificationInfoDao usrCertificationInfoDao;

    @Transactional(rollbackFor = Exception.class)
    public void addBaseInfo(Cash2BaseInfo baseInfo) throws Exception {
        ExternalOrderRelation existOrderRelation = externalChannelDataService
                .getExternalOrderRelationByExternalOrderNo(baseInfo.getOrderInfo().getOrderNo());

        if (existOrderRelation != null) {
            //检查订单信息
            OrdOrder dbExistOrder = ordService.getOrderByOrderNo(existOrderRelation.getOrderNo());
            if (dbExistOrder != null && dbExistOrder.getStatus() != OrdStateEnum.SUBMITTING
                    .getCode()) {
                //订单已经存在而且非待提交状态
                throw new IllegalArgumentException("the exist order cannot modify");
            }

        }
        if (baseInfo.getAddInfo() != null && baseInfo.getAddInfo().getDeviceInfo() == null) {
            log.warn("the device info is empty");
            throw new IllegalArgumentException("invalid param");
        }
        //在做其他处理之前统一处理图片上传到本地服务
        baseInfoExtractor.processImage(baseInfo);

        //检查参数是否为空，格式是否正确
        //转为未Do-It各个接口需要的参数
        UsrRequst userInfo = baseInfoExtractor.fetchUserSignUpInfo(baseInfo);
        OrdRequest ordRequest = baseInfoExtractor.fetchOrderRequestInfo(baseInfo);
        UsrRolesRequest userRoleRequest = baseInfoExtractor.fetchUserRoleRequest(baseInfo);
        List<SaveUserPhotoRequest> userPhotoList = baseInfoExtractor
                .fetchUserPhotoRequest(baseInfo);
        UsrIdentityInfoRequest usrIdentityInfoRequest = baseInfoExtractor
                .fetchUserIdentityInfo(baseInfo);

        UsrStudentBaseInfoRequest studentBaseInfo = null;
        UsrSchoolInfoRequest schoolInfoRequest = null;
        UsrWorkBaseInfoRequest workerBaseInfo = null;
        UsrWorkInfoRequest workerInfo = null;
        if (baseInfo.getApplyDetail().getUserType() == Cash2UserType.Student.getCode()) {
            studentBaseInfo = baseInfoExtractor.fetchStudentBaseInfo(baseInfo);
            schoolInfoRequest = baseInfoExtractor.fetchStudentSchoolInfo(baseInfo);
        } else {
            workerBaseInfo = baseInfoExtractor.fetchWorkBaseInfo(baseInfo);
            workerInfo = baseInfoExtractor.fetchWorkInfo(baseInfo);
        }


        UploadContactRequest contactList = baseInfoExtractor.fetchContactList(baseInfo);
        UploadMsgsRequest shortMsgList = baseInfoExtractor.fetchMsgList(baseInfo);
        UploadCallRecordsRequest callRecordList = baseInfoExtractor.fetchCallRecordList(baseInfo);
        UploadAppsRequest installedAppRequest = baseInfoExtractor.fetchInstalledAppList(baseInfo);

        //判定用户是否可借[防止调用可接接口和推送基本新接口之间相隔很长时间]
        UsrUser searchInfo = new UsrUser();
        searchInfo.setMobileNumberDES(DESUtils.encrypt(userInfo.getMobileNumber()));
        List<UsrUser> userList = usrService.getUserInfo(searchInfo);
        String userUuid = null;
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
                    throw new IllegalArgumentException("the realName and id card cannot modify");
                }
            }
            userUuid = dbUser.getUuid();

            //用户做个登录处理[放款的时候有Do-It用户的特殊控制]
            userInfo.setUserUuid(userUuid);
            usrService.addUsrLoginHistory(userInfo);
        }

        //

        log.info("cashcash用户的id为"+userUuid);

        //调用下单接口
        ordRequest.setUserUuid(userUuid);


        // 判断借款次数
        String duefeeRate =  "0.192";

        //产品产品id，对应期限直接从配置表获取
        SysProduct sysProduct = ordService
                .getProductByAmountAndTermWithDuefeeRate(baseInfo.getOrderInfo().getApplicationAmount(),
                        baseInfo.getOrderInfo().getApplicationTerm(),duefeeRate);
        ordRequest.setProductUuid(sysProduct.getUuid());
        ordRequest.setOrderType(1);
        OrderOrderResponse orderResponse = ordService.toOrder(ordRequest, redisClient);

        //记录cashcash订单和Do-It订单的关联关系
        if (existOrderRelation == null) {
            externalChannelDataService
                    .addExternalOrderRelation(baseInfo.getOrderInfo().getOrderNo(),
                            orderResponse.getOrderNo(), userUuid,
                            ExternalChannelEnum.CASHCASH.name());
        } else {
            log.info("base info already received");
        }

        //选择角色
        userRoleRequest.setOrderNo(orderResponse.getOrderNo());
        userRoleRequest.setUserUuid(userUuid);
        if (dbUser != null && dbUser.getUserRole() != null && dbUser.getUserRole() != 0) {
            //角色已经选择
            //20181129,如果我们这边已有角色，更新角色数据
            log.info("update userRole param: {} ", JsonUtils.serialize(userRoleRequest));
            usrBaseInfoService.updateUserRoleForCashCash(userRoleRequest);
        } else {
            userRoleRequest.setOrderNo(orderResponse.getOrderNo());
            userRoleRequest.setUserUuid(userUuid);
            usrBaseInfoService.rolesChoose(userRoleRequest);
        }

        //保存身份图片
        for (SaveUserPhotoRequest elem : userPhotoList) {
            elem.setUserUuid(userUuid);
            usrBaseInfoService.saveUserPhoto(elem);
        }
        //身份校验接口

        UsrCertificationInfo usrCertificationInfo = new UsrCertificationInfo();
        usrCertificationInfo.setUserUuid(userUuid);
        usrCertificationInfo.setDisabled(0);
        usrCertificationInfo.setCertificationType(CertificationEnum.USER_IDENTITY.getType());
        //usrCertificationInfo.setCertificationResult(CertificationResultEnum.AUTH_SUCCESS.getType());
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
        //保存学生、工作人员基本信息
        if (baseInfo.getApplyDetail().getUserType() == Cash2UserType.Student.getCode()) {
            //学生
            studentBaseInfo.setUserUuid(userUuid);
            studentBaseInfo.setOrderNo(orderResponse.getOrderNo());
            schoolInfoRequest.setUserUuid(userUuid);
            schoolInfoRequest.setOrderNo(orderResponse.getOrderNo());
            usrBaseInfoService.addOrUpdateStudentBaseInfo(studentBaseInfo);
            //usrBaseInfoService.addOrUpdateStudentSchoolInfo(schoolInfoRequest);
        } else {
            //工作人员
            workerBaseInfo.setUserUuid(userUuid);
            workerBaseInfo.setOrderNo(orderResponse.getOrderNo());
            workerInfo.setUserUuid(userUuid);
            workerInfo.setOrderNo(orderResponse.getOrderNo());
            usrBaseInfoService.addWorkBaseInfo(workerBaseInfo);
            //usrBaseInfoService.addUsrWorkInfo(workerInfo);
        }

//        //记录联系人信息
//        contactUser.setOrderNo(orderResponse.getOrderNo());
//        contactUser.setUserUuid(userUuid);
//        usrBaseInfoService.addLinkManInfo(contactUser);
        //记录抓取的数据
        contactList.setOrderNo(orderResponse.getOrderNo());
        contactList.setUserUuid(userUuid);
        installedAppRequest.setOrderNo(orderResponse.getOrderNo());
        installedAppRequest.setUserUuid(userUuid);
        shortMsgList.setOrderNo(orderResponse.getOrderNo());
        shortMsgList.setUserUuid(userUuid);
        callRecordList.setOrderNo(orderResponse.getOrderNo());
        callRecordList.setUserUuid(userUuid);
        uploadInfoService.uploadContacts(contactList);
        uploadInfoService.uploadApps(installedAppRequest);
        uploadInfoService.uploadMsgs(shortMsgList);
        uploadInfoService.uploadCallRecords(callRecordList);
    }

    /***
     * 复借简化流程
     * @param baseInfo
     */
    @Transactional(rollbackFor = Exception.class)
    public void reBorrowing(Cash2BaseInfo baseInfo) throws Exception {

        ExternalOrderRelation existOrderRelation = externalChannelDataService
                .getExternalOrderRelationByExternalOrderNo(baseInfo.getOrderInfo().getOrderNo());

        if (existOrderRelation != null) {
            //检查订单信息
            OrdOrder dbExistOrder = ordService.getOrderByOrderNo(existOrderRelation.getOrderNo());
            if (dbExistOrder != null && dbExistOrder.getStatus() != OrdStateEnum.SUBMITTING
                    .getCode()) {
                //订单已经存在而且非待提交状态
                throw new IllegalArgumentException("the exist order cannot modify");
            }

        }

        //在做其他处理之前统一处理图片上传到本地服务
        baseInfoExtractor.processImage(baseInfo);

        //检查参数是否为空，格式是否正确
        //转为未Do-It各个接口需要的参数
        UsrRequst userInfo = baseInfoExtractor.fetchUserSignUpInfo(baseInfo);
        OrdRequest ordRequest = baseInfoExtractor.fetchOrderRequestInfo(baseInfo);

        //联系人信息
        UsrContactInfoRequest contactUser = baseInfoExtractor.fetchContactUserInfo(baseInfo);
        //通讯录
        UploadContactRequest contactList = baseInfoExtractor.fetchContactList(baseInfo);
        //短信
        UploadMsgsRequest shortMsgList = baseInfoExtractor.fetchMsgList(baseInfo);
        //通话记录
        UploadCallRecordsRequest callRecordList = baseInfoExtractor.fetchCallRecordList(baseInfo);
        //安装app
        UploadAppsRequest installedAppRequest = baseInfoExtractor.fetchInstalledAppList(baseInfo);

        //判定用户是否可借[防止调用可接接口和推送基本新接口之间相隔很长时间]
        UsrUser searchInfo = new UsrUser();
        searchInfo.setMobileNumberDES(DESUtils.encrypt(userInfo.getMobileNumber()));
        List<UsrUser> userList = usrService.getUserInfo(searchInfo);

        if (CollectionUtils.isEmpty(userList)) {
            throw new Exception("the param is not correct, without user but the is_reloan =1");
        }

        String userUuid = userList.get(0).getUuid();
        //调用下单接口
        ordRequest.setUserUuid(userUuid);

        // 判断借款次数
        String duefeeRate  = "0.192";

        //产品产品id，对应期限直接从配置表获取
        SysProduct sysProduct = ordService
                .getProductByAmountAndTermWithDuefeeRate(baseInfo.getOrderInfo().getApplicationAmount(),
                        baseInfo.getOrderInfo().getApplicationTerm(),duefeeRate);
        ordRequest.setProductUuid(sysProduct.getUuid());
        ordRequest.setOrderType(1);
        OrderOrderResponse orderResponse = ordService.toOrder(ordRequest, redisClient);

        //记录cashcash订单和Do-It订单的关联关系
        if (existOrderRelation == null) {
            externalChannelDataService
                    .addExternalOrderRelation(baseInfo.getOrderInfo().getOrderNo(),
                            orderResponse.getOrderNo(), userUuid,
                            ExternalChannelEnum.CASHCASH.name());
        } else {
            log.info("base info already received... reBorrowing");
        }

//        //记录联系人信息
//        contactUser.setOrderNo(orderResponse.getOrderNo());
//        contactUser.setUserUuid(userUuid);
//        usrBaseInfoService.addLinkManInfo(contactUser);
        //记录抓取的数据
        contactList.setOrderNo(orderResponse.getOrderNo());
        contactList.setUserUuid(userUuid);
        installedAppRequest.setOrderNo(orderResponse.getOrderNo());
        installedAppRequest.setUserUuid(userUuid);
        shortMsgList.setOrderNo(orderResponse.getOrderNo());
        shortMsgList.setUserUuid(userUuid);
        callRecordList.setOrderNo(orderResponse.getOrderNo());
        callRecordList.setUserUuid(userUuid);
        uploadInfoService.uploadContacts(contactList);
        uploadInfoService.uploadApps(installedAppRequest);
        uploadInfoService.uploadMsgs(shortMsgList);
        uploadInfoService.uploadCallRecords(callRecordList);

    }

    /**
     * 获取app中用到的url 列表
     */
    public List<Cash2H5ContractResponse> getH5Contract(RedisClient redisClient)
            throws ServiceException {

        String cacheStr = redisClient.get(RedisContants.CACHE_H5_CASE2_URL_LIST_KEY);
        if (cacheStr != null) {
            return JsonUtils.deserialize(cacheStr, List.class);
        }
        List<SysAppH5> appH5List = this.sysAppH5Dao.getCash2H5Contract();
        if (CollectionUtils.isEmpty(appH5List)) {
            log.info("没有找到app配置url列表");
            throw new ServiceException(ExceptionEnum.SYSTEM_APP_CONFIG_LIST_IS_NULL);
        }
        List<Cash2H5ContractResponse> sysH5UrlList = new ArrayList<>();
        for (SysAppH5 sysAppH5 : appH5List) {
            Cash2H5ContractResponse sysAppH5ModelSpec = new Cash2H5ContractResponse();
            sysAppH5ModelSpec.setUrlKey(sysAppH5.getUrlKey());
            sysAppH5ModelSpec.setUrlValue(sysAppH5.getUrlValue());
            sysAppH5ModelSpec.setUrlDesc(sysAppH5.getUrlDesc());
            sysH5UrlList.add(sysAppH5ModelSpec);
        }
        redisClient
                .set(RedisContants.CACHE_H5_CASE2_URL_LIST_KEY, JsonUtils.serialize(sysH5UrlList));
        return sysH5UrlList;
    }
}
