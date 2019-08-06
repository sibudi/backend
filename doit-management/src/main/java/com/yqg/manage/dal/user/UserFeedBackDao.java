package com.yqg.manage.dal.user;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.dal.provider.UserFeedBackSqlProvider;
import com.yqg.manage.service.user.request.ManUserUserRequest;
import com.yqg.user.entity.UsrFeedBack;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by tonggen on 2018/1/25
 */
@Mapper
public interface UserFeedBackDao extends BaseMapper<UsrFeedBack> {
    @SelectProvider(type = UserFeedBackSqlProvider.class, method = "userFeedBackList")
    @Options(useGeneratedKeys = true)
    List<UsrFeedBack> userFeedBackList(@Param("ManUserUserRequest") ManUserUserRequest dataRequest);

    @SelectProvider(type = UserFeedBackSqlProvider.class, method = "userFeedBackListCountNum")
    @Options(useGeneratedKeys = true)
    Integer userFeedBackListCountNum(@Param("ManUserUserRequest") ManUserUserRequest dataRequest);


    @SelectProvider(type = UserFeedBackSqlProvider.class, method = "userFeedBackListByPage")
    @Options(useGeneratedKeys = true)
    List<UsrFeedBack> userFeedBackListByPage(@Param("ManUserUserRequest") ManUserUserRequest dataRequest);
}
