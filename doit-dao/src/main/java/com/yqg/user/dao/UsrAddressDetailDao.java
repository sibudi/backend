package com.yqg.user.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.user.entity.UsrAddressDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Mapper
public interface UsrAddressDetailDao extends BaseMapper<UsrAddressDetail> {

    @Update("update usrAddressDetail set disabled = 1 where userUuid = #{userUuid} and " +
            "addressType = #{addressType} and disabled = 0")
    void deleteUsrAddressDetail(@Param("userUuid") String userUuid,
                                @Param("addressType") Integer addressType);

    @Select("select count(1) from usrAddressDetail a where disabled=0 and userUuid != #{userUuid} " +
            " and smallDirect=#{smallDirect} and detailed = #{detail}" +
            " and addressType = #{addressType}" +
            " and exists(select 1 from ordOrder o where o.userUuid = a.userUuid and o.status!=1)")
    Integer countOfSameSmallDirectAndDetail(@Param("userUuid") String userUuid, @Param("smallDirect") String smallDirect,
                                            @Param("detail") String detail,@Param("addressType") Integer addressType);
}