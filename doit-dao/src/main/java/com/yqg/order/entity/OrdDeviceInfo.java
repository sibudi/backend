package com.yqg.order.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import com.yqg.common.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Didit Dwianto on 2017/11/29.
 */
@Data
@Table("ordDeviceInfo")
public class OrdDeviceInfo extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -6334482387697444938L;

    private String userUuid;

    private  String orderNo;

    private String deviceType;

    private String deviceName;

    private String deviceId;

    private String systemVersion;

    private String phoneBrand;

    private String totalMemory;

    private String remainMemory;

    private String totalSpace;

    private String remainSpace;

    private String IMEI;

    private String IMSI;

    private String simNo;

    private String cpuType;


    private String lastPowerOnTime;

    private String DNS;


    private String isRoot;


    private String netType;


    private String memoryCardCapacity;


    private String wifiList;


    private String macAddress;


    private String mobileLanguage;


    private String IPAddress;


    private String isSimulator;


    private String battery;


    private String pictureNumber;

    private String androidId;

    private String appFingerprint;

    public static BigDecimal getFormatStorageCapacity(String storage) {
        if(!StringUtils.isEmpty(storage)&&storage.contains(",")){
            storage = storage.replaceAll(",",".");
        }
        if (StringUtils.isNotEmpty(storage) && (
                storage.contains("G") || storage
                        .contains("M"))) {
            String regNum = "[^0-9\\.]";
            Pattern p = Pattern.compile(regNum);
            Matcher m = p.matcher(storage);
            String temp = m.replaceAll("").trim();
            if (storage.contains("G") && StringUtils.isNotEmpty(temp)) {
                return new BigDecimal(temp);
            }

            if (storage.contains("M") && StringUtils.isNotEmpty(temp)) {
                return new BigDecimal(temp)
                        .divide(new BigDecimal("1000"), 6, BigDecimal.ROUND_HALF_UP);
            }
        }

        return null;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class CustomDeviceFingerprint {
        private String androidId;
        private String deviceType;
        private String totalMemory;
        private String ip;
        private String userUuid;
        private String phoneBrand;
        private String deviceName;
        private String cpuType;

        public boolean isCustomDeviceFingerprintNotEmptyWithIp(){
            return StringUtils.isNotEmpty(androidId) && StringUtils.isNotEmpty(deviceType)
                    && StringUtils.isNotEmpty(totalMemory) && StringUtils.isNotEmpty(ip);
        }
        public boolean isCustomDeviceFingerprintNotEmptyWithPhoneBrand(){
            return StringUtils.isNotEmpty(androidId) && StringUtils.isNotEmpty(deviceType)
                    && StringUtils.isNotEmpty(totalMemory) && StringUtils.isNotEmpty(phoneBrand);
        }

        public boolean isCustomDeviceFingerprintNotEmptyWithDeviceNameCpuType() {
            return StringUtils.isNotEmpty(androidId) && StringUtils.isNotEmpty(deviceName) && StringUtils.isNotEmpty(totalMemory)
                    && StringUtils.isNotEmpty(cpuType);
        }
    }
}
