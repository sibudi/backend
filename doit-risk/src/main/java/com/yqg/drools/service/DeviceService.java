package com.yqg.drools.service;

import com.yqg.order.dao.OrdDeviceInfoDao;
import com.yqg.order.entity.OrdDeviceInfo;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/*****
 * @Author zengxiangcai
 * Created at 2018/1/19
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Service
@Slf4j
public class DeviceService {

    @Autowired
    private OrdDeviceInfoDao ordDeviceInfoDao;

    public OrdDeviceInfo getOrderDeviceInfo(String orderUUID) {
        OrdDeviceInfo search = new OrdDeviceInfo();
        search.setDisabled(0);
        search.setOrderNo(orderUUID);
        List<OrdDeviceInfo> mobileDeviceInfos = this.ordDeviceInfoDao.scan(search);
        if (CollectionUtils.isEmpty(mobileDeviceInfos)) {
            log.warn("the device info is empty, orderId: {}", orderUUID);
            return null;
        }

        OrdDeviceInfo orderDevice = mobileDeviceInfos.get(0);
        return orderDevice;
    }


    /***
     * 设备deviceId不属于userUUID的设备记录
     * @param deviceId
     * @param userUUID
     * @return
     */
    public List<OrdDeviceInfo> getDeviceListForOthers(String deviceId, String userUUID) {
        return this.ordDeviceInfoDao.deviceIdList(deviceId, userUUID);
    }

    /***
     * 同一天相同ip申请记录
     * @param ipAddress
     * @param date
     * @return
     */
    public List<OrdDeviceInfo> getDeviceListForSameIpByDate(String ipAddress, String date) {
        return this.ordDeviceInfoDao.ipListOneDay(ipAddress, date);
    }


    public List<OrdDeviceInfo> getImeiForOthers(String imei,String userUuid){
        return ordDeviceInfoDao.imeiList(imei,userUuid);
    }

    public List<OrdDeviceInfo> getImsiForOthers(String imsi,String userUuid){
        return ordDeviceInfoDao.imsiList(imsi,userUuid);
    }

    public List<OrdDeviceInfo> getCustomDeviceFingerprintForOthers(String androidId, String customFingerprint,String userUuid){
        return ordDeviceInfoDao.customDeviceFingerprintList(androidId,customFingerprint,userUuid);
    }

    public List<OrdDeviceInfo> getCustomDeviceFingerprintWithIpForOthers(OrdDeviceInfo.CustomDeviceFingerprint queryParam){
        return ordDeviceInfoDao.customDeviceFingerprintListWithIp(queryParam);
    }

    public List<OrdDeviceInfo> getCustomDeviceFingerprintWithPhoneBrandForOthers(OrdDeviceInfo.CustomDeviceFingerprint queryParam){
        return ordDeviceInfoDao.customDeviceFingerprintListWithPhoneBrand(queryParam);
    }

    public List<OrdDeviceInfo> getCustomDeviceFingerprintWithDeviceNameCpuType(OrdDeviceInfo.CustomDeviceFingerprint queryParam) {
        return ordDeviceInfoDao.getCustomDeviceFingerprintWithDeviceNameCpuType(queryParam);
    }
    public List<OrdDeviceInfo> getCustomDeviceFingerprintWithoutAndroidId(OrdDeviceInfo queryParam) {
        return ordDeviceInfoDao.getCustomDeviceFingerprintWithoutAndroidId(queryParam);
    }
}
