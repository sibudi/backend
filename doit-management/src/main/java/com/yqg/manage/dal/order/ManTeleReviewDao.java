package com.yqg.manage.dal.order;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.entity.check.ManTeleReviewQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author Jacob
 */
@Mapper
public interface ManTeleReviewDao  extends BaseMapper<ManTeleReviewQuestion> {

    @Select("select id from manTeleReviewQuestion where type = #{type} and disabled = 0 and langue = #{langue} ")
    List<Integer> getTeleReviewQuestionId(@Param("type") Integer type, @Param("langue") Integer langue);


    @Select("<script> select * from manTeleReviewQuestion " +
            "where langue = #{langue} and id in " +
            "<foreach collection='ids' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + " </foreach></script>")
    List<ManTeleReviewQuestion> getTeleReviewQuestion(@Param("ids") List<Integer> ids, @Param("langue") Integer langue);
}
