package com.yqg.manage.dal.user;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.dal.provider.ManQuestionnaireSqlProvider;
import com.yqg.service.user.request.UsrQuestionnaireListRequest;
import com.yqg.service.user.response.UserQuestionnaireDetailResponse;
import com.yqg.service.user.response.UsrQuestionnaireBaseResponse;
import com.yqg.user.entity.UsrQuestionnaire;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Author: tonggen
 * Date: 2019/6/20
 * time: 11:04 AM
 */
@Mapper
public interface ManUsrQuestionnaireDao extends BaseMapper<UsrQuestionnaire> {

    @SelectProvider(type = ManQuestionnaireSqlProvider.class, method = "listQuestionnaire")
    @Options(useGeneratedKeys = true)
    List<UsrQuestionnaireBaseResponse> listQuestionnaire(@Param("request") UsrQuestionnaireListRequest request);

    @Select("select * from usrQuestionnaire where disabled = 0 and userUuid=#{userUuid}")
    UserQuestionnaireDetailResponse getManQuestionnaireDetail(@Param("userUuid") String userUuid);

    @Update("update usrQuestionnaire set checker=#{checker}, state=#{state}, updateTime=now() where disabled = 0 and userUuid=#{userUuid};")
    int updateChecker(@Param("userUuid") String userUuid, @Param("state") Integer state,
                      @Param("checker") Integer checker);
}
