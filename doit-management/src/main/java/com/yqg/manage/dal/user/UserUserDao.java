package com.yqg.manage.dal.user;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.dal.provider.UserUserSqlProvider;
import com.yqg.manage.service.user.request.ManUserUserRequest;
import com.yqg.manage.service.user.response.PayDepositResponse;
import com.yqg.user.entity.UsrUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author alan
 */
@Mapper
public interface UserUserDao extends BaseMapper<UsrUser> {
    @SelectProvider(type = UserUserSqlProvider.class, method = "getUuidByRealNameOrMobile")
    @Options(useGeneratedKeys = true)
    public List<UsrUser> getUuidByRealNameOrMobile(@Param("ManUserUserRequest") ManUserUserRequest entity);

    @SelectProvider(type = UserUserSqlProvider.class, method = "getUserInfoByUuids")
    @Options(useGeneratedKeys = true)
    public List<UsrUser> getInfoByUuids(@Param("Uuids") String uuids);


    @SelectProvider(type = UserUserSqlProvider.class, method = "listpayDeposit")
    @Options(useGeneratedKeys = true)
    List<PayDepositResponse> listpayDeposit(@Param("orderNo") String orderNo, @Param("paymentCode") String paymentCode);

    @Select("select * from usrUser where uuid = #{uuid} and disabled = 0;")
    UsrUser getUserByUuid(@Param("uuid") String uuid);
}
