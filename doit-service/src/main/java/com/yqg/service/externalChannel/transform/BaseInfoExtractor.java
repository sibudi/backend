package com.yqg.service.externalChannel.transform;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.yqg.common.enums.user.UserSourceEnum;
import com.yqg.common.enums.user.UsrAttachmentEnum;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.service.externalChannel.request.Cash2BaseInfo;
import com.yqg.service.externalChannel.request.Cash2BaseInfo.ApplyDetail;
import com.yqg.service.externalChannel.request.Cash2BaseInfo.Cash2UserType;
import com.yqg.service.externalChannel.request.Cash2BaseInfo.EducationEnum;
import com.yqg.service.externalChannel.request.Cash2BaseInfo.LoanPurpose;
import com.yqg.service.externalChannel.request.Cash2BaseInfo.OrderInfo;
import com.yqg.service.externalChannel.request.Cash2BaseInfo.ReligionEnum;
import com.yqg.service.order.request.OrdRequest;
import com.yqg.service.order.request.UploadAppsRequest;
import com.yqg.service.order.request.UploadContactRequest;
import com.yqg.service.order.request.UploadMsgsRequest;
import com.yqg.service.user.request.SaveUserPhotoRequest;
import com.yqg.service.user.request.UsrContactInfoRequest;
import com.yqg.service.user.request.UsrIdentityInfoRequest;
import com.yqg.service.user.request.UsrRequst;
import com.yqg.service.user.request.UsrRolesRequest;
import com.yqg.service.user.request.UsrSchoolInfoRequest;
import com.yqg.service.user.request.UsrStudentBaseInfoRequest;
import com.yqg.service.user.request.UsrSubmitCerInfoRequest;
import com.yqg.service.user.request.UsrWorkBaseInfoRequest;
import com.yqg.service.user.request.UsrWorkInfoRequest;
import com.yqg.user.entity.UsrAttachmentInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/6
 * @Email zengxiangcai@yishufu.com
 * 基本信息中相关数据提取
 ****/
@Slf4j
@Service
// Used by BaseInfoService.java -> which used by Cash2BaseInfoController.java 
// API: /external/cash2/baseInfo
public class BaseInfoExtractor {

    @Autowired
    private ImagePathService imagePathService;


    /****
     * 基本信息中图片相关的url都替换为自己图片服务器的数据
     * @param baseInfo
     * @return
     */
    public boolean processImage(Cash2BaseInfo baseInfo) {

        ApplyDetail applyDetail = baseInfo.getApplyDetail();

        applyDetail.setIdCardFrontImage(
            imagePathService.getLocalFileUrl(applyDetail.getIdCardFrontImage(),
                UsrAttachmentEnum.ID_CARD));
        applyDetail.setIdCardHandImage(
            imagePathService
                .getLocalFileUrl(applyDetail.getIdCardHandImage(), UsrAttachmentEnum.HAND_ID_CARD));

        return true;
    }


    /***
     * 注册用户接口用到的相关数据
     * @param baseInfo
     * @return
     */
    public UsrRequst fetchUserSignUpInfo(Cash2BaseInfo baseInfo) {
        try {
            UsrRequst userRequest = new UsrRequst();
            String userMobile = CheakTeleUtils
                    .telephoneNumberValid2(baseInfo.getOrderInfo().getUserMobile());
            if (StringUtils.isEmpty(userMobile)) {
                throw new IllegalArgumentException("user_mobile empty");
            }
            userRequest.setMobileNumber(userMobile);
            userRequest.setUserSource(UserSourceEnum.CashCash.getCode());//后台定义为28
            userRequest.setClient_type("android");
            userRequest.setChannel_name("");
            userRequest.setChannel_sn("");

            if (baseInfo.getAddInfo() != null && baseInfo.getAddInfo().getDeviceInfo() != null) {
                Cash2BaseInfo.DeviceInfo deviceInfo = baseInfo.getAddInfo().getDeviceInfo();
                userRequest.setDeviceId("");
                userRequest.setMac("");
                userRequest.setIPAdress("");
                userRequest.setNet_type("");
                userRequest.setSystem_version("");
                userRequest.setLbsX(
                        deviceInfo.getLongitude() == null ? null
                                : deviceInfo.getLongitude().toString());
                userRequest
                        .setLbsY(
                                deviceInfo.getLatitude() == null ? null
                                        : deviceInfo.getLatitude().toString());
            }

            return userRequest;
        } catch (Exception e) {
            log.error("data invalid", e);
            throw new IllegalArgumentException("invalid param");
        }
    }


    public OrdRequest fetchOrderRequestInfo(Cash2BaseInfo baseInfo) {
        try {
            OrdRequest ordRequest = new OrdRequest();
            ordRequest.setClient_type("android");
            ordRequest.setDeviceType("android");

            if (baseInfo.getAddInfo() == null || baseInfo.getAddInfo().getDeviceInfo() == null) {
                return ordRequest;
            }
            Cash2BaseInfo.DeviceInfo deviceInfo = baseInfo.getAddInfo().getDeviceInfo();
            ordRequest.setDetailed(deviceInfo.getOrderAddressDetail());
            ordRequest.setProvince(deviceInfo.getOrderProvince());
            ordRequest.setCity(deviceInfo.getOrderCity());
            ordRequest.setBigDirect(deviceInfo.getOrderLargeDistrict());
            ordRequest.setSmallDirect(deviceInfo.getOrderSmallDistrict());
            ordRequest.setLbsX(
                    deviceInfo.getLongitude() == null ? null : deviceInfo.getLongitude().toString());
            ordRequest
                    .setLbsY(
                            deviceInfo.getLatitude() == null ? null : deviceInfo.getLatitude().toString());


            ordRequest.setDeviceName("");
            ordRequest.setDeviceId("");
            ordRequest.setSystem_version(deviceInfo.getSystemVersion());
            ordRequest.setPhoneBrand("");
            ordRequest.setSystem_version("");
            ordRequest.setTotalMemory("");
            ordRequest.setRemainMemory("");//剩余内存
            ordRequest.setTotalSpace("");
            ordRequest.setRemainSpace("");
            ordRequest.setIMEI(deviceInfo.getImei());
            ordRequest.setIMSI("");
            ordRequest.setCpuType("");
            // 格式化处理，传过来的是秒
            try {
                if (StringUtils.isNotEmpty(deviceInfo.getLastPowerOnTime())) {
                    Date d1 = new Date(deviceInfo.getLastPowerOnTime().length() == 10 ? Long.valueOf(deviceInfo.getLastPowerOnTime()) * 1000L :
                            Long.valueOf(deviceInfo.getLastPowerOnTime()));
                    ordRequest.setLastPowerOnTime(DateUtils.formDate(d1, DateUtils.FMT_YYYY_MM_DD_HH_mm_ss));
                }
            } catch (Exception e) {
                ordRequest.setLastPowerOnTime("");
            }

            ordRequest.setDnsStr("");
            ordRequest.setIsRoot("");
            ordRequest.setNet_type("");
            ordRequest.setMemoryCardCapacity("");
            ordRequest.setMac("");
            ordRequest.setMobileLanguage("");

            ordRequest.setIPAdress("");
            ordRequest.setIsSimulator("");
            ordRequest.setBattery("");
            ordRequest.setPictureNumber("");

            return ordRequest;
        } catch (Exception e) {
            log.error("data invalid", e);
            throw new IllegalArgumentException("invalid param");
        }
    }

    public UsrRolesRequest fetchUserRoleRequest(Cash2BaseInfo baseInfo) {
        try {
            UsrRolesRequest userRoleInfo = new UsrRolesRequest();
            userRoleInfo.setRole(
                    baseInfo.getApplyDetail().getUserType() == Cash2UserType.Student.getCode() ? 1 : 2);
            return userRoleInfo;
        } catch (Exception e) {
            log.error("data invalid", e);
            throw new IllegalArgumentException("invalid param");
        }
    }

    public List<SaveUserPhotoRequest> fetchUserPhotoRequest(Cash2BaseInfo baseInfo) {
        try {
            List<SaveUserPhotoRequest> imgList = new ArrayList<>();
            SaveUserPhotoRequest request = new SaveUserPhotoRequest();
            request.setPhotoType("0");
            request.setPhotoUrl(baseInfo.getApplyDetail().getIdCardFrontImage());
            imgList.add(request);

            request = new SaveUserPhotoRequest();
            request.setPhotoType("1");
            request.setPhotoUrl(baseInfo.getApplyDetail().getIdCardHandImage());
            imgList.add(request);
            return imgList;
        } catch (Exception e) {
            log.error("data invalid", e);
            throw new IllegalArgumentException("invalid param");
        }
    }

    public UsrIdentityInfoRequest fetchUserIdentityInfo(Cash2BaseInfo baseInfo) {
        try {
            OrderInfo orderInfo = baseInfo.getOrderInfo();
            ApplyDetail applyDetail = baseInfo.getApplyDetail();
            UsrIdentityInfoRequest usrIdentityInfoRequest = new UsrIdentityInfoRequest();
            usrIdentityInfoRequest.setIdCardNo(orderInfo.getUserIdCard());
            usrIdentityInfoRequest.setName(orderInfo.getUserName());

            usrIdentityInfoRequest.setSex(applyDetail.getUserSex());

            usrIdentityInfoRequest.setIdCardPhoto(applyDetail.getIdCardFrontImage());
            usrIdentityInfoRequest.setHandIdCardPhoto(applyDetail.getIdCardHandImage());
            return usrIdentityInfoRequest;
        } catch (Exception e) {
            log.error("data invalid", e);
            throw new IllegalArgumentException("invalid param");

        }
    }

    public UsrStudentBaseInfoRequest fetchStudentBaseInfo(Cash2BaseInfo baseInfo) {
        try {
            ApplyDetail applyDetail = baseInfo.getApplyDetail();
            UsrStudentBaseInfoRequest studentBaseInfo = new UsrStudentBaseInfoRequest();
            studentBaseInfo.setAcademic(
                    EducationEnum.getByCode(applyDetail.getEducation()).getName());
            studentBaseInfo
                    .setBorrowUse(LoanPurpose.getByCode(applyDetail.getLoanRemark()).getName());

//            studentBaseInfo.setEmail(applyDetail.getUserEmail());
//            //birthday格式转为dd/mm/yyyy
//            Date birthday = DateUtils
//                    .stringToDate(applyDetail.getBirthday(), DateUtils.FMT_YYYY_MM_DD);
//            studentBaseInfo.setBirthday(DateUtils.formDate(birthday, DateUtils.FMT_DDMMYYYY));
//            studentBaseInfo.setFamilyMemberAmount(applyDetail.getFamilyNumber());
//            studentBaseInfo.setFamilyAnnualIncome(applyDetail.getFamilyMonthlyIncome().toString());
//            studentBaseInfo.setFatherName(applyDetail.getFatherName());
//
//            String fatherMobile = CheakTeleUtils
//                    .telephoneNumberValid2(applyDetail.getFatherMobile());
//
//            studentBaseInfo.setFatherMobile(fatherMobile);
//            studentBaseInfo.setFatherPosition(
//                    JobPositionEnum.getByCode(applyDetail.getFatherPosition()).getName());
//            studentBaseInfo.setMotherName(applyDetail.getMotherName());
//
//            String motherMobile = CheakTeleUtils
//                    .telephoneNumberValid2(applyDetail.getMotherMobile());
//
//            studentBaseInfo.setMotherMobile(motherMobile);
//
//            studentBaseInfo.setMotherPosition(
//                    JobPositionEnum.getByCode(applyDetail.getMotherPosition()).getName());
//            studentBaseInfo.setDwellingCondition(applyDetail.getLivingCondition());
//            studentBaseInfo.setProvince(applyDetail.getAddressProvince());
//            studentBaseInfo.setCity(applyDetail.getAddressCity());
//            studentBaseInfo.setBigDirect(applyDetail.getAddressLargeDistrict());
//            studentBaseInfo.setSmallDirect(applyDetail.getAddressSmallDistrict());
//            studentBaseInfo.setDetailed(applyDetail.getAddressStreet());
//            studentBaseInfo.setAddressType(UsrAddressEnum.HOME.getType());

            return studentBaseInfo;
        } catch (Exception e) {
            log.error("data invalid", e);
            throw new IllegalArgumentException("invalid param");
        }
    }

    public UsrSchoolInfoRequest fetchStudentSchoolInfo(Cash2BaseInfo baseInfo) {
        try {
            ApplyDetail applyDetail = baseInfo.getApplyDetail();
            UsrSchoolInfoRequest schoolInfo = new UsrSchoolInfoRequest();

//            schoolInfo.setSchoolName(applyDetail.getSchoolName());
//            schoolInfo.setMajor(applyDetail.getMajor());
//
//            //enrolmentTime 格式转为dd/mm/yyyy
//            Date enrolmentTime = DateUtils
//                    .stringToDate(applyDetail.getSchoolEnrolmentTime(), DateUtils.FMT_YYYY_MM_DD);
//            schoolInfo
//                    .setStartSchoolDate(DateUtils.formDate(enrolmentTime, DateUtils.FMT_DDMMYYYY));
//            schoolInfo.setStudentNo(applyDetail.getStudentId());
//            schoolInfo.setAddressType(UsrAddressEnum.SCHOOL.getType());
//            schoolInfo.setProvince(applyDetail.getSchoolAddressProvince());
//            schoolInfo.setCity(applyDetail.getSchoolAddressCity());
//            schoolInfo.setBigDirect(applyDetail.getSchoolAddressLargeDistrict());
//            schoolInfo.setSmallDirect(applyDetail.getSchoolAddressSmallDistrict());
//            schoolInfo.setDetailed(applyDetail.getSchoolAddressDetail());
//
//            schoolInfo.setStudentCardUrl(applyDetail.getStudentIdCardImage());
//            schoolInfo.setScholarshipUrl(applyDetail.getScholarshipCertificateImage());
//            schoolInfo.setEnglishUrl(applyDetail.getEnglishCertificateImage());
//            //schoolInfo.setComputerUrl();
//            schoolInfo.setSchoolCardUrl(applyDetail.getCampusCardImage());
//            schoolInfo.setOtherCertificateUrl(applyDetail.getOtherCertificateImage());
            return schoolInfo;
        } catch (Exception e) {
            log.error("data invalid", e);
            throw new IllegalArgumentException("invalid param");
        }
    }


    public UsrWorkBaseInfoRequest fetchWorkBaseInfo(Cash2BaseInfo baseInfo) {
        try {
            ApplyDetail applyDetail = baseInfo.getApplyDetail();
            UsrWorkBaseInfoRequest workerBaseInfo = new UsrWorkBaseInfoRequest();
            workerBaseInfo
                    .setAcademic(EducationEnum.getByCode(applyDetail.getEducation()).getName());
            workerBaseInfo.setMaritalStatus(applyDetail.getMaritalStatus());
            workerBaseInfo.setReligion(ReligionEnum.getByCode(applyDetail.getReligion()).getName());
            workerBaseInfo.setChildrenAmount(applyDetail.getChildren());
            workerBaseInfo
                    .setBorrowUse(LoanPurpose.getByCode(applyDetail.getLoanRemark()).getName());
            workerBaseInfo.setMonthlyIncome(
                    applyDetail.getMonthlyIncome() != null ? applyDetail.getMonthlyIncome()
                            : null);

//            workerBaseInfo.setEmail(applyDetail.getUserEmail());
//            Date birthday = DateUtils
//                    .stringToDate(applyDetail.getBirthday(), DateUtils.FMT_YYYY_MM_DD);
//            workerBaseInfo.setBirthday(DateUtils.formDate(birthday, DateUtils.FMT_DDMMYYYY));
//            workerBaseInfo.setMotherName(applyDetail.getMotherName());
//            workerBaseInfo.setAddressType(UsrAddressEnum.HOME.getType());
//            workerBaseInfo.setProvince(applyDetail.getAddressProvince());
//            workerBaseInfo.setCity(applyDetail.getAddressCity());
//            workerBaseInfo.setBigDirect(applyDetail.getAddressLargeDistrict());
//            workerBaseInfo.setSmallDirect(applyDetail.getAddressSmallDistrict());
//            workerBaseInfo.setDetailed(applyDetail.getAddressStreet());
            return workerBaseInfo;
        } catch (Exception e) {
            log.error("data invalid", e);
            throw new IllegalArgumentException("invalid param");
        }
    }

    public UsrWorkInfoRequest fetchWorkInfo(Cash2BaseInfo baseInfo) {
        try {
            ApplyDetail applyDetail = baseInfo.getApplyDetail();
            UsrWorkInfoRequest workInfo = new UsrWorkInfoRequest();
//            workInfo.setMonthlyIncome(
//                    applyDetail.getMonthlyIncome() != null ? applyDetail.getMonthlyIncome().toString()
//                            : null);
//            workInfo.setCompanyName(applyDetail.getCompanyName());
////            workInfo.setCompanyPhone(applyDetail.getCompanyMobile());
////            workInfo.setPositionName(JobPositionEnum.getByCode(applyDetail.getJobPosition()).getName());
////            workInfo.setAddressType(UsrAddressEnum.COMPANY.getType());
////            workInfo.setProvince(applyDetail.getCompanyAddressProvince());
////            workInfo.setCity(applyDetail.getCompanyAddressCity());
////            workInfo.setBigDirect(applyDetail.getCompanyAddressLargeDistrict());
////            workInfo.setSmallDirect(applyDetail.getCompanyAddressSmallDistrict());
////            workInfo.setDetailed(applyDetail.getCompanyAddressDetail());
            return workInfo;
        } catch (Exception e) {
            log.error("data invalid", e);
            throw new IllegalArgumentException("invalid param");
        }
    }

    public UsrContactInfoRequest fetchContactUserInfo(Cash2BaseInfo baseInfo) {
        try {
            ApplyDetail applyDetail = baseInfo.getApplyDetail();
            UsrContactInfoRequest contactUser = new UsrContactInfoRequest();

//            contactUser.setAlternatePhoneNo(applyDetail.getReserveMobile());
//            contactUser.setContactsName1(applyDetail.getEmergencyContactName());
//
//            String contactMobile = CheakTeleUtils
//                    .telephoneNumberValid2(applyDetail.getEmergencyContactMobile());
//
//            contactUser.setContactsMobile1(
//                    StringUtils.isEmpty(contactMobile) ? applyDetail.getEmergencyContactMobile()
//                            : contactMobile);
//
//            contactUser.setRelation1(
//                    EmergencyContactRelationEnum.getByCode(applyDetail.getEmergencyContactRelation())
//                            .getName());
//            contactUser.setContactsName2(applyDetail.getEmergencyContactName2());
//
//            String contactMobile2 = CheakTeleUtils
//                    .telephoneNumberValid2(applyDetail.getEmergencyContactMobile2());
//
//            contactUser.setContactsMobile2(
//                    StringUtils.isEmpty(contactMobile2) ? applyDetail.getEmergencyContactMobile2()
//                            : contactMobile2);
//
//            contactUser.setRelation2(
//                    EmergencyContactRelationEnum.getByCode(applyDetail.getEmergencyContactRelation2())
//                            .getName());
            return contactUser;
        } catch (Exception e) {
            log.error("data invalid", e);
            throw new IllegalArgumentException("invalid param");
        }
    }

    public UsrSubmitCerInfoRequest fetchCertificationInfo(Cash2BaseInfo baseInfo) {
        UsrSubmitCerInfoRequest certificationInfo = new UsrSubmitCerInfoRequest();
        //人脸验证数据
        //税卡【账号】
        //保险卡【账号密码】
        //视频验证
        return certificationInfo;
    }

    /***
     * 补充信息中图片
     * @param baseInfo
     * @return
     */
    public List<UsrAttachmentInfo> fetchSupplementInfo(Cash2BaseInfo baseInfo) {
        List<UsrAttachmentInfo> attachmentList = new ArrayList<>();
        ApplyDetail applyDetail = baseInfo.getApplyDetail();
        //暂无相关数据
        return attachmentList;
    }

    /***
     * 获取手机安装的app
     * @param baseInfo
     * @return
     */
    public UploadAppsRequest fetchInstalledAppList(Cash2BaseInfo baseInfo) {
        try {

            UploadAppsRequest appListRequest = new UploadAppsRequest();
            if(baseInfo.getAddInfo()==null||baseInfo.getAddInfo().getDeviceInfo()==null){
                return appListRequest;
            }

            Cash2BaseInfo.DeviceInfo deviceInfo = baseInfo.getAddInfo().getDeviceInfo();

            if (CollectionUtils.isEmpty(deviceInfo.getInstalledAppList())) {
                appListRequest.setAppsListStr(JsonUtils.serialize(new ArrayList<>()));
                return appListRequest;
            } else {

                List<Map<String, String>> appList = deviceInfo.getInstalledAppList().stream()
                        .map(elem -> {
                            Map<String, String> dataElem = new HashMap<>();
                            dataElem.put("appBundleId", elem.getPackageName());
                            dataElem.put("appName", elem.getAppName());
                            //dataElem.put("appVersion", );
                            return dataElem;
                        }).collect(Collectors.toList());
                appListRequest.setAppsListStr(JsonUtils.serialize(appList));
                return appListRequest;
            }
        } catch (Exception e) {
            log.error("data invalid", e);
            throw new IllegalArgumentException("invalid param");
        }
    }




}
