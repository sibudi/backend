package com.yqg.risk.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.risk.entity.ScoreTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ScoreTemplateDao extends BaseMapper<ScoreTemplate> {
    @Select("select * from scoreTemplate where disabled =0 ")
    List<ScoreTemplate> getAllAvailableTemplate();

    @Select("select * from scoreTemplate where modelName=#{modelName} and version=#{version} limit 1")
    ScoreTemplate getScoreTemplateByModelNameAndVersion(@Param("modelName") String modelName, @Param("version") Integer version);
}
