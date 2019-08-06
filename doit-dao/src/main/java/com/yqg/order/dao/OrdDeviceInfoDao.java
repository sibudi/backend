package com.yqg.order.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.order.entity.OrdDeviceInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/29.
 */
@Mapper
public interface OrdDeviceInfoDao extends BaseMapper<OrdDeviceInfo>{

    @Select("SELECT * FROM ordDeviceInfo where IPAddress = #{IPAddress} and disabled = 0  and updateTime like '%${updateTime}%';")
    List<OrdDeviceInfo> ipListOneDay (@Param("IPAddress") String ipAddress,@Param("updateTime") String updateTime);


    @Select("SELECT * FROM ordDeviceInfo d where deviceId = #{deviceId} and disabled = 0  and userUuid != #{userUuid} " +
            " and exists(select 1 from ordOrder oo where oo.uuid = d.orderNo and oo.status!=1);")
    List<OrdDeviceInfo> deviceIdList (@Param("deviceId") String deviceId,@Param("userUuid") String userUuid);

    @Select("SELECT * FROM ordDeviceInfo d where IMEI = #{imei} and disabled = 0  and userUuid != #{userUuid} " +
            " and exists(select 1 from ordOrder oo where oo.uuid = d.orderNo and oo.status!=1);")
    List<OrdDeviceInfo> imeiList (@Param("imei") String imei,@Param("userUuid") String userUuid);

    @Select("SELECT * FROM ordDeviceInfo d where IMSI = #{imsi} and disabled = 0  and userUuid != #{userUuid} " +
            " and exists(select 1 from ordOrder oo where oo.uuid = d.orderNo and oo.status!=1);")
    List<OrdDeviceInfo> imsiList (@Param("imsi") String imsi,@Param("userUuid") String userUuid);

    @Select("SELECT * FROM ordDeviceInfo d where androidId= #{androidId} " +
            " and appFingerprint = #{appFingerprint} and disabled = 0  and userUuid != #{userUuid} " +
            " and exists(select 1 from ordOrder oo where oo.uuid = d.orderNo and oo.status!=1);")
    List<OrdDeviceInfo> customDeviceFingerprintList(@Param("androidId") String androidId,
                                                    @Param("appFingerprint") String appFingerprint,
                                                    @Param("userUuid") String userUuid);

    @Select("SELECT * FROM ordDeviceInfo d where androidId= #{queryParam.androidId} " +
            " and deviceType = #{queryParam.deviceType} " +
            " and phoneBrand = #{queryParam.phoneBrand} " +
            " and totalMemory = #{queryParam.totalMemory}" +
            " and disabled = 0  and userUuid != #{queryParam.userUuid} " +
            " and exists(select 1 from ordOrder oo where oo.uuid = d.orderNo and oo.status!=1);")
    List<OrdDeviceInfo> customDeviceFingerprintListWithPhoneBrand(@Param("queryParam") OrdDeviceInfo.CustomDeviceFingerprint queryParam);

    @Select("SELECT * FROM ordDeviceInfo d where androidId= #{queryParam.androidId} " +
            " and deviceType = #{queryParam.deviceType} " +
            " and IPAddress = #{queryParam.ip} " +
            " and totalMemory = #{queryParam.totalMemory}" +
            " and disabled = 0  and userUuid != #{queryParam.userUuid} " +
            " and exists(select 1 from ordOrder oo where oo.uuid = d.orderNo and oo.status!=1);")
    List<OrdDeviceInfo> customDeviceFingerprintListWithIp(@Param("queryParam") OrdDeviceInfo.CustomDeviceFingerprint queryParam);

    @Select("SELECT * FROM ordDeviceInfo d where androidId= #{queryParam.androidId} " +
            " and deviceName = #{queryParam.deviceName} " +
            " and cpuType = #{queryParam.cpuType} " +
            " and totalMemory = #{queryParam.totalMemory}" +
            " and disabled = 0  and userUuid != #{queryParam.userUuid} " +
            " and exists(select 1 from ordOrder oo where oo.uuid = d.orderNo and oo.status!=1 and oo.disabled=0);")
    List<OrdDeviceInfo> getCustomDeviceFingerprintWithDeviceNameCpuType(@Param("queryParam") OrdDeviceInfo.CustomDeviceFingerprint queryParam);

    @Select("SELECT * FROM ordDeviceInfo d where macAddress= #{queryParam.macAddress} " +
            " and deviceType = #{queryParam.deviceType} " +
            " and deviceName = #{queryParam.deviceName} " +
            " and phoneBrand = #{queryParam.phoneBrand}" +
            " and totalMemory = #{queryParam.totalMemory}" +
            " and cpuType = #{queryParam.cpuType}" +
            " and  lastPowerOnTime  = #{queryParam.lastPowerOnTime }" +
            " and disabled = 0  and userUuid != #{queryParam.userUuid} " +
            " and exists(select 1 from ordOrder oo where oo.uuid = d.orderNo and oo.status!=1 and oo.disabled=0);")
    List<OrdDeviceInfo> getCustomDeviceFingerprintWithoutAndroidId(@Param("queryParam") OrdDeviceInfo queryParam);


    @Select("select * from ordDeviceInfo where id>=#{startId} and id<#{endId}")
    List<OrdDeviceInfo> getListById(@Param("startId") Integer startId, @Param("endId") Integer endId);

}
