package com.yqg.system.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.system.entity.SysDist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Mapper
public interface SysDistDao extends BaseMapper<SysDist> {
    @Select(" select id,uuid,createUser,createTime,updateUser,updateTime,remark,dicName,dicCode from sysDict where disabled = 0 and language=#{language} limit #{pageStart},#{pageSize} ")
    public List<SysDist> sysDicByPage(@Param("pageSize") Integer pageSize, @Param("pageStart") Integer pageStart, @Param("language") String language);
}
