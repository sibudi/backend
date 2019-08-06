package com.yqg.drools.extract;

import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.model.DeviceModel;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.service.DeviceService;
import com.yqg.drools.service.UserService;
import com.yqg.drools.service.UsrBlackListService;
import com.yqg.order.entity.OrdDeviceInfo;
import com.yqg.order.entity.OrdOrder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yqg.service.order.OrdDeviceExtendInfoService;
import com.yqg.user.entity.UsrBlackList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 ****/

@Service
@Slf4j
public class DeviceInfoExtractor implements BaseExtractor<DeviceModel> {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private UsrBlackListService usrBlackListService;
    @Autowired
    private OrdDeviceExtendInfoService ordDeviceExtendInfoService;

    @Override
    public boolean filter(RuleSetEnum ruleSet) {
        return RuleSetEnum.DEVICE_INFO.equals(ruleSet);
    }


    @Override
    public Optional<DeviceModel> extractModel(OrdOrder order, KeyConstant keyConstant) {

        OrdDeviceInfo ordDeviceInfo = deviceService.getOrderDeviceInfo(order.getUuid());

        DeviceModel deviceInfo = new DeviceModel();

        if (ordDeviceInfo == null) {
            return Optional.of(deviceInfo);
        }

        //设置设备id
        deviceInfo.setDeviceId(ordDeviceInfo.getDeviceId());

        deviceInfo.setIsIOS("iOS".equalsIgnoreCase(ordDeviceInfo.getDeviceType()));

        if (StringUtils.isNotEmpty(ordDeviceInfo.getDeviceId())) {
            List<OrdDeviceInfo> deviceListForSameDeviceForOthers = deviceService
                    .getDeviceListForOthers(ordDeviceInfo.getDeviceId(), order.getUserUuid());

            if (CollectionUtils.isEmpty(deviceListForSameDeviceForOthers)) {
                deviceInfo.setMatchedForOthersCount(0L);
            } else {
                deviceInfo
                        .setMatchedForOthersCount(
                                deviceListForSameDeviceForOthers.stream().map(OrdDeviceInfo::getUserUuid)
                                        .distinct().count());
            }
        }

        if (StringUtils.isNotEmpty(ordDeviceInfo.getIMEI())) {
            List<OrdDeviceInfo> imeiListForOthers = deviceService.getImeiForOthers(ordDeviceInfo.getIMEI(), order.getUserUuid());
            if (CollectionUtils.isEmpty(imeiListForOthers)) {
                deviceInfo.setMatchedIMEIForOthersCount(0L);
            } else {
                deviceInfo.setMatchedIMEIForOthersCount(imeiListForOthers.stream().map(OrdDeviceInfo::getUserUuid).distinct().count());
            }
        }
        if (StringUtils.isNotEmpty(ordDeviceInfo.getIMSI())) {
            List<OrdDeviceInfo> imsiListForOthers = deviceService.getImsiForOthers(ordDeviceInfo.getIMSI(), order.getUserUuid());
            if (CollectionUtils.isEmpty(imsiListForOthers)) {
                deviceInfo.setMatchedIMSIForOthersCount(0L);
            } else {
                deviceInfo.setMatchedIMSIForOthersCount(imsiListForOthers.stream().map(OrdDeviceInfo::getUserUuid).distinct().count());
            }
        }
        if (StringUtils.isNotEmpty(ordDeviceInfo.getAndroidId())) {
            OrdDeviceInfo.CustomDeviceFingerprint fingerprintParam = new OrdDeviceInfo.CustomDeviceFingerprint(ordDeviceInfo.getAndroidId(),
                    ordDeviceInfo.getDeviceType(),
                    ordDeviceInfo.getTotalMemory(),
                    ordDeviceInfo.getIPAddress(),
                    ordDeviceInfo.getUserUuid(),
                    ordDeviceInfo.getPhoneBrand(),
                    ordDeviceInfo.getDeviceName(),
                    ordDeviceInfo.getCpuType());

            if (StringUtils.isNotEmpty(ordDeviceInfo.getAppFingerprint())) {
                List<OrdDeviceInfo> customDeviceFingerprintListForOthers =
                        deviceService.getCustomDeviceFingerprintForOthers(ordDeviceInfo.getAndroidId(), ordDeviceInfo.getAppFingerprint(),
                                ordDeviceInfo.getUserUuid());
                if (CollectionUtils.isEmpty(customDeviceFingerprintListForOthers)) {
                    deviceInfo.setMatchedCustomDeviceFingerprintWithApps(0L);
                } else {
                    deviceInfo.setMatchedCustomDeviceFingerprintWithApps(customDeviceFingerprintListForOthers.stream().map(OrdDeviceInfo::getUserUuid).distinct().count());
                }
            }
            if (fingerprintParam.isCustomDeviceFingerprintNotEmptyWithPhoneBrand()) {
                List<OrdDeviceInfo> customDeviceFingerprintListForOthers =
                        deviceService.getCustomDeviceFingerprintWithPhoneBrandForOthers(fingerprintParam);

                if (CollectionUtils.isEmpty(customDeviceFingerprintListForOthers)) {
                    deviceInfo.setMatchedCustomDeviceFingerprintWithPhoneBrand(0L);
                } else {
                    deviceInfo.setMatchedCustomDeviceFingerprintWithPhoneBrand(customDeviceFingerprintListForOthers.stream().map(OrdDeviceInfo::getUserUuid).distinct().count());
                }
            }
            if (fingerprintParam.isCustomDeviceFingerprintNotEmptyWithIp()) {
                List<OrdDeviceInfo> customDeviceFingerprintListForOthers =
                        deviceService.getCustomDeviceFingerprintWithIpForOthers(fingerprintParam);

                if (CollectionUtils.isEmpty(customDeviceFingerprintListForOthers)) {
                    deviceInfo.setMatchedCustomDeviceFingerprintWithIp(0L);
                } else {
                    deviceInfo.setMatchedCustomDeviceFingerprintWithIp(customDeviceFingerprintListForOthers.stream().map(OrdDeviceInfo::getUserUuid).distinct().count());
                }
            }

            if (fingerprintParam.isCustomDeviceFingerprintNotEmptyWithDeviceNameCpuType()) {
                List<OrdDeviceInfo> customFingerprintListForOthers = deviceService.getCustomDeviceFingerprintWithDeviceNameCpuType(fingerprintParam);
                if (CollectionUtils.isEmpty(customFingerprintListForOthers)) {
                    deviceInfo.setMatchedCustomDeviceFingerprintNotEmptyWithDeviceNameCpuType(0L);
                } else {
                    deviceInfo.setMatchedCustomDeviceFingerprintNotEmptyWithDeviceNameCpuType(customFingerprintListForOthers.stream().map(OrdDeviceInfo::getUserUuid).distinct().count());
                }
            }

        }
        if (!"iOS".equalsIgnoreCase(ordDeviceInfo.getDeviceType())) {
            //android设备无androidId时
            if (StringUtils.isNotEmpty(ordDeviceInfo.getDeviceName()) && StringUtils.isNotEmpty(ordDeviceInfo.getPhoneBrand())
                    && StringUtils.isNotEmpty(ordDeviceInfo.getTotalMemory())
                    && StringUtils.isNotEmpty(ordDeviceInfo.getCpuType())
                    && StringUtils.isNotEmpty(ordDeviceInfo.getLastPowerOnTime())
                    && StringUtils.isNotEmpty(ordDeviceInfo.getMacAddress())) {
                List<OrdDeviceInfo> customFingerprintWithoutAndroidId = deviceService.getCustomDeviceFingerprintWithoutAndroidId(ordDeviceInfo);


                //如果开机时间是秒数，转化为统一的格式
                if (!ordDeviceInfo.getLastPowerOnTime().contains(":")) {
                    Date lastPowerOnDateTime =
                            new Date(ordDeviceInfo.getLastPowerOnTime().length() == 10 ? Long.valueOf(ordDeviceInfo.getLastPowerOnTime()) * 1000L :
                                    Long.valueOf(ordDeviceInfo.getLastPowerOnTime()));
                    ordDeviceInfo.setLastPowerOnTime(DateUtils.formDate(lastPowerOnDateTime, DateUtils.FMT_YYYY_MM_DD_HH_mm_ss));
                }
                List<OrdDeviceInfo> customFingerprintWithoutAndroidId2 = deviceService.getCustomDeviceFingerprintWithoutAndroidId(ordDeviceInfo);

                List<OrdDeviceInfo> resultList = new ArrayList<>();
                if(!CollectionUtils.isEmpty(customFingerprintWithoutAndroidId)){
                    resultList.addAll(customFingerprintWithoutAndroidId);
                }
                if(!CollectionUtils.isEmpty(customFingerprintWithoutAndroidId2)){
                    resultList.addAll(customFingerprintWithoutAndroidId2);
                }

                if (CollectionUtils.isEmpty(resultList)) {
                    deviceInfo.setMatchedCustomDeviceFingerprintWithoutAndroidId(0L);
                } else {
                    deviceInfo.setMatchedCustomDeviceFingerprintWithoutAndroidId(resultList.stream().map(OrdDeviceInfo::getUserUuid).distinct().count());
                }


            }
        }


        String dateStr = DateUtils.DateToString(new Date());
        List<OrdDeviceInfo> deviceListForSameIpAddressCurrentDay = deviceService
                .getDeviceListForSameIpByDate(ordDeviceInfo.getIPAddress(), dateStr);

        if (CollectionUtils.isEmpty(deviceListForSameIpAddressCurrentDay)) {
            deviceInfo.setSameIpApplyCount(0L);
        } else {
            deviceInfo
                    .setSameIpApplyCount(deviceListForSameIpAddressCurrentDay.stream().count());
        }

        deviceInfo.setMobileLanguage(ordDeviceInfo.getMobileLanguage());

        deviceInfo.setNetType(ordDeviceInfo.getNetType());// 手机网络类型

        deviceInfo.setPictureNumber(ordDeviceInfo.getPictureNumber());// 手机图片数量

        BigDecimal totalMemory = OrdDeviceInfo.getFormatStorageCapacity(ordDeviceInfo.getTotalMemory());
        deviceInfo.setTotalMemory(totalMemory == null ? null : totalMemory.floatValue());// 手机总内存

        deviceInfo.setTotalSpace(OrdDeviceInfo.getFormatStorageCapacity(ordDeviceInfo.getTotalSpace()));// 手机总容量

        deviceInfo.setIsJailBreak("1".equals(ordDeviceInfo.getIsRoot())); //是否越狱1：是

        deviceInfo.setPhoneBrand(ordDeviceInfo.getPhoneBrand());

        deviceInfo.setHitSameExtendDeviceCount(ordDeviceExtendInfoService.hitExtendDeviceUserCount(ordDeviceInfo));

        return Optional.of(deviceInfo);
    }


}
