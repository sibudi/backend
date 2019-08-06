package com.yqg.risk.dao;

import com.yqg.risk.entity.RuleParam;
import com.yqg.system.entity.SysAutoReviewRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FlowRuleSetDao {
    @Select("<script>" +
            "select *from sysAutoReviewRule s where s.disabled = 0\n" +
            "and s.ruleDetailType in (\n" +
            "select f.ruleDetailType from flowRuleSet f where f.flowName in "
            + "<foreach collection='flowNames' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            "and f.disabled=0\n" +
            " )"
            + "</script>")
    List<SysAutoReviewRule> getExecutedRulesByFlowName(@Param("flowNames") List<String> flowNames);

    @Select("<script>" +
            "select p.ruleDetailType,p.thresholdValue\n" +
            "from ruleParam p\n" +
            "where p.flowName in "
            + "<foreach collection='flowNames' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            "and p.disabled =0"
            + "</script>")
    List<RuleParam> getRuleParamsByFlowName(@Param("flowNames") List<String> flowNames);

//    @Select("select flowName,ruleDetailType from flowRuleSet where f.flowName not in ('AUTO_CALL') and f.ruleDetailType in (" +
//            " select f1.ruleDetailType from flowRuleSet f1 where f1.flowName = 'AUTO_CALL')")
//    List<FlowRuleSet> getAutoCallSpecifiedRules();
}
