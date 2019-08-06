package com.yqg.manage.dal.order;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.entity.check.ManOrderCheckRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author alan
 */
@Mapper
public interface ManOrderCheckRuleDao extends BaseMapper<ManOrderCheckRule> {

    @Update("update doit.manOrderCheckRule set disabled = 1 where disabled = 0 and orderNo = #{orderNo}")
    void deleteManOrderCheckRule(@Param("orderNo") String orderNo);

    @Select("select * from doit.manOrderCheckRule where disabled = 1 and orderNo = #{orderNo} and infoType = #{infoType} order by ruleCount , createTime desc")
    List<ManOrderCheckRule> getDisabedList(@Param("orderNo") String orderNo,
                                           @Param("infoType") Integer infoType);
}
