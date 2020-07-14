package com.yqg.service.externalChannel.transform;

import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.enums.user.UsrAttachmentEnum;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.service.externalChannel.request.Cash2AdditionalInfoParam;
import com.yqg.service.externalChannel.request.Cash2AdditionalInfoParam.ApplyDetail;
import com.yqg.service.externalChannel.request.Cash2AdditionalInfoParam.EmergencyContactRelationEnum;
import com.yqg.service.externalChannel.request.Cash2AdditionalInfoParam.JobPositionEnum;
import com.yqg.service.externalChannel.request.Cash2ExtralInfo;
import com.yqg.service.user.request.*;
import com.yqg.service.util.CustomEmojiUtils;
import com.yqg.user.entity.UsrAttachmentInfo;
import com.yqg.user.entity.UsrLinkManInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/*****
 * @Author zengxiangcai
 * Created at 2018/6/22
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Service
@Slf4j
// Used by AdditionalInfoService.java -> which used by Cash2AdditionalInfoController.java 
// API: /external/cash2/additional-info
public class AdditionalInfoExtractor {

    @Autowired
    private ImagePathService imagePathService;

    /****
     * 基本信息中图片相关的url都替换为自己图片服务器的数据
     * @param additionalInfo
     * @return
     */
    public boolean processImage(Cash2AdditionalInfoParam additionalInfo) {

        ApplyDetail applyDetail = additionalInfo.getApplyDetail();

        applyDetail.setStudentIdCardImage(
                imagePathService.getLocalFileUrl(applyDetail.getStudentIdCardImage(),
                        UsrAttachmentEnum.STUDENT_CARD)); //学生证照片
        applyDetail.setScholarshipCertificateImage(
                imagePathService.getLocalFileUrl(applyDetail.getScholarshipCertificateImage(),
                        UsrAttachmentEnum.SCHOLARSHIP)); //奖学金证书照片
        applyDetail.setEnglishCertificateImage(
                imagePathService.getLocalFileUrl(applyDetail.getEnglishCertificateImage(),
                        UsrAttachmentEnum.ENGLISH));//英语证书照片
        //schoolInfo.setComputerUrl();
        applyDetail.setCampusCardImage(
                imagePathService
                        .getLocalFileUrl(applyDetail.getCampusCardImage(), UsrAttachmentEnum.SCHOOL_CARD));//校园卡照片
        applyDetail
                .setOtherCertificateImage(
                        imagePathService.getLocalFileUrl(applyDetail.getOtherCertificateImage(),
                                UsrAttachmentEnum.OTHER_CERTIFICATION));//其他大赛照片

        applyDetail.setPayroll(imagePathService
                .getLocalFileUrl(applyDetail.getPayroll(), UsrAttachmentEnum.PAYROLL));//工资单
        applyDetail.setBankStatementImg(imagePathService
                .getLocalFileUrl(applyDetail.getBankStatementImg(),
                        UsrAttachmentEnum.BANK_CARD_RECORD));//银行流水
        applyDetail.setWorkProofImg(imagePathService
                .getLocalFileUrl(applyDetail.getWorkProofImg(), UsrAttachmentEnum.WORK_PROOF));//工作证明
        applyDetail.setDriverLicenseImage(imagePathService
                .getLocalFileUrl(applyDetail.getDriverLicenseImage(), UsrAttachmentEnum.SIM));//驾驶证


        //活体图片
        if (!CollectionUtils.isEmpty(additionalInfo.getApplyDetail().getImgList())) {
            String imgUrl = additionalInfo.getApplyDetail().getImgList().get(0);
            additionalInfo.getApplyDetail().getImgList().set(0, imagePathService.getLocalFileUrl(imgUrl, UsrAttachmentEnum.FACE));
        }

        if (!StringUtils.isEmpty(applyDetail.getFamilyCard())) {
            applyDetail.setFamilyCard(imagePathService
                    .getLocalFileUrl(applyDetail.getFamilyCard(), UsrAttachmentEnum.KK));
        }

        if (!StringUtils.isEmpty(applyDetail.getInsuranceCard())) {
            applyDetail.setInsuranceCard(imagePathService
                    .getLocalFileUrl(applyDetail.getInsuranceCard(), UsrAttachmentEnum.INSURANCE_CARD));
        }

        return true;
    }


    public boolean processExtralImage(Cash2ExtralInfo extralInfo) {
        if (CollectionUtils.isEmpty(extralInfo.getAttachmentInfoList())) {
            return false;
        }
        //过滤掉空数据
        List<Cash2ExtralInfo.AttachmentInfo> attachmentInfos = extralInfo.getAttachmentInfoList().stream().filter(elem -> !StringUtils.isEmpty(elem
                .getImgUrl())).collect(Collectors.toList());
        extralInfo.setAttachmentInfoList(attachmentInfos);
        if(CollectionUtils.isEmpty(attachmentInfos)){
            return false;
        }

        for (Cash2ExtralInfo.AttachmentInfo item : attachmentInfos) {
            item.setImgUrl(imagePathService.getLocalFileUrl(item.getImgUrl(), UsrAttachmentEnum.valueOf(item.getType())));
        }
        return true;
    }


    public UsrSubmitSupplementInfoRequest fetchSupplementInfo(ApplyDetail detail) {
        List<Map<String, String>> urlList = new ArrayList<>();
        Map<String, String> attachmentUrls = new HashMap<>();
        if (!StringUtils.isEmpty(detail.getStudentIdCardImage())) {
            attachmentUrls.put("url", detail.getStudentIdCardImage());
            attachmentUrls.put("type", String.valueOf(UsrAttachmentEnum.STUDENT_CARD.getType()));
            urlList.add(attachmentUrls);
        }

        if (!StringUtils.isEmpty(detail.getScholarshipCertificateImage())) {
            attachmentUrls = new HashMap<>();
            attachmentUrls.put("url", detail.getScholarshipCertificateImage());
            attachmentUrls.put("type", String.valueOf(UsrAttachmentEnum.SCHOLARSHIP.getType()));
            urlList.add(attachmentUrls);
        }

        if (!StringUtils.isEmpty(detail.getEnglishCertificateImage())) {
            attachmentUrls = new HashMap<>();
            attachmentUrls.put("url", detail.getEnglishCertificateImage());
            attachmentUrls.put("type", String.valueOf(UsrAttachmentEnum.ENGLISH.getType()));
            urlList.add(attachmentUrls);
        }

        if (!StringUtils.isEmpty(detail.getCampusCardImage())) {
            attachmentUrls = new HashMap<>();
            attachmentUrls.put("url", detail.getCampusCardImage());
            attachmentUrls.put("type", String.valueOf(UsrAttachmentEnum.SCHOOL_CARD.getType()));
            urlList.add(attachmentUrls);
        }

        if (!StringUtils.isEmpty(detail.getOtherCertificateImage())) {
            attachmentUrls = new HashMap<>();
            attachmentUrls.put("url", detail.getOtherCertificateImage());
            attachmentUrls.put("type", String.valueOf(UsrAttachmentEnum.OTHER_CERTIFICATION.getType()));
            urlList.add(attachmentUrls);
        }

        if (!StringUtils.isEmpty(detail.getPayroll())) {
            attachmentUrls = new HashMap<>();
            attachmentUrls.put("url", detail.getPayroll());
            attachmentUrls.put("type", String.valueOf(UsrAttachmentEnum.PAYROLL.getType()));
            urlList.add(attachmentUrls);
        }

        if (!StringUtils.isEmpty(detail.getBankStatementImg())) {
            attachmentUrls = new HashMap<>();
            attachmentUrls.put("url", detail.getBankStatementImg());
            attachmentUrls.put("type", String.valueOf(UsrAttachmentEnum.BANK_CARD_RECORD.getType()));
            urlList.add(attachmentUrls);
        }

        if (!StringUtils.isEmpty(detail.getWorkProofImg())) {
            attachmentUrls = new HashMap<>();
            attachmentUrls.put("url", detail.getWorkProofImg());
            attachmentUrls.put("type", String.valueOf(UsrAttachmentEnum.WORK_PROOF.getType()));
            urlList.add(attachmentUrls);
        }

        if (!StringUtils.isEmpty(detail.getDriverLicenseImage())) {
            attachmentUrls = new HashMap<>();
            attachmentUrls.put("url", detail.getDriverLicenseImage());
            attachmentUrls.put("type", String.valueOf(UsrAttachmentEnum.SIM.getType()));
            urlList.add(attachmentUrls);
        }
        if (!StringUtils.isEmpty(detail.getFamilyCard())) {
            attachmentUrls = new HashMap<>();
            attachmentUrls.put("url", detail.getFamilyCard());
            attachmentUrls.put("type", String.valueOf(UsrAttachmentEnum.KK.getType()));
            urlList.add(attachmentUrls);
        }
        if (!StringUtils.isEmpty(detail.getInsuranceCard())) {
            attachmentUrls = new HashMap<>();
            attachmentUrls.put("url", detail.getInsuranceCard());
            attachmentUrls.put("type", String.valueOf(UsrAttachmentEnum.INSURANCE_CARD.getType()));
            urlList.add(attachmentUrls);
        }
        UsrSubmitSupplementInfoRequest request = new UsrSubmitSupplementInfoRequest();
        request.setImageUrls(JsonUtils.serialize(urlList));
        return request;
    }



//    public OrdRequest fetchOrderRequestInfo(Cash2AdditionalInfoParam additionalInfo) {
//        try {
//            OrdRequest ordRequest = new OrdRequest();
//            ordRequest.setClient_type("android");
//            ordRequest.setDeviceType("android");
//
//            if (additionalInfo.getAddInfo() == null
//                    || additionalInfo.getAddInfo().getDeviceInfo() == null) {
//                return ordRequest;
//            }
//            DeviceInfo deviceInfo = additionalInfo.getAddInfo().getDeviceInfo();
//            ordRequest.setDetailed(deviceInfo.getOrderAddressDetail());
//            ordRequest.setProvince(deviceInfo.getOrderProvince());
//            ordRequest.setCity(deviceInfo.getOrderCity());
//            ordRequest.setBigDirect(deviceInfo.getOrderLargeDistrict());
//            ordRequest.setSmallDirect(deviceInfo.getOrderSmallDistrict());
//            ordRequest.setLbsX(
//                    deviceInfo.getLongitude() == null ? null : deviceInfo.getLongitude().toString());
//            ordRequest
//                    .setLbsY(
//                            deviceInfo.getLatitude() == null ? null : deviceInfo.getLatitude().toString());
//
//            ordRequest.setDeviceName(deviceInfo.getDeviceName());
//            ordRequest.setDeviceId(deviceInfo.getDeviceId());
//            ordRequest.setSystem_version(deviceInfo.getSystemVersion());
//            ordRequest.setPhoneBrand(deviceInfo.getDeviceModel());
//            ordRequest.setSystem_version(deviceInfo.getSystemVersion());
//            ordRequest.setSystem_version(deviceInfo.getSystemVersion());
//            ordRequest.setSystem_version(deviceInfo.getSystemVersion());
//            ordRequest.setSystem_version(deviceInfo.getSystemVersion());
//            ordRequest.setTotalMemory(deviceInfo.getMemorySize());
//            // ordRequest.setRemainMemory();//剩余内存
//            ordRequest.setTotalSpace(deviceInfo.getInternalStorageTotal());
//            ordRequest.setRemainSpace(deviceInfo.getInternalStorageUsable());
//            ordRequest.setIMEI(deviceInfo.getImei());
////        ordRequest.setIMSI();
////        ordRequest.setCpuType();
////        ordRequest.setLastPowerOnTime();
//            ordRequest.setDnsStr(deviceInfo.getDns());
//            ordRequest.setIsRoot(String.valueOf(deviceInfo.getRootJailBreak()));
//            ordRequest.setNet_type(deviceInfo.getNetworkType());
//            ordRequest.setMemoryCardCapacity(deviceInfo.getMemoryCardSize());
//            ordRequest.setMac(deviceInfo.getMac());
//            ordRequest.setMobileLanguage(deviceInfo.getLanguage());
//
//            ordRequest.setIPAdress(deviceInfo.getIp());
//            ordRequest.setIsSimulator(String.valueOf(deviceInfo.getSimulator()));
//            ordRequest.setBattery(deviceInfo.getBattery());
//            ordRequest.setPictureNumber(String.valueOf(deviceInfo.getImageNum()));
//            if (!CollectionUtils.isEmpty(deviceInfo.getWifiDataList())) {
//                //转为需要的格式
//                List<Map<String, String>> wifiList = deviceInfo.getWifiDataList().stream()
//                        .map(elem -> {
//                            Map<String, String> wifiData = new HashMap<>();
//                            wifiData.put("BSSID", elem.getMac());
//                            wifiData.put("SSID", elem.getName());
//                            return wifiData;
//                        }).collect(Collectors.toList());
//                ordRequest.setWifiList(JsonUtils.serialize(wifiList));
//            }
//
//            return ordRequest;
//        } catch (Exception e) {
//            log.error("data invalid", e);
//            throw new IllegalArgumentException("invalid param");
//        }
//    }


    public UsrStudentBaseInfoRequest fetchStudentBaseInfo(Cash2AdditionalInfoParam additionalInfo) {
        try {
            ApplyDetail applyDetail = additionalInfo.getApplyDetail();
            UsrStudentBaseInfoRequest studentadditionalInfo = new UsrStudentBaseInfoRequest();
//            studentadditionalInfo.setAcademic(
//                EducationEnum.getByCode(applyDetail.getEducation()).getName());
//            studentadditionalInfo
//                .setBorrowUse(LoanPurpose.getByCode(applyDetail.getLoanRemark()).getName());

            studentadditionalInfo.setEmail(applyDetail.getUserEmail());
            //birthday格式转为dd/mm/yyyy
            Date birthday = DateUtils
                    .stringToDate(applyDetail.getBirthday(), DateUtils.FMT_YYYY_MM_DD);
            studentadditionalInfo.setBirthday(DateUtils.formDate(birthday, DateUtils.FMT_DDMMYYYY));
            studentadditionalInfo.setFamilyMemberAmount(applyDetail.getFamilyNumber());
            studentadditionalInfo.setFamilyAnnualIncome(applyDetail.getFamilyMonthlyIncome().toString());
            studentadditionalInfo.setFatherName(applyDetail.getFatherName());

            String fatherMobile = CheakTeleUtils
                    .telephoneNumberValid2(applyDetail.getFatherMobile());

            studentadditionalInfo.setFatherMobile(fatherMobile);
            studentadditionalInfo.setFatherPosition(
                    JobPositionEnum.getByCode(applyDetail.getFatherPosition()).getName());
            studentadditionalInfo.setMotherName(applyDetail.getMotherName());

            String motherMobile = CheakTeleUtils
                    .telephoneNumberValid2(applyDetail.getMotherMobile());

            studentadditionalInfo.setMotherMobile(motherMobile);

            studentadditionalInfo.setMotherPosition(
                    JobPositionEnum.getByCode(applyDetail.getMotherPosition()).getName());
            studentadditionalInfo.setDwellingCondition(applyDetail.getLivingCondition());
            studentadditionalInfo.setProvince(applyDetail.getAddressProvince());
            studentadditionalInfo.setCity(applyDetail.getAddressCity());
            studentadditionalInfo.setBigDirect(applyDetail.getAddressLargeDistrict());
            studentadditionalInfo.setSmallDirect(applyDetail.getAddressSmallDistrict());
            studentadditionalInfo.setDetailed(applyDetail.getAddressStreet());
            studentadditionalInfo.setAddressType(UsrAddressEnum.HOME.getType());

            return studentadditionalInfo;
        } catch (Exception e) {
            log.error("data invalid", e);
            throw new IllegalArgumentException("invalid param");
        }
    }

    public UsrSchoolInfoRequest fetchStudentSchoolInfo(Cash2AdditionalInfoParam additionalInfo) {
        try {
            ApplyDetail applyDetail = additionalInfo.getApplyDetail();
            UsrSchoolInfoRequest schoolInfo = new UsrSchoolInfoRequest();
            schoolInfo.setSchoolName(applyDetail.getSchoolName());
            schoolInfo.setMajor(applyDetail.getMajor());

            //enrolmentTime 格式转为dd/mm/yyyy
            Date enrolmentTime = DateUtils
                    .stringToDate(applyDetail.getSchoolEnrolmentTime(), DateUtils.FMT_YYYY_MM_DD);
            schoolInfo
                    .setStartSchoolDate(DateUtils.formDate(enrolmentTime, DateUtils.FMT_DDMMYYYY));
            schoolInfo.setStudentNo(applyDetail.getStudentId());
            schoolInfo.setAddressType(UsrAddressEnum.SCHOOL.getType());
            schoolInfo.setProvince(applyDetail.getSchoolAddressProvince());
            schoolInfo.setCity(applyDetail.getSchoolAddressCity());
            schoolInfo.setBigDirect(applyDetail.getSchoolAddressLargeDistrict());
            schoolInfo.setSmallDirect(applyDetail.getSchoolAddressSmallDistrict());
            schoolInfo.setDetailed(applyDetail.getSchoolAddressDetail());

            schoolInfo.setStudentCardUrl(applyDetail.getStudentIdCardImage());
            schoolInfo.setScholarshipUrl(applyDetail.getScholarshipCertificateImage());
            schoolInfo.setEnglishUrl(applyDetail.getEnglishCertificateImage());
            //schoolInfo.setComputerUrl();
            schoolInfo.setSchoolCardUrl(applyDetail.getCampusCardImage());
            schoolInfo.setOtherCertificateUrl(applyDetail.getOtherCertificateImage());
            return schoolInfo;
        } catch (Exception e) {
            log.error("data invalid", e);
            throw new IllegalArgumentException("invalid param");
        }
    }


    public UsrWorkBaseInfoRequest fetchWorkBaseInfo(Cash2AdditionalInfoParam additionalInfo) {
        try {
            ApplyDetail applyDetail = additionalInfo.getApplyDetail();
            UsrWorkBaseInfoRequest workerBaseInfo = new UsrWorkBaseInfoRequest();
//            workeradditionalInfo
//                .setAcademic(EducationEnum.getByCode(applyDetail.getEducation()).getName());
//            workeradditionalInfo.setMaritalStatus(applyDetail.getMaritalStatus());
//            workeradditionalInfo.setChildrenAmount(applyDetail.getChildren());
//            workeradditionalInfo
//                .setBorrowUse(LoanPurpose.getByCode(applyDetail.getLoanRemark()).getName());
//            workerBaseInfo.setReligion(ReligionEnum.getByCode(applyDetail.getReligion()).getName());
            workerBaseInfo.setEmail(applyDetail.getUserEmail());
            Date birthday = DateUtils
                    .stringToDate(applyDetail.getBirthday(), DateUtils.FMT_YYYY_MM_DD);
            workerBaseInfo.setBirthday(DateUtils.formDate(birthday, DateUtils.FMT_DDMMYYYY));
            workerBaseInfo.setMotherName(applyDetail.getMotherName());
            workerBaseInfo.setAddressType(UsrAddressEnum.HOME.getType());
            workerBaseInfo.setProvince(applyDetail.getAddressProvince());
            workerBaseInfo.setCity(applyDetail.getAddressCity());
            workerBaseInfo.setBigDirect(applyDetail.getAddressLargeDistrict());
            workerBaseInfo.setSmallDirect(applyDetail.getAddressSmallDistrict());
            workerBaseInfo.setDetailed(applyDetail.getAddressStreet());
            return workerBaseInfo;
        } catch (Exception e) {
            log.error("data invalid", e);
            throw new IllegalArgumentException("invalid param");
        }
    }

    public UsrWorkInfoRequest fetchWorkInfo(Cash2AdditionalInfoParam additionalInfo) {
        try {
            ApplyDetail applyDetail = additionalInfo.getApplyDetail();
            UsrWorkInfoRequest workInfo = new UsrWorkInfoRequest();
//            workInfo.setMonthlyIncome(
//                applyDetail.getMonthlyIncome() != null ? applyDetail.getMonthlyIncome().toString()
//                    : null);

            workInfo.setCompanyName(applyDetail.getCompanyName());
            workInfo.setCompanyPhone(applyDetail.getCompanyMobile());
            workInfo
                    .setPositionName(JobPositionEnum.getByCode(applyDetail.getJobPosition()).getName());

            workInfo.setAddressType(UsrAddressEnum.COMPANY.getType());
            workInfo.setProvince(applyDetail.getCompanyAddressProvince());
            workInfo.setCity(applyDetail.getCompanyAddressCity());
            workInfo.setBigDirect(applyDetail.getCompanyAddressLargeDistrict());
            workInfo.setSmallDirect(applyDetail.getCompanyAddressSmallDistrict());
            workInfo.setDetailed(applyDetail.getCompanyAddressDetail());
            return workInfo;
        } catch (Exception e) {
            log.error("data invalid", e);
            throw new IllegalArgumentException("invalid param");
        }
    }



    public LinkManRequest fetchContactUserInfo(Cash2AdditionalInfoParam additionalInfo) {

            ApplyDetail applyDetail = additionalInfo.getApplyDetail();


            LinkManRequest linkManRequest = new LinkManRequest();

            List<UsrLinkManInfo> linkmanList = new ArrayList<>();
            linkManRequest.setLinkmanList(linkmanList);

            //第一联系人
            UsrLinkManInfo linkManInfo1 = new UsrLinkManInfo();
            linkManInfo1.setSequence(UsrLinkManInfo.SequenceEnum.FIRST.getCode());
            linkManInfo1.setContactsName(applyDetail.getEmergencyContactName());
            String contactMobile = CheakTeleUtils
                    .telephoneNumberValid2(applyDetail.getEmergencyContactMobile());
            linkManInfo1.setContactsMobile(
                    StringUtils.isEmpty(contactMobile) ? applyDetail.getEmergencyContactMobile()
                            : contactMobile);
            linkManInfo1.setRelation(
                    EmergencyContactRelationEnum.getByCode(applyDetail.getEmergencyContactRelation())
                            .getName());
            if(!StringUtils.isEmpty(linkManInfo1.getContactsMobile())){
                linkmanList.add(linkManInfo1);
            }

            //第二联系人
            UsrLinkManInfo linkManInfo2 = new UsrLinkManInfo();
            linkManInfo2.setSequence(UsrLinkManInfo.SequenceEnum.SECOND.getCode());
            linkManInfo2.setContactsName(applyDetail.getEmergencyContactName2());
            String contactMobile2 = CheakTeleUtils
                    .telephoneNumberValid2(applyDetail.getEmergencyContactMobile2());
            linkManInfo2.setContactsMobile(
                    StringUtils.isEmpty(contactMobile2) ? applyDetail.getEmergencyContactMobile2()
                            : contactMobile2);
            linkManInfo2.setRelation(
                    EmergencyContactRelationEnum.getByCode(applyDetail.getEmergencyContactRelation2())
                            .getName());
            if(!StringUtils.isEmpty(linkManInfo2.getContactsMobile())){
                linkmanList.add(linkManInfo2);
            }
            //第三联系人
            UsrLinkManInfo linkManInfo3 = new UsrLinkManInfo();
            linkManInfo3.setSequence(UsrLinkManInfo.SequenceEnum.THIRD.getCode());
            linkManInfo3.setContactsName(applyDetail.getEmergencyContactName3());
            String contactMobile3 = CheakTeleUtils
                    .telephoneNumberValid2(applyDetail.getEmergencyContactMobile3());
            linkManInfo3.setContactsMobile(
                    StringUtils.isEmpty(contactMobile3) ? applyDetail.getEmergencyContactMobile3()
                            : contactMobile3);
            linkManInfo3.setRelation(
                    EmergencyContactRelationEnum.getByCode(applyDetail.getEmergencyContactRelation3())
                            .getName());
            if(!StringUtils.isEmpty(linkManInfo3.getContactsMobile())){
                linkmanList.add(linkManInfo3);
            }

            //第四联系人
            UsrLinkManInfo linkManInfo4 = new UsrLinkManInfo();
            linkManInfo4.setSequence(UsrLinkManInfo.SequenceEnum.FOURTH.getCode());
            linkManInfo4.setContactsName(applyDetail.getEmergencyContactName4());
            String contactMobile4 = CheakTeleUtils
                    .telephoneNumberValid2(applyDetail.getEmergencyContactMobile4());
            linkManInfo4.setContactsMobile(
                    StringUtils.isEmpty(contactMobile4) ? applyDetail.getEmergencyContactMobile4()
                            : contactMobile4);
            linkManInfo4.setRelation(
                    EmergencyContactRelationEnum.getByCode(applyDetail.getEmergencyContactRelation4())
                            .getName());
            if(!StringUtils.isEmpty(linkManInfo4.getContactsMobile())){
                linkmanList.add(linkManInfo4);
            }
            //备选号码
            UsrLinkManInfo backupNumber = new UsrLinkManInfo();
            backupNumber.setSequence(UsrLinkManInfo.SequenceEnum.OWNER_BACKUP.getCode());
            backupNumber.setContactsMobile(applyDetail.getReserveMobile());
            if(!StringUtils.isEmpty(backupNumber.getContactsMobile())){
              linkmanList.add(backupNumber);
            }

            return linkManRequest;
    }

    public UsrSubmitCerInfoRequest fetchCertificationInfo(Cash2AdditionalInfoParam additionalInfo) {
        UsrSubmitCerInfoRequest certificationInfo = new UsrSubmitCerInfoRequest();
        //人脸验证数据
        //税卡【账号】
        //保险卡【账号密码】
        //视频验证
        return certificationInfo;
    }

    /***
     * 补充信息中图片
     * @param additionalInfo
     * @return
     */
    public List<UsrAttachmentInfo> fetchSupplementInfo(Cash2AdditionalInfoParam additionalInfo) {
        List<UsrAttachmentInfo> attachmentList = new ArrayList<>();
        ApplyDetail applyDetail = additionalInfo.getApplyDetail();
        //暂无相关数据
        return attachmentList;
    }


}
