package com.yqg.user.dao;


import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.user.entity.ManUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author tonggen
 */
@Mapper
public interface ManSysUserDao extends BaseMapper<ManUser> {

    @Select("select count(*) from manSysUserRole where disabled = 0 and userId = #{userId} and roleId in" +
            " (${roleId}) and `status`=0;")
    Integer hasAuthorityByRoleName(@Param("userId") Integer userId, @Param("roleId") String roleId);
}
