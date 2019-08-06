package com.yqg.user.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.user.entity.UsrStudentDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Mapper
public interface UsrStudentDetailDao extends BaseMapper<UsrStudentDetail> {

    @Select("select * from (select * from usrStudentDetail a where disabled=0 and userUuid!=#{userUuid} and email=#{email} order by " +
            " createTime asc ) a limit 1")
    UsrStudentDetail getFirstEmailExistsUser(@Param("userUuid") String userUuid, @Param("email") String email);
}