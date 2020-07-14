package com.yqg.service.externalChannel.transform;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.yqg.common.enums.user.UserSourceEnum;
import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.service.externalChannel.request.Cash2BaseInfo.EducationEnum;
import com.yqg.service.externalChannel.request.Cash2BaseInfo.ReligionEnum;
import com.yqg.service.externalChannel.request.CheetahBaseInfo;
import com.yqg.service.order.request.OrdRequest;
import com.yqg.service.order.request.UploadAppsRequest;
import com.yqg.service.user.request.LinkManRequest;
import com.yqg.service.user.request.SaveUserPhotoRequest;
import com.yqg.service.user.request.UsrIdentityInfoRequest;
import com.yqg.service.user.request.UsrRequst;
import com.yqg.service.user.request.UsrRolesRequest;
import com.yqg.service.user.request.UsrWorkBaseInfoRequest;
import com.yqg.service.user.request.UsrWorkInfoRequest;
import com.yqg.user.entity.UsrLinkManInfo;

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
public class CheetahBaseInfoExtractor {

    @Autowired
    private ImagePathService imagePathService;

    //ahalim: remark unused code
    // /****
    //  * 基本信息中图片相关的url都替换为自己图片服务器的数据
    //  * @param baseInfo
    //  * @return
    //  */
    // public boolean processImage(CheetahBaseInfo baseInfo) {

    //     CheetahBaseInfo.UserInfoBean userInfo = baseInfo.getUserInfo();

    //     userInfo.setIdFrontPhoto(
    //         imagePathService.getLocalFileUrl(userInfo.getIdFrontPhoto(),
    //             UsrAttachmentEnum.ID_CARD));
    //     userInfo.setHandHeldPhoto(
    //         imagePathService
    //             .getLocalFileUrl(userInfo.getHandHeldPhoto(), UsrAttachmentEnum.HAND_ID_CARD));

    //     return true;
    // }


    /***
     * 注册用户接口用到的相关数据
     * @param baseInfo
     * @return
     */
    public UsrRequst fetchUserSignUpInfo(CheetahBaseInfo baseInfo) {
        try {
            UsrRequst userRequest = new UsrRequst();
            String userMobile = CheakTeleUtils
                    .telephoneNumberValid2(baseInfo.getUserInfo().getMobile());
            if (StringUtils.isEmpty(userMobile)) {
                throw new IllegalArgumentException("user_mobile empty");
            }
            userRequest.setMobileNumber(userMobile);
            userRequest.setUserSource(UserSourceEnum.Cheetah.getCode());//后台定义为68
            userRequest.setClient_type("android");
            userRequest.setChannel_name("");
            userRequest.setChannel_sn("");

            if (baseInfo.getDeviceInfo() != null) {
                CheetahBaseInfo.DeviceInfoBean deviceInfo = baseInfo.getDeviceInfo();
                userRequest.setDeviceId(deviceInfo.getGeneral_data().getTelephony_info().getDevice_id());
                userRequest.setMac("");
                userRequest.setIPAdress("");
                userRequest.setNet_type("");
                userRequest.setSystem_version("");
            }

            return userRequest;
        } catch (Exception e) {
            log.error("data invalid", e);
            throw new IllegalArgumentException("invalid param");
        }
    }


    public OrdRequest fetchOrderRequestInfo(CheetahBaseInfo baseInfo) {
        try {
            OrdRequest ordRequest = new OrdRequest();
            ordRequest.setClient_type("android");
            ordRequest.setDeviceType("android");

            if (baseInfo.getDeviceInfo() == null) {
                return ordRequest;
            }
            CheetahBaseInfo.DeviceInfoBean deviceInfo = baseInfo.getDeviceInfo();
//            ordRequest.setDetailed(deviceInfo.getOrderAddressDetail());//下单位置
//            ordRequest.setProvince(deviceInfo.getOrderProvince());
//            ordRequest.setCity(deviceInfo.getOrderCity());
//            ordRequest.setBigDirect(deviceInfo.getOrderLargeDistrict());
//            ordRequest.setSmallDirect(deviceInfo.getOrderSmallDistrict());
            ordRequest.setLbsX(
                    deviceInfo.getLocation().getNetwork().getLongitude() == null ?
                            null : deviceInfo.getLocation().getNetwork().getLongitude().toString());
            ordRequest
                    .setLbsY(
                            deviceInfo.getLocation().getNetwork().getLatitude() == null ?
                                    null : deviceInfo.getLocation().getNetwork().getLatitude().toString());


            ordRequest.setDeviceName(deviceInfo.getHardware().getProduct());
            ordRequest.setDeviceId(deviceInfo.getGeneral_data().getTelephony_info().getDevice_id());
            ordRequest.setSystem_version(deviceInfo.getHardware().getRelease());
            ordRequest.setPhoneBrand(deviceInfo.getHardware().getBrand());
            ordRequest.setTotalMemory(deviceInfo.getHardware().getRam_total_size() + " MB");
//            ordRequest.setRemainMemory(deviceInfo.getRemainingMemory());//剩余内存
            ordRequest.setTotalSpace(deviceInfo.getHardware().getPhysical_size() + " GB");
//            ordRequest.setRemainSpace(deviceInfo.getInternalStorageUsable());
//            ordRequest.setIMEI(deviceInfo.getImei());
            ordRequest.setIMSI(deviceInfo.getGeneral_data().getTelephony_info().getImsi());
//            ordRequest.setCpuType(deviceInfo.getCpuType());
            ordRequest.setLastPowerOnTime(DateUtils.DateToString2(new Date(deviceInfo.getGeneral_data().getTelephony_info().getLast_boot_time())));
//            ordRequest.setDnsStr(deviceInfo.getDns());
            ordRequest.setIsRoot(String.valueOf(deviceInfo.getGeneral_data().getTelephony_info().isIs_rooted()));
//            ordRequest.setNet_type(deviceInfo.getNetworkType());
//            ordRequest.setMemoryCardCapacity(deviceInfo.getMemoryCardSize());
            ordRequest.setMac(deviceInfo.getBlue_tooth().getMac_address());
            ordRequest.setMobileLanguage(deviceInfo.getGeneral_data().getTelephony_configuration().getLocale_display_language());

            ordRequest.setIPAdress(deviceInfo.getIp_address().getIp_v4());
            ordRequest.setIsSimulator(String.valueOf(deviceInfo.getGeneral_data().getTelephony_info().isIs_emulator()));
            ordRequest.setBattery(deviceInfo.getBattery_status().getBattery_pct());
//            ordRequest.setPictureNumber(String.valueOf(deviceInfo.getImageNum()));
            if (!CollectionUtils.isEmpty(deviceInfo.getConfigured_wifi())) {
                //转为需要的格式
                List<Map<String, String>> wifiList = deviceInfo.getConfigured_wifi().stream()
                        .map(elem -> {
                            Map<String, String> wifiData = new HashMap<>();
                            wifiData.put("BSSID", elem.getBssid());
                            wifiData.put("SSID", elem.getSsid());
                            return wifiData;
                        }).collect(Collectors.toList());
                ordRequest.setWifiList(JsonUtils.serialize(wifiList));
            }

            return ordRequest;
        } catch (Exception e) {
            log.error("data invalid", e);
            throw new IllegalArgumentException("invalid param");
        }
    }

    public UsrRolesRequest fetchUserRoleRequest() {
        try {
            UsrRolesRequest userRoleInfo = new UsrRolesRequest();
            userRoleInfo.setRole(2);
            return userRoleInfo;
        } catch (Exception e) {
            log.error("data invalid", e);
            throw new IllegalArgumentException("invalid param");
        }
    }

    public UsrIdentityInfoRequest fetchUserIdentityInfo(CheetahBaseInfo baseInfo) {
        try {
            CheetahBaseInfo.UserInfoBean userInfo = baseInfo.getUserInfo();
            UsrIdentityInfoRequest usrIdentityInfoRequest = new UsrIdentityInfoRequest();
            usrIdentityInfoRequest.setIdCardNo(userInfo.getIdNumber());
            usrIdentityInfoRequest.setName(userInfo.getFullName());
            usrIdentityInfoRequest.setSex(userInfo.getGender());

            usrIdentityInfoRequest.setIdCardPhoto(userInfo.getIdFrontPhoto());
            usrIdentityInfoRequest.setHandIdCardPhoto(userInfo.getHandHeldPhoto());
            return usrIdentityInfoRequest;
        } catch (Exception e) {
            log.error("data invalid", e);
            throw new IllegalArgumentException("invalid param");

        }
    }

    public List<SaveUserPhotoRequest> fetchUserPhotoRequest(CheetahBaseInfo baseInfo) {
        try {
            List<SaveUserPhotoRequest> imgList = new ArrayList<>();
            SaveUserPhotoRequest request = new SaveUserPhotoRequest();
            request.setPhotoType("0");
            request.setPhotoUrl(baseInfo.getUserInfo().getIdFrontPhoto());
            imgList.add(request);

            request = new SaveUserPhotoRequest();
            request.setPhotoType("1");
            request.setPhotoUrl(baseInfo.getUserInfo().getHandHeldPhoto());
            imgList.add(request);
            return imgList;
        } catch (Exception e) {
            log.error("data invalid", e);
            throw new IllegalArgumentException("invalid param");
        }
    }


    public UsrWorkBaseInfoRequest fetchWorkBaseInfo(CheetahBaseInfo baseInfo) {
        try {

            CheetahBaseInfo.UserInfoBean userInfoBean = baseInfo.getUserInfo();

            UsrWorkBaseInfoRequest workerBaseInfo = new UsrWorkBaseInfoRequest();
            workerBaseInfo
                    .setAcademic(EducationEnum.getByCode(userInfoBean.getEducation()).getName());
            workerBaseInfo.setMaritalStatus(userInfoBean.getMaritalStatus());
            workerBaseInfo.setReligion(CheetahBaseInfo.UserInfoBean.ReligionEnum
                    .getByCode(userInfoBean.getReligion()).getName());
            workerBaseInfo.setChildrenAmount(userInfoBean.getNumberOfDependents());
            workerBaseInfo
                    .setBorrowUse(CheetahBaseInfo.UserInfoBean.LoanPurposeEnum
                            .getByCode(userInfoBean.getLoanPurpose()).getName());
            workerBaseInfo.setMonthlyIncome(
                    userInfoBean.getMonthlyIncome() != 0 ? String.valueOf(userInfoBean.getMonthlyIncome())
                            : null);

            workerBaseInfo.setEmail(userInfoBean.getEmail());
            workerBaseInfo.setBirthday(userInfoBean.getBirthday());
            workerBaseInfo.setMotherName(userInfoBean.getMotherMaidenName());
            workerBaseInfo.setAddressType(UsrAddressEnum.HOME.getType());

            CheetahBaseInfo.ResidentialInfoBean residentialInfoBean = baseInfo.getResidentialInfo();
            if (residentialInfoBean != null) {
                workerBaseInfo.setProvince(residentialInfoBean.getProvince());
                workerBaseInfo.setCity(residentialInfoBean.getCity());
                workerBaseInfo.setBigDirect(residentialInfoBean.getDistrict());
                workerBaseInfo.setSmallDirect(residentialInfoBean.getVillage());
                workerBaseInfo.setDetailed(residentialInfoBean.getAddress());
            }
            return workerBaseInfo;
        } catch (Exception e) {
            log.error("data invalid", e);
            throw new IllegalArgumentException("invalid param");
        }
    }

    public UsrWorkInfoRequest fetchWorkInfo(CheetahBaseInfo baseInfo) {
        try {
            CheetahBaseInfo.WorkInfoBean workInfoBean = baseInfo.getWorkInfo();
            UsrWorkInfoRequest workInfo = new UsrWorkInfoRequest();
//            workInfo.setMonthlyIncome(
//                    applyDetail.getMonthlyIncome() != null ? applyDetail.getMonthlyIncome().toString()
//                            : null);
            workInfo.setCompanyName(workInfoBean.getEmployerName());
            workInfo.setCompanyPhone(workInfoBean.getCompanyPhone());
            workInfo.setPositionName(CheetahBaseInfo.WorkInfoBean.OccupationIndustryEnum
                    .getByCode(workInfoBean.getOccupationIndustry()).getName());
            workInfo.setAddressType(UsrAddressEnum.COMPANY.getType());
            workInfo.setProvince(workInfoBean.getBusinessAddressProvince());
            workInfo.setCity(workInfoBean.getBusinessAddressCity());
            workInfo.setBigDirect(workInfoBean.getBusinessAddressDistrict());
            workInfo.setSmallDirect(workInfoBean.getBusinessAddressVillage());
            workInfo.setDetailed(workInfoBean.getBusinessAddress());
            return workInfo;
        } catch (Exception e) {
            log.error("data invalid", e);
            throw new IllegalArgumentException("invalid param");
        }
    }

    /***
     * 获取手机安装的app
     * @param baseInfo
     * @return
     */
    public UploadAppsRequest fetchInstalledAppList(CheetahBaseInfo baseInfo) {
        try {

            UploadAppsRequest appListRequest = new UploadAppsRequest();
            if(baseInfo.getDeviceInfo() == null){
                return appListRequest;
            }

            CheetahBaseInfo.DeviceInfoBean deviceInfo = baseInfo.getDeviceInfo();

            if (CollectionUtils.isEmpty(deviceInfo.getApplication())) {
                appListRequest.setAppsListStr(JsonUtils.serialize(new ArrayList<>()));
                return appListRequest;
            } else {

                List<Map<String, String>> appList = deviceInfo.getApplication().stream()
                        .map(elem -> {
                            Map<String, String> dataElem = new HashMap<>();
                            dataElem.put("appBundleId", elem.getPackage_name());
                            dataElem.put("appName", elem.getVersion_name());
                            dataElem.put("appInstallTime", StringUtils.isNotEmpty(elem.getFirst_install_time()) ?
                                    DateUtils.DateToString2(new Date(Long.valueOf(elem.getFirst_install_time()))) : "");
                            dataElem.put("applastUpdateTime", StringUtils.isNotEmpty(elem.getLast_update_time()) ?
                                    DateUtils.DateToString2(new Date(Long.valueOf(elem.getLast_update_time()))) : "");
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
    public LinkManRequest fetchContactUserInfo(CheetahBaseInfo additionalInfo) {

        List<CheetahBaseInfo.EmergencyInfoBean> emergencyInfos = additionalInfo.getEmergencyInfo();

        if (emergencyInfos == null || emergencyInfos.size() < 4) {
            log.info("emergencyInfo data is error!");
            throw new IllegalArgumentException("invalid param");
        }

        LinkManRequest linkManRequest = new LinkManRequest();

        List<UsrLinkManInfo> linkmanList = new ArrayList<>();
        linkManRequest.setLinkmanList(linkmanList);

        for (int index = 0 ; index < 4; index ++) {
            UsrLinkManInfo linkManInfo = new UsrLinkManInfo();
            if (index == 0) {
                linkManInfo.setSequence(UsrLinkManInfo.SequenceEnum.FIRST.getCode());
            } else if (index == 1) {
                linkManInfo.setSequence(UsrLinkManInfo.SequenceEnum.SECOND.getCode());
            } else if (index == 2) {
                linkManInfo.setSequence(UsrLinkManInfo.SequenceEnum.THIRD.getCode());
            } else if (index == 3) {
                linkManInfo.setSequence(UsrLinkManInfo.SequenceEnum.FOURTH.getCode());
            }
            linkManInfo.setContactsName(emergencyInfos.get(index).getName());
            String contactMobile = CheakTeleUtils
                    .telephoneNumberValid2(emergencyInfos.get(index).getMobile());
            linkManInfo.setContactsMobile(
                    org.springframework.util.StringUtils.isEmpty(contactMobile) ? emergencyInfos.get(index).getMobile()
                            : contactMobile);
            linkManInfo.setRelation(ReligionEnum.getByCode(emergencyInfos.get(index).getRelation())
                    .getName());
            if(!org.springframework.util.StringUtils.isEmpty(linkManInfo.getContactsMobile())){
                linkmanList.add(linkManInfo);
            }
        }

        return linkManRequest;
    }



}
