package com.yqg.manage.dal.order;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.entity.check.ManOrderCheckRule;
import com.yqg.manage.entity.check.ManOrderCheckRuleConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author tonggen
 */
@Mapper
public interface ManOrderCheckRuleConfigDao extends BaseMapper<ManOrderCheckRuleConfig> {

    @Select("select * from doit.manOrderCheckRuleConfig where disabled = 0 and infoType = #{infoType} order by ruleCount")
    List<ManOrderCheckRuleConfig> listRuleConfig(@Param("infoType") Integer infoType);
}
