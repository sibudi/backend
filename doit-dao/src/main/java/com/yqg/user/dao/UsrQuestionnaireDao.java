package com.yqg.user.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.user.entity.UsrQuestionnaire;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * Author: tonggen
 * Date: 2019/6/18
 * time: 12:46 PM
 */
@Mapper
public interface UsrQuestionnaireDao extends BaseMapper<UsrQuestionnaire> {


    @Update("update usrQuestionnaire set disabled = 1 , remark = 'update disabled' where disabled = 0 and userUuid = #{userUuid};")
    int deleteUsrQuestionnaire(@Param("userUuid") String userUuid);

    @Update("update usrQuestionnaire set state = 1 where disabled = 0 and userUuid = #{userUuid};")
    int updateState(@Param("userUuid") String userUuid);
}
