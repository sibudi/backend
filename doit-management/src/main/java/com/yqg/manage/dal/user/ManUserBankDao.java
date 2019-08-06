package com.yqg.manage.dal.user;


import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.dal.provider.ManUserSqlProvider;
import com.yqg.manage.entity.user.ManUser;
import com.yqg.manage.service.order.request.ManOrderListSearchResquest;
import com.yqg.manage.service.user.request.ManSysUserListRequest;
import com.yqg.user.entity.UsrBank;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author Jacob
 */
@Mapper
public interface ManUserBankDao extends BaseMapper<UsrBank> {

    @Select(" select * from usrBank " +
            " where status = 2 and disabled = 0 and userUuid = #{userUuid} order by createTime DESC")
    List<UsrBank> getUsrBankByUsrId(@Param("userUuid") String userUuid);

    @Update(" update manSysUserRole set disabled = 1 where userId = #{userId} and disabled = 0 ")
    public void delUserRoleLink(@Param("userId") Integer userId);

}
