package com.yqg.service.externalChannel.service;

import com.yqg.common.enums.order.OrdStepTypeEnum;
import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.common.enums.user.CertificationResultEnum;
import com.yqg.common.enums.user.UsrAttachmentEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.externalChannel.dao.ExternalOrderRelationDao;
import com.yqg.externalChannel.entity.ExternalOrderRelation;
import com.yqg.mongo.dao.ExternalChannelDataDal;
import com.yqg.mongo.entity.ExternalChannelDataMongo;
import com.yqg.order.dao.OrdBlackDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.externalChannel.request.Cash2AdditionalInfoParam;
import com.yqg.service.externalChannel.request.Cash2ExtralInfo;
import com.yqg.service.externalChannel.response.Cash2Response;
import com.yqg.service.externalChannel.transform.AdditionalInfoExtractor;
import com.yqg.service.externalChannel.utils.Cash2ResponseBuiler;
import com.yqg.service.externalChannel.utils.Cash2ResponseCode;
import com.yqg.service.order.OrdService;
import com.yqg.service.user.model.UsrStudentBaseInfoModel;
import com.yqg.service.user.model.UsrWorkBaseInfoModel;
import com.yqg.service.user.request.*;
import com.yqg.service.user.service.UserLinkManService;
import com.yqg.service.user.service.UsrBaseInfoService;
import com.yqg.service.user.service.UsrService;
import com.yqg.user.dao.UsrCertificationDao;
import com.yqg.user.dao.UsrFaceVerifyResultDao;
import com.yqg.user.entity.UsrCertificationInfo;
import com.yqg.user.entity.UsrFaceVerifyResult;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/6
 * @Email zengxiangcai@yishufu.com
 * 扩展信息
 ****/

@Service
@Slf4j
public class AdditionalInfoService {

    @Autowired
    private ExternalChannelDataService externalChannelDataService;

    @Autowired
    private AdditionalInfoExtractor additionalInfoExtractor;

    @Autowired
    private UsrService usrService;

    @Autowired
    private OrdService ordService;

    @Autowired
    private UsrBaseInfoService usrBaseInfoService;

    @Autowired
    private UsrFaceVerifyResultDao usrFaceVerifyResultDao;

    @Autowired
    private UserLinkManService userLinkManService;

    @Autowired
    private UsrCertificationDao usrCertificationDao;

    public Cash2Response addAdditionalInfo(Cash2AdditionalInfoParam additionalInfo)
            throws Exception {

        ExternalOrderRelation relation = externalChannelDataService
                .getExternalOrderRelationByExternalOrderNo(additionalInfo.getOrderInfo().getOrderNo());

        //图片保存处理
        additionalInfoExtractor.processImage(additionalInfo);

        UsrUser userInfo = usrService.getUserByUuid(relation.getUserUuid());
        OrdOrder orderInfo = ordService.getOrderByOrderNo(relation.getOrderNo());

        UsrStudentBaseInfoRequest studentBaseInfo = null;
        UsrSchoolInfoRequest schoolInfoRequest = null;
        UsrWorkBaseInfoRequest workerBaseInfo = null;
        UsrWorkInfoRequest workerInfo = null;
        if (userInfo.getUserRole() == 1) {
            studentBaseInfo = additionalInfoExtractor.fetchStudentBaseInfo(additionalInfo);
            schoolInfoRequest = additionalInfoExtractor.fetchStudentSchoolInfo(additionalInfo);
        } else {
            workerBaseInfo = additionalInfoExtractor.fetchWorkBaseInfo(additionalInfo);
            workerInfo = additionalInfoExtractor.fetchWorkInfo(additionalInfo);
        }

        UsrSubmitSupplementInfoRequest supplementInfoRequest = additionalInfoExtractor.fetchSupplementInfo(additionalInfo.getApplyDetail());
        LinkManRequest linkManRequest = additionalInfoExtractor.fetchContactUserInfo(additionalInfo);
        linkManRequest.setOrderNo(orderInfo.getUuid());
        linkManRequest.setUserUuid(userInfo.getUuid());

//        UploadContactRequest contactList = additionalInfoExtractor.fetchContactList(additionalInfo);
//        UploadMsgsRequest shortMsgList = additionalInfoExtractor.fetchMsgList(additionalInfo);
//        UploadCallRecordsRequest callRecordList = additionalInfoExtractor.fetchCallRecordList(additionalInfo);
//        UploadAppsRequest installedAppRequest = additionalInfoExtractor.fetchInstalledAppList(additionalInfo);


//        //保存下单位置
//        OrdRequest orderRequest = additionalInfoExtractor.fetchOrderRequestInfo(additionalInfo);
//        orderRequest.setUserUuid(userInfo.getUuid());
//        UsrAddressDetail usrAddressDetail = ordService.saveUsrAddressDetail(orderRequest);
//        //更新下单位置
//        orderInfo.setOrderPositionId(usrAddressDetail.getUuid());
//        ordService.updateOrder(orderInfo);
//
//        //保存设备信息
//        ordService.addUserMobileDeviceInfo(orderRequest, orderInfo.getUuid());

        //保存学生、工作人员基本信息
        if (userInfo.getUserRole() == 1) {
            //学生
            studentBaseInfo.setUserUuid(userInfo.getUuid());
            studentBaseInfo.setOrderNo(orderInfo.getUuid());
            schoolInfoRequest.setUserUuid(userInfo.getUuid());
            schoolInfoRequest.setOrderNo(orderInfo.getUuid());
            //查询基础信息接口推送的数据
            UsrStudentBaseInfoModel dbStudentBaseInfo = usrBaseInfoService.getStudentBaseInfo(userInfo.getUuid());
            studentBaseInfo.setAcademic(dbStudentBaseInfo.getAcademic());
            studentBaseInfo.setBorrowUse(dbStudentBaseInfo.getBorrowUse());
            usrBaseInfoService.addOrUpdateStudentBaseInfo(studentBaseInfo);
            usrBaseInfoService.addOrUpdateStudentSchoolInfo(schoolInfoRequest);
        } else {
            //工作人员
            workerBaseInfo.setUserUuid(userInfo.getUuid());
            workerBaseInfo.setOrderNo(orderInfo.getUuid());
            workerInfo.setUserUuid(userInfo.getUuid());
            workerInfo.setOrderNo(orderInfo.getUuid());
            //查询基础信息接口推送的数据
            UsrWorkBaseInfoModel dbWrokBaseInfo = usrBaseInfoService.getWorkBaseInfo(userInfo.getUuid());
            workerBaseInfo.setAcademic(dbWrokBaseInfo.getAcademic());
            workerBaseInfo.setMaritalStatus(dbWrokBaseInfo.getMaritalStatus());
            workerBaseInfo.setChildrenAmount(dbWrokBaseInfo.getChildrenAmount());
            workerBaseInfo.setBorrowUse(dbWrokBaseInfo.getBorrowUse());
            workerBaseInfo.setReligion(dbWrokBaseInfo.getReligion());
            usrBaseInfoService.addWorkBaseInfo(workerBaseInfo);
            usrBaseInfoService.addUsrWorkInfo(workerInfo);
        }


        if (!StringUtils.isEmpty(additionalInfo.getApplyDetail().getWhatsApp())){

            UsrCertificationInfo certificationInfo = new UsrCertificationInfo();
            certificationInfo.setUserUuid(userInfo.getUuid());
            certificationInfo.setCertificationType(CertificationEnum.WHATS_APP.getType());
            List<UsrCertificationInfo> scanList = this.usrCertificationDao.scan(certificationInfo);

            Map<String, String> data = new HashMap<>();
            data.put("account", additionalInfo.getApplyDetail().getWhatsApp());
            String wpJson =  JsonUtils.serialize(data);

            if (CollectionUtils.isEmpty(scanList)) {
                certificationInfo.setCertificationData(wpJson);
                certificationInfo.setCertificationResult(CertificationResultEnum.NOT_AUTH.getType());
                this.usrCertificationDao.insert(certificationInfo);

            } else {
                UsrCertificationInfo update = scanList.get(0);
                update.setCertificationData(wpJson);
                this.usrCertificationDao.update(update);
            }
        }

        //记录联系人信息
         userLinkManService.addEmergencyLinkmans(linkManRequest, false);
        //更新订单步骤到联系人信息
        usrBaseInfoService.updateOrderStep(linkManRequest.getOrderNo(), linkManRequest.getUserUuid(), OrdStepTypeEnum.CONTACT_INFO.getType());

//        //记录抓取的数据
//        contactList.setOrderNo(orderInfo.getUuid());
//        contactList.setUserUuid(userInfo.getUuid());
//        installedAppRequest.setOrderNo(orderInfo.getUuid());
//        installedAppRequest.setUserUuid(userInfo.getUuid());
//        shortMsgList.setOrderNo(orderInfo.getUuid());
//        shortMsgList.setUserUuid(userInfo.getUuid());
//        callRecordList.setOrderNo(orderInfo.getUuid());
//        callRecordList.setUserUuid(userInfo.getUuid());
//        uploadInfoService.uploadContacts(contactList);
//        uploadInfoService.uploadApps(installedAppRequest);
//        uploadInfoService.uploadMsgs(shortMsgList);
//        uploadInfoService.uploadCallRecords(callRecordList);


        String faceImgUrl = additionalInfo.getApplyDetail().getImgList().get(0);
//        UsrSubmitCerInfoRequest request = new UsrSubmitCerInfoRequest();
//        request.setCertificationType(CertificationEnum.FACE_IDENTITY.getType());
//        request.setCertificationData(
//                Base64Utils.getBase64ImgFromRemoteUrl(faceImgUrl));
//        request.setOrderNo(relation.getOrderNo());
//        request.setUserUuid(relation.getUserUuid());
        // 保存人脸识别的附件
        usrService.insertAttachment(userInfo.getUuid(), faceImgUrl, String.valueOf(UsrAttachmentEnum.FACE.getType()));
        // 保存人脸识别认证 认证信息
        usrService.dealWithCertificationInfo(CertificationEnum.FACE_IDENTITY.getType(), userInfo.getUuid(), CertificationResultEnum.AUTH_SUCCESS.getType
                ());
        //更新验证步骤？ CHECK_INFO
        //保存人脸识别人数
        addFaceVerifyResult(additionalInfo.getApplyDetail().getConfidence(), userInfo.getUuid(),orderInfo.getUuid());

        //usrService.submitCertificationInfo(request);

        //税卡
        if (!StringUtils.isEmpty(additionalInfo.getApplyDetail().getTaxCardNumber())) {
            UsrSubmitCerInfoRequest request = new UsrSubmitCerInfoRequest();
            request.setCertificationType(CertificationEnum.STEUERKARTED.getType());
            Map<String, String> data = new HashMap<>();
            data.put("account", additionalInfo.getApplyDetail().getTaxCardNumber());
            request.setCertificationData(JsonUtils.serialize(data));
            request.setOrderNo(relation.getOrderNo());
            request.setUserUuid(relation.getUserUuid());
            usrService.submitCertificationInfo(request);
        }
        //保存补充信息
        supplementInfoRequest.setUserUuid(userInfo.getUuid());
        usrService.submitSupplementInfo(supplementInfoRequest);



        return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.CODE_OK_1);
    }


    /***
     * 放到订单确认后面的信息
     * @param extralInfo
     * @return
     */
    public Cash2Response addExtralInfo(Cash2ExtralInfo extralInfo) throws ServiceException {
        ExternalOrderRelation relation = externalChannelDataService
                .getExternalOrderRelationByExternalOrderNo(extralInfo.getOrderNo());
        if(relation==null){
            return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.ORDER_NO_ERROR);
        }

        additionalInfoExtractor.processExtralImage(extralInfo);

        if(!CollectionUtils.isEmpty(extralInfo.getAttachmentInfoList())){
            //工作证明
            UsrSubmitSupplementInfoRequest supplementInfoRequest = new UsrSubmitSupplementInfoRequest();
            supplementInfoRequest.setUserUuid(relation.getUserUuid());
            supplementInfoRequest.setOrderNo(relation.getOrderNo());

            List<Map<String, String>> urlList  = extralInfo.getAttachmentInfoList().stream().map(elem->{
                    Map<String, String> attachmentUrls = new HashMap<>();
                    attachmentUrls.put("url", elem.getImgUrl());
                    attachmentUrls.put("type", String.valueOf(UsrAttachmentEnum.valueOf(elem.getType()).getType()));
                    return attachmentUrls;
            }).collect(Collectors.toList());

            supplementInfoRequest.setImageUrls(JsonUtils.serialize(urlList));
            usrService.submitSupplementInfo(supplementInfoRequest);
        }

        return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.CODE_OK_1);
    }


    private void addFaceVerifyResult(BigDecimal score, String userUuid,String orderNo) {
        try {
            UsrFaceVerifyResult scan = new UsrFaceVerifyResult();
            scan.setDisabled(0);
            scan.setUserUuid(userUuid);
            scan.setOrderNo(orderNo);
            scan.setChannel("face++");
            List<UsrFaceVerifyResult>scanList = usrFaceVerifyResultDao.scan(scan);
            if (CollectionUtils.isEmpty(scanList)){

                scan.setScore(score);
                scan.setCreateTime(new Date());
                scan.setUpdateTime(new Date());
                scan.setUuid(UUIDGenerateUtil.uuid().replaceAll("-", ""));
                usrFaceVerifyResultDao.insert(scan);

            }else {

                UsrFaceVerifyResult update = scanList.get(0);
                update.setScore(score);
                update.setUpdateTime(new Date());
                usrFaceVerifyResultDao.update(update);
            }
        } catch (Exception e) {
            log.error("add face verify result error,userUuid=" + userUuid, e);
        }
    }

    @Autowired
    private ExternalOrderRelationDao externalOrderRelationDao;

    @Autowired
    private ExternalChannelDataDal externalChannelDataDal;

    @Autowired
    private OrdBlackDao ordBlackDao;

    public void handleErrorLinkmanInfo(){
      List<ExternalOrderRelation> errorOrders = externalOrderRelationDao.getErrorOrders();
      for(ExternalOrderRelation order: errorOrders){
          try{
              log.info("start process order: "+order.getOrderNo());
              //查询最新数据
              ExternalChannelDataMongo search = new ExternalChannelDataMongo();
              search.setExternalOrderNo(order.getExternalOrderNo());
              search.setRequestUri("/external/cash2/additional-info");
              List<ExternalChannelDataMongo> searchResultList = externalChannelDataDal.find(search);
              if(CollectionUtils.isEmpty(searchResultList)){
                  log.info("no additional info,orderNo: "+order.getOrderNo());
                  continue;
              }
              ExternalChannelDataMongo latestResult =
                      searchResultList.stream().max(Comparator.comparing(ExternalChannelDataMongo::getCreateTime)).get();
              String additionalInfo= latestResult.getDecryptedText();
              Cash2AdditionalInfoParam param = JsonUtils.deserialize(additionalInfo,Cash2AdditionalInfoParam.class);
              LinkManRequest linkManRequest = additionalInfoExtractor.fetchContactUserInfo(param);
              linkManRequest.setUserUuid(order.getUserUuid());
              linkManRequest.setOrderNo(order.getOrderNo());
              if (CollectionUtils.isEmpty(linkManRequest.getLinkmanList())) {
                  log.info("the linkman is empty,orderNo: {} ,externalOrderNo: {}", order.getOrderNo(), order.getExternalOrderNo());
                  continue;
              }
              //更新联系人信息
              //disabled 原来的
              ordBlackDao.updateUsrLinkmanInfo(order.getUserUuid());

              //disabled掉原来联系人信息
              // log.info("linkman request: "+JsonUtils.serialize(linkManRequest));
              userLinkManService.addEmergencyLinkmans(linkManRequest, false);




              //更新订单拒绝remark为："联系人出错重新处理"
              ordBlackDao.updateBlackInfo(order.getOrderNo(),"联系人出错需要重跑-2018-12-18");
          }catch (Exception e){
              log.error("error in order: "+ order.getOrderNo(),e);
          }
      }
      log.info("finished....");
    }
}


