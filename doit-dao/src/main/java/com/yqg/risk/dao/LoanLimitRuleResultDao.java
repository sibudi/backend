package com.yqg.risk.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.risk.entity.LoanLimitRuleResultEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LoanLimitRuleResultDao extends BaseMapper<LoanLimitRuleResultEntity> {

    @Select("select count(1) from loanLimitRuleResult where orderNo = #{orderNo} and productType = #{productType} and pass='true'")
    Integer hitRuleCount(@Param("orderNo") String orderNo, @Param("productType") String productType);
}
