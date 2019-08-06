package com.yqg.system.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.system.entity.SysAutoReviewRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/30.
 */
@Mapper
public interface SysAutoReviewRuleDao extends BaseMapper<SysAutoReviewRule>{

    @Select(" select * from sysAutoReviewRule where disabled = 0 and ruleStatus in(1,3) ")
    List<SysAutoReviewRule> reviewRuleList();

    @Select(" select * from sysAutoReviewRule where disabled = 0 and ruleStatus in(1,3) and  ruleDetailType = #{ruleName}")
    SysAutoReviewRule getRuleConfigByName(@Param("ruleName") String ruleName);


}
