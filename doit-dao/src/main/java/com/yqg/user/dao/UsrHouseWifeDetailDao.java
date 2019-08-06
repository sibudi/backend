package com.yqg.user.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.user.entity.UsrHouseWifeDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by wanghuaizhou on 2018/8/17.
 */
@Mapper
public interface UsrHouseWifeDetailDao extends BaseMapper<UsrHouseWifeDetail> {


    @Select("select * from (select * from usrHouseWifeDetail a where disabled=0 and userUuid!=#{userUuid} and email=#{email} order by " +
            " createTime asc ) a limit 1")
    UsrHouseWifeDetail getFirstEmailExistsUser(@Param("userUuid") String userUuid, @Param("email") String email);
}
