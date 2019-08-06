package com.yqg.system.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.system.entity.SysAppH5;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Mapper
public interface SysAppH5Dao extends BaseMapper<SysAppH5>{
    @Select("select * from sysAppH5 where urlKey = 'HTML_LOAN_AGREEMENT'")
    List<SysAppH5> getCash2H5Contract();
}
