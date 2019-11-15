package com.yqg.system.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.system.entity.SysSmsCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 短信code
 */
@Mapper
public interface SysSmsCodeDao extends BaseMapper<SysSmsCode> {

    @Select("select * from sysSmsCode limit 1;")
    List<SysSmsCode> getAllSysSmsCode();
}
