package com.yqg.manage.dal.system;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.system.entity.SysAutoReviewRule;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Mapper
public interface AutoReviewRuleDao extends BaseMapper<SysAutoReviewRule> {

    @Select({" select * from sysAutoReviewRule where disabled = 0 order by ruleSequence asc "})
    List<SysAutoReviewRule> getAllAutoReviewRules();
}

