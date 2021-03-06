package com.yqg.user.dao;

import com.yqg.user.entity.RegisterDeviceInfo;
import com.yqg.base.data.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.*;

/**
 * Created by Didit Dwianto on 2017/11/24.
 * edited 2020/02/18
 */
@Mapper
public interface RegisterDeviceInfoDao extends BaseMapper<RegisterDeviceInfo> {
    @Select("select fcmToken from usrRegisterDevice where disabled = 0 and userUuid = #{userUuid}")
    String getFcmTokenByUserUuid(@Param("userUuid") String userUuid);

    @Select("select * from usrRegisterDevice where disabled = 0 and userUuid = #{userUuid}")
    RegisterDeviceInfo getRegisterDeviceByUserUuid(@Param("userUuid") String userUuid);

    @Update("update usrRegisterDevice set fcmToken = #{fcmToken} where userUuid = #{userUuid} and disabled = 0")
    void updateFcmToken(@Param("userUuid") String userUuid, @Param("fcmToken") String fcmToken);
}
