package com.yqg.collection.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.collection.dao.entity.ManCollectorInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Mapper
public interface ManCollectorInfoDao extends BaseMapper<ManCollectorInfo> {


    @Select("select * from doit.collectorInfo where disabled = 0 and userId = #{userId};")
    List<ManCollectorInfo> listCollectorInfo(@Param("userId") Integer userId);
}
