package com.yqg.user.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.user.entity.UsrIziVerifyResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by wanghuaizhou on 2018/12/7.
 */
@Mapper
public interface UsrIziVerifyResultDao extends BaseMapper<UsrIziVerifyResult> {
    @Select("\n" +
            "select * from usrIziVerifyResult where userUuid =#{userUuid} and iziVerifyType = #{iziVerifyType} order by createTime desc limit 1;")
    UsrIziVerifyResult getLatestResult(@Param("userUuid") String userUuid ,@Param("iziVerifyType") String type);
}
