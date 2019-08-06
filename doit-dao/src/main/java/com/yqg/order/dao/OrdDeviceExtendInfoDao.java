package com.yqg.order.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.order.entity.OrdDeviceExtendInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrdDeviceExtendInfoDao extends BaseMapper<OrdDeviceExtendInfo> {

    @Select("select distinct userUuid from ordDeviceExtendInfo where disabled=0 and (device1=#{device} or device2=#{device} or device3=#{device} or" +
            " device4=#{device}) and userUuid !=#{userUuid}")
    List<String> getHistUserIdList(@Param("device") String device,@Param("userUuid") String userUuid);
}
