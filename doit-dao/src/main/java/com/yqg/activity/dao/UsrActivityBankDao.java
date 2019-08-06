package com.yqg.activity.dao;

import com.yqg.activity.entity.UsrActivityBank;
import com.yqg.base.data.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Features:
 * Created by huwei on 18.8.17.
 */
@Mapper
public interface UsrActivityBankDao extends BaseMapper<UsrActivityBank>{

    @Select("SELECT * FROM usrActivityBank WHERE disabled=0 AND userUuid=#{userUuid} AND STATUS IN (1,2)")
    List<UsrActivityBank> scanUsrActivityBank(@Param("userUuid") String userUuid);

}
