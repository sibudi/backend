package com.yqg.drools.model;

import com.yqg.common.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 * 手机设备相关
 ****/
@Getter
@Setter
public class DeviceModel {

    private String deviceId;//设备id

    private Long matchedForOthersCount;//用户设备号匹配到存量其他人的设备号的次数

    private Long matchedIMEIForOthersCount;

    private Long sameIpApplyCount;//一个ip在同一天申请的次数

    private String mobileLanguage;//手机语言

    private String netType;// 网络类型

    private String pictureNumber;// 手机图片数量

    private Float totalMemory;// 手机总内存

    private BigDecimal totalSpace;//总容量

    private Boolean isJailBreak;//是否越狱

    private String phoneBrand;//手机品牌

    private Boolean isIOS;//是否是ios
//    private String deviceType;//设备类型iOS，android

    private Long matchedIMSIForOthersCount;// imsi匹配的量

    private Long matchedCustomDeviceFingerprintWithApps; //android+apps组合
    private Long matchedCustomDeviceFingerprintWithIp; //android+ip等组合
    private Long matchedCustomDeviceFingerprintWithPhoneBrand;//android+phoneBrand等组合

    private Long matchedCustomDeviceFingerprintNotEmptyWithDeviceNameCpuType; // android+deviceName+totalMemory+cpuType

    private Long matchedCustomDeviceFingerprintWithoutAndroidId; // deviceName etc.

    private Integer hitSameExtendDeviceCount;


}
