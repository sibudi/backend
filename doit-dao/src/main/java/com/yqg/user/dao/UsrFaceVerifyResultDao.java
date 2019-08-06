package com.yqg.user.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.user.entity.UsrFaceVerifyResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UsrFaceVerifyResultDao extends BaseMapper<UsrFaceVerifyResult> {

    @Select("select * from (select * from usrFaceVerifyResult u where u.userUuid = #{userUuid} order by id desc) aa limit 1")
    UsrFaceVerifyResult getLatestResult(@Param("userUuid") String userUuid);

    @Select("select * from (select * from usrFaceVerifyResult u where u.userUuid = #{userUuid} and channel=#{channel} order by id desc) aa limit 1")
    UsrFaceVerifyResult getLatestResultByUserIdAndChannel(@Param("userUuid") String userUuid, @Param("channel") String channel);
}
