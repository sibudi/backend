package com.yqg.user.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.user.entity.UsrLoginHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/25.
 */
@Mapper
public interface UsrLoginHistoryDao extends BaseMapper<UsrLoginHistory> {

    @Select("SELECT * FROM doit.usrLoginHistory where userUuid = #{userUuid} and createTime > '2018-06-08 00:00:00' and disabled = 0 ")
    List<UsrLoginHistory> getLoginByTiming(@Param("userUuid") String userUuid);

}