package com.yqg.system.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.system.entity.SysShareDict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Didit Dwianto on 2017/12/2.
 */
@Mapper
public interface SysShareDictDao extends BaseMapper<SysShareDict> {

    @Select("SELECT * FROM sysShareDict where disabled = 0 and id =#{shareId}")
    List<SysShareDict> getShareDictWithShareId(@Param("shareId") Integer shareId);
}
